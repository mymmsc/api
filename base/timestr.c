#include "api/time.h"
#include "api/lib.h"
#include "api/strings.h"
#include "api_private.h"

/* System Headers required for time library */
#if API_HAVE_SYS_TIME_H
#include <sys/time.h>
#endif
#ifdef HAVE_TIME_H
#include <time.h>
#endif
#if API_HAVE_STRING_H
#include <string.h>
#endif
/* End System Headers */

API_DECLARE_DATA const char api_month_snames[12][4] =
{
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
};
API_DECLARE_DATA const char api_day_snames[7][4] =
{
    "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
};

api_status_t api_rfc822_date(char *date_str, api_time_t t)
{
    api_time_exp_t xt;
    const char *s;
    int real_year;

    api_time_exp_gmt(&xt, t);

    /* example: "Sat, 08 Jan 2000 18:31:41 GMT" */
    /*           12345678901234567890123456789  */

    s = &api_day_snames[xt.tm_wday][0];
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = ',';
    *date_str++ = ' ';
    *date_str++ = xt.tm_mday / 10 + '0';
    *date_str++ = xt.tm_mday % 10 + '0';
    *date_str++ = ' ';
    s = &api_month_snames[xt.tm_mon][0];
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = ' ';
    real_year = 1900 + xt.tm_year;
    /* This routine isn't y10k ready. */
    *date_str++ = real_year / 1000 + '0';
    *date_str++ = real_year % 1000 / 100 + '0';
    *date_str++ = real_year % 100 / 10 + '0';
    *date_str++ = real_year % 10 + '0';
    *date_str++ = ' ';
    *date_str++ = xt.tm_hour / 10 + '0';
    *date_str++ = xt.tm_hour % 10 + '0';
    *date_str++ = ':';
    *date_str++ = xt.tm_min / 10 + '0';
    *date_str++ = xt.tm_min % 10 + '0';
    *date_str++ = ':';
    *date_str++ = xt.tm_sec / 10 + '0';
    *date_str++ = xt.tm_sec % 10 + '0';
    *date_str++ = ' ';
    *date_str++ = 'G';
    *date_str++ = 'M';
    *date_str++ = 'T';
    *date_str++ = 0;
    return API_SUCCESS;
}

api_status_t api_ctime(char *date_str, api_time_t t)
{
    api_time_exp_t xt;
    const char *s;
    int real_year;

    /* example: "Wed Jun 30 21:49:08 1993" */
    /*           123456789012345678901234  */

    api_time_exp_lt(&xt, t);
    s = &api_day_snames[xt.tm_wday][0];
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = ' ';
    s = &api_month_snames[xt.tm_mon][0];
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = *s++;
    *date_str++ = ' ';
    *date_str++ = xt.tm_mday / 10 + '0';
    *date_str++ = xt.tm_mday % 10 + '0';
    *date_str++ = ' ';
    *date_str++ = xt.tm_hour / 10 + '0';
    *date_str++ = xt.tm_hour % 10 + '0';
    *date_str++ = ':';
    *date_str++ = xt.tm_min / 10 + '0';
    *date_str++ = xt.tm_min % 10 + '0';
    *date_str++ = ':';
    *date_str++ = xt.tm_sec / 10 + '0';
    *date_str++ = xt.tm_sec % 10 + '0';
    *date_str++ = ' ';
    real_year = 1900 + xt.tm_year;
    *date_str++ = real_year / 1000 + '0';
    *date_str++ = real_year % 1000 / 100 + '0';
    *date_str++ = real_year % 100 / 10 + '0';
    *date_str++ = real_year % 10 + '0';
    *date_str++ = 0;

    return API_SUCCESS;
}

api_status_t api_strftime(char *s, api_size_t *retsize, api_size_t max, 
                        const char *format, api_time_exp_t *xt)
{
    struct tm tm;
    memset(&tm, 0, sizeof tm);
    tm.tm_sec  = xt->tm_sec;
    tm.tm_min  = xt->tm_min;
    tm.tm_hour = xt->tm_hour;
    tm.tm_mday = xt->tm_mday;
    tm.tm_mon  = xt->tm_mon;
    tm.tm_year = xt->tm_year;
    tm.tm_wday = xt->tm_wday;
    tm.tm_yday = xt->tm_yday;
    tm.tm_isdst = xt->tm_isdst;
#if defined(HAVE_STRUCT_TM_TM_GMTOFF)
    tm.tm_gmtoff = xt->tm_gmtoff;
#elif defined(HAVE_STRUCT_TM___TM_GMTOFF)
    tm.__tm_gmtoff = xt->tm_gmtoff;
#endif
    (*retsize) = strftime(s, max, format, &tm);
    return API_SUCCESS;
}

char * api_gettime(char *szTime, api_size_t nLen, const char *lpszFormat)
{
    api_status_t rv;
    api_time_t nowTime = api_time_now();
    api_time_exp_t xt;
    
    memset(szTime, 0x00, nLen);
    
    rv = api_time_exp_lt(&xt, nowTime);
    if (rv == API_ENOTIMPL)
    {
        return NULL;
    }
    
    //格式化输出时间串
    if(!strcmp(lpszFormat, "0"))
    {
        /*
        api_snprintf(szTime, nLen, "%04d-%02d-%02d %02d:%02d:%02d.%06d %+05d [%d %s]%s",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec,
                         xt.tm_gmtoff,
                         xt.tm_yday + 1,
                         api_day_snames[xt.tm_wday],
                         (xt.tm_isdst ? " DST" : ""));
        */
        api_snprintf(szTime, nLen, "%04d-%02d-%02d %02d:%02d:%02d.%06d",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec);
    
        //len = sprintf(_time, "%4d-%02d-%02d %02d:%02d:%02d.%03d", gt.tm_year + 1900, gt.tm_mon + 1, gt.tm_mday, gt.tm_hour, gt.tm_min, gt.tm_sec, (int)(temp_time.tv_usec/1000));
        *(szTime + strlen(szTime)) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "1"))
    {
        nLen = api_snprintf(szTime, nLen, "%" API_TIME_T_FMT, nowTime/1000);
        *(szTime + nLen) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "2"))
    {
        nLen = sprintf(szTime, "%" API_TIME_T_FMT, nowTime);
        *(szTime + nLen) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "3"))
    {
        nLen = api_snprintf(szTime, nLen, "%04d%02d%02d%02d%02d%02d%06d",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec);
        *(szTime + nLen) = 0x00;
    }
    else
    {
        if(api_strftime(szTime, &nLen, nLen, lpszFormat, &xt) != API_SUCCESS)
        {
            return NULL;
        }
        *(szTime + nLen) = 0x00;
    }
    
    return szTime;
}

char * api_parsetime(char *szTime, api_size_t nLen, api_time_t nTime, const char *lpszFormat)
{
    api_status_t rv;
    api_time_exp_t xt;
    
    memset(szTime, 0x00, nLen);
    
    rv = api_time_exp_lt(&xt, nTime);
    if (rv == API_ENOTIMPL)
    {
        return NULL;
    }
    
    //格式化输出时间串
    if(!strcmp(lpszFormat, "0"))
    {
        /*
        api_snprintf(szTime, nLen, "%04d-%02d-%02d %02d:%02d:%02d.%06d %+05d [%d %s]%s",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec,
                         xt.tm_gmtoff,
                         xt.tm_yday + 1,
                         api_day_snames[xt.tm_wday],
                         (xt.tm_isdst ? " DST" : ""));
        */
        api_snprintf(szTime, nLen, "%04d-%02d-%02d %02d:%02d:%02d.%06d",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec);
    
        //len = sprintf(_time, "%4d-%02d-%02d %02d:%02d:%02d.%03d", gt.tm_year + 1900, gt.tm_mon + 1, gt.tm_mday, gt.tm_hour, gt.tm_min, gt.tm_sec, (int)(temp_time.tv_usec/1000));
        *(szTime + strlen(szTime)) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "1"))
    {
        nLen = api_snprintf(szTime, nLen, "%" API_TIME_T_FMT, nTime/1000);
        *(szTime + nLen) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "2"))
    {
        nLen = sprintf(szTime, "%" API_TIME_T_FMT, nTime);
        *(szTime + nLen) = 0x00;
    }
    //格式化输出时间串
    else if(!strcmp(lpszFormat, "3"))
    {
        nLen = api_snprintf(szTime, nLen, "%04d%02d%02d%02d%02d%02d%06d",
                         xt.tm_year + 1900,
                         xt.tm_mon + 1,
                         xt.tm_mday,
                         xt.tm_hour,
                         xt.tm_min,
                         xt.tm_sec,
                         xt.tm_usec);
        *(szTime + nLen) = 0x00;
    }
    else
    {
        if(api_strftime(szTime, &nLen, nLen, lpszFormat, &xt) != API_SUCCESS)
        {
            return NULL;
        }
        *(szTime + nLen) = 0x00;
    }
    
    return szTime;
}

