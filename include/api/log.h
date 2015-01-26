#ifndef __API_LOG_H_INCLUDED__
#define __API_LOG_H_INCLUDED__

#include <api.h>

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>

#ifdef _WIN32
#include <io.h>
#endif

// stdout.log ???
#define trace_out(fmt, ...)  api_log_trace(API_LOG_INFO, __FILE__, __LINE__, __FUNCTION__, fmt, ##__VA_ARGS__)
#define trace_err(fmt, ...)  api_log_trace(API_LOG_ERROR, __FILE__, __LINE__, __FUNCTION__, fmt, ##__VA_ARGS__)

// access.log
#define do_info(fmt, ...)    api_log_message(API_LOG_FILE | API_LOG_ACCESS, fmt, ##__VA_ARGS__)
#define do_error(fmt, ...)   api_log_message(API_LOG_FILE | API_LOG_ERROR, fmt, ##__VA_ARGS__)
#define do_debug(fmt, ...)   api_log_trace(API_LOG_FILE | API_LOG_DEBUG, __FILE__, __LINE__, __FUNCTION__, fmt, ##__VA_ARGS__)
#define do_fatal(fmt, ...)   api_log_fatal(API_LOG_FILE | API_LOG_FATAL, fmt, ##__VA_ARGS__)
#define do_assert(exp)       api_log_assert(API_LOG_FILE | API_LOG_FATAL, (exp), #exp, __FILE__, __LINE__, __FUNCTION__) 

#ifdef __cplusplus
extern "C" {
#endif

typedef enum level_enum {
	API_LOG_ACCESS  = 0x01,
	API_LOG_ERROR   = 0x02,
	API_LOG_DEBUG   = 0x03,
	API_LOG_FATAL   = 0x04,
	API_LOG_INFO    = 0x05,
	API_LOG_STDERR  = 0x06, // output stderr
	
	API_LOG_CASE0   = 0x07,
	API_LOG_CASE1   = API_LOG_CASE0 + 1,
	API_LOG_CASE2   = API_LOG_CASE0 + 2,
	API_LOG_CASE3   = API_LOG_CASE0 + 3,
	API_LOG_CASE4   = API_LOG_CASE0 + 4,
	API_LOG_CASE5   = API_LOG_CASE0 + 5,
	API_LOG_CASE6   = API_LOG_CASE0 + 6,
	API_LOG_CASE7   = API_LOG_CASE0 + 7, // 0x0e
	API_LOG_CASE8   = API_LOG_CASE0 + 8, // 0x0f
	
	API_LOG_STDOUT  = 0x10, // output stdout
	API_LOG_FILE    = 0x20, // write log files
	#if 0
	API_LOG_FACCESS = API_LOG_FILE | API_LOG_ACCESS,
	API_LOG_FERROR  = API_LOG_FILE | API_LOG_ERROR,
	API_LOG_FDEBUG  = API_LOG_FILE | API_LOG_DEBUG,
	API_LOG_FFATAL  = API_LOG_FILE | API_LOG_FATAL,
	API_LOG_FILE1   = API_LOG_FILE | 0x04,
	API_LOG_FILE2   = API_LOG_FILE | 0x05,
	API_LOG_FILE3   = API_LOG_FILE | 0x06,
	API_LOG_FILE4   = API_LOG_FILE | 0x07,
	API_LOG_FILE5   = API_LOG_FILE | 0x08,
	API_LOG_FILE6   = API_LOG_FILE | 0x09,
	API_LOG_FILE7   = API_LOG_FILE | 0x0a,
	API_LOG_FILE8   = API_LOG_FILE | 0x0b,
	API_LOG_FILE9   = API_LOG_FILE | 0x0c,
	API_LOG_FILE10  = API_LOG_FILE | 0x0d,
	API_LOG_FILE11  = API_LOG_FILE | 0x0e,
	API_LOG_FILE12  = API_LOG_FILE | 0x0f,
	#endif
	API_LOG_NOT_SET = -1
} log_level_e;

API void api_logger_init(const char *path);
API void api_logger_close(void);
API void api_log_init(byte_t type, int interval, const char *prefix);

API int api_log_core(log_level_e level, const char *fmt, va_list args);

API int api_log_message(log_level_e level, const char *format, ...);

API int api_log_trace(log_level_e level, const char *filename, int line, const char *function, const char *format, ...);

API void api_log_fatal(log_level_e level, const char *fmt,...);
API void api_log_assert(log_level_e level, int exp, const char *exps, const char *filename, int line, const char *function);

#ifdef __cplusplus
}
#endif


#endif /* ! __API_LOG_H_INCLUDED__ */

