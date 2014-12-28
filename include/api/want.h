#ifndef API_WANT_H
#define API_WANT_H

#include <api.h>        /* configuration data */

/**
 * @file api_want.h
 * @brief API Standard Headers Support
 *
 * <PRE>
 * Features:
 *
 *   API_WANT_STRFUNC:  strcmp, strcat, strcpy, etc
 *   API_WANT_MEMFUNC:  memcmp, memcpy, etc
 *   API_WANT_STDIO:    <stdio.h> and related bits
 *   API_WANT_IOVEC:    struct iovec
 *   API_WANT_BYTEFUNC: htons, htonl, ntohl, ntohs
 *
 * Typical usage:
 *
 *   \#define API_WANT_STRFUNC
 *   \#define API_WANT_MEMFUNC
 *   \#include "api_want.h"
 *
 * The appropriate headers will be included.
 *
 * Note: it is safe to use this in a header (it won't interfere with other
 *       headers' or source files' use of api_want.h)
 * </PRE>
 */

/* --------------------------------------------------------------------- */

#ifdef API_WANT_STRFUNC

#if API_HAVE_STRING_H
#include <string.h>
#endif
#if API_HAVE_STRINGS_H
#include <strings.h>
#endif

#undef API_WANT_STRFUNC
#endif

/* --------------------------------------------------------------------- */

#ifdef API_WANT_MEMFUNC

#if API_HAVE_STRING_H
#include <string.h>
#endif

#undef API_WANT_MEMFUNC
#endif

/* --------------------------------------------------------------------- */

#ifdef API_WANT_STDIO

#if API_HAVE_STDIO_H
#include <stdio.h>
#endif

#undef API_WANT_STDIO
#endif

/* --------------------------------------------------------------------- */

#ifdef API_WANT_IOVEC

#if API_HAVE_IOVEC

#if API_HAVE_SYS_UIO_H
#include <sys/uio.h>
#endif

#else

#ifndef API_IOVEC_DEFINED
#define API_IOVEC_DEFINED
struct iovec
{
    void *iov_base;
    size_t iov_len;
};
#endif /* !API_IOVEC_DEFINED */

#endif /* API_HAVE_IOVEC */

#undef API_WANT_IOVEC
#endif

/* --------------------------------------------------------------------- */

#ifdef API_WANT_BYTEFUNC

/* Single Unix says they are in arpa/inet.h.  Linux has them in
 * netinet/in.h.  FreeBSD has them in arpa/inet.h but requires that
 * netinet/in.h be included first.
 */
#if API_HAVE_NETINET_IN_H
#include <netinet/in.h>
#endif
#if API_HAVE_ARPA_INET_H
#include <arpa/inet.h>
#endif

#undef API_WANT_BYTEFUNC
#endif

/* --------------------------------------------------------------------- */
#endif /* ! API_WANT_H */