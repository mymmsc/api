#ifndef __API_LIB_H_INCLUDED__
#define __API_LIB_H_INCLUDED__

/**
 * @file api_lib.h
 * This is collection of oddballs that didn't fit anywhere else,
 * and might move to more appropriate headers with the release
 * of API 1.0.
 * @brief API general purpose library routines
 */

#include <api.h>
#include <api/core.h>
#include <api/errno.h>
#include <api/memory.h>
#include <api/strings.h>

#if API_HAVE_CTYPE_H
#include <ctype.h>
#endif
#if API_HAVE_STDARG_H
#include <stdarg.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/**
 * @defgroup api_lib General Purpose Library Routines
 * @ingroup API 
 * This is collection of oddballs that didn't fit anywhere else,
 * and might move to more appropriate headers with the release
 * of API 1.0.
 * @{
 */

/** for -Werror */
extern int unused_result;

/** A constant representing a 'large' string. */
#define HUGE_STRING_LEN 8192

/** @see api_vformatter_buff_t */
typedef struct api_vformatter_buff_t api_vformatter_buff_t;

/**
 * Structure used by the variable-formatter routines.
 */
struct api_vformatter_buff_t {
    /** The current position */
    char *curpos;
    /** The end position of the format string */
    char *endpos;
};

/**
 * return the final element of the pathname
 * @param pathname The path to get the final element of
 * @return the final element of the path
 * @remark
 * <PRE>
 * For example:
 *                 "/foo/bar/gum"    -> "gum"
 *                 "/foo/bar/gum/"   -> ""
 *                 "gum"             -> "gum"
 *                 "bs\\path\\stuff" -> "stuff"
 * </PRE>
 */
API const char * api_filepath_name_get(const char *pathname);

/**
 * api_vformatter() is a generic printf-style formatting routine
 * with some extensions.
 * @param flush_func The function to call when the buffer is full
 * @param c The buffer to write to
 * @param fmt The format string
 * @param ap The arguments to use to fill out the format string.
 *
 * @remark
 * <PRE>
 * The extensions are:
 *
 * - %%pA takes a struct in_addr *, and prints it as a.b.c.d
 * - %%pI takes an api_sockaddr_t * and prints it as a.b.c.d:port or
 * \[ipv6-address\]:port
 * - %%pT takes an api_os_thread_t * and prints it in decimal
 * ('0' is printed if !API_HAS_THREADS)
 * - %%pt takes an api_os_thread_t * and prints it in hexadecimal
 * ('0' is printed if !API_HAS_THREADS)
 * - %%pm takes an api_status_t * and prints the appropriate error
 * string (from apr_strerror) corresponding to that error code.
 * - %%pp takes a void * and outputs it in hex
 * - %%pB takes a apr_uint32_t * as bytes and outputs it's apr_strfsize
 * - %%pF same as above, but takes a api_off_t *
 * - %%pS same as above, but takes a api_size_t *
 *
 * %%pA, %%pI, %%pT, %%pp are available from API 1.0.0 onwards (and in 0.9.x).
 * %%pt is only available from API 1.2.0 onwards.
 * %%pm, %%pB, %%pF and %%pS are only available from API 1.3.0 onwards.
 *
 * The %%p hacks are to force gcc's printf warning code to skip
 * over a pointer argument without complaining.  This does
 * mean that the ANSI-style %%p (output a void * in hex format) won't
 * work as expected at all, but that seems to be a fair trade-off
 * for the increased robustness of having printf-warnings work.
 *
 * Additionally, apr_vformatter allows for arbitrary output methods
 * using the api_vformatter_buff and flush_func.
 *
 * The apr_vformatter_buff has two elements curpos and endpos.
 * curpos is where apr_vformatter will write the next byte of output.
 * It proceeds writing output to curpos, and updating curpos, until
 * either the end of output is reached, or curpos == endpos (i.e. the
 * buffer is full).
 *
 * If the end of output is reached, apr_vformatter returns the
 * number of bytes written.
 *
 * When the buffer is full, the flush_func is called.  The flush_func
 * can return -1 to indicate that no further output should be attempted,
 * and apr_vformatter will return immediately with -1.  Otherwise
 * the flush_func should flush the buffer in whatever manner is
 * appropriate, re api_pool_t nitialize curpos and endpos, and return 0.
 *
 * Note that flush_func is only invoked as a result of attempting to
 * write another byte at curpos when curpos >= endpos.  So for
 * example, it's possible when the output exactly matches the buffer
 * space available that curpos == endpos will be true when
 * api_vformatter returns.
 *
 * api_vformatter does not call out to any other code, it is entirely
 * self-contained.  This allows the callers to do things which are
 * otherwise "unsafe".  For example, apr_psprintf uses the "scratch"
 * space at the unallocated end of a block, and doesn't actually
 * complete the allocation until apr_vformatter returns.  apr_psprintf
 * would be completely broken if apr_vformatter were to call anything
 * that used this same pool.  Similarly http_bprintf() uses the "scratch"
 * space at the end of its output buffer, and doesn't actually note
 * that the space is in use until it either has to flush the buffer
 * or until apr_vformatter returns.
 * </PRE>
 */
API int api_vformatter(int (*flush_func)(api_vformatter_buff_t *b),
			        api_vformatter_buff_t *c, const char *fmt,
			        va_list ap);


/**
 * api_killpg
 * Small utility macros to make things easier to read.  Not usually a
 * goal, to be sure..
 */

#ifdef WIN32
#define api_killpg(x, y)
#else /* WIN32 */
#ifdef NO_KILLPG
#define api_killpg(x, y)        (kill (-(x), (y)))
#else /* NO_KILLPG */
#define api_killpg(x, y)        (killpg ((x), (y)))
#endif /* NO_KILLPG */
#endif /* WIN32 */

/** @} */

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
#if API_CHARSET_EBCDIC || defined(isascii)
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

/** Free memory space */
#define SAFE_FREE(x) do { if ((x) != NULL) {free(x); x = NULL;} } while(0)

/** Zero a structure */
#define ZERO_STRUCT(x) memset((char *)&(x), 0, sizeof(x))

/** Zero a structure given a pointer to the structure */
#define ZERO_STRUCTP(x) do { if ((x) != NULL) memset((char *)(x), 0, sizeof(*(x))); } while(0)

/** Get the size of an array */
#define ARRAY_SIZE(a) (sizeof(a)/sizeof(a[0]))

#ifdef __cplusplus
}
#endif

#endif	/*! __API_LIB_H_INCLUDED__ */
