#ifndef API_STRINGS_H
#define API_STRINGS_H
//////////////////////////////////////////////////////////////////////////////////////////

/**
 * @file api_strings.h
 * @brief API Strings library
 */

#include "api/errno.h"
#include "api/lib.h"
#define API_WANT_IOVEC
#include "api/want.h"

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

/* --------------------------------------------------------------------- */

#ifndef API_HAVE_EXPLICIT_BZERO
API void explicit_bzero(void *p, size_t n);
#endif
/* --------------------------------------------------------------------- */


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

/* --------------------------------------------------------------------- */


/**
 * @defgroup api_strings String routines
 * @ingroup API 
 * @{
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
API int api_strnatcmp(char const *a, char const *b);

/**
 * Do a natural order comparison of two strings ignoring the case of the 
 * strings.
 * @param a The first string to compare
 * @param b The second string to compare
 * @return Either <0, 0, or >0.  If the first string is less than the second
 *         this returns <0, if they are equivalent it returns 0, and if the
 *         first string is greater than second string it retuns >0.
 */
API int api_strnatcasecmp(char const *a, char const *b);

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
API char * api_cpystrn(char *dst, const char *src, size_t dst_size);

/**
 * Strip spaces from a string
 * @param dest The destination string.  It is okay to modify the string
 *             in place.  Namely dest == src
 * @param src The string to rid the spaces from.
 * @return The destination string, dest.
 */
API char * api_collapse_spaces(char *dest, const char *src);

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
API char * api_strtok(char *str, const char *sep, char **last);

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
#ifdef API_WINDOWS
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
API api_status_t api_strtoff(api_off_t *offset, const char *buf, 
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
API api_int64_t api_strtoi64(const char *buf, char **end, int base);

/**
 * parse a base-10 numeric string into a 64-bit numeric value.
 * Equivalent to api_strtoi64(buf, (char**)NULL, 10).
 * @param buf The string to parse
 * @return The numeric value of the string
 */
API api_int64_t api_atoi64(const char *buf);

/**
 * Format a binary size (magnitiudes are 2^10 rather than 10^3) from an api_off_t,
 * as bytes, K, M, T, etc, to a four character compacted human readable string.
 * @param size The size to format
 * @param buf The 5 byte text buffer (counting the trailing null)
 * @return The buf passed to api_strfsize()
 * @remark All negative sizes report '  - ', api_strfsize only formats positive values.
 */
API char * api_strfsize(api_off_t size, char *buf);

//////////////////////////////////////////////////////////////////////////////////////////

typedef struct {
    size_t   len;
    uint8_t *data;
} api_str_t;

#define api_string(str)        { sizeof(str) - 1, (uint8_t *) str }
#define api_null_string        { 0, NULL }
#define api_str_set(str, text) (str)->len = sizeof(text) - 1; (str)->data = (uint8_t *) text
#define api_str_null(str)      (str)->len = 0; (str)->data = NULL

API char * api_str2rep(char *src, char * (*value_cb)(api_str_t *key, void *, size_t *), void *data);

API char * api_strtolc(char *str);

API char * api_strtouc(char *str);

API char * api_mem2hex(char *strHex, const char *str, const api_int32_t len);

API int api_hex2mem(char *str, const char *hex, const api_int32_t len);

API char * api_strcat(char *buffer, const char *format, ...);


API char * api_substr(char *str, const char *src, ...);

#define api_strstr(s1, s2) (((s1) == NULL || (s2) == NULL) ? NULL : strstr((s1), (s2)))

API char * api_stristr(const char* haystack, const char *needle);

API char * api_strtrim(char *pstr);

API char * api_strrep(char* haystack, const char *needle, const char *rep, const api_int32_t times);

API char * api_strirep(char *haystack, const char *needle, const char *rep, const api_int32_t times);

/**
 * ${needle}
 */
API char * api_strcontain(char *haystack, const char *needle, const char *rep);

API int api_url_encode(char *buf, const char *str);

API int api_url_decode(char *buf, const char *str);

API char * api_ultoa(char *buf, const api_uint32_t val, const api_int32_t base, const api_int32_t bitlen);

API int api_iconv(const char *from_code, const char *from, const char *to_code, char *to, size_t iLength);

API int A2U(char *strDestination, const char *strSource, int nLength);

API int U2A(char *strDestination, const char *strSource, int nLength);

API const char * api_locale_encoding(void);

API const char * api_filename_path_get(char *path, size_t size, const char *filename);

API const char * api_filename_ext_get(const char *filename);

API char * api_distill(char *buf, const char *str, const char *prefix, const char *suffix);

/**
 * ·Ö¸î×Ö·û´®, Êä³ö×Ö·û´®¶þÎ¬Êý×é
 * @param[out] buf, »º³åÇø
 * @param[in] str, ×Ö·û´®
 * @param[in] spliter, Ç°×º×Ö·û´®
 * @param[in] pool, ÄÚ´æ³Ø
 * @return ·Ö¸î×ÜÊý
 */
API api_uint32_t api_split(char ***buf, const char *str, const char *spliter);

#include "api/hashtable.h"
/**
 * ·Ö¸î×Ö·û´®, ²¢±£´æµ½hash tables
 * @param[out] tbl, hash tables
 * @param[in] str, ×Ö·û´®
 * @param[in] spliter, Ç°×º×Ö·û´®
 * @param[in] pool, ÄÚ´æ³Ø
 * @return ·Ö¸î×ÜÊý
 */
API api_uint32_t api_str2tbl(api_hashtable_t *tbl, const char *str, const char *spliter);


/** @} */

#ifdef __cplusplus
}
#endif

//////////////////////////////////////////////////////////////////////////////////////////
#endif  /* !API_STRINGS_H */
