/* Portions of this file are covered by */
/* -*- mode: c; c-file-style: "k&r" -*-

  strnatcmp.c -- Perform 'natural order' comparisons of strings in C.
  Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.
*/

#ifndef API_STRINGS_H
#define API_STRINGS_H
//////////////////////////////////////////////////////////////////////////////////////////

/**
 * @file api_strings.h
 * @brief API Strings library
 */

#include <api_errno.h>
#include <api_lib.h>
//#include <api_object.h>
//#include <api_tables.h>
#define API_WANT_IOVEC
#include <api_want.h>

#if API_HAVE_STDARG_H
#include <stdarg.h>
#endif

#if API_HAVE_ICONV_H
#include <iconv.h>
#endif
#if API_HAVE_SYS_ICONV_H
#include <sys/iconv.h>
#endif

#if API_HAVE_STRING_H
#include <string.h>
#include <stdio.h> //snprintf()
#endif
//////////////////////////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/* A couple of prototypes for functions in case some platform doesn't 
 * have it
 */
#if (!API_HAVE_STRCASECMP) && (API_HAVE_STRICMP) 
#define strcasecmp(s1, s2) stricmp(s1, s2)
#elif (!API_HAVE_STRCASECMP)
int strcasecmp(const char *a, const char *b);
#endif

#if (!API_HAVE_STRNCASECMP) && (API_HAVE_STRNICMP)
#define strncasecmp(s1, s2, n) strnicmp(s1, s2, n)
#elif (!API_HAVE_STRNCASECMP)
int strncasecmp(const char *a, const char *b, size_t n);
#endif

/**
 * @defgroup api_ctype ctype functions
 * These macros allow correct support of 8-bit characters on systems which
 * support 8-bit characters.  Pretty dumb how the cast is required, but
 * that's legacy libc for ya.  These new macros do not support EOF like
 * the standard macros do.  Tough.
 * @{
 */

/** @see isalnum */
#define api_isalnum(c) (isalnum(((unsigned char)(c))))
/** @see isalpha */
#define api_isalpha(c) (isalpha(((unsigned char)(c))))
/** @see iscntrl */
#define api_iscntrl(c) (iscntrl(((unsigned char)(c))))
/** @see isdigit */
#define api_isdigit(c) (isdigit(((unsigned char)(c))))
/** @see isgraph */
#define api_isgraph(c) (isgraph(((unsigned char)(c))))
/** @see islower*/
#define api_islower(c) (islower(((unsigned char)(c))))
/** @see isascii */
#ifdef isascii
#define api_isascii(c) (isascii(((unsigned char)(c))))
#else
#define api_isascii(c) (((c) & ~0x7f)==0)
#endif
/** @see isprint */
#define api_isprint(c) (isprint(((unsigned char)(c))))
/** @see ispunct */
#define api_ispunct(c) (ispunct(((unsigned char)(c))))
/** @see isspace */
#define api_isspace(c) (isspace(((unsigned char)(c))))
/** @see isupper */
#define api_isupper(c) (isupper(((unsigned char)(c))))
/** @see isxdigit */
#define api_isxdigit(c) (isxdigit(((unsigned char)(c))))
/** @see tolower */
#define api_tolower(c) (tolower(((unsigned char)(c))))
/** @see toupper */
#define api_toupper(c) (toupper(((unsigned char)(c))))

/** @} */

/**
 * @defgroup api_strings String routines
 * @ingroup API 
 * @{
 */

/**
 * 取字符串长度
 * @param[in] s 字符串首地址
 * @return 字符串长度
 */
#define api_strlen(s) ((s) == NULL ? 0 : strlen(s))

/**
 * Do a natural order comparison of two strings.
 * @param a The first string to compare
 * @param b The second string to compare
 * @return Either <0, 0, or >0.  If the first string is less than the second
 *          this returns <0, if they are equivalent it returns 0, and if the
 *          first string is greater than second string it retuns >0.
 */
API_DECLARE(int) api_strnatcmp(char const *a, char const *b);

/**
 * Do a natural order comparison of two strings ignoring the case of the 
 * strings.
 * @param a The first string to compare
 * @param b The second string to compare
 * @return Either <0, 0, or >0.  If the first string is less than the second
 *         this returns <0, if they are equivalent it returns 0, and if the
 *         first string is greater than second string it retuns >0.
 */
API_DECLARE(int) api_strnatcasecmp(char const *a, char const *b);

/**
 * Copy up to dst_size characters from src to dst; does not copy
 * past a NUL terminator in src, but always terminates dst with a NUL
 * regardless.
 * @param dst The destination string
 * @param src The source string
 * @param dst_size The space available in dst; dst always receives
 *                 NUL termination, so if src is longer than
 *                 dst_size, the actual number of characters copied is
 *                 dst_size - 1.
 * @return Pointer to the NUL terminator of the destination string, dst
 * @remark
 * <PRE>
 * Note the differences between this function and strncpy():
 *  1) strncpy() doesn't always NUL terminate; api_cpystrn() does.
 *  2) strncpy() pads the destination string with NULs, which is often 
 *     unnecessary; api_cpystrn() does not.
 *  3) strncpy() returns a pointer to the beginning of the dst string;
 *     api_cpystrn() returns a pointer to the NUL terminator of dst, 
 *     to allow a check for truncation.
 * </PRE>
 */
API_DECLARE(char *) api_cpystrn(char *dst, const char *src, size_t dst_size);

/**
 * Strip spaces from a string
 * @param dest The destination string.  It is okay to modify the string
 *             in place.  Namely dest == src
 * @param src The string to rid the spaces from.
 * @return The destination string, dest.
 */
API_DECLARE(char *) api_collapse_spaces(char *dest, const char *src);

/**
 * Split a string into separate null-terminated tokens.  The tokens are 
 * delimited in the string by one or more characters from the sep
 * argument.
 * @param str The string to separate; this should be specified on the
 *            first call to api_strtok() for a given string, and NULL
 *            on subsequent calls.
 * @param sep The set of delimiters
 * @param last Internal state saved by api_strtok() between calls.
 * @return The next token from the string
 */
API_DECLARE(char *) api_strtok(char *str, const char *sep, char **last);

/**
 * @defgroup API_Strings_Snprintf snprintf implementations
 * @warning
 * These are snprintf implementations based on api_vformatter().
 *
 * Note that various standards and implementations disagree on the return
 * value of snprintf, and side-effects due to %n in the formatting string.
 * api_snprintf (and api_vsnprintf) behaves as follows:
 *
 * Process the format string until the entire string is exhausted, or
 * the buffer fills.  If the buffer fills then stop processing immediately
 * (so no further %n arguments are processed), and return the buffer
 * length.  In all cases the buffer is NUL terminated. It will return the
 * number of characters inserted into the buffer, not including the
 * terminating NUL. As a special case, if len is 0, api_snprintf will
 * return the number of characters that would have been inserted if
 * the buffer had been infinite (in this case, *buffer can be NULL)
 *
 * In no event does api_snprintf return a negative number.
 * @{
 */

#if 0
/**
 * snprintf routine based on api_vformatter.  This means it understands the
 * same extensions.
 * @param buf The buffer to write to
 * @param len The size of the buffer
 * @param format The format string
 * @param ... The arguments to use to fill out the format string.
 */
API_DECLARE_NONSTD(int) api_snprintf(char *buf, api_size_t len,
                                     const char *format, ...)
        __attribute__((format(printf,3,4)));

/**
 * vsnprintf routine based on api_vformatter.  This means it understands the
 * same extensions.
 * @param buf The buffer to write to
 * @param len The size of the buffer
 * @param format The format string
 * @param ap The arguments to use to fill out the format string.
 */
API_DECLARE(int) api_vsnprintf(char *buf, api_size_t len, const char *format,
                               va_list ap);
#else
#ifdef __API_WINDOWS__
#define api_snprintf _snprintf
#define api_vsnprintf _vsnprintf
#else
#define api_snprintf snprintf
#define api_vsnprintf vsnprintf
#endif
#endif
/** @} */

/**
 * Convert a numeric string into an api_off_t numeric value.
 * @param offset The value of the parsed string.
 * @param buf The string to parse. It may contain optional whitespace,
 *   followed by an optional '+' (positive, default) or '-' (negative)
 *   character, followed by an optional '0x' prefix if base is 0 or 16,
 *   followed by numeric digits appropriate for base.
 * @param end A pointer to the end of the valid character in buf. If
 *   not NULL, it is set to the first invalid character in buf.
 * @param base A numeric base in the range between 2 and 36 inclusive,
 *   or 0.  If base is zero, buf will be treated as base ten unless its
 *   digits are prefixed with '0x', in which case it will be treated as
 *   base 16.
 */
API_DECLARE(api_status_t) api_strtoff(api_off_t *offset, const char *buf, 
                                      char **end, int base);

/**
 * parse a numeric string into a 64-bit numeric value
 * @param buf The string to parse. It may contain optional whitespace,
 *   followed by an optional '+' (positive, default) or '-' (negative)
 *   character, followed by an optional '0x' prefix if base is 0 or 16,
 *   followed by numeric digits appropriate for base.
 * @param end A pointer to the end of the valid character in buf. If
 *   not NULL, it is set to the first invalid character in buf.
 * @param base A numeric base in the range between 2 and 36 inclusive,
 *   or 0.  If base is zero, buf will be treated as base ten unless its
 *   digits are prefixed with '0x', in which case it will be treated as
 *   base 16.
 * @return The numeric value of the string.  On overflow, errno is set
 * to ERANGE.
 */
API_DECLARE(api_int64_t) api_strtoi64(const char *buf, char **end, int base);

/**
 * parse a base-10 numeric string into a 64-bit numeric value.
 * Equivalent to api_strtoi64(buf, (char**)NULL, 10).
 * @param buf The string to parse
 * @return The numeric value of the string
 */
API_DECLARE(api_int64_t) api_atoi64(const char *buf);

/**
 * Format a binary size (magnitiudes are 2^10 rather than 10^3) from an api_off_t,
 * as bytes, K, M, T, etc, to a four character compacted human readable string.
 * @param size The size to format
 * @param buf The 5 byte text buffer (counting the trailing null)
 * @return The buf passed to api_strfsize()
 * @remark All negative sizes report '  - ', api_strfsize only formats positive values.
 */
API_DECLARE(char *) api_strfsize(api_off_t size, char *buf);

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * 转换字符串从大写到小写
 * @param[in] str 字符串
 * @return 大写字符串
 */
API_DECLARE(char *) api_strtolc(char *str);

/**
 * 转换字符串从小写到大写
 * @param[in] str 源字符串
 * @return 大写的字符串
 */
API_DECLARE(char *) api_strtouc(char *str);

/**
 * 将字符数组转换成十六进制字符串
 * @param[out] strHex 十六进制字符串
 * @param[in] str 字符数组
 * @param[in] len 字符数组长度
 */
API_DECLARE(char *) api_mem2hex(char *strHex, const char *str, const api_int32_t len);

/**
 * 将十六进制字符串转换成字节数组
 * @param[out] str ASCII字符串
 * @param[in] strHex 十六进制字符串
 * @param[in] len  十六进制字符串长度
 * @return 字节数组长度
 */
API_DECLARE(int) api_hex2mem(char  *str, const char  *strHex, const api_int32_t len);

/**
 * 连接字符串
 * @param[out] buffer 目标字符串
 * @param[in] lpszFormat 格式化字符串
 * @param[in] ... 输出的信息内容
 */
API_DECLARE_NONSTD(char *) api_strcat(char *buffer, const char * lpszFormat, ...);

/**
 * 截取字符串
 * @param[out] str 字符串
 * @param[in] src 源字符串
 * @param[in] ... 起始位置和长度
 */
API_DECLARE_NONSTD(char *) api_substr(char *str, const char *src, ...);

/**
 * 查找字符串
 * @param[in] haystack 目标字符串
 * @param[in] needle   搜索子串
 */
#define api_strstr(s1, s2) (((s1) == NULL || (s2) == NULL) ? NULL : strstr((s1), (s2)))

/**
 * 忽略大小写敏感查找字符串
 * @param[in] haystack 目标字符串
 * @param[in] needle   搜索子串
 */
API_DECLARE_NONSTD(char *) api_stristr(const char* haystack, const char *needle);

/**
 * 去掉首尾空格或制表符
 * @param[in] pstr 字节数组或字符串指针
 * @return 去掉左右空格制表符回车换行等符号的字符型指针
 */
API_DECLARE(char *) api_strtrim(char *pstr);

/**
 * 替换字符串
 * @param[out] haystack 目标字符串
 * @param[in] needle 搜索子串
 * @param[in] rep 替代的字符串
 * @param[in] times 替换次数, 默认1次
 */
API_DECLARE(char *) api_strrep(char* haystack, const char *needle, const char *rep, const api_int32_t times);

/**
 * 忽略大小写敏感替换字符串
 * @param[out] haystack 目标字符串
 * @param[in] needle 搜索子串
 * @param[in] rep 替代的字符串
 * @param[in] times 替换次数, 默认1次
 */
API_DECLARE(char *) api_strirep(char *haystack, const char *needle, const char *rep, const api_int32_t times);

/**
 * 替换字符串
 * @param[out] haystack 目标字符串
 * @param[in] needle 搜索子串, 格式${needle}
 * @param[in] rep  替代的字符串
 */
API_DECLARE(char *) api_strcontain(char *haystack, const char *needle, const char *rep);

/**
 * URL编码
 * @param[out] buf 输出缓冲区
 * @param[in] str url字符串
 * @return 返回编码后的字符串长度
 */
API_DECLARE(int) api_url_encode(char *buf, const char *str);

/**
 * URL解码
 * @param[out] buf 输出缓冲区
 * @param[in] str url字符串
 * @return 返回编码后的字符串长度
 */
API_DECLARE(int) api_url_decode(char *buf, const char *str);

/**
 * 整数进制转换成字符串
 * @param[out] buf 字符串指针
 * @param[in] val 四个字节无符号整数
 * @param[in] base 基数
 * @param[in] bitlen 位长
 */
API_DECLARE(char *) api_ultoa(char *buf, const api_uint32_t val, const api_int32_t base, const api_int32_t bitlen);

/**
 * 编码转换
 * @param[in] from_code 原码编码格式
 * @param[in] from 原码
 * @param[in] to_code 目标编码格式
 * @param[in] to 目标编码缓冲区
 * @param[in] iLength 原码长度
 */
API_DECLARE(int) api_iconv(const char *from_code, const char *from, const char *to_code, char *to, size_t iLength);

/**
 * 从GB2312编码转换成UCS2编码
 * @param[in] strDestination 目的字符串
 * @param[in] strSource 源字符串
 * @param[in] nLength GB2312源字符串长度
 */
API_DECLARE(int) A2U(char *strDestination, const char *strSource, int nLength);

/**
 * 从UCS2编码转换成GB2312编码
 * @param[out] strDestination 目的字符串
 * @param[in] strSource 源字符串
 * @param[in] nLength UCS2编码源字符串长度
 */
API_DECLARE(int) U2A(char *strDestination, const char *strSource, int nLength);

/**
 * 获取本地字符集
 * @return 本地系统的字符集
 */
API_DECLARE(const char *) api_locale_encoding(void);

/**
 * 获得文件路径
 * @param[in] path 路径
 * @param[in] size 尺寸
 * @param[in] filename 文件名
 * @return 不含路径的文件名
 */
API_DECLARE(const char *) api_filename_path_get(char *path, size_t size, const char *filename);

/**
 * 获得文件扩展名
 * @param[in] filename 文件名
 * @return 文件扩展名
 */
API_DECLARE(const char *) api_filename_ext_get(const char *filename);

/**
 * 按照前缀后缀截取中间部分的字符串
 * @param[out] buf 缓冲区
 * @param[in] str 字符串
 * @param[in] prefix 前缀字符串
 * @param[in] suffix 后缀字符串
 */
API_DECLARE(char *) api_distill(char *buf, const char *str, const char *prefix, const char *suffix);

/**
 * 分割字符串, 输出字符串二维数组
 * @param[out] buf 缓冲区
 * @param[in] str 字符串
 * @param[in] spliter 前缀字符串
 * @param[in] pool 内存池
 * @return 分割总数
 */
//API_DECLARE(api_uint32_t) api_split(char ***buf, const char *str, const char *spliter, api_pool_t *pool);

/**
 * 分割字符串, 并保存到hash tables
 * @param[out] tbl hash tables
 * @param[in] str 字符串
 * @param[in] spliter 前缀字符串
 * @param[in] pool 内存池
 * @return 分割总数
 */
//API_DECLARE(api_uint32_t) api_str2tbl(api_table_t *tbl, const char *str, const char *spliter, api_pool_t *pool);

/** @} */

#ifdef __cplusplus
}
#endif

//////////////////////////////////////////////////////////////////////////////////////////
#endif  /* !API_STRINGS_H */
