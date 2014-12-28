#ifndef API_LOG_H
#define API_LOG_H

#include <api.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <assert.h>

#ifdef _WIN32
#include <io.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
	LOG_LEVEL_QUIET   = 0,
	LOG_LEVEL_FATAL   = 1,
	LOG_LEVEL_ERROR   = 2,
	LOG_LEVEL_VERBOSE = 3,
	LOG_LEVEL_DEBUG   = 4,
	LOG_LEVEL_INFO    = 0x10,
	LOG_LEVEL_FERROR  = LOG_LEVEL_INFO | 0x01,
	LOG_LEVEL_FILE1   = LOG_LEVEL_INFO | 0x02,
	LOG_LEVEL_FILE2   = LOG_LEVEL_INFO | 0x03,
	LOG_LEVEL_FILE3   = LOG_LEVEL_INFO | 0x04,
	LOG_LEVEL_FILE4   = LOG_LEVEL_INFO | 0x05,
	LOG_LEVEL_FILE5   = LOG_LEVEL_INFO | 0x06,
	LOG_LEVEL_FILE6   = LOG_LEVEL_INFO | 0x07,
	LOG_LEVEL_FILE7   = LOG_LEVEL_INFO | 0x08,
	LOG_LEVEL_FILE8   = LOG_LEVEL_INFO | 0x09,
	LOG_LEVEL_FILE9   = LOG_LEVEL_INFO | 0x0a,
	LOG_LEVEL_FILE10  = LOG_LEVEL_INFO | 0x0b,
	LOG_LEVEL_FILE11  = LOG_LEVEL_INFO | 0x0c,
	LOG_LEVEL_FILE12  = LOG_LEVEL_INFO | 0x0d,
	LOG_LEVEL_FILE13  = LOG_LEVEL_INFO | 0x0e,
	LOG_LEVEL_FILE14  = LOG_LEVEL_INFO | 0x0f,
	LOG_LEVEL_NOT_SET = -1
} log_level_e;

API void api_logger_init(void);
API void api_logger_close(void);
API void api_log_init(byte_t type, int interval, const char *prefix);

// stdout.log ???
#define trace_out(fmt, ...)  api_log_trace(LOG_LEVEL_VERBOSE, __FILE__, __LINE__, __FUNCTION__, fmt, ##__VA_ARGS__)
#define trace_err(fmt, ...)  api_log_trace(LOG_LEVEL_ERROR, __FILE__, __LINE__, __FUNCTION__, fmt, ##__VA_ARGS__)

// access.log
#define do_info(fmt, ...)    api_log_message(LOG_LEVEL_INFO, fmt, ##__VA_ARGS__)
#define do_out(fmt, ...)     api_log_message(LOG_LEVEL_VERBOSE, fmt, ##__VA_ARGS__)
#define do_error(fmt, ...)   api_log_message(LOG_LEVEL_ERROR, fmt, ##__VA_ARGS__)
#define do_fatal(fmt, ...)   api_log_fatal(LOG_LEVEL_FATAL, fmt, ##__VA_ARGS__)
#define do_assert(exp)       api_log_assert(LOG_LEVEL_FATAL, (exp), #exp, __FILE__, __LINE__, __FUNCTION__) 

API int api_log(log_level_e level, const char *fmt, va_list args);

API int api_log_message(log_level_e level, const char *format, ...);

API int api_log_trace(log_level_e level, const char *filename, int line, const char *function, const char *format, ...);

API void api_log_fatal(log_level_e level, const char *fmt,...);
API void api_log_assert(log_level_e level, int exp, const char *exps, const char *filename, int line, const char *function);

#ifdef __cplusplus
}
#endif


#endif /* ! API_LOG_H */

