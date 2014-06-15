#include <api_strings.h>
#include <api_private.h>

#if HAVE_LANGINFO_H
#include <langinfo.h>
#endif

#if API_HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#if API_HAVE_STRING_H
#include <string.h>
#endif
#if API_HAVE_CTYPE_H
#include <ctype.h>
#endif

/*
 * MyMMSC's "replacement" for the strncpy() function. We roll our
 * own to implement these specific changes:
 *   (1) strncpy() doesn't always null terminate and we want it to.
 *   (2) strncpy() null fills, which is bogus, esp. when copy 8byte
 *       strings into 8k blocks.
 *   (3) Instead of returning the pointer to the beginning of
 *       the destination string, we return a pointer to the
 *       terminating '\0' to allow us to "check" for truncation
 *
 * api_cpystrn() follows the same call structure as strncpy().
 */

API_DECLARE(char *) api_cpystrn(char *dst, const char *src, api_size_t dst_size)
{

    char *d, *end;

    if (dst_size == 0) {
        return (dst);
    }
    
    d = dst;
    end = dst + dst_size - 1;

    for (; d < end; ++d, ++src) {
    if (!(*d = *src)) {
        return (d);
    }
    }

    *d = '\0';    /* always null terminate */

    return (d);
}

/* Filepath_name_get returns the final element of the pathname.
 * Using the current platform's filename syntax.
 *   "/foo/bar/gum" -> "gum"
 *   "/foo/bar/gum/" -> ""
 *   "gum" -> "gum"
 *   "wi\\n32\\stuff" -> "stuff
 *
 * Corrected Win32 to accept "a/b\\stuff", "a:stuff"
 */

API_DECLARE(const char *) api_filepath_name_get(const char *pathname)
{
    const char path_separator = '/';
    const char *s = strrchr(pathname, path_separator);

#ifdef WIN32
    const char path_separator_win = '\\';
    const char drive_separator_win = ':';
    const char *s2 = strrchr(pathname, path_separator_win);

    if (s2 > s) s = s2;

    if (!s) s = strrchr(pathname, drive_separator_win);
#endif

    return s ? ++s : pathname;
}

/* length of dest assumed >= length of src
 * collapse in place (src == dest) is legal.
 * returns terminating null ptr to dest string.
 */
API_DECLARE(char *) api_collapse_spaces(char *dest, const char *src)
{
    while (*src) {
        if (!api_isspace(*src)) 
            *dest++ = *src;
        ++src;
    }
    *dest = 0;
    return (dest);
}

#if !API_HAVE_STRDUP
char *strdup(const char *str)
{
    char *sdup;
    size_t len = api_strlen(str) + 1;
    
    sdup = (char *) malloc(len);
    memcpy(sdup, str, len);

    return sdup;
}
#endif

/* The following two routines were donated for SVR4 by Andreas Vogel */
#if (!API_HAVE_STRCASECMP && !API_HAVE_STRICMP)
int strcasecmp(const char *a, const char *b)
{
    const char *p = a;
    const char *q = b;
    for (p = a, q = b; *p && *q; p++, q++) {
        int diff = api_tolower(*p) - api_tolower(*q);
        if (diff) {
            return diff;
        }
    }
    if (*p) {
        return 1;               /* p was longer than q */
    }
    if (*q) {
        return -1;              /* p was shorter than q */
    }
    return 0;                   /* Exact match */
}

#endif

#if (!API_HAVE_STRNCASECMP && !API_HAVE_STRNICMP)
int strncasecmp(const char *a, const char *b, size_t n)
{
    const char *p = a;
    const char *q = b;
    
    for (p = a, q = b; /*NOTHING */ ; p++, q++) {
        int diff;
        if (p == a + n) {
            return 0;           /*   Match up to n characters */
        }
        if (!(*p && *q)) {
            return *p - *q;
        }
        diff = api_tolower(*p) - api_tolower(*q);
        if (diff) {
            return diff;
        }
    }
    /*NOTREACHED */
}
#endif

/* The following routine was donated for UTS21 by dwd@bell-labs.com */
#if (!API_HAVE_STRSTR)
char *strstr(char *s1, char *s2)
{
    char *p1, *p2;
    if (*s2 == '\0') {
        /* an empty s2 */
        return(s1);
    }
    while((s1 = strchr(s1, *s2)) != NULL) {
        /* found first character of s2, see if the rest matches */
        p1 = s1;
        p2 = s2;
        while (*++p1 == *++p2) {
            if (*p1 == '\0') {
                /* both strings ended together */
                return(s1);
            }
        }
        if (*p2 == '\0') {
            /* second string ended, a match */
            break;
        }
        /* didn't find a match here, try starting at next character in s1 */
        s1++;
    }
    return(s1);
}
#endif

#if defined(__GNUC__)
#  define UNUSED __attribute__((__unused__))
#else
#  define UNUSED
#endif

/* based on "strnatcmp.c,v 1.6 2000/04/20 07:30:11 mbp Exp $" */

static int compare_right(char const *a, char const *b)
{
    int bias = 0;
     
    /* The longest run of digits wins.  That aside, the greatest
       value wins, but we can't know that it will until we've scanned
       both numbers to know that they have the same magnitude, so we
       remember it in BIAS.
    */
    for (;; a++, b++) {
        if (!api_isdigit(*a)  &&  !api_isdigit(*b)) {
            break;
        } else if (!api_isdigit(*a)) {
            return -1;
        } else if (!api_isdigit(*b)) {
            return +1;
        } else if (*a < *b) {
            if (!bias) {
                bias = -1;
            }
        } else if (*a > *b) {
            if (!bias) {
                bias = +1;
            }
        } else if (!*a  &&  !*b) {
            break;
        }
    }
    
    return bias;
}

static int compare_left(char const *a, char const *b)
{
    /* Compare two left-aligned numbers: the first to have a
       different value wins. */
    for (;; a++, b++) {
        if (!api_isdigit(*a)  &&  !api_isdigit(*b)) {
            break;
        } else if (!api_isdigit(*a)) {
            return -1;
        } else if (!api_isdigit(*b)) {
            return +1;
        } else if (*a < *b) {
            return -1;
        } else if (*a > *b) {
            return +1;
        }
    }
    
    return 0;
}

static int strnatcmp0(char const *a, char const *b, int fold_case)
{
    int ai, bi;
    char ca, cb;
    int fractional, result;
    ai = bi = 0;
    while (1) {
        ca = a[ai]; cb = b[bi];
        /* skip over leading spaces or zeros */
        while (api_isspace(ca)) {
           ca = a[++ai];
        }
        while (api_isspace(cb)) {
           cb = b[++bi];
        }
        /* process run of digits */
        if (api_isdigit(ca)  &&  api_isdigit(cb)) {
            fractional = (ca == '0' || cb == '0');
            
            if (fractional) {
                if ((result = compare_left(a+ai, b+bi)) != 0) {
                    return result;
                }
            } else {
                if ((result = compare_right(a+ai, b+bi)) != 0) {
                    return result;
                }
            }
        }
        
        if (!ca && !cb) {
            /* The strings compare the same.  Perhaps the caller
               will want to call strcmp to break the tie. */
            return 0;
        }
        
        if (fold_case) {
            ca = api_toupper(ca);
            cb = api_toupper(cb);
        }
        
        if (ca < cb) {
           return -1;
        } else if (ca > cb) {
           return +1;
        }
        
        ++ai; ++bi;
    }
}

API_DECLARE(int) api_strnatcmp(char const *a, char const *b)
{
     return strnatcmp0(a, b, 0);
}


/* Compare, recognizing numeric string and ignoring case. */
API_DECLARE(int) api_strnatcasecmp(char const *a, char const *b)
{
     return strnatcmp0(a, b, 1);
}

//////////////////////////////////////////////////////////////////////////////////////////

static char ascii_from_hex(char c)
{
    return  c >= '0' && c <= '9' ?  c - '0'
            : c >= 'A' && c <= 'F'? c - 'A' + 10
            : c - 'a' + 10;     /* accept small letters just in case */
}

//////////////////////////////////////////////////////////////////////////////////////////

API_DECLARE(char *) api_strtolc(char *str)
{
    register char *p = (char *)str;
    
    if(api_strlen(str) == 0)
    {
        return "";
    }
    
    while(*p)
    {
        if(isupper(*p))
        {
            *p = *p - 'A' + 'a';
        }
        p++;
    }
    return (char *)str;
}

API_DECLARE(char *) api_strtouc(char *str)
{
    register char *p = (char *)str;
    
    if(api_strlen(str) == 0)
    {
        return "";
    }
    
    while(*p)
    {
        if(islower(*p))
        {
            *p = *p - 'a' + 'A';
        }
        p++;
    }
    return (char *)str;
}

//////////////////////////////////////////////////////////////////////////////////////////

API_DECLARE(char *) api_mem2hex(char *strHex, const char *str, const api_int32_t len)
{
    api_int32_t i;
    api_byte_t  tmpValue;
    api_int32_t nStep = 1;
    api_int32_t tmpLen = (api_int32_t)(len / nStep);
    
    if(strHex == NULL || /* str == NULL || */len == 0)
    {
        return NULL;
    }
    
    *strHex = 0x00;
    for(i = 0; i < tmpLen; i++)
    {
        //tmpValue = (api_byte_t)str[i];
        tmpValue = (api_byte_t)*(str+i);
        api_strcat(strHex, "%02X", tmpValue);
    }
     
    return strHex;
}

API_DECLARE(int) api_hex2mem(char *str, const char *strHex, const api_int32_t len)
{
    api_int32_t  i;
    api_byte_t   tmpValue;
    char         tmpStr[9];
    api_int32_t  nStep = 2;
    api_int32_t  tmpLen = (api_int32_t)(len / nStep);
    api_status_t iRet = API_SUCCESS;
    
    if(str == NULL || api_strlen(strHex) == 0 || tmpLen == 0)
    {
        return 0;
    }

#define __is_hex(ch) ((*(ch) >= '0' && *(ch) <= '9') || (*(ch) >= 'a' && *(ch) <= 'f') || (*(ch) >= 'A' && *(ch) <= 'F'))
    
    for(i = 0; i < tmpLen; i++)
    {
        if (__is_hex(strHex + (nStep * i)) && __is_hex(strHex + (nStep * i) + 1))
        {
            memset(tmpStr, 0x00, sizeof(tmpStr));
            strncpy(tmpStr, strHex + (nStep * i), nStep);
            tmpValue = (api_byte_t)api_strtoi64(tmpStr, (char**)NULL, 16);
            //if(errno) break;
            memcpy(&str[i], (char *)&tmpValue, 1);
        }
        else
        {
            iRet = API_ENOTIMPL;
            break;
        }
    }
    if (iRet != API_SUCCESS)
    {
        memset(str, 0x00, tmpLen);
        i = 0;
    }
#undef __is_hex
    
    return i;
}

API_DECLARE_NONSTD(char *) api_strcat(char *buffer, const char * lpszFormat, ...)
{
    va_list     args;
    api_int32_t tmpLen = 0;
    char        tmpBuffer[4096];
    
    if(buffer == NULL || api_strlen(lpszFormat) == 0)
    {
        return NULL;
    }
    
    va_start(args, lpszFormat);
    
    memset(tmpBuffer, 0x00, sizeof(tmpBuffer));
    tmpLen = api_vsnprintf(tmpBuffer, sizeof(tmpBuffer), lpszFormat, args);
    va_end(args);
    *(tmpBuffer + tmpLen) = 0x00;
    strcat(buffer, tmpBuffer);
    buffer[api_strlen(buffer)] = 0x00;
    return buffer;
}

API_DECLARE_NONSTD(char *) api_substr(char *str, const char *src, ...)
{
    va_list ap;
    api_int32_t tmpStart = 0;
    api_int32_t tmpLen = 0;
    api_int32_t len = api_strlen(src);
    
    if(api_strlen(src) == 0)
    {
        return NULL;
    }
    
    //memset(str, 0x00, api_strlen(str));
    va_start(ap, src);
    tmpStart = va_arg(ap, api_int32_t);
    tmpLen = va_arg(ap, api_int32_t);
    va_end(ap);
    
    //起始位置 > len或 < -len
    if(tmpStart > (len - 1) || tmpStart < (-1 * len))
    {
        return NULL;
    }
    else if(tmpStart < 0) //起始位置为负数
    {
        tmpStart += len;
    }
    
    len -= tmpStart;
    
    if(tmpLen > 0)
    {
        if(tmpLen > len)
        {
            tmpLen = len;
        }
    }
    else if(tmpLen == 0)
    {
        tmpLen = len;
    }
    else if(tmpLen < (-1 * len))
    {
        return NULL;
    }
    else
    {
        tmpLen += len;
    }
    strncpy(str, (char *)(src + tmpStart), tmpLen);
    *(str + tmpLen) = 0x00;
    
    return (char *)str;
}

API_DECLARE_NONSTD(char *) api_stristr(const char* haystack, const char *needle)
{
    const char *p = haystack;
    
    if(api_strlen(haystack) == 0 || api_strlen(needle) == 0)
    {
        return NULL;
    }
    
    while(api_strlen(p) > 0)
    {
        if(!strncasecmp(p, needle, api_strlen(needle)))
        {
            return (char *)p;
        }
        p++;
    }
    return NULL;
}

API_DECLARE(char *) api_strtrim(char *pstr)
{
    api_int32_t  len;
    char        *pRet = pstr;
    
    if(api_strlen(pRet) == 0)
    {
        return "";
    }
    
    // 去掉首部空格及制表符
    while(strchr(" \t\r\n", *pRet))
    {
        pRet++;
    }
    
    // 去掉尾部空格及制表符
    len = api_strlen(pRet);
    if(len == 0)
    {
        return "";
    }
    while(len > 0)
    {
        if(strchr(" \t\r\n", *(pRet + len - 1)))
        {
            *(pRet + len - 1) = 0x00;
            len--;
        }
        else
        {
            break;
        }
    }
    *(pRet + len) = 0x00;
    api_cpystrn(pstr, pRet, len+1);
    *(pstr + len) = 0x00;
    return (pstr);
}

/**
 * 替换字符串
 * @param[in] lpStrStr回调函数
 * @param[out] haystack, 目标字符串
 * @param[in] needle, 搜索子串
 * @param[in] rep, 替代的字符串
 * @param[in] times, 替换次数,默认1次
 */
static char * api_strrep_s(char * (*lpStrStr)(const char *, const char *), char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
    api_int32_t nPoint = -1; //子串的起始位置
    const char *p      = NULL;
    api_size_t  nLen = api_strlen(haystack);
    
    // 检查传入参数
    if(api_strlen(haystack) == 0 || api_strlen(needle) == 0)
    {
        return NULL;
    }
    while((p = lpStrStr(haystack, needle)) != NULL)
    {
        //得到匹配到的子串的位置
        nPoint = p - haystack;
        memmove(haystack + (nPoint + (api_strlen(rep))), p + (api_strlen(needle)), api_strlen(haystack) - nPoint - 1);
        strncpy(haystack + nPoint, rep, api_strlen(rep));
        *(haystack + nLen + (api_strlen(rep) - api_strlen(needle))) = 0x00;
        if(times == 1) break;
        nLen = api_strlen(haystack);
    }
    return (haystack);
}

API_DECLARE(char *) api_strrep(char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
     return api_strrep_s(strstr, haystack, needle, rep, times);
}

API_DECLARE(char *) api_strirep(char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
    return api_strrep_s(api_stristr, haystack, needle, rep, times);
}

API_DECLARE(char *) api_strcontain(char *haystack, const char *needle, const char *rep)
{
    char szSub[1024];
    
    if(api_strlen(haystack) == 0 || api_strlen(needle) == 0 || rep == NULL)
    {
        return NULL;
    }
    memset(szSub, 0x00, sizeof(szSub));
    api_snprintf(szSub, sizeof(szSub), "${%s}", needle);
    return api_strrep(haystack, szSub, rep, 0);
}

//////////////////////////////////////////////////////////////////////////////////////////

API_DECLARE(int) api_url_encode(char *buf, const char *str)
{
    unsigned int i = 0;
    char        src[4096];
    /* special    characters */
    //const char special[] = "!\"#$%&'()*+,/:;<=>?@[\\]^`{|}~%";
    const char special[] = "!\"#$%'()*+,;<>@[\\]^`{|}~%";
    /* temp buffer */
    char tmp[10];
    
    buf[0] = '\0';
    api_cpystrn(src, str, sizeof(src));
    
    for (i = 0; i < api_strlen(src); i++)
    {
        char c = src[i];
        memset(tmp, 0x00, sizeof(tmp));
        /* special character */
        if (strchr(special, c))        
        {
            sprintf(tmp, "%%%02X", (unsigned char)c);
        }
        else if (c == ' ') /* blank */
        {
            tmp[0] = '+';
        }
        else
        {
            if (c < 0) /* none ASCII character */
            {
                sprintf(tmp, "%%%02X%%%02X", (unsigned char)src[i], (unsigned char)src[i + 1]);
                ++i;
            }
            else /* ASCII character */
            {
                sprintf(tmp, "%c", c);
            }
        }
        strcat(buf, tmp);
    }
    //buf[i] = '\0';
    //free(src);
    //src = NULL;
    api_strrep(buf, "%2F", "/", 0);
    return api_strlen(buf);
}

API_DECLARE(int) api_url_decode(char *buf, const char *str)
{
    static char blank[] = "";
    
    if (!buf || !str)
    {
        return 0;
    }
    while(*str)
    {
        if (*str == '%')
        {
            str++;
            if (*str) *buf = ascii_from_hex(*str++) * 16;
            if (*str) *buf = (*buf + ascii_from_hex(*str++));
            buf++;
        }
        else
        {
            if (*str == '+')
            {
                *buf++ = ' ';
                str++;
            }
            else
            {
                *buf++ = *str++;
            }
        }
    }
    
    *buf++ = 0;
    return api_strlen(buf);
}

//////////////////////////////////////////////////////////////////////////////////////////

API_DECLARE(char *) api_ultoa(char *buf, const api_uint32_t val, const api_int32_t base, const api_int32_t bitlen)
{
    api_uint32_t MAXBUF = 64;
    static char *__BASE_STRING = "0123456789abcdefghijklmnopqrstuvwxyz";
    char *p = NULL;
    api_uint32_t tmpVal = val;
    api_uint32_t tmpLen = bitlen;
    
    //if(buf == NULL || val == 0 || base < 2 || bitlen == 0) return NULL;
    if(buf == NULL || bitlen == 0)
    {
        return "";
    }

    if(tmpLen > MAXBUF)
    {
        tmpLen = MAXBUF;
    }
    
    p = buf + tmpLen;
    *p = 0;
    
    do
    {
        *--p = __BASE_STRING[tmpVal % base];
    }
    while( tmpVal /= base );
    /*
    //不足位前面补0 NO.1
    while(api_strlen(p) % bitlen)
    {
    *--p = '0';
    }
    */
    /*
    //不足位前面补0 NO.2
    tmpLen = bitlen - api_strlen(p);
    if(tmpLen)
    {
        memset(buf, '0', tmpLen);
    }
    */
    //不足位前面补0 NO.3
    tmpLen = (p - buf);
    if(tmpLen)
    {
        memset(buf, '0', tmpLen);
    }
    
    // strncpy(buf + tmpLen, p, api_strlen(p)); // 画蛇添足
    
    return buf;
}

//////////////////////////////////////////////////////////////////////////////////////////
#if 0
#if API_HAVE_ICONV

API_DECLARE(int) api_iconv(const char* from_code, const char* from, const char* to_code, char* to, size_t iLength)
{
    iconv_t     cd;
    size_t      oLength;
    
    cd = iconv_open((const char *)to_code, (const char *)from_code);
    if(cd == (iconv_t)-1)
    {
        /* iconv_open failed */
        (void) fprintf(stderr, "iconv_open(%s, %s) failed\\n", to_code, from_code);
        return (1);
    }
    
    oLength = BUFSIZ;
#ifdef __API_LINUX__ /* only Linux */
    if(iconv(cd, (char**)&from, &iLength, (char**)&to, &oLength) == ((size_t) - 1))
#else
    if(iconv(cd, (const char**)&from, &iLength, (char**)&to, &oLength) == ((size_t) - 1))
#endif
    {
        /**/
    }
    (void) iconv_close(cd);
    return (BUFSIZ - oLength);
}

/**
 * 从一个2个字节的无符号整数中取出高位字节
 * @param[in] integer, 2个字节的无符号整数
 */
static API_INLINE unsigned char getH(unsigned short int integer)
{
    return (unsigned char)((integer & 0xFF00) >> 8);
}

/**
 * 从一个2个字节的无符号整数中取出低位字节
 * @param[in] integer, 2个字节的无符号整数
 */
static API_INLINE unsigned char getL(unsigned short int integer)
{
    return (unsigned char)(integer & 0x00FF);
}

#else
static API_INLINE UINT iconv_get_code(const char *codeing)
{
	UINT cp = CP_ACP;
	if (!strcasecmp("gb2312", codeing)) {
		cp = 20936;
	} else if (!strcasecmp("gbk", codeing)) {
		cp = 936;
	} else if (!strcasecmp("gb18030", codeing)) {
		cp = 54936;
	} else if (!strcasecmp("utf-8", codeing)) {
		cp = CP_UTF8;
	} else {
		//
	}
	return cp;
}

API_DECLARE(int) api_iconv(const char* from_code, const char* from, const char* to_code, char* to, size_t iLength)
{
	unsigned short * wszFrom = NULL;
	char           * szTo = NULL;
	UINT             cp_from = iconv_get_code(from_code);
	UINT             cp_to = iconv_get_code(to_code);
	size_t           iSize = 0;
    size_t           oSize = 0;
	
    //if (cp_from > 0 && cp_to > 0)
	{
		iSize = MultiByteToWideChar(cp_from, 0, from, -1, NULL, 0);
		wszFrom = malloc(sizeof(unsigned short) * (iSize + 1));
		memset(wszFrom, 0, iSize * 2 + 2);
		MultiByteToWideChar(cp_from, 0, (LPCTSTR)from, -1, (LPWSTR)wszFrom, iSize);
		
		oSize = WideCharToMultiByte(cp_to, 0, (LPCWSTR)wszFrom, -1, NULL, 0, NULL, NULL); 
		//char *szUtf8=new char[len + 1]; 
		memset(to, 0, oSize + 1); 
		WideCharToMultiByte (cp_to, 0, (LPCWSTR)wszFrom, -1, to, oSize, NULL,NULL);
		free(wszFrom);
		wszFrom = NULL;
	}
	

    return oSize;
}

#endif

API_DECLARE(int) A2U(char *strDestination, const char *strSource, int nLength)
{
	nLength = api_iconv("gb2312", (char*)strSource, "ucs-2be", strDestination, nLength);
	return nLength;
}

API_DECLARE(int) U2A(char *strDestination, const char *strSource, int nLength)
{
	nLength = api_iconv("ucs-2be", (char*)strSource, "gb2312", strDestination, nLength);
	return nLength;
}
#endif 
//////////////////////////////////////////////////////////////////////////////////////////

static char * api_os_locale_encoding (void)
{
#ifndef __API_WINDOWS__
    char *charset = nl_langinfo(CODESET);
    char *cp = strdup(charset);
#else
#ifdef _UNICODE
    int i;
#endif
#if defined(_WIN32_WCE)
    LCID locale = GetUserDefaultLCID();
#else
    LCID locale = GetThreadLocale();
#endif
    int len = GetLocaleInfo(locale, LOCALE_IDEFAULTANSICODEPAGE, NULL, 0);
    int size = (len * sizeof(TCHAR)) + 2;
    char *cp = malloc(size);
    memset(cp, 0x00, size);
    if (0 < GetLocaleInfo(locale, LOCALE_IDEFAULTANSICODEPAGE, (TCHAR*) (cp + 2), len))
    {
    /* Fix up the returned number to make a valid codepage name of
        the form "CPnnnn". */
        cp[0] = 'C';
        cp[1] = 'P';
#ifdef _UNICODE
        for(i = 0; i < len; i++) {
            cp[i + 2] = (char) ((TCHAR*) (cp + 2))[i];
        }
#endif
        return cp;
    }
    api_snprintf(cp, size, "CP%u", (unsigned) GetACP());
#endif
    return cp;
}

API_DECLARE(const char *) api_locale_encoding(void)
{   
    const char *charset = NULL;
    char *cs = api_os_locale_encoding();
#ifdef __API_WINDOWS__
    if (!strcasecmp("CP20936", cs)) {
        charset = "gb2312";        
    } else if (!strcasecmp("CP936", cs)) {        
        charset = "gbk";        
    } else if (!strcasecmp("CP54936", cs)) {        
        charset = "gb18030";        
    } else if (!strcasecmp("CP65001", cs)) {        
        charset = "utf-8";        
    } else {
        //      
    }
#else
    if (!strcasecmp("gb2312", cs)) {        
        charset = "gb2312";        
    } else if (!strcasecmp("gbk", cs)) {        
        charset = "gbk";        
    } else if (!strcasecmp("gb18030", cs)) {        
        charset = "gb18030";        
    } else if (!strcasecmp("utf-8", cs)) {        
        charset = "utf-8";        
    } else {        
        //        
    }    
#endif
    if (cs)
    {
        free(cs);
        cs = NULL;
    }
    
    return charset;
}

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * 获得文件路径
 * @param[in] path, 路径
 * @param[in] size, 尺寸
 * @param[in] filename, 文件名
 * @return 不含路径的文件名
 */
API_DECLARE(const char *) api_filename_path_get(char *path, size_t size, const char *filename)
{
    size_t i = api_strlen(filename);
    memset(path, 0x00, size);
    
    if(path == NULL || size == 0 || api_strlen(filename) == 0)
    {
        return "";
    }
    
    api_cpystrn(path, filename, size);
    i = api_strlen(path);
    while(i && path[i - 1] != '\\' && path[i - 1] != '/')
    {
        path[i - 1] = '\0';
        i--;
    }
    if (i > 0)
    {
        path[i - 1] = '\0';
    }
    
    return path;
}

/**
 * 获得文件扩展名
 * @param[in] filename, 文件名
 * @return 文件扩展名
 */
API_DECLARE(const char *) api_filename_ext_get(const char *filename)
{
    const char *p = NULL;
    const char *ext = filename;
    do
    {
        p = strchr(ext, '.');
        if(p == NULL)
        {
            break;
        }
        ++p;
        ext = p;
    }while(p);
    
    return ext;
}

//////////////////////////////////////////////////////////////////////////////////////////


#if 0
/*
 * Do format conversion placing the output in buffer
 */
API_DECLARE(int) api_vformatter(int (*flush_func)(api_vformatter_buff_t *),
    api_vformatter_buff_t *vbuff, const char *fmt, va_list ap)
{
    register char *sp;
    register char *bep;
    register int cc = 0;
    register api_size_t i;

    register char *s = NULL;
    char *q;
    api_size_t s_len = 0;

    register api_size_t min_width = 0;
    api_size_t precision = 0;
    enum {
        LEFT, RIGHT
    } adjust;
    char pad_char;
    char prefix_char;

    double fp_num;
    api_int64_t i_quad = 0;
    api_uint64_t ui_quad;
    api_int32_t i_num = 0;
    api_uint32_t ui_num;

    char num_buf[NUM_BUF_SIZE];
    char char_buf[2];                /* for printing %% and %<unknown> */

    enum var_type_enum {
            IS_QUAD, IS_LONG, IS_SHORT, IS_INT
    };
    enum var_type_enum var_type = IS_INT;

    /*
     * Flag variables
     */
    boolean_e alternate_form;
    boolean_e print_sign;
    boolean_e print_blank;
    boolean_e adjust_precision;
    boolean_e adjust_width;
    int is_negative;

    sp = vbuff->curpos;
    bep = vbuff->endpos;

    while (*fmt) {
        if (*fmt != '%') {
            INS_CHAR(*fmt, sp, bep, cc);
        }
        else {
            /*
             * Default variable settings
             */
            boolean_e print_something = YES;
            adjust = RIGHT;
            alternate_form = print_sign = print_blank = NO;
            pad_char = ' ';
            prefix_char = NUL;

            fmt++;

            /*
             * Try to avoid checking for flags, width or precision
             */
            if (!api_islower(*fmt)) {
                /*
                 * Recognize flags: -, #, BLANK, +
                 */
                for (;; fmt++) {
                    if (*fmt == '-')
                        adjust = LEFT;
                    else if (*fmt == '+')
                        print_sign = YES;
                    else if (*fmt == '#')
                        alternate_form = YES;
                    else if (*fmt == ' ')
                        print_blank = YES;
                    else if (*fmt == '0')
                        pad_char = '0';
                    else
                        break;
                }

                /*
                 * Check if a width was specified
                 */
                if (api_isdigit(*fmt)) {
                    STR_TO_DEC(fmt, min_width);
                    adjust_width = YES;
                }
                else if (*fmt == '*') {
                    int v = va_arg(ap, int);
                    fmt++;
                    adjust_width = YES;
                    if (v < 0) {
                        adjust = LEFT;
                        min_width = (api_size_t)(-v);
                    }
                    else
                        min_width = (api_size_t)v;
                }
                else
                    adjust_width = NO;

                /*
                 * Check if a precision was specified
                 */
                if (*fmt == '.') {
                    adjust_precision = YES;
                    fmt++;
                    if (api_isdigit(*fmt)) {
                        STR_TO_DEC(fmt, precision);
                    }
                    else if (*fmt == '*') {
                        int v = va_arg(ap, int);
                        fmt++;
                        precision = (v < 0) ? 0 : (api_size_t)v;
                    }
                    else
                        precision = 0;
                }
                else
                    adjust_precision = NO;
            }
            else
                adjust_precision = adjust_width = NO;

            /*
             * Modifier check.  Note that if API_INT64_T_FMT is "d",
             * the first if condition is never true.
             */
            if ((sizeof(API_INT64_T_FMT) == 4 &&
                 fmt[0] == API_INT64_T_FMT[0] &&
                 fmt[1] == API_INT64_T_FMT[1]) ||
                (sizeof(API_INT64_T_FMT) == 3 &&
                 fmt[0] == API_INT64_T_FMT[0]) ||
                (sizeof(API_INT64_T_FMT) > 4 &&
                 strncmp(fmt, API_INT64_T_FMT, 
                         sizeof(API_INT64_T_FMT) - 2) == 0)) {
                /* Need to account for trailing 'd' and null in sizeof() */
                var_type = IS_QUAD;
                fmt += (sizeof(API_INT64_T_FMT) - 2);
            }
            else if (*fmt == 'q') {
                var_type = IS_QUAD;
                fmt++;
            }
            else if (*fmt == 'l') {
                var_type = IS_LONG;
                fmt++;
            }
            else if (*fmt == 'h') {
                var_type = IS_SHORT;
                fmt++;
            }
            else {
                var_type = IS_INT;
            }

            /*
             * Argument extraction and printing.
             * First we determine the argument type.
             * Then, we convert the argument to a string.
             * On exit from the switch, s points to the string that
             * must be printed, s_len has the length of the string
             * The precision requirements, if any, are reflected in s_len.
             *
             * NOTE: pad_char may be set to '0' because of the 0 flag.
             *   It is reset to ' ' by non-numeric formats
             */
            switch (*fmt) {
            case 'u':
                if (var_type == IS_QUAD) {
                    i_quad = va_arg(ap, api_uint64_t);
                    s = conv_10_quad(i_quad, 1, &is_negative,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                else {
                    if (var_type == IS_LONG)
                        i_num = (api_int32_t) va_arg(ap, api_uint32_t);
                    else if (var_type == IS_SHORT)
                        i_num = (api_int32_t) (unsigned short) va_arg(ap, unsigned int);
                    else
                        i_num = (api_int32_t) va_arg(ap, unsigned int);
                    s = conv_10(i_num, 1, &is_negative,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                FIX_PRECISION(adjust_precision, precision, s, s_len);
                break;

            case 'd':
            case 'i':
                if (var_type == IS_QUAD) {
                    i_quad = va_arg(ap, api_int64_t);
                    s = conv_10_quad(i_quad, 0, &is_negative,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                else {
                    if (var_type == IS_LONG)
                        i_num = va_arg(ap, api_int32_t);
                    else if (var_type == IS_SHORT)
                        i_num = (short) va_arg(ap, int);
                    else
                        i_num = va_arg(ap, int);
                    s = conv_10(i_num, 0, &is_negative,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                FIX_PRECISION(adjust_precision, precision, s, s_len);

                if (is_negative)
                    prefix_char = '-';
                else if (print_sign)
                    prefix_char = '+';
                else if (print_blank)
                    prefix_char = ' ';
                break;


            case 'o':
                if (var_type == IS_QUAD) {
                    ui_quad = va_arg(ap, api_uint64_t);
                    s = conv_p2_quad(ui_quad, 3, *fmt,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                else {
                    if (var_type == IS_LONG)
                        ui_num = va_arg(ap, api_uint32_t);
                    else if (var_type == IS_SHORT)
                        ui_num = (unsigned short) va_arg(ap, unsigned int);
                    else
                        ui_num = va_arg(ap, unsigned int);
                    s = conv_p2(ui_num, 3, *fmt,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                FIX_PRECISION(adjust_precision, precision, s, s_len);
                if (alternate_form && *s != '0') {
                    *--s = '0';
                    s_len++;
                }
                break;


            case 'x':
            case 'X':
                if (var_type == IS_QUAD) {
                    ui_quad = va_arg(ap, api_uint64_t);
                    s = conv_p2_quad(ui_quad, 4, *fmt,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                else {
                    if (var_type == IS_LONG)
                        ui_num = va_arg(ap, api_uint32_t);
                    else if (var_type == IS_SHORT)
                        ui_num = (unsigned short) va_arg(ap, unsigned int);
                    else
                        ui_num = va_arg(ap, unsigned int);
                    s = conv_p2(ui_num, 4, *fmt,
                            &num_buf[NUM_BUF_SIZE], &s_len);
                }
                FIX_PRECISION(adjust_precision, precision, s, s_len);
                if (alternate_form && i_num != 0) {
                    *--s = *fmt;        /* 'x' or 'X' */
                    *--s = '0';
                    s_len += 2;
                }
                break;


            case 's':
                s = va_arg(ap, char *);
                if (s != NULL) {
                    if (!adjust_precision) {
                        s_len = api_strlen(s);
                    }
                    else {
                        /* From the C library standard in section 7.9.6.1:
                         * ...if the precision is specified, no more then
                         * that many characters are written.  If the
                         * precision is not specified or is greater
                         * than the size of the array, the array shall
                         * contain a null character.
                         *
                         * My reading is is precision is specified and
                         * is less then or equal to the size of the
                         * array, no null character is required.  So
                         * we can't do a strlen.
                         *
                         * This figures out the length of the string
                         * up to the precision.  Once it's long enough
                         * for the specified precision, we don't care
                         * anymore.
                         *
                         * NOTE: you must do the length comparison
                         * before the check for the null character.
                         * Otherwise, you'll check one beyond the
                         * last valid character.
                         */
                        const char *walk;

                        for (walk = s, s_len = 0;
                             (s_len < precision) && (*walk != '\0');
                             ++walk, ++s_len);
                    }
                }
                else {
                    s = S_NULL;
                    s_len = S_NULL_LEN;
                }
                pad_char = ' ';
                break;


            case 'f':
            case 'e':
            case 'E':
                fp_num = va_arg(ap, double);
                /*
                 * We use &num_buf[ 1 ], so that we have room for the sign
                 */
                s = NULL;
#ifdef HAVE_ISNAN
                if (isnan(fp_num)) {
                    s = "nan";
                    s_len = 3;
                }
#endif
#ifdef HAVE_ISINF
                if (!s && isinf(fp_num)) {
                    s = "inf";
                    s_len = 3;
                }
#endif
                if (!s) {
                    s = conv_fp(*fmt, fp_num, alternate_form,
                                (int)((adjust_precision == NO) ? FLOAT_DIGITS : precision),
                                &is_negative, &num_buf[1], &s_len);
                    if (is_negative)
                        prefix_char = '-';
                    else if (print_sign)
                        prefix_char = '+';
                    else if (print_blank)
                        prefix_char = ' ';
                }
                break;


            case 'g':
            case 'G':
                if (adjust_precision == NO)
                    precision = FLOAT_DIGITS;
                else if (precision == 0)
                    precision = 1;
                /*
                 * * We use &num_buf[ 1 ], so that we have room for the sign
                 */
                s = api_gcvt(va_arg(ap, double), (int) precision, &num_buf[1],
                            alternate_form);
                if (*s == '-')
                    prefix_char = *s++;
                else if (print_sign)
                    prefix_char = '+';
                else if (print_blank)
                    prefix_char = ' ';

                s_len = api_strlen(s);

                if (alternate_form && (q = strchr(s, '.')) == NULL) {
                    s[s_len++] = '.';
                    s[s_len] = '\0'; /* delimit for following strchr() */
                }
                if (*fmt == 'G' && (q = strchr(s, 'e')) != NULL)
                    *q = 'E';
                break;


            case 'c':
                char_buf[0] = (char) (va_arg(ap, int));
                s = &char_buf[0];
                s_len = 1;
                pad_char = ' ';
                break;


            case '%':
                char_buf[0] = '%';
                s = &char_buf[0];
                s_len = 1;
                pad_char = ' ';
                break;


            case 'n':
                if (var_type == IS_QUAD)
                    *(va_arg(ap, api_int64_t *)) = cc;
                else if (var_type == IS_LONG)
                    *(va_arg(ap, long *)) = cc;
                else if (var_type == IS_SHORT)
                    *(va_arg(ap, short *)) = cc;
                else
                    *(va_arg(ap, int *)) = cc;
                print_something = NO;
                break;

                /*
                 * This is where we extend the printf format, with a second
                 * type specifier
                 */
            case 'p':
                switch(*++fmt) {
                /*
                 * If the pointer size is equal to or smaller than the size
                 * of the largest unsigned int, we convert the pointer to a
                 * hex number, otherwise we print "%p" to indicate that we
                 * don't handle "%p".
                 */
                case 'p':
#if API_SIZEOF_VOIDP == 8
                    if (sizeof(void *) <= sizeof(api_uint64_t)) {
                        ui_quad = (api_uint64_t) va_arg(ap, void *);
                        s = conv_p2_quad(ui_quad, 4, 'x',
                                &num_buf[NUM_BUF_SIZE], &s_len);
                    }
#else
                    if (sizeof(void *) <= sizeof(api_uint32_t)) {
                        ui_num = (api_uint32_t) va_arg(ap, void *);
                        s = conv_p2(ui_num, 4, 'x',
                                &num_buf[NUM_BUF_SIZE], &s_len);
                    }
#endif
                    else {
                        s = "%p";
                        s_len = 2;
                        prefix_char = NUL;
                    }
                    pad_char = ' ';
                    break;

                /* print an api_sockaddr_t as a.b.c.d:port */
                case 'I':
                {
                    api_sockaddr_t *sa;

                    sa = va_arg(ap, api_sockaddr_t *);
                    if (sa != NULL) {
                        s = conv_api_sockaddr(sa, &num_buf[NUM_BUF_SIZE], &s_len);
                        if (adjust_precision && precision < s_len)
                            s_len = precision;
                    }
                    else {
                        s = S_NULL;
                        s_len = S_NULL_LEN;
                    }
                    pad_char = ' ';
                }
                break;

                /* print a struct in_addr as a.b.c.d */
                case 'A':
                {
                    struct in_addr *ia;

                    ia = va_arg(ap, struct in_addr *);
                    if (ia != NULL) {
                        s = conv_in_addr(ia, &num_buf[NUM_BUF_SIZE], &s_len);
                        if (adjust_precision && precision < s_len)
                            s_len = precision;
                    }
                    else {
                        s = S_NULL;
                        s_len = S_NULL_LEN;
                    }
                    pad_char = ' ';
                }
                break;

                /* print the error for an api_status_t */
                case 'm':
                {
                    api_status_t *mrv;

                    mrv = va_arg(ap, api_status_t *);
                    if (mrv != NULL) {
                        s = api_strerror(*mrv, num_buf, NUM_BUF_SIZE-1);
                        s_len = api_strlen(s);
                    }
                    else {
                        s = S_NULL;
                        s_len = S_NULL_LEN;
                    }
                    pad_char = ' ';
                }
                break;

                case 'T':
#if API_HAS_THREADS
                {
                    api_os_thread_t *tid;

                    tid = va_arg(ap, api_os_thread_t *);
                    if (tid != NULL) {
                        s = conv_os_thread_t(tid, &num_buf[NUM_BUF_SIZE], &s_len);
                        if (adjust_precision && precision < s_len)
                            s_len = precision;
                    }
                    else {
                        s = S_NULL;
                        s_len = S_NULL_LEN;
                    }
                    pad_char = ' ';
                }
#else
                    char_buf[0] = '0';
                    s = &char_buf[0];
                    s_len = 1;
                    pad_char = ' ';
#endif
                    break;

                case 't':
#if API_HAS_THREADS
                {
                    api_os_thread_t *tid;

                    tid = va_arg(ap, api_os_thread_t *);
                    if (tid != NULL) {
                        s = conv_os_thread_t_hex(tid, &num_buf[NUM_BUF_SIZE], &s_len);
                        if (adjust_precision && precision < s_len)
                            s_len = precision;
                    }
                    else {
                        s = S_NULL;
                        s_len = S_NULL_LEN;
                    }
                    pad_char = ' ';
                }
#else
                    char_buf[0] = '0';
                    s = &char_buf[0];
                    s_len = 1;
                    pad_char = ' ';
#endif
                    break;

                case 'B':
                case 'F':
                case 'S':
                {
                    char buf[5];
                    api_off_t size = 0;

                    if (*fmt == 'B') {
                        api_uint32_t *arg = va_arg(ap, api_uint32_t *);
                        size = (arg) ? *arg : 0;
                    }
                    else if (*fmt == 'F') {
                        api_off_t *arg = va_arg(ap, api_off_t *);
                        size = (arg) ? *arg : 0;
                    }
                    else {
                        api_size_t *arg = va_arg(ap, api_size_t *);
                        size = (arg) ? *arg : 0;
                    }

                    s = api_strfsize(size, buf);
                    s_len = api_strlen(s);
                    pad_char = ' ';
                }
                break;

                case NUL:
                    /* if %p ends the string, oh well ignore it */
                    continue;

                default:
                    s = "bogus %p";
                    s_len = 8;
                    prefix_char = NUL;
                    (void)va_arg(ap, void *); /* skip the bogus argument on the stack */
                    break;
                }
                break;

            case NUL:
                /*
                 * The last character of the format string was %.
                 * We ignore it.
                 */
                continue;


                /*
                 * The default case is for unrecognized %'s.
                 * We print %<char> to help the user identify what
                 * option is not understood.
                 * This is also useful in case the user wants to pass
                 * the output of format_converter to another function
                 * that understands some other %<char> (like syslog).
                 * Note that we can't point s inside fmt because the
                 * unknown <char> could be preceded by width etc.
                 */
            default:
                char_buf[0] = '%';
                char_buf[1] = *fmt;
                s = char_buf;
                s_len = 2;
                pad_char = ' ';
                break;
            }

            if (prefix_char != NUL && s != S_NULL && s != char_buf) {
                *--s = prefix_char;
                s_len++;
            }

            if (adjust_width && adjust == RIGHT && min_width > s_len) {
                if (pad_char == '0' && prefix_char != NUL) {
                    INS_CHAR(*s, sp, bep, cc);
                    s++;
                    s_len--;
                    min_width--;
                }
                PAD(min_width, s_len, pad_char);
            }

            /*
             * Print the string s. 
             */
            if (print_something == YES) {
                for (i = s_len; i != 0; i--) {
                      INS_CHAR(*s, sp, bep, cc);
                    s++;
                }
            }

            if (adjust_width && adjust == LEFT && min_width > s_len)
                PAD(min_width, s_len, pad_char);
        }
        fmt++;
    }
    vbuff->curpos = sp;

    return cc;
}


static int snprintf_flush(api_vformatter_buff_t *vbuff)
{
    /* if the buffer fills we have to abort immediately, there is no way
     * to "flush" an api_snprintf... there's nowhere to flush it to.
     */
    return -1;
}

API_DECLARE_NONSTD(int) api_snprintf(char *buf, api_size_t len, 
                                     const char *format, ...)
{
    int cc;
    va_list ap;
    api_vformatter_buff_t vbuff;
    
    if (len == 0) {
    /* NOTE: This is a special case; we just want to return the number
    * of chars that would be written (minus \0) if the buffer
    * size was infinite. We leverage the fact that INS_CHAR
    * just does actual inserts iff the buffer pointer is non-NULL.
    * In this case, we don't care what buf is; it can be NULL, since
    * we don't touch it at all.
        */
        vbuff.curpos = NULL;
        vbuff.endpos = NULL;
    } else {
        /* save one byte for nul terminator */
        vbuff.curpos = buf;
        vbuff.endpos = buf + len - 1;
    }
    va_start(ap, format);
    cc = api_vformatter(snprintf_flush, &vbuff, format, ap);
    va_end(ap);
    if (len != 0) {
        *vbuff.curpos = '\0';
    }
    return (cc == -1) ? (int)len - 1 : cc;
}


API_DECLARE(int) api_vsnprintf(char *buf, api_size_t len, const char *format,
                               va_list ap)
{
    int cc;
    api_vformatter_buff_t vbuff;
    
    if (len == 0) {
        /* See above note */
        vbuff.curpos = NULL;
        vbuff.endpos = NULL;
    } else {
        /* save one byte for nul terminator */
        vbuff.curpos = buf;
        vbuff.endpos = buf + len - 1;
    }
    cc = api_vformatter(snprintf_flush, &vbuff, format, ap);
    if (len != 0) {
        *vbuff.curpos = '\0';
    }
    return (cc == -1) ? (int)len - 1 : cc;
}
#endif

API_DECLARE(api_status_t) api_strtoff(api_off_t *offset, const char *nptr,
                                      char **endptr, int base)
{
    errno = 0;
    *offset = API_OFF_T_STRFN(nptr, endptr, base);
    return API_FROM_OS_ERROR(errno);
}

API_DECLARE(api_int64_t) api_strtoi64(const char *nptr, char **endptr, int base)
{
#ifdef API_INT64_STRFN
    return API_INT64_STRFN(nptr, endptr, base);
#else
    const char *s;
    api_int64_t acc;
    api_int64_t val;
    int neg, any;
    char c;

    /*
     * Skip white space and pick up leading +/- sign if any.
     * If base is 0, allow 0x for hex and 0 for octal, else
     * assume decimal; if base is already 16, allow 0x.
     */
    s = nptr;
    do {
    c = *s++;
    } while (api_isspace(c));
    if (c == '-') {
    neg = 1;
    c = *s++;
    } else {
    neg = 0;
    if (c == '+')
        c = *s++;
    }
    if ((base == 0 || base == 16) &&
    c == '0' && (*s == 'x' || *s == 'X')) {
        c = s[1];
        s += 2;
        base = 16;
    }
    if (base == 0)
    base = c == '0' ? 8 : 10;
    acc = any = 0;
    if (base < 2 || base > 36) {
    errno = EINVAL;
        if (endptr != NULL)
        *endptr = (char *)(any ? s - 1 : nptr);
        return acc;
    }

    /* The classic bsd implementation requires div/mod operators
     * to compute a cutoff.  Benchmarking proves that is very, very
     * evil to some 32 bit processors.  Instead, look for underflow
     * in both the mult and add/sub operation.  Unlike the bsd impl,
     * we also work strictly in a signed int64 word as we haven't
     * implemented the unsigned type in win32.
     * 
     * Set 'any' if any `digits' consumed; make it negative to indicate
     * overflow.
     */
    val = 0;
    for ( ; ; c = *s++) {
        if (c >= '0' && c <= '9')
        c -= '0';
#if (('Z' - 'A') == 25)
    else if (c >= 'A' && c <= 'Z')
        c -= 'A' - 10;
    else if (c >= 'a' && c <= 'z')
        c -= 'a' - 10;
#elif API_CHARSET_EBCDIC
    else if (c >= 'A' && c <= 'I')
        c -= 'A' - 10;
    else if (c >= 'J' && c <= 'R')
        c -= 'J' - 19;
    else if (c >= 'S' && c <= 'Z')
        c -= 'S' - 28;
    else if (c >= 'a' && c <= 'i')
        c -= 'a' - 10;
    else if (c >= 'j' && c <= 'r')
        c -= 'j' - 19;
    else if (c >= 's' && c <= 'z')
        c -= 'z' - 28;
#else
#error "CANNOT COMPILE api_strtoi64(), only ASCII and EBCDIC supported" 
#endif
    else
        break;
    if (c >= base)
        break;
    val *= base;
        if ( (any < 0)    /* already noted an over/under flow - short circuit */
           || (neg && (val > acc || (val -= c) > acc)) /* underflow */
           || (!neg && (val < acc || (val += c) < acc))) {       /* overflow */
            any = -1;    /* once noted, over/underflows never go away */
#ifdef API_STRTOI64_OVERFLOW_IS_BAD_CHAR
            break;
#endif
        } else {
            acc = val;
        any = 1;
        }
    }

    if (any < 0) {
    acc = neg ? API_INT64_MIN : API_INT64_MAX;
    errno = ERANGE;
    } else if (!any) {
    errno = EINVAL;
    }
    if (endptr != NULL)
    *endptr = (char *)(any ? s - 1 : nptr);
    return (acc);
#endif
}

API_DECLARE(api_int64_t) api_atoi64(const char *buf)
{
    return api_strtoi64(buf, NULL, 10);
}

//////////////////////////////////////////////////////////////////////////////////////////
