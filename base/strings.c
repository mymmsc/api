#include "api/strings.h"
#include "api_private.h"
#include "api/memory.h"

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

#if API_HAVE_ICONV
#include <iconv.h>
#endif

/* --------------------------------------------------------------------- */
ssize_t
api_atosz(uint8_t *line, size_t n)
{
    ssize_t  value;

    if (n == 0) {
        return API_ERROR;
    }

    for (value = 0; n--; line++) {
        if (*line < '0' || *line > '9') {
            return API_ERROR;
        }

        value = value * 10 + (*line - '0');
    }

    if (value < 0) {
        return API_ERROR;
    } else {
        return value;
    }
}

ssize_t
api_parse_size(api_str_t *line)
{
    uint8_t    unit;
    size_t     len;
    ssize_t    size;
    api_int_t  scale;

    len = line->len;
    unit = line->data[len - 1];

    switch (unit) {
    case 'K':
    case 'k':
        len--;
        scale = 1024;
        break;

    case 'M':
    case 'm':
        len--;
        scale = 1024 * 1024;
        break;

    default:
        scale = 1;
    }

    size = api_atosz(line->data, len);
    if (size == API_ERROR) {
        return API_ERROR;
    }

    size *= scale;

    return size;
}

/* --------------------------------------------------------------------- */
api_int_t
api_memn2cmp(uint8_t *s1, uint8_t *s2, size_t n1, size_t n2)
{
    size_t     n;
    api_int_t  m, z;

    if (n1 <= n2) {
        n = n1;
        z = -1;

    } else {
        n = n2;
        z = 1;
    }

    m = api_memcmp(s1, s2, n);

    if (m || n1 == n2) {
        return m;
    }

    return z;
}

/* --------------------------------------------------------------------- */

#ifndef API_HAVE_EXPLICIT_BZERO

/*
 * explicit_bzero - don't let the compiler optimize away bzero
 */
void explicit_bzero(void *p, size_t n)
{
	bzero(p, n);
}
#endif

/*
 * "replacement" for the strncpy() function. We roll our
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

char * api_cpystrn(char *dst, const char *src, api_size_t dst_size)
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

const char * api_filepath_name_get(const char *pathname)
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
char * api_collapse_spaces(char *dest, const char *src)
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
	sdup[len - 1] = 0x00;
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

int api_strnatcmp(char const *a, char const *b)
{
     return strnatcmp0(a, b, 0);
}


/* Compare, recognizing numeric string and ignoring case. */
int api_strnatcasecmp(char const *a, char const *b)
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
char * api_str2rep(char *haystack, char * (*value_cb)(api_str_t *key, void *, size_t *), void *data)
{
	int i = 0, j = 0;
	size_t len = api_strlen(haystack);

	if(len <= 4) {
		return haystack;
	} else {
		char *p = NULL;
		int flag = 0, fp = 0, fs = 0;
		byte_t ch;
		p = haystack;
		api_str_t key = api_null_string;
		for(i = 0; i < len; i++) {
			//printf("%d: %c\n", i, ch);
			ch = haystack[i];
			//printf("%d: %c\n", i, ch);
			switch(ch) {
			case '%':
				switch(flag) {
				case 0:
					flag = 1;
					fp = 1;
					break;
				case 1:
					fp = 2;
					flag = 2;
					key.data = (uint8_t *)haystack + i + 1;
					key.len = 0;
					break;
				case 2:
					flag = 3;
					fs = 1;
					break;
				case 3:
					fs = 2;
					flag = 0;
					{
						//printf("----------------------------------------------\n");
						size_t vlen = 0;
						//printf("key = [%s]\n", key.data);
						char *value = value_cb(&key, data, &vlen);
						if(value != NULL && vlen > 0) {
							int nPoint = p - haystack;
							int nNeedle = key.len + 4;
							//printf("find: pos=%d, needle=%d, vlen=%d\n", nPoint, nNeedle, vlen);
					        memmove(haystack + (nPoint + vlen), p + nNeedle, len - nPoint - 1);
					        memcpy(haystack + nPoint, value, vlen);
					        *(haystack + len + (vlen - nNeedle)) = 0x00;
							p += vlen;
							i += vlen - nNeedle;
							len += vlen - nNeedle;
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				if(flag == 2) {
					key.len++;
				} else {
					*p++ = ch;
				}
				break;
			}	
		}
	}
	return haystack;
}


static char *filter_env(api_str_t *key, void *data, size_t *size)
{
    char *sRet = NULL;
    char env_key[128];
    size_t len = sizeof(env_key);
    memset(env_key, 0x00, len);
    api_snprintf(env_key, len, "%V", key);
    sRet = getenv(env_key);
    *size = api_strlen(sRet);
    return sRet;
}

char * api_strfilter(char *haystack, char * (*value_cb)(api_str_t *key, void *, size_t *), void *data)
{
    int i = 0, j = 0;
    size_t len = api_strlen(haystack);
    
    if(value_cb == NULL) {
        value_cb = filter_env;
    }

    if(len <= 4) {
        return haystack;
    } else {
        const char *str = haystack;
        char *p = NULL;
        int flag = 0;
        int fp = 0; // prefix
        int fs = 0; // suffix
        byte_t ch;
        p = haystack;
        api_str_t key = api_null_string;
        for(i = 0; i < len; i++) {
            //printf("%d: %c\n", i, ch);
            ch = haystack[i];
            if(ch == '$') {
                flag = 1;
                fp = 1;
                continue;
            }
            //printf("%d: %c\n", i, ch);
            switch(ch) {
            case '{':
                if(flag != 1) {
                    flag = 0;
                    fp = 0;
                    continue;
                }
                fp = 2;
                flag = 2;
                key.data = (uint8_t *)haystack + i + 1;
                key.len = 0;
                break;
            case '}':
                if(flag != 2) {
                    flag = 0;
                    fp = 0;
                    continue;
                }
                fs = 1;
                flag = 0;
                {
                    //printf("----------------------------------------------\n");
                    size_t vlen = 0;
                    //printf("key = [%s]\n", key.data);
                    char *value = value_cb(&key, data, &vlen);
                    if(value != NULL && vlen > 0) {
                        int nPoint = p - haystack;
                        int nNeedle = key.len + 3;
                        //printf("find: pos=%d, needle=%d, vlen=%d\n", nPoint, nNeedle, vlen);
                        memmove(haystack + (nPoint + vlen), p + nNeedle, len - nPoint - 1);
                        memcpy(haystack + nPoint, value, vlen);
                        *(haystack + len + (vlen - nNeedle)) = 0x00;
                        p += vlen;
                        i += vlen - nNeedle;
                        len += vlen - nNeedle;
                    }
                }
                break;
            default:
                if(flag == 2) {
                    key.len++;
                } else {
                    *p++ = ch;
                }
                break;
            }
        }
    }
    return haystack;
}

char * api_strtolc(char *str)
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

char * api_strtouc(char *str)
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

char * api_mem2hex(char *strHex, const char *str, const api_int32_t len)
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

int api_hex2mem(char *str, const char *strHex, const api_int32_t len)
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

char * api_strcat(char *buffer, const char * lpszFormat, ...)
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

char * api_substr(char *str, const char *src, ...)
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
    
    //start > len or < -len
    if(tmpStart > (len - 1) || tmpStart < (-1 * len))
    {
        return NULL;
    }
    else if(tmpStart < 0)
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

char * api_stristr(const char* haystack, const char *needle)
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

char * api_strtrim(char *pstr)
{
    api_int32_t  len;
    char        *pRet = pstr;
    
    if(api_strlen(pRet) == 0) {
        return "";
    }
    
    while(strchr(" \t\r\n", *pRet)) {
        pRet++;
    }
    
    len = api_strlen(pRet);
    if(len == 0) {
        return "";
    }
    while(len > 0)
    {
        if(strchr(" \t\r\n", *(pRet + len - 1))) {
            *(pRet + len - 1) = 0x00;
            len--;
        } else {
            break;
        }
    }
    *(pRet + len) = 0x00;
    api_cpystrn(pstr, pRet, len + 1);
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
static char * api_strrep_s(char * (*lpStrStr)(const char *, const char *),
		char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
    api_int32_t nPoint  = -1; //子串的起始位置
    const char *p       = NULL;
    api_size_t  nLen    = 0;
	api_size_t  nNeedle = api_strlen(needle);
	api_size_t  nRep    = api_strlen(rep);
    
    // 检查传入参数
    if(haystack == NULL || nNeedle == 0)
    {
        return NULL;
    }
    while((p = lpStrStr(haystack, needle)) != NULL)
    {
		nLen = api_strlen(haystack);
        //得到匹配到的子串的位置
        nPoint = p - haystack;
        memmove(haystack + (nPoint + nRep), p + nNeedle, nLen - nPoint - 1);
        strncpy(haystack + nPoint, rep, nRep);
        *(haystack + nLen + (nRep - nNeedle)) = 0x00;
        if(times == 1) break;
    }
    return (haystack);
}

char * api_strrep(char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
     return api_strrep_s(strstr, haystack, needle, rep, times);
}

char * api_strirep(char *haystack, const char *needle, const char *rep, const api_int32_t times)
{
    return api_strrep_s(api_stristr, haystack, needle, rep, times);
}

char * api_strcontain(char *haystack, const char *needle, const char *rep)
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

int api_url_encode(char *buf, const char *str)
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
        if (strchr(special, c)) {
            sprintf(tmp, "%%%02X", (unsigned char)c);
        } else if (c == ' ') /* blank */ {
            tmp[0] = '+';
        } else {
            if (c < 0) /* none ASCII character */ {
                sprintf(tmp, "%%%02X%%%02X", (unsigned char)src[i], (unsigned char)src[i + 1]);
                ++i;
            } else /* ASCII character */ {
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

int api_url_decode(char *buf, const char *str)
{
    static char blank[] = "";
    
    if (!buf || !str)
    {
        return 0;
    }
    while(*str)
    {
        if (*str == '%') {
            str++;
            if (*str) *buf = ascii_from_hex(*str++) * 16;
            if (*str) *buf = (*buf + ascii_from_hex(*str++));
            buf++;
        } else {
            if (*str == '+') {
                *buf++ = ' ';
                str++;
            } else {
                *buf++ = *str++;
            }
        }
    }
    
    *buf++ = 0;
    return api_strlen(buf);
}

//////////////////////////////////////////////////////////////////////////////////////////

char * api_ultoa(char *buf, const api_uint32_t val, const api_int32_t base, const api_int32_t bitlen)
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
    //不足位前面补0 NO.3
    tmpLen = (p - buf);
    if(tmpLen)
    {
        memset(buf, '0', tmpLen);
    }
    
    return buf;
}

//////////////////////////////////////////////////////////////////////////////////////////
#if 0
#if API_HAVE_ICONV

int api_iconv(const char* from_code, const char* from, const char* to_code, char* to, size_t iLength)
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
#ifdef API_LINUX /* only Linux */
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

#elif defined(API_WINDOWS)
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

int api_iconv(const char* from_code, const char* from, const char* to_code, char* to, size_t iLength)
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
#else
int api_iconv(const char* from_code, const char* from, const char* to_code, char* to, size_t iLength)
{
	return -1;
}

#endif

int A2U(char *strDestination, const char *strSource, int nLength)
{
	nLength = api_iconv("gb2312", (char*)strSource, "ucs-2be", strDestination, nLength);
	return nLength;
}

int U2A(char *strDestination, const char *strSource, int nLength)
{
	nLength = api_iconv("ucs-2be", (char*)strSource, "gb2312", strDestination, nLength);
	return nLength;
}
#endif
//////////////////////////////////////////////////////////////////////////////////////////

static char * api_os_locale_encoding (void)
{
#ifndef API_WINDOWS
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

const char * api_locale_encoding(void)
{   
    const char *charset = NULL;
    char *cs = api_os_locale_encoding();
#ifdef API_WINDOWS
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
const char * api_filename_path_get(char *path, size_t size, const char *filename)
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
const char * api_filename_ext_get(const char *filename)
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

api_status_t api_strtoff(api_off_t *offset, const char *nptr,
                                      char **endptr, int base)
{
    errno = 0;
    *offset = API_OFF_T_STRFN(nptr, endptr, base);
    return API_FROM_OS_ERROR(errno);
}

api_int64_t api_strtoi64(const char *nptr, char **endptr, int base)
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

api_int64_t api_atoi64(const char *buf)
{
    return api_strtoi64(buf, NULL, 10);
}

char * api_strfsize(api_off_t size, char *buf)
{
    const char ord[] = "KMGTPE";
    const char *o = ord;
    int remain;

    if (size < 0) {
        return strcpy(buf, "  - ");
    }
    if (size < 973) {
        if (api_snprintf(buf, 5, "%3d ", (int) size) < 0)
            return strcpy(buf, "****");
        return buf;
    }
    do {
        remain = (int)(size & 1023);
        size >>= 10;
        if (size >= 973) {
            ++o;
            continue;
        }
        if (size < 9 || (size == 9 && remain < 973)) {
            if ((remain = ((remain * 5) + 256) / 512) >= 10)
                ++size, remain = 0;
            if (api_snprintf(buf, 5, "%d.%d%c", (int) size, remain, *o) < 0)
                return strcpy(buf, "****");
            return buf;
        }
        if (remain >= 512)
            ++size;
        if (api_snprintf(buf, 5, "%3d%c", (int) size, *o) < 0)
            return strcpy(buf, "****");
        return buf;
    } while (1);
}

//////////////////////////////////////////////////////////////////////////////////////////

char * api_strtok(char *str, const char *sep, char **last)
{
    char *token;
    
    if (!str)           /* subsequent call */
        str = *last;    /* start where we left off */
    
    /* skip characters in sep (will terminate at '\0') */
    while (*str && strchr(sep, *str)) {
        ++str;
    }
    if (!*str) {          /* no more tokens */
        return NULL;
    }
    token = str;
    
    /* skip valid token characters to terminate token and
     * prepare for the next call (will terminate at '\0) 
     */
    *last = token + 1;
    while (**last && !strchr(sep, **last)) {
        ++*last;
    }
    if (**last) {
        **last = '\0';
        ++*last;
    }
    
    return token;
}

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * 按照前缀后缀截取中间部分的字符串
 * @param[out] buf, 缓冲区
 * @param[in] str, 字符串
 * @param[in] prefix, 前缀字符串
 * @param[in] suffix, 后缀字符串
 */
char * api_distill(char *buf, const char *str, const char *prefix, const char *suffix)
{
    const char *p1 = api_stristr(str, prefix);
    if(p1) {
        const char *p2 = NULL;
        int len = 0;
        p1 += api_strlen(prefix);
        p2 = api_stristr(p1, suffix);
        if(p2 == NULL) {
            len = api_strlen(p1);
        } else {
            len = p2 - p1;
        }
        strncpy(buf, p1, len);
        *(buf + len) = 0x00;
    } else {
        *buf = 0x00;
    }
    
    return buf;
}

/**
 * 分割字符串, 输出字符串二维数组
 * @param[out] buf, 缓冲区
 * @param[in] str, 字符串
 * @param[in] spliter, 前缀字符串
 * @param[in] pool, 内存池
 * @return 分割总数
 */
api_uint32_t api_split(char ***buf, const char *str, const char *spliter)
{
    const char *src = str;
    char *q = NULL;
    char *s = NULL;
    int argc = 0;
    
    if(api_strlen(str) == 0 || api_strlen(spliter) == 0)
    {
        return 0;
    }
    (*buf) = (char **)api_calloc(1, sizeof(char *) * 256);
    do
    {
        s = api_strtok(src, spliter, &q);
        if(s)
        {
            if(argc == 0)
            {
                (*buf) = (char **)api_malloc(sizeof(char *) * ++argc);
            }
            else
            {
                (*buf) = (char **)realloc((*buf), sizeof(char *) * (++argc + 1));
            }
            (*buf)[argc] = NULL;
            (*buf)[argc - 1] = strdup(s);
            *(*buf + argc++) = strdup(s);
            //*(*buf + argc++) = api_pstrdup(pool, s);
        }
        src = q;
    }while(api_strlen(src) > 0);
    
    return argc;
}

/**
 * 分割字符串, 并保存到hash tables
 * @param[out] tbl, hash tables
 * @param[in] str, 字符串
 * @param[in] spliter, 前缀字符串
 * @param[in] pool, 内存池
 * @return 分割总数
 */
api_uint32_t api_str2tbl(api_hashtable_t *tbl, const char *str, const char *spliter)
{
    char **buf = NULL;
    api_uint32_t c = api_split(&buf, str, spliter);
    api_uint32_t i = 0;
    api_uint32_t nCount = 0;
    
    if(c == 0)
    {
        return 0;
    }
    for(i = 0; i < c; i++)
    {
        char key[HUGE_STRING_LEN + 1];
        char value[HUGE_STRING_LEN + 1];
        char *p = api_stristr(buf[i], "=");
        
        memset(key, 0x00, sizeof(key));
        memset(value, 0x00, sizeof(value));
        
        if(p) {
            strncpy(key, buf[i], p - buf[i]);
            strncpy(value, p + 1, api_strlen(p));
        } else {
            strncpy(key, buf[i], api_strlen(buf[i]));
        }
        
        api_strtrim(key);
        api_strtrim(value);
        if(api_strlen(key) > 0)
        {
            nCount++;
            api_hashtable_insert(tbl, key, api_strlen(key), value, api_strlen(value));
        }
    }
    return nCount;
}


