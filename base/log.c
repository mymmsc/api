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

typedef struct __log_struct{
	int         fd;
	int64_t     current;
	int         interval;
	off_t       length; // max
	size_t      size; // use
	byte_t      type;
	char        prefix[64];
//	char        rule[128];
	char        fname[256];
}api_log_t;

static api_log_t g_logger[16];

#ifdef _WIN32
static const char *log_path = "/runtime/logs/bid/";
#else
static const char *log_path = "/home/runtime/logs/bid/";
#endif
static const char *log_access = "access.log";
//static const char *log_stdout = "stdout.log";
//static int log_fd_stdout = -1;

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


void api_logger_init(void)
{
	int i = 0;
	int num = ARRAY_SIZE(g_logger);
	memset(g_logger, 0x00, sizeof(g_logger));
	for(i = 0; i < num; i++) {
		api_log_t *log = g_logger + i;
		log->fd = -1;
	}
	api_log_init(LOG_LEVEL_INFO, 3600, "access");
	api_log_init(LOG_LEVEL_FERROR, 3600, "error");
}

void api_logger_close(void)
{
	int i = 0;
	int num = ARRAY_SIZE(g_logger);
	for(i = 0; i < num; i++) {
		api_log_t *log = g_logger + i;
		if(log->fd != -1) {
			close(log->fd);
			log->fd = -1;
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
	}
}

int api_log(log_level_e level, const char *fmt, va_list args)
{
	int iRet = -1;
	int len = 0;
	FILE *out = NULL;
	FILE *fInfo = stdout;
	FILE *fError = stderr;
	char fmtbuf[10240] = {0};
	char msgbuf[40960] = {0};
	char *prefix = NULL;
	
	switch (level) {
		case LOG_LEVEL_FATAL:
			prefix = "fatal";
			out = fError;
			break;
		case LOG_LEVEL_ERROR:
			prefix = "error";
			out = fError;
			break;
		case LOG_LEVEL_INFO:
			prefix = "info";
			out = fInfo;
			break;
		case LOG_LEVEL_VERBOSE:
			prefix = "info";
			out = fInfo;
			break;
		case LOG_LEVEL_DEBUG:
			prefix = "debug";
			out = fInfo;
			break;
		default:
			prefix = "warn";
			out = fError;
			break;
	}
	memset(fmtbuf, 0x00, sizeof(fmtbuf));
	memset(msgbuf, 0x00, sizeof(msgbuf));
	if(level & LOG_LEVEL_INFO) {
		char ts[100];
		memset(ts, 0x00, sizeof(ts));
		current_timestring(1, ts, sizeof(ts));
		snprintf(fmtbuf, sizeof(fmtbuf), "[%s] %s", ts, fmt);
	} else {
		snprintf(fmtbuf, sizeof(fmtbuf), "%s: %s", prefix, fmt);
	}
	len = vsnprintf(msgbuf, sizeof(msgbuf) - 2, fmtbuf, args);
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
	iRet = fprintf(out, "%s\r\n", msgbuf);
	if(level & LOG_LEVEL_INFO)
	{
		api_log_t *log = api_log_get(level);
		api_time_t t0 = api_time_now();
		api_time_t t1 = api_time_sec(t0);
		
		int64_t t2 = t1/log->interval;
		if(log->fd == -1 || t2 > log->current) {
			log->current = t2;
			if(log->fd != -1) {
				close(log->fd);
				log->fd = -1;
				log->size = 0;
			}
			if(access(log_path, F_OK) != 0) {
				trace_out("create path %s.", log_path);
				if(api_mkdirs(log_path) != 0) {
					trace_err("mkdir error: %s", log_path);
				}
			}

			time_t t = time(NULL);
			struct tm *dt = localtime(&t);
			char logfile[1024];
			
			memset(logfile, 0x00, sizeof(logfile));
			if(log->interval == 60) {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i%.2i%.2i.log", log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday,
					dt->tm_hour, dt->tm_min);
			} else if(log->interval == 3600) {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i%.2i.log", log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday,
					dt->tm_hour);
			} else {
				snprintf(logfile, sizeof(logfile) - 1, "%s/%s.%4i%.2i%.2i.log", log_path, 
					log->prefix,
					dt->tm_year + 1900, dt->tm_mon + 1,	dt->tm_mday);
			}
			if((log->fd = open(logfile, O_RDWR|O_CREAT|O_APPEND
#ifndef _WIN32
				, S_IRUSR|S_IWUSR
#endif
				)) == -1) {
				log->fd = -1;
				trace_out("can not open file: %s", logfile);
			}
		}
		if(log->fd > 0) {
			strcat(msgbuf, "\r\n");
			size_t ms = api_strlen(msgbuf);
			iRet = write(log->fd, msgbuf, ms);
			log->size + ms;
		}
	}
	return iRet;
}

int api_log_message(log_level_e level, const char *format, ...)
{
	int iRet = -1;
	va_list args;
	va_start(args, format);
	iRet = api_log(level, format, args);
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
	iRet = api_log(level, fmtbuf, args);
	va_end(args);
	
	return iRet;
}

void api_log_fatal(log_level_e level, const char *fmt,...)
{
	va_list args;
	va_start(args, fmt);
	api_log(level, fmt, args);
	va_end(args);
	exit(255);
}

void api_log_assert(log_level_e level, int exp, const char *exps, const char *filename, int line, const char *function)
{
	if(!exp) {
		char fmtbuf[1024] = {0};
		snprintf(fmtbuf, sizeof(fmtbuf), "%s, (%s), abort.", fmt_trace, exps);
		api_log_message(level, fmtbuf, filename, line, function);
		abort();
	}
}


