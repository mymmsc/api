#ifndef API_LIB_H
#define API_LIB_H

/**
 * @file api_lib.h
 * This is collection of oddballs that didn't fit anywhere else,
 * and might move to more appropriate headers with the release
 * of API 1.0.
 * @brief API general purpose library routines
 */

#include "api.h"
#include "api/core.h"
#include "api/errno.h"
#include "api/memory.h"
#include "api/strings.h"

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

/** A constant representing a 'large' string. */
#define HUGE_STRING_LEN 8192

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
API_DECLARE(const char *) api_filepath_name_get(const char *pathname);

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

#endif	/* ! API_LIB_H */
