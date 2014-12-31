#ifndef __API_TIME_H_INCLUDED__
#define __API_TIME_H_INCLUDED__

/**
 * @file api_time.h
 * @brief API Time Library
 */

#include "api.h"
#include "api/errno.h"

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/**
 * @defgroup api_time Time Routines
 * @ingroup API 
 * @{
 */

/** month names */
API_DECLARE_DATA extern const char api_month_snames[12][4];
/** day names */
API_DECLARE_DATA extern const char api_day_snames[7][4];


/** number of microseconds since 00:00:00 january 1, 1970 UTC */
typedef api_int64_t api_time_t;


/** mechanism to properly type api_time_t literals */
#define API_TIME_C(val) API_INT64_C(val)

/** mechanism to properly print api_time_t values */
#define API_TIME_T_FMT API_INT64_T_FMT

/** intervals for I/O timeouts, in microseconds */
typedef api_int64_t api_interval_time_t;
/** short interval for I/O timeouts, in microseconds */
typedef api_int32_t api_short_interval_time_t;

/** number of microseconds per second */
#define API_USEC_PER_SEC API_TIME_C(1000000)

/** @return api_time_t as a second */
#define api_time_sec(time) ((time) / API_USEC_PER_SEC)

/** @return api_time_t as a usec */
#define api_time_usec(time) ((time) % API_USEC_PER_SEC)

/** @return api_time_t as a msec */
#define api_time_msec(time) (((time) / 1000) % 1000)

/** @return api_time_t as a msec */
#define api_time_as_msec(time) ((time) / 1000)

/** @return milliseconds as an api_time_t */
#define api_time_from_msec(msec) ((api_time_t)(msec) * 1000)

/** @return a second as an api_time_t */
#define api_time_from_sec(sec) ((api_time_t)(sec) * API_USEC_PER_SEC)

/** @return a second and usec combination as an api_time_t */
#define api_time_make(sec, usec) ((api_time_t)(sec) * API_USEC_PER_SEC \
                                + (api_time_t)(usec))

/**
 * @return the current time
 */
API api_time_t api_time_now(void);

/** @see api_time_exp_t */
typedef struct api_time_exp_t api_time_exp_t;

/**
 * a structure similar to ANSI struct tm with the following differences:
 *  - tm_usec isn't an ANSI field
 *  - tm_gmtoff isn't an ANSI field (it's a bsdism)
 */
struct api_time_exp_t {
    /** microseconds past tm_sec */
    api_int32_t tm_usec;
    /** (0-61) seconds past tm_min */
    api_int32_t tm_sec;
    /** (0-59) minutes past tm_hour */
    api_int32_t tm_min;
    /** (0-23) hours past midnight */
    api_int32_t tm_hour;
    /** (1-31) day of the month */
    api_int32_t tm_mday;
    /** (0-11) month of the year */
    api_int32_t tm_mon;
    /** year since 1900 */
    api_int32_t tm_year;
    /** (0-6) days since sunday */
    api_int32_t tm_wday;
    /** (0-365) days since jan 1 */
    api_int32_t tm_yday;
    /** daylight saving time */
    api_int32_t tm_isdst;
    /** seconds east of UTC */
    api_int32_t tm_gmtoff;
};

/**
 * convert an ansi time_t to an api_time_t
 * @param result the resulting api_time_t
 * @param input the time_t to convert
 */
API api_status_t api_time_ansi_put(api_time_t *result, 
                                                    time_t input);

/**
 * convert a time to its human readable components using an offset
 * from GMT
 * @param result the exploded time
 * @param input the time to explode
 * @param offs the number of seconds offset to apply
 */
API api_status_t api_time_exp_tz(api_time_exp_t *result,
                                          api_time_t input,
                                          api_int32_t offs);

/**
 * convert a time to its human readable components in GMT timezone
 * @param result the exploded time
 * @param input the time to explode
 */
API api_status_t api_time_exp_gmt(api_time_exp_t *result, 
                                           api_time_t input);

/**
 * convert a time to its human readable components in local timezone
 * @param result the exploded time
 * @param input the time to explode
 */
API api_status_t api_time_exp_lt(api_time_exp_t *result, 
                                          api_time_t input);

/**
 * Convert time value from human readable format to a numeric api_time_t 
 * e.g. elapsed usec since epoch
 * @param result the resulting imploded time
 * @param input the input exploded time
 */
API api_status_t api_time_exp_get(api_time_t *result, 
                                           api_time_exp_t *input);

/**
 * Convert time value from human readable format to a numeric api_time_t that
 * always represents GMT
 * @param result the resulting imploded time
 * @param input the input exploded time
 */
API api_status_t api_time_exp_gmt_get(api_time_t *result, 
                                               api_time_exp_t *input);

/**
 * Sleep for the specified number of micro-seconds.
 * @param t desired amount of time to sleep.
 * @warning May sleep for longer than the specified time. 
 */
API void api_sleep(api_interval_time_t t);

/** length of a RFC822 Date */
#define API_RFC822_DATE_LEN (30)
/**
 * api_rfc822_date formats dates in the RFC822
 * format in an efficient manner.  It is a fixed length
 * format which requires the indicated amount of storage,
 * including the trailing NUL terminator.
 * @param date_str String to write to.
 * @param t the time to convert 
 */
API api_status_t api_rfc822_date(char *date_str, api_time_t t);

/** length of a CTIME date */
#define API_CTIME_LEN (25)
/**
 * api_ctime formats dates in the ctime() format
 * in an efficient manner.  it is a fixed length format
 * and requires the indicated amount of storage including
 * the trailing NUL terminator.
 * Unlike ANSI/ISO C ctime(), api_ctime() does not include
 * a \n at the end of the string.
 * @param date_str String to write to.
 * @param t the time to convert 
 */
API api_status_t api_ctime(char *date_str, api_time_t t);

/**
 * formats the exploded time according to the format specified
 * @param s string to write to
 * @param retsize The length of the returned string
 * @param max The maximum length of the string
 * @param format The format for the time string
 * @param tm The time to convert
 */
API api_status_t api_strftime(char *s, api_size_t *retsize, 
                                       api_size_t max, const char *format, 
                                       api_time_exp_t *tm);

/*------------------< 日期时间操作相关自定义函数 >------------------*/

/**
 * 取得目前时间戳
 * @param[out] szTime 时间串缓冲区
 * @param[in] nLen 时间串缓冲区尺寸
 * @param[in] format 时间格式串
 */
API char * api_gettime(char *szTime, api_size_t nLen, const char *format);

/**
 * 根据取得格式化的时间串
 * @param[in] szTime time的缓存区
 * @param[in] nLen 缓冲区长度
 * @param[in] nTime 毫秒数
 * @param[in] format time的格式化字符串
 */
API char * api_parsetime(char *szTime, api_size_t nLen, api_time_t nTime, const char *format);

/**
 * Get the current timestamp
 * @see QueryPerformanceCounter
 * @see QueryPerformanceFrequency
 */
API api_time_t api_tickcount(void);

/** @} */

#ifdef __cplusplus
}
#endif

#endif  /* ! __API_TIME_H_INCLUDED__ */
