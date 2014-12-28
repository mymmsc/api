#include "api/time.h"
#include "api/atomic.h"
#include <time.h>

static atomic_t have_realtime = -1;
static atomic_t have_monotonic = -1;


/// @brief A wrapper for getting the current time.
/// @returns The current time.
static struct timespec snap_time(void)
{
    struct timespec t;
    clock_gettime(CLOCK_REALTIME, &t);
    return t;
}

/// @brief Calculates the time difference between two struct timespecs
/// @param t1 The first time.
/// @param t2 The second time.
/// @returns The difference between the two times.
static double get_elapsed(struct timespec t1, struct timespec t2)
{
    double ft1 = t1.tv_sec + ((double)t1.tv_nsec / 1000000000.0);
    double ft2 = t2.tv_sec + ((double)t2.tv_nsec / 1000000000.0);
    return ft2 - ft1;
}

api_time_t api_tickcount(void)
{
    api_time_t curTime = 0;
#ifdef API_WINDOWS
    static BOOL       initialized = FALSE;
    static api_time_t freq        = 0;
    
    if (initialized == FALSE)
    {
        QueryPerformanceFrequency((LARGE_INTEGER *)&freq);
        initialized = TRUE;
    }
    if (QueryPerformanceCounter((LARGE_INTEGER *)&curTime) != 0)
    {
        curTime = (api_time_t)((curTime * 1000000.0F) / (freq * 1.0F));
    }
 else
#endif
	{
		status_t rc = 0;
		struct timespec ts;
		
		if(have_realtime != 0) {
			rc = 1;
		}
		if(rc) {
			rc = clock_gettime (CLOCK_REALTIME, &ts);
			if(!rc) {
				have_realtime = 1;
				curTime = ts.tv_sec * API_USEC_PER_SEC + ts.tv_nsec / 1000;
				//printf("CLOCK_REALTIME...\n");
			} else {
				have_realtime = 0;
				rc = 1;
			}
		}
		if(rc) {
			curTime = api_time_now();
			//printf("api_time_now...\n");
		}
	}
 	
    return curTime;
}

#include "api/time.h"
#include "api/lib.h"
#include "api_private.h"
#include "api/strings.h"

/* private API headers */
//#include "api_arch_internal_time.h"

/* System Headers required for time library */
#if API_HAVE_SYS_TIME_H
#include <sys/time.h>
#endif
#if API_HAVE_UNISTD_H
#include <unistd.h>
#endif
#ifdef HAVE_TIME_H
#include <time.h>
#endif
/* End System Headers */

#if !defined(HAVE_STRUCT_TM_TM_GMTOFF) && !defined(HAVE_STRUCT_TM___TM_GMTOFF)
static api_int32_t server_gmt_offset;
#define NO_GMTOFF_IN_STRUCT_TM
#endif          

static api_int32_t get_offset(struct tm *tm)
{
#if defined(HAVE_STRUCT_TM_TM_GMTOFF)
    return tm->tm_gmtoff;
#elif defined(HAVE_STRUCT_TM___TM_GMTOFF)
    return tm->__tm_gmtoff;
#else
#ifdef NETWARE
    /* Need to adjust the global variable each time otherwise
        the web server would have to be restarted when daylight
        savings changes.
    */
    if (daylightOnOff) {
        return server_gmt_offset + daylightOffset;
    }
#else
    if (tm->tm_isdst)
        return server_gmt_offset + 3600;
#endif
    return server_gmt_offset;
#endif
}

api_status_t api_time_ansi_put(api_time_t *result,
                                            time_t input)
{
    *result = (api_time_t)input * API_USEC_PER_SEC;
    return API_SUCCESS;
}

/* NB NB NB NB This returns GMT!!!!!!!!!! */
api_time_t api_time_now(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * API_USEC_PER_SEC + tv.tv_usec;
}

static void explode_time(api_time_exp_t *xt, api_time_t t,
                         api_int32_t offset, int use_localtime)
{
    struct tm tm;
    time_t tt = (t / API_USEC_PER_SEC) + offset;
    xt->tm_usec = t % API_USEC_PER_SEC;

#if API_HAS_THREADS && defined (_POSIX_THREAD_SAFE_FUNCTIONS)
    if (use_localtime)
        localtime_r(&tt, &tm);
    else
        gmtime_r(&tt, &tm);
#else
    if (use_localtime)
        tm = *localtime(&tt);
    else
        tm = *gmtime(&tt);
#endif

    xt->tm_sec  = tm.tm_sec;
    xt->tm_min  = tm.tm_min;
    xt->tm_hour = tm.tm_hour;
    xt->tm_mday = tm.tm_mday;
    xt->tm_mon  = tm.tm_mon;
    xt->tm_year = tm.tm_year;
    xt->tm_wday = tm.tm_wday;
    xt->tm_yday = tm.tm_yday;
    xt->tm_isdst = tm.tm_isdst;
    xt->tm_gmtoff = get_offset(&tm);
}

api_status_t api_time_exp_tz(api_time_exp_t *result,
                                          api_time_t input, api_int32_t offs)
{
    explode_time(result, input, offs, 0);
    result->tm_gmtoff = offs;
    return API_SUCCESS;
}

api_status_t api_time_exp_gmt(api_time_exp_t *result,
                                           api_time_t input)
{
    return api_time_exp_tz(result, input, 0);
}

api_status_t api_time_exp_lt(api_time_exp_t *result,
                                                api_time_t input)
{
#if defined(__EMX__)
    /* EMX gcc (OS/2) has a timezone global we can use */
    return api_time_exp_tz(result, input, -timezone);
#else
    explode_time(result, input, 0, 1);
    return API_SUCCESS;
#endif /* __EMX__ */
}

api_status_t api_time_exp_get(api_time_t *t, api_time_exp_t *xt)
{
    api_time_t year = xt->tm_year;
    api_time_t days;
    static const int dayoffset[12] =
    {306, 337, 0, 31, 61, 92, 122, 153, 184, 214, 245, 275};

    /* shift new year to 1st March in order to make leap year calc easy */

    if (xt->tm_mon < 2)
        year--;

    /* Find number of days since 1st March 1900 (in the Gregorian calendar). */

    days = year * 365 + year / 4 - year / 100 + (year / 100 + 3) / 4;
    days += dayoffset[xt->tm_mon] + xt->tm_mday - 1;
    days -= 25508;              /* 1 jan 1970 is 25508 days since 1 mar 1900 */
    days = ((days * 24 + xt->tm_hour) * 60 + xt->tm_min) * 60 + xt->tm_sec;

    if (days < 0) {
        return API_EBADDATE;
    }
    *t = days * API_USEC_PER_SEC + xt->tm_usec;
    return API_SUCCESS;
}

api_status_t api_time_exp_gmt_get(api_time_t *t, 
                                               api_time_exp_t *xt)
{
    api_status_t status = api_time_exp_get(t, xt);
    if (status == API_SUCCESS)
        *t -= (api_time_t) xt->tm_gmtoff * API_USEC_PER_SEC;
    return status;
}
void api_sleep(api_interval_time_t t)
{
#ifdef OS2
    DosSleep(t/1000);
#elif defined(BEOS)
    snooze(t);
#elif defined(NETWARE)
    delay(t/1000);
#else
    struct timeval tv;
    tv.tv_usec = t % API_USEC_PER_SEC;
    tv.tv_sec = t / API_USEC_PER_SEC;
    select(0, NULL, NULL, NULL, &tv);
#endif
}

