#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>

#include <fcntl.h> // for file 
#include <sys/stat.h>

#include <api/log.h>
#include <api/file.h>

#ifndef _WIN32
#include <sys/time.h>
#else
#include <sys/utime.h>
#include <direct.h>
#endif

#include <api/time.h>
#include <api/lib.h>
#include <api/strings.h>
#include <time.h>
#include <api/atomic.h>

typedef struct __log_struct{
	int         fd;
	int64_t     current;
	int         interval;
	byte_t      type;
	char        prefix[64];
//	char        rule[128];
	char        fname[256];

	atomic_t    buffer_mutex;
	char        buffer[4096];
	off_t		length; // max
	size_t		size; // use
}api_log_t;

static api_log_t g_logger[16];
static char      g_log_path[1024];

#ifdef _WIN32
static const char *log_path_default = "/runtime/logs/";
#elif defined(API_APPLE)
static const char *log_path_default = "/Users/wangfeng/runtime/logs/";
#else
static const char *log_path_default = "/home/runtime/logs/";
#endif

static const char *fmt_trace = "%s(%d) : %s: ";

#ifdef _WIN32
#define SSH_USEC_IN_SEC         1000000LL
#define SSH_SECONDS_SINCE_1601  11644473600LL

int gettimeofday(struct timeval *__p, void *__t) {
	union {
		unsigned long long ns100; /* time since 1 Jan 1601 in 100ns units */
		FILETIME ft;
	} now;

	GetSystemTimeAsFileTime (&now.ft);
	__p->tv_usec = (long) ((now.ns100 / 10LL) % SSH_USEC_IN_SEC);
	__p->tv_sec  = (long)(((now.ns100 / 10LL ) / SSH_USEC_IN_SEC) - SSH_SECONDS_SINCE_1601);

	return (0);
}
#endif

static int current_timestring(int hires, char *buf, size_t len)
{
    char tbuf[64];
    struct timeval tv;
    struct tm *tm;
    time_t t;

    gettimeofday(&tv, NULL);
    t = (time_t) tv.tv_sec;

    tm = localtime(&t);
    if (tm == NULL) {
        return -1;
    }

    if (hires) {
        strftime(tbuf, sizeof(tbuf) - 1, "%Y/%m/%d %H:%M:%S", tm);
        snprintf(buf, len, "%s.%06ld", tbuf, (long)tv.tv_usec);
    } else {
        strftime(tbuf, sizeof(tbuf) - 1, "%Y/%m/%d %H:%M:%S", tm);
        snprintf(buf, len, "%s", tbuf);
    }

    return 0;
}

static void log_flush_data(api_log_t *log)
{
	int rc;
	if(log->size > 0) {
		api_spinlock(&log->buffer_mutex, 1, 2048);
		rc = write(log->fd, log->buffer, log->size);
		log->size = 0;
		api_unlock(&log->buffer_mutex);
	}
}

void api_logger_init(const char *path)
{
	int i = 0;
	int num = ARRAY_SIZE(g_logger);
	memset(g_logger, 0x00, sizeof(g_logger));
	for(i = 0; i < num; i++) {
		api_log_t *log = g_logger + i;
		log->buffer_mutex = 0;
		log->fd = -1;
	}
	if(api_strlen(path) > 0) {
		api_cpystrn(g_log_path, path, sizeof(g_log_path));
	} else {
		api_cpystrn(g_log_path, log_path_default, sizeof(g_log_path));
	}
	api_log_init(API_LOG_FILE | API_LOG_ACCESS, 3600, "access");
	api_log_init(API_LOG_FILE | API_LOG_ERROR, 3600, "error");
	api_log_init(API_LOG_FILE | API_LOG_DEBUG, 3600, "debug");
	api_log_init(API_LOG_FILE | API_LOG_FATAL, 3600, "fatal");
}

void api_logger_close(void)
{
	int i = 0;
	int num = ARRAY_SIZE(g_logger);
	for(i = 0; i < num; i++) {
		api_log_t *log = g_logger + i;
		if(log->fd != -1) {
			log_flush_data(log);
			close(log->fd);
			log->fd = -1;
			log->buffer_mutex = 0;
		}
	}
}

static api_log_t * api_log_get(byte_t type)
{
	api_log_t *log = NULL;
	byte_t idx = type & 0x0f;
	if(idx < 16) {
		log = g_logger + idx;
		//memset(log, 0x00, sizeof(*log));
		//log->fd = -1;
		//log->type = type;
	}
	return log;
}

void api_log_init(byte_t type, int interval, const char *prefix)
{
	api_log_t *log = api_log_get(type);
	if(log != NULL) {
		api_cpystrn(log->prefix, prefix, sizeof(log->prefix));
		if(interval == 60) {
			log->interval = 60;
		} else if(interval == 60 * 60) {
			log->interval = 3600;
		} else {
			log->interval = 3600 * 24;
		}
		log->length = sizeof(log->buffer);
		memset(log->buffer, 0x00, log->length);
		log->size = 0;
		log->buffer_mutex = 0;
	}
}


int api_log_core(log_level_e level, const char *fmt, va_list args)
{
	int iRet = -1;
	int len = 0;
	FILE *out = NULL;
	FILE *fInfo = stdout;
	FILE *fError = stderr;
	char fmtbuf[10240] = {0};
	char msgbuf[40960] = {0};
	char *prefix = NULL;
	
	switch (level & 0x0F) {
		case API_LOG_ERROR:
			prefix = "error";
			out = fError;
			break;
		case API_LOG_FATAL:
			prefix = "fatal";
			out = fError;
			break;
		case API_LOG_DEBUG:
			prefix = "debug";
			out = fInfo;
			break;
		default:
			prefix = "info";
			out = fInfo;
			break;
	}
	memset(fmtbuf, 0x00, sizeof(fmtbuf));
	memset(msgbuf, 0x00, sizeof(msgbuf));
	if(level & API_LOG_FILE) {
		char ts[100];
		memset(ts, 0x00, sizeof(ts));
		current_timestring(1, ts, sizeof(ts));
		snprintf(fmtbuf, sizeof(fmtbuf), "[%s] %s", ts, fmt);
	} else {
		snprintf(fmtbuf, sizeof(fmtbuf), "%s: %s", prefix, fmt);
	}
	len = api_vsnprintf(msgbuf, sizeof(msgbuf) - 2, fmtbuf, args);
	if(len > 0) {
		char *p = msgbuf;
		while(len > 0)
	    {
			if(strchr(" \t\r\n", *(p + len - 1))) {
	            *(p + len - 1) = 0x00;
	            len--;
	        } else {
	            break;
	        }
	    }
	}
	if(level & API_LOG_STDOUT) {
		iRet = fprintf(out, "%s\r\n", msgbuf);
	}
	if(level & API_LOG_FILE)
	{
		api_log_t *log = api_log_get(level);
		api_time_t t0 = api_time_now();
		api_time_t t1 = api_time_sec(t0);
		
		int64_t t2 = t1/log->interval;
		if(log->fd == -1 || t2 > log->current) {
			log->current = t2;
			if(log->fd != -1) {
				log_flush_data(log);
				close(log->fd);
				log->fd = -1;
				log->size = 0;
			}
			if(access(g_log_path, F_OK) != 0) {
				//trace_out("create path %s.", g_log_path);
				if(api_mkdirs(g_log_path) != 0) {
					trace_err("mkdir error: %s", g_log_path);
				}
			}
			
			time_t t = time(NULL);
			struct tm *dt = localtime(&t);
			char logfile[1024];
			
			memset(logfile, 0x00, sizeof(logfile));
			if(log->interval == 60) {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i%.2i%.2i.log", g_log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday,
					dt->tm_hour, dt->tm_min);
			} else if(log->interval == 3600) {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i%.2i.log", g_log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday,
					dt->tm_hour);
			} else {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i.log", g_log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday);
			}
			if((log->fd = open(logfile, O_WRONLY|O_CREAT|O_APPEND
			#ifndef _WIN32
				, S_IRUSR|S_IWUSR
			#endif
				)) == -1) {
				log->fd = -1;
				trace_err("can not open file: %s", logfile);
			}
		}
		if(log->fd > 0) {
			strcat(msgbuf, "\r\n");
			size_t ms = api_strlen(msgbuf);
			//if(ms > log->length - log->size) {
			//	log_flush_data(log);
			//}
			//if(ms <= log->length - log->size) {
			//	memcpy(log->buffer + log->size, msgbuf, ms);
			//	log->size += ms;
			//} else {
			//	log_flush_data(log);
				iRet = write(log->fd, msgbuf, ms);
			//}			
		}
	}
	return iRet;
}

int api_log_message(log_level_e level, const char *format, ...)
{
	int iRet = -1;
	va_list args;
	va_start(args, format);
	iRet = api_log_core(level, format, args);
	va_end(args);
	return iRet;
}

int api_log_trace(log_level_e level, const char *filename, int line, const char *function, const char *fmt, ...)
{
	int iRet = -1;
	char tmpbuf[1024] = {0};
	char fmtbuf[1024] = {0};
	va_list args;
	
	iRet = snprintf(tmpbuf, sizeof(tmpbuf), fmt_trace, filename, line, function);
	if(iRet > 0) {
		tmpbuf[iRet] = 0x00;
	}
	va_start(args, fmt);
	iRet = snprintf(fmtbuf, sizeof(fmtbuf), "%s%s", tmpbuf, fmt);
	if(iRet > 0) {
		fmtbuf[iRet] = 0x00;
	}
	iRet = api_log_core(level, fmtbuf, args);
	va_end(args);
	
	return iRet;
}

void api_log_fatal(log_level_e level, const char *fmt,...)
{
	va_list args;
	va_start(args, fmt);
	api_log_core(level, fmt, args);
	va_end(args);
	exit(255);
}

void api_log_assert(log_level_e level, int exp, const char *exps, const char *filename, int line, const char *function)
{
	if(!exp) {
		char fmtbuf[4096] = {0};
		snprintf(fmtbuf, sizeof(fmtbuf), "%s, (%s), abort.", fmt_trace, exps);
		api_log_message(level, fmtbuf, filename, line, function);
		abort();
	}
}
