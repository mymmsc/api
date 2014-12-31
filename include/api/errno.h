#ifndef __API_ERRNO_H_INCLUDED__
#define __API_ERRNO_H_INCLUDED__

/**
 * @file api_errno.h
 * @brief API Error Codes
 */

#include <api.h>

#if API_HAVE_ERRNO_H
#include <errno.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/**
 * @defgroup api_errno Error Codes
 * @ingroup API 
 * @{
 */

/**
 * Type for specifying an error or status code.
 */
typedef int api_status_t;
#define api_errno  errno

#define API_ERROR  -1

/**
 * Return a human readable string describing the specified error.
 * @param statcode The error code to get a string for.
 * @param buf A buffer to hold the error string.
 * @param bufsize Size of the buffer to hold the string.
 */
API_DECLARE(char *) api_strerror(api_status_t statcode, char *buf, 
                                 api_size_t bufsize);

#if defined(DOXYGEN)
/**
 * @def API_FROM_OS_ERROR(os_err_type syserr)
 * Fold a platform specific error into an api_status_t code.
 * @return api_status_t
 * @param e The platform os error code.
 * @warning  macro implementation; the syserr argument may be evaluated
 *      multiple times.
 */
#define API_FROM_OS_ERROR(e) (e == 0 ? API_SUCCESS : e + API_OS_START_SYSERR)

/**
 * @def API_TO_OS_ERROR(api_status_t statcode)
 * @return os_err_type
 * Fold an api_status_t code back to the native platform defined error.
 * @param e The api_status_t folded platform os error code.
 * @warning  macro implementation; the statcode argument may be evaluated
 *      multiple times.  If the statcode was not created by api_get_os_error 
 *      or API_FROM_OS_ERROR, the results are undefined.
 */
#define API_TO_OS_ERROR(e) (e == 0 ? API_SUCCESS : e - API_OS_START_SYSERR)

/** @def api_get_os_error()
 * @return api_status_t the last platform error, folded into api_status_t, on most platforms
 * @remark This retrieves errno, or calls a GetLastError() style function, and
 *      folds it with API_FROM_OS_ERROR.  Some platforms (such as OS2) have no
 *      such mechanism, so this call may be unsupported.  Do NOT use this
 *      call for socket errors from socket, send, recv etc!
 */

/** @def api_set_os_error(e)
 * Reset the last platform error, unfolded from an api_status_t, on some platforms
 * @param e The OS error folded in a prior call to API_FROM_OS_ERROR()
 * @warning This is a macro implementation; the statcode argument may be evaluated
 *      multiple times.  If the statcode was not created by api_get_os_error
 *      or API_FROM_OS_ERROR, the results are undefined.  This macro sets
 *      errno, or calls a SetLastError() style function, unfolding statcode
 *      with API_TO_OS_ERROR.  Some platforms (such as OS2) have no such
 *      mechanism, so this call may be unsupported.
 */

/** @def api_get_netos_error()
 * Return the last socket error, folded into api_status_t, on all platforms
 * @remark This retrieves errno or calls a GetLastSocketError() style function,
 *      and folds it with API_FROM_OS_ERROR.
 */

/** @def api_set_netos_error(e)
 * Reset the last socket error, unfolded from an api_status_t
 * @param e The socket error folded in a prior call to API_FROM_OS_ERROR()
 * @warning This is a macro implementation; the statcode argument may be evaluated
 *      multiple times.  If the statcode was not created by api_get_os_error
 *      or API_FROM_OS_ERROR, the results are undefined.  This macro sets
 *      errno, or calls a WSASetLastError() style function, unfolding 
 *      socketcode with API_TO_OS_ERROR.
 */

#endif /* defined(DOXYGEN) */

/**
 * API_OS_START_ERROR is where the API specific error values start.
 */
#define API_OS_START_ERROR     20000
/**
 * API_OS_ERRSPACE_SIZE is the maximum number of errors you can fit
 *    into one of the error/status ranges below -- except for
 *    API_OS_START_USERERR, which see.
 */
#define API_OS_ERRSPACE_SIZE 50000
/**
 * API_UTIL_ERRSPACE_SIZE is the size of the space that is reserved for
 * use within api-util. This space is reserved above that used by API
 * internally.
 * @note This number MUST be smaller than API_OS_ERRSPACE_SIZE by a
 *       large enough amount that API has sufficient room for its
 *       codes.
 */
#define API_UTIL_ERRSPACE_SIZE 20000
/**
 * API_OS_START_STATUS is where the API specific status codes start.
 */
#define API_OS_START_STATUS    (API_OS_START_ERROR + API_OS_ERRSPACE_SIZE)
/**
 * API_UTIL_START_STATUS is where API-Util starts defining its
 * status codes.
 */
#define API_UTIL_START_STATUS   (API_OS_START_STATUS + \
                           (API_OS_ERRSPACE_SIZE - API_UTIL_ERRSPACE_SIZE))
/**
 * API_OS_START_USERERR are reserved for applications that use API that
 *     layer their own error codes along with API's.  Note that the
 *     error immediately following this one is set ten times farther
 *     away than usual, so that users of api have a lot of room in
 *     which to declare custom error codes.
 *
 * In general applications should try and create unique error codes. To try
 * and assist in finding suitable ranges of numbers to use, the following
 * ranges are known to be used by the listed applications. If your 
 * application defines error codes please advise the range of numbers it
 * uses to dev@api.apache.org for inclusion in this list.
 *
 * Ranges shown are in relation to API_OS_START_USERERR
 *
 * Subversion - Defined ranges, of less than 100, at intervals of 5000
 *              starting at an offset of 5000, e.g.
 *               +5000 to 5100,  +10000 to 10100
 *
 * Apache HTTPD - +2000 to 2999
 */
#define API_OS_START_USERERR    (API_OS_START_STATUS + API_OS_ERRSPACE_SIZE)
/**
 * API_OS_START_USEERR is obsolete, defined for compatibility only.
 * Use API_OS_START_USERERR instead.
 */
#define API_OS_START_USEERR     API_OS_START_USERERR
/**
 * API_OS_START_CANONERR is where API versions of errno values are defined
 *     on systems which don't have the corresponding errno.
 */
#define API_OS_START_CANONERR  (API_OS_START_USERERR \
                                 + (API_OS_ERRSPACE_SIZE * 10))
/**
 * API_OS_START_EAIERR folds EAI_ error codes from getaddrinfo() into 
 *     api_status_t values.
 */
#define API_OS_START_EAIERR    (API_OS_START_CANONERR + API_OS_ERRSPACE_SIZE)
/**
 * API_OS_START_SYSERR folds platform-specific system error values into 
 *     api_status_t values.
 */
#define API_OS_START_SYSERR    (API_OS_START_EAIERR + API_OS_ERRSPACE_SIZE)

/**
 * @defgroup API_ERROR_map API Error Space
 * <PRE>
 * The following attempts to show the relation of the various constants
 * used for mapping API Status codes.
 *
 *       0          
 *
 *  20,000     API_OS_START_ERROR
 *
 *         + API_OS_ERRSPACE_SIZE (50,000)
 *
 *  70,000      API_OS_START_STATUS
 *
 *         + API_OS_ERRSPACE_SIZE - API_UTIL_ERRSPACE_SIZE (30,000)
 *
 * 100,000      API_UTIL_START_STATUS
 *
 *         + API_UTIL_ERRSPACE_SIZE (20,000)
 *
 * 120,000      API_OS_START_USERERR
 *
 *         + 10 x API_OS_ERRSPACE_SIZE (50,000 * 10)
 *
 * 620,000      API_OS_START_CANONERR
 *
 *         + API_OS_ERRSPACE_SIZE (50,000)
 *
 * 670,000      API_OS_START_EAIERR
 *
 *         + API_OS_ERRSPACE_SIZE (50,000)
 *
 * 720,000      API_OS_START_SYSERR
 *
 * </PRE>
 */

/** no error. */
#define API_SUCCESS 0

/** 
 * @defgroup API_Error API Error Values
 * <PRE>
 * <b>API ERROR VALUES</b>
 * API_ENOSTAT      API was unable to perform a stat on the file 
 * API_ENOPOOL      API was not provided a pool with which to allocate memory
 * API_EBADDATE     API was given an invalid date 
 * API_EINVALSOCK   API was given an invalid socket
 * API_ENOPROC      API was not given a process structure
 * API_ENOTIME      API was not given a time structure
 * API_ENODIR       API was not given a directory structure
 * API_ENOLOCK      API was not given a lock structure
 * API_ENOPOLL      API was not given a poll structure
 * API_ENOSOCKET    API was not given a socket
 * API_ENOTHREAD    API was not given a thread structure
 * API_ENOTHDKEY    API was not given a thread key structure
 * API_ENOSHMAVAIL  There is no more shared memory available
 * API_EDSOOPEN     API was unable to open the dso object.  For more 
 *                  information call api_dso_error().
 * API_EGENERAL     General failure (specific information not available)
 * API_EBADIP       The specified IP address is invalid
 * API_EBADMASK     The specified netmask is invalid
 * API_ESYMNOTFOUND Could not find the requested symbol
 * API_ENOTENOUGHENTROPY Not enough entropy to continue
 * </PRE>
 *
 * <PRE>
 * <b>API STATUS VALUES</b>
 * API_INCHILD        Program is currently executing in the child
 * API_INPARENT       Program is currently executing in the parent
 * API_DETACH         The thread is detached
 * API_NOTDETACH      The thread is not detached
 * API_CHILD_DONE     The child has finished executing
 * API_CHILD_NOTDONE  The child has not finished executing
 * API_TIMEUP         The operation did not finish before the timeout
 * API_INCOMPLETE     The operation was incomplete although some processing
 *                    was performed and the results are partially valid
 * API_BADCH          Getopt found an option not in the option string
 * API_BADARG         Getopt found an option that is missing an argument 
 *                    and an argument was specified in the option string
 * API_EOF            API has encountered the end of the file
 * API_NOTFOUND       API was unable to find the socket in the poll structure
 * API_ANONYMOUS      API is using anonymous shared memory
 * API_FILEBASED      API is using a file name as the key to the shared memory
 * API_KEYBASED       API is using a shared key as the key to the shared memory
 * API_EINIT          Ininitalizer value.  If no option has been found, but 
 *                    the status variable requires a value, this should be used
 * API_ENOTIMPL       The API function has not been implemented on this 
 *                    platform, either because nobody has gotten to it yet, 
 *                    or the function is impossible on this platform.
 * API_EMISMATCH      Two passwords do not match.
 * API_EABSOLUTE      The given path was absolute.
 * API_ERELATIVE      The given path was relative.
 * API_EINCOMPLETE    The given path was neither relative nor absolute.
 * API_EABOVEROOT     The given path was above the root path.
 * API_EBUSY          The given lock was busy.
 * API_EPROC_UNKNOWN  The given process wasn't recognized by API
 * </PRE>
 * @{
 */
/** @see API_STATUS_IS_ENOSTAT */
#define API_ENOSTAT        (API_OS_START_ERROR + 1)
/** @see API_STATUS_IS_ENOPOOL */
#define API_ENOPOOL        (API_OS_START_ERROR + 2)
/* empty slot: +3 */
/** @see API_STATUS_IS_EBADDATE */
#define API_EBADDATE       (API_OS_START_ERROR + 4)
/** @see API_STATUS_IS_EINVALSOCK */
#define API_EINVALSOCK     (API_OS_START_ERROR + 5)
/** @see API_STATUS_IS_ENOPROC */
#define API_ENOPROC        (API_OS_START_ERROR + 6)
/** @see API_STATUS_IS_ENOTIME */
#define API_ENOTIME        (API_OS_START_ERROR + 7)
/** @see API_STATUS_IS_ENODIR */
#define API_ENODIR         (API_OS_START_ERROR + 8)
/** @see API_STATUS_IS_ENOLOCK */
#define API_ENOLOCK        (API_OS_START_ERROR + 9)
/** @see API_STATUS_IS_ENOPOLL */
#define API_ENOPOLL        (API_OS_START_ERROR + 10)
/** @see API_STATUS_IS_ENOSOCKET */
#define API_ENOSOCKET      (API_OS_START_ERROR + 11)
/** @see API_STATUS_IS_ENOTHREAD */
#define API_ENOTHREAD      (API_OS_START_ERROR + 12)
/** @see API_STATUS_IS_ENOTHDKEY */
#define API_ENOTHDKEY      (API_OS_START_ERROR + 13)
/** @see API_STATUS_IS_EGENERAL */
#define API_EGENERAL       (API_OS_START_ERROR + 14)
/** @see API_STATUS_IS_ENOSHMAVAIL */
#define API_ENOSHMAVAIL    (API_OS_START_ERROR + 15)
/** @see API_STATUS_IS_EBADIP */
#define API_EBADIP         (API_OS_START_ERROR + 16)
/** @see API_STATUS_IS_EBADMASK */
#define API_EBADMASK       (API_OS_START_ERROR + 17)
/* empty slot: +18 */
/** @see API_STATUS_IS_EDSOPEN */
#define API_EDSOOPEN       (API_OS_START_ERROR + 19)
/** @see API_STATUS_IS_EABSOLUTE */
#define API_EABSOLUTE      (API_OS_START_ERROR + 20)
/** @see API_STATUS_IS_ERELATIVE */
#define API_ERELATIVE      (API_OS_START_ERROR + 21)
/** @see API_STATUS_IS_EINCOMPLETE */
#define API_EINCOMPLETE    (API_OS_START_ERROR + 22)
/** @see API_STATUS_IS_EABOVEROOT */
#define API_EABOVEROOT     (API_OS_START_ERROR + 23)
/** @see API_STATUS_IS_EBADPATH */
#define API_EBADPATH       (API_OS_START_ERROR + 24)
/** @see API_STATUS_IS_EPATHWILD */
#define API_EPATHWILD      (API_OS_START_ERROR + 25)
/** @see API_STATUS_IS_ESYMNOTFOUND */
#define API_ESYMNOTFOUND   (API_OS_START_ERROR + 26)
/** @see API_STATUS_IS_EPROC_UNKNOWN */
#define API_EPROC_UNKNOWN  (API_OS_START_ERROR + 27)
/** @see API_STATUS_IS_ENOTENOUGHENTROPY */
#define API_ENOTENOUGHENTROPY (API_OS_START_ERROR + 28)
/** @} */

/** 
 * @defgroup API_STATUS_IS Status Value Tests
 * @warning For any particular error condition, more than one of these tests
 *      may match. This is because platform-specific error codes may not
 *      always match the semantics of the POSIX codes these tests (and the
 *      corresponding API error codes) are named after. A notable example
 *      are the API_STATUS_IS_ENOENT and API_STATUS_IS_ENOTDIR tests on
 *      Win32 platforms. The programmer should always be aware of this and
 *      adjust the order of the tests accordingly.
 * @{
 */
/** 
 * API was unable to perform a stat on the file 
 * @warning always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_ENOSTAT(s)        ((s) == API_ENOSTAT)
/** 
 * API was not provided a pool with which to allocate memory 
 * @warning always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_ENOPOOL(s)        ((s) == API_ENOPOOL)
/** API was given an invalid date  */
#define API_STATUS_IS_EBADDATE(s)       ((s) == API_EBADDATE)
/** API was given an invalid socket */
#define API_STATUS_IS_EINVALSOCK(s)     ((s) == API_EINVALSOCK)
/** API was not given a process structure */
#define API_STATUS_IS_ENOPROC(s)        ((s) == API_ENOPROC)
/** API was not given a time structure */
#define API_STATUS_IS_ENOTIME(s)        ((s) == API_ENOTIME)
/** API was not given a directory structure */
#define API_STATUS_IS_ENODIR(s)         ((s) == API_ENODIR)
/** API was not given a lock structure */
#define API_STATUS_IS_ENOLOCK(s)        ((s) == API_ENOLOCK)
/** API was not given a poll structure */
#define API_STATUS_IS_ENOPOLL(s)        ((s) == API_ENOPOLL)
/** API was not given a socket */
#define API_STATUS_IS_ENOSOCKET(s)      ((s) == API_ENOSOCKET)
/** API was not given a thread structure */
#define API_STATUS_IS_ENOTHREAD(s)      ((s) == API_ENOTHREAD)
/** API was not given a thread key structure */
#define API_STATUS_IS_ENOTHDKEY(s)      ((s) == API_ENOTHDKEY)
/** Generic Error which can not be put into another spot */
#define API_STATUS_IS_EGENERAL(s)       ((s) == API_EGENERAL)
/** There is no more shared memory available */
#define API_STATUS_IS_ENOSHMAVAIL(s)    ((s) == API_ENOSHMAVAIL)
/** The specified IP address is invalid */
#define API_STATUS_IS_EBADIP(s)         ((s) == API_EBADIP)
/** The specified netmask is invalid */
#define API_STATUS_IS_EBADMASK(s)       ((s) == API_EBADMASK)
/* empty slot: +18 */
/** 
 * API was unable to open the dso object.  
 * For more information call api_dso_error().
 */
#if defined(WIN32)
#define API_STATUS_IS_EDSOOPEN(s)       ((s) == API_EDSOOPEN \
                       || API_TO_OS_ERROR(s) == ERROR_MOD_NOT_FOUND)
#elif defined(OS2)
#define API_STATUS_IS_EDSOOPEN(s)       ((s) == API_EDSOOPEN \
                       || API_TO_OS_ERROR(s) == ERROR_FILE_NOT_FOUND)
#else
#define API_STATUS_IS_EDSOOPEN(s)       ((s) == API_EDSOOPEN)
#endif
/** The given path was absolute. */
#define API_STATUS_IS_EABSOLUTE(s)      ((s) == API_EABSOLUTE)
/** The given path was relative. */
#define API_STATUS_IS_ERELATIVE(s)      ((s) == API_ERELATIVE)
/** The given path was neither relative nor absolute. */
#define API_STATUS_IS_EINCOMPLETE(s)    ((s) == API_EINCOMPLETE)
/** The given path was above the root path. */
#define API_STATUS_IS_EABOVEROOT(s)     ((s) == API_EABOVEROOT)
/** The given path was bad. */
#define API_STATUS_IS_EBADPATH(s)       ((s) == API_EBADPATH)
/** The given path contained wildcards. */
#define API_STATUS_IS_EPATHWILD(s)      ((s) == API_EPATHWILD)
/** Could not find the requested symbol.
 * For more information call api_dso_error().
 */
#if defined(WIN32)
#define API_STATUS_IS_ESYMNOTFOUND(s)   ((s) == API_ESYMNOTFOUND \
                       || API_TO_OS_ERROR(s) == ERROR_PROC_NOT_FOUND)
#elif defined(OS2)
#define API_STATUS_IS_ESYMNOTFOUND(s)   ((s) == API_ESYMNOTFOUND \
                       || API_TO_OS_ERROR(s) == ERROR_INVALID_NAME)
#else
#define API_STATUS_IS_ESYMNOTFOUND(s)   ((s) == API_ESYMNOTFOUND)
#endif
/** The given process was not recognized by API. */
#define API_STATUS_IS_EPROC_UNKNOWN(s)  ((s) == API_EPROC_UNKNOWN)
/** API could not gather enough entropy to continue. */
#define API_STATUS_IS_ENOTENOUGHENTROPY(s) ((s) == API_ENOTENOUGHENTROPY)

/** @} */

/** 
 * @addtogroup API_Error
 * @{
 */
/** @see API_STATUS_IS_INCHILD */
#define API_INCHILD        (API_OS_START_STATUS + 1)
/** @see API_STATUS_IS_INPARENT */
#define API_INPARENT       (API_OS_START_STATUS + 2)
/** @see API_STATUS_IS_DETACH */
#define API_DETACH         (API_OS_START_STATUS + 3)
/** @see API_STATUS_IS_NOTDETACH */
#define API_NOTDETACH      (API_OS_START_STATUS + 4)
/** @see API_STATUS_IS_CHILD_DONE */
#define API_CHILD_DONE     (API_OS_START_STATUS + 5)
/** @see API_STATUS_IS_CHILD_NOTDONE */
#define API_CHILD_NOTDONE  (API_OS_START_STATUS + 6)
/** @see API_STATUS_IS_TIMEUP */
#define API_TIMEUP         (API_OS_START_STATUS + 7)
/** @see API_STATUS_IS_INCOMPLETE */
#define API_INCOMPLETE     (API_OS_START_STATUS + 8)
/* empty slot: +9 */
/* empty slot: +10 */
/* empty slot: +11 */
/** @see API_STATUS_IS_BADCH */
#define API_BADCH          (API_OS_START_STATUS + 12)
/** @see API_STATUS_IS_BADARG */
#define API_BADARG         (API_OS_START_STATUS + 13)
/** @see API_STATUS_IS_EOF */
#define API_EOF            (API_OS_START_STATUS + 14)
/** @see API_STATUS_IS_NOTFOUND */
#define API_NOTFOUND       (API_OS_START_STATUS + 15)
/* empty slot: +16 */
/* empty slot: +17 */
/* empty slot: +18 */
/** @see API_STATUS_IS_ANONYMOUS */
#define API_ANONYMOUS      (API_OS_START_STATUS + 19)
/** @see API_STATUS_IS_FILEBASED */
#define API_FILEBASED      (API_OS_START_STATUS + 20)
/** @see API_STATUS_IS_KEYBASED */
#define API_KEYBASED       (API_OS_START_STATUS + 21)
/** @see API_STATUS_IS_EINIT */
#define API_EINIT          (API_OS_START_STATUS + 22)  
/** @see API_STATUS_IS_ENOTIMPL */
#define API_ENOTIMPL       (API_OS_START_STATUS + 23)
/** @see API_STATUS_IS_EMISMATCH */
#define API_EMISMATCH      (API_OS_START_STATUS + 24)
/** @see API_STATUS_IS_EBUSY */
#define API_EBUSY          (API_OS_START_STATUS + 25)
/** @} */

/** 
 * @addtogroup API_STATUS_IS
 * @{
 */
/** 
 * Program is currently executing in the child 
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code */
#define API_STATUS_IS_INCHILD(s)        ((s) == API_INCHILD)
/** 
 * Program is currently executing in the parent 
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_INPARENT(s)       ((s) == API_INPARENT)
/** 
 * The thread is detached 
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_DETACH(s)         ((s) == API_DETACH)
/** 
 * The thread is not detached 
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_NOTDETACH(s)      ((s) == API_NOTDETACH)
/** 
 * The child has finished executing
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_CHILD_DONE(s)     ((s) == API_CHILD_DONE)
/** 
 * The child has not finished executing
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_CHILD_NOTDONE(s)  ((s) == API_CHILD_NOTDONE)
/** 
 * The operation did not finish before the timeout
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_TIMEUP(s)         ((s) == API_TIMEUP)
/** 
 * The operation was incomplete although some processing was performed
 * and the results are partially valid.
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_INCOMPLETE(s)     ((s) == API_INCOMPLETE)
/* empty slot: +9 */
/* empty slot: +10 */
/* empty slot: +11 */
/** 
 * Getopt found an option not in the option string
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_BADCH(s)          ((s) == API_BADCH)
/** 
 * Getopt found an option not in the option string and an argument was 
 * specified in the option string
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_BADARG(s)         ((s) == API_BADARG)
/** 
 * API has encountered the end of the file
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_EOF(s)            ((s) == API_EOF)
/** 
 * API was unable to find the socket in the poll structure
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_NOTFOUND(s)       ((s) == API_NOTFOUND)
/* empty slot: +16 */
/* empty slot: +17 */
/* empty slot: +18 */
/** 
 * API is using anonymous shared memory
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_ANONYMOUS(s)      ((s) == API_ANONYMOUS)
/** 
 * API is using a file name as the key to the shared memory
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_FILEBASED(s)      ((s) == API_FILEBASED)
/** 
 * API is using a shared key as the key to the shared memory
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_KEYBASED(s)       ((s) == API_KEYBASED)
/** 
 * Ininitalizer value.  If no option has been found, but 
 * the status variable requires a value, this should be used
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_EINIT(s)          ((s) == API_EINIT)
/** 
 * The API function has not been implemented on this 
 * platform, either because nobody has gotten to it yet, 
 * or the function is impossible on this platform.
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_ENOTIMPL(s)       ((s) == API_ENOTIMPL)
/** 
 * Two passwords do not match.
 * @warning
 * always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_EMISMATCH(s)      ((s) == API_EMISMATCH)
/** 
 * The given lock was busy
 * @warning always use this test, as platform-specific variances may meet this
 * more than one error code 
 */
#define API_STATUS_IS_EBUSY(s)          ((s) == API_EBUSY)

/** @} */

/** 
 * @addtogroup API_Error API Error Values
 * @{
 */
/* API CANONICAL ERROR VALUES */
/** @see API_STATUS_IS_EACCES */
#ifdef EACCES
#define API_EACCES EACCES
#else
#define API_EACCES         (API_OS_START_CANONERR + 1)
#endif

/** @see API_STATUS_IS_EEXIST */
#ifdef EEXIST
#define API_EEXIST EEXIST
#else
#define API_EEXIST         (API_OS_START_CANONERR + 2)
#endif

/** @see API_STATUS_IS_ENAMETOOLONG */
#ifdef ENAMETOOLONG
#define API_ENAMETOOLONG ENAMETOOLONG
#else
#define API_ENAMETOOLONG   (API_OS_START_CANONERR + 3)
#endif

/** @see API_STATUS_IS_ENOENT */
#ifdef ENOENT
#define API_ENOENT ENOENT
#else
#define API_ENOENT         (API_OS_START_CANONERR + 4)
#endif

/** @see API_STATUS_IS_ENOTDIR */
#ifdef ENOTDIR
#define API_ENOTDIR ENOTDIR
#else
#define API_ENOTDIR        (API_OS_START_CANONERR + 5)
#endif

/** @see API_STATUS_IS_ENOSPC */
#ifdef ENOSPC
#define API_ENOSPC ENOSPC
#else
#define API_ENOSPC         (API_OS_START_CANONERR + 6)
#endif

/** @see API_STATUS_IS_ENOMEM */
#ifdef ENOMEM
#define API_ENOMEM ENOMEM
#else
#define API_ENOMEM         (API_OS_START_CANONERR + 7)
#endif

/** @see API_STATUS_IS_EMFILE */
#ifdef EMFILE
#define API_EMFILE EMFILE
#else
#define API_EMFILE         (API_OS_START_CANONERR + 8)
#endif

/** @see API_STATUS_IS_ENFILE */
#ifdef ENFILE
#define API_ENFILE ENFILE
#else
#define API_ENFILE         (API_OS_START_CANONERR + 9)
#endif

/** @see API_STATUS_IS_EBADF */
#ifdef EBADF
#define API_EBADF EBADF
#else
#define API_EBADF          (API_OS_START_CANONERR + 10)
#endif

/** @see API_STATUS_IS_EINVAL */
#ifdef EINVAL
#define API_EINVAL EINVAL
#else
#define API_EINVAL         (API_OS_START_CANONERR + 11)
#endif

/** @see API_STATUS_IS_ESPIPE */
#ifdef ESPIPE
#define API_ESPIPE ESPIPE
#else
#define API_ESPIPE         (API_OS_START_CANONERR + 12)
#endif

/** 
 * @see API_STATUS_IS_EAGAIN 
 * @warning use API_STATUS_IS_EAGAIN instead of just testing this value
 */
#ifdef EAGAIN
#define API_EAGAIN EAGAIN
#elif defined(EWOULDBLOCK)
#define API_EAGAIN EWOULDBLOCK
#else
#define API_EAGAIN         (API_OS_START_CANONERR + 13)
#endif

/** @see API_STATUS_IS_EINTR */
#ifdef EINTR
#define API_EINTR EINTR
#else
#define API_EINTR          (API_OS_START_CANONERR + 14)
#endif

/** @see API_STATUS_IS_ENOTSOCK */
#ifdef ENOTSOCK
#define API_ENOTSOCK ENOTSOCK
#else
#define API_ENOTSOCK       (API_OS_START_CANONERR + 15)
#endif

/** @see API_STATUS_IS_ECONNREFUSED */
#ifdef ECONNREFUSED
#define API_ECONNREFUSED ECONNREFUSED
#else
#define API_ECONNREFUSED   (API_OS_START_CANONERR + 16)
#endif

/** @see API_STATUS_IS_EINPROGRESS */
#ifdef EINPROGRESS
#define API_EINPROGRESS EINPROGRESS
#else
#define API_EINPROGRESS    (API_OS_START_CANONERR + 17)
#endif

/** 
 * @see API_STATUS_IS_ECONNABORTED
 * @warning use API_STATUS_IS_ECONNABORTED instead of just testing this value
 */

#ifdef ECONNABORTED
#define API_ECONNABORTED ECONNABORTED
#else
#define API_ECONNABORTED   (API_OS_START_CANONERR + 18)
#endif

/** @see API_STATUS_IS_ECONNRESET */
#ifdef ECONNRESET
#define API_ECONNRESET ECONNRESET
#else
#define API_ECONNRESET     (API_OS_START_CANONERR + 19)
#endif

/** @see API_STATUS_IS_ETIMEDOUT 
 *  @deprecated */
#ifdef ETIMEDOUT
#define API_ETIMEDOUT ETIMEDOUT
#else
#define API_ETIMEDOUT      (API_OS_START_CANONERR + 20)
#endif

/** @see API_STATUS_IS_EHOSTUNREACH */
#ifdef EHOSTUNREACH
#define API_EHOSTUNREACH EHOSTUNREACH
#else
#define API_EHOSTUNREACH   (API_OS_START_CANONERR + 21)
#endif

/** @see API_STATUS_IS_ENETUNREACH */
#ifdef ENETUNREACH
#define API_ENETUNREACH ENETUNREACH
#else
#define API_ENETUNREACH    (API_OS_START_CANONERR + 22)
#endif

/** @see API_STATUS_IS_EFTYPE */
#ifdef EFTYPE
#define API_EFTYPE EFTYPE
#else
#define API_EFTYPE        (API_OS_START_CANONERR + 23)
#endif

/** @see API_STATUS_IS_EPIPE */
#ifdef EPIPE
#define API_EPIPE EPIPE
#else
#define API_EPIPE         (API_OS_START_CANONERR + 24)
#endif

/** @see API_STATUS_IS_EXDEV */
#ifdef EXDEV
#define API_EXDEV EXDEV
#else
#define API_EXDEV         (API_OS_START_CANONERR + 25)
#endif

/** @see API_STATUS_IS_ENOTEMPTY */
#ifdef ENOTEMPTY
#define API_ENOTEMPTY ENOTEMPTY
#else
#define API_ENOTEMPTY     (API_OS_START_CANONERR + 26)
#endif

/** @see API_STATUS_IS_EAFNOSUPPORT */
#ifdef EAFNOSUPPORT
#define API_EAFNOSUPPORT EAFNOSUPPORT
#else
#define API_EAFNOSUPPORT  (API_OS_START_CANONERR + 27)
#endif

/** @see API_STATUS_IS_EOPNOTSUPP */
#ifdef EOPNOTSUPP
#define API_EOPNOTSUPP EOPNOTSUPP
#else
#define API_EOPNOTSUPP    (API_OS_START_CANONERR + 28)
#endif

/** @} */

#if defined(OS2) && !defined(DOXYGEN)

#define API_FROM_OS_ERROR(e) (e == 0 ? API_SUCCESS : e + API_OS_START_SYSERR)
#define API_TO_OS_ERROR(e)   (e == 0 ? API_SUCCESS : e - API_OS_START_SYSERR)

#define INCL_DOSERRORS
#define INCL_DOS

/* Leave these undefined.
 * OS2 doesn't rely on the errno concept.
 * The API calls always return a result codes which
 * should be filtered through API_FROM_OS_ERROR().
 *
 * #define api_get_os_error()   (API_FROM_OS_ERROR(GetLastError()))
 * #define api_set_os_error(e)  (SetLastError(API_TO_OS_ERROR(e)))
 */

/* A special case, only socket calls require this;
 */
#define api_get_netos_error()   (API_FROM_OS_ERROR(errno))
#define api_set_netos_error(e)  (errno = API_TO_OS_ERROR(e))

/* These can't sit in a private header, so in spite of the extra size, 
 * they need to be made available here.
 */
#define SOCBASEERR              10000
#define SOCEPERM                (SOCBASEERR+1)             /* Not owner */
#define SOCESRCH                (SOCBASEERR+3)             /* No such process */
#define SOCEINTR                (SOCBASEERR+4)             /* Interrupted system call */
#define SOCENXIO                (SOCBASEERR+6)             /* No such device or address */
#define SOCEBADF                (SOCBASEERR+9)             /* Bad file number */
#define SOCEACCES               (SOCBASEERR+13)            /* Permission denied */
#define SOCEFAULT               (SOCBASEERR+14)            /* Bad address */
#define SOCEINVAL               (SOCBASEERR+22)            /* Invalid argument */
#define SOCEMFILE               (SOCBASEERR+24)            /* Too many open files */
#define SOCEPIPE                (SOCBASEERR+32)            /* Broken pipe */
#define SOCEOS2ERR              (SOCBASEERR+100)           /* OS/2 Error */
#define SOCEWOULDBLOCK          (SOCBASEERR+35)            /* Operation would block */
#define SOCEINPROGRESS          (SOCBASEERR+36)            /* Operation now in progress */
#define SOCEALREADY             (SOCBASEERR+37)            /* Operation already in progress */
#define SOCENOTSOCK             (SOCBASEERR+38)            /* Socket operation on non-socket */
#define SOCEDESTADDRREQ         (SOCBASEERR+39)            /* Destination address required */
#define SOCEMSGSIZE             (SOCBASEERR+40)            /* Message too long */
#define SOCEPROTOTYPE           (SOCBASEERR+41)            /* Protocol wrong type for socket */
#define SOCENOPROTOOPT          (SOCBASEERR+42)            /* Protocol not available */
#define SOCEPROTONOSUPPORT      (SOCBASEERR+43)            /* Protocol not supported */
#define SOCESOCKTNOSUPPORT      (SOCBASEERR+44)            /* Socket type not supported */
#define SOCEOPNOTSUPP           (SOCBASEERR+45)            /* Operation not supported on socket */
#define SOCEPFNOSUPPORT         (SOCBASEERR+46)            /* Protocol family not supported */
#define SOCEAFNOSUPPORT         (SOCBASEERR+47)            /* Address family not supported by protocol family */
#define SOCEADDRINUSE           (SOCBASEERR+48)            /* Address already in use */
#define SOCEADDRNOTAVAIL        (SOCBASEERR+49)            /* Can't assign requested address */
#define SOCENETDOWN             (SOCBASEERR+50)            /* Network is down */
#define SOCENETUNREACH          (SOCBASEERR+51)            /* Network is unreachable */
#define SOCENETRESET            (SOCBASEERR+52)            /* Network dropped connection on reset */
#define SOCECONNABORTED         (SOCBASEERR+53)            /* Software caused connection abort */
#define SOCECONNRESET           (SOCBASEERR+54)            /* Connection reset by peer */
#define SOCENOBUFS              (SOCBASEERR+55)            /* No buffer space available */
#define SOCEISCONN              (SOCBASEERR+56)            /* Socket is already connected */
#define SOCENOTCONN             (SOCBASEERR+57)            /* Socket is not connected */
#define SOCESHUTDOWN            (SOCBASEERR+58)            /* Can't send after socket shutdown */
#define SOCETOOMANYREFS         (SOCBASEERR+59)            /* Too many references: can't splice */
#define SOCETIMEDOUT            (SOCBASEERR+60)            /* Connection timed out */
#define SOCECONNREFUSED         (SOCBASEERR+61)            /* Connection refused */
#define SOCELOOP                (SOCBASEERR+62)            /* Too many levels of symbolic links */
#define SOCENAMETOOLONG         (SOCBASEERR+63)            /* File name too long */
#define SOCEHOSTDOWN            (SOCBASEERR+64)            /* Host is down */
#define SOCEHOSTUNREACH         (SOCBASEERR+65)            /* No route to host */
#define SOCENOTEMPTY            (SOCBASEERR+66)            /* Directory not empty */

/* API CANONICAL ERROR TESTS */
#define API_STATUS_IS_EACCES(s)         ((s) == API_EACCES \
                || (s) == API_OS_START_SYSERR + ERROR_ACCESS_DENIED \
                || (s) == API_OS_START_SYSERR + ERROR_SHARING_VIOLATION)
#define API_STATUS_IS_EEXIST(s)         ((s) == API_EEXIST \
                || (s) == API_OS_START_SYSERR + ERROR_OPEN_FAILED \
                || (s) == API_OS_START_SYSERR + ERROR_FILE_EXISTS \
                || (s) == API_OS_START_SYSERR + ERROR_ALREADY_EXISTS \
                || (s) == API_OS_START_SYSERR + ERROR_ACCESS_DENIED)
#define API_STATUS_IS_ENAMETOOLONG(s)   ((s) == API_ENAMETOOLONG \
                || (s) == API_OS_START_SYSERR + ERROR_FILENAME_EXCED_RANGE \
                || (s) == API_OS_START_SYSERR + SOCENAMETOOLONG)
#define API_STATUS_IS_ENOENT(s)         ((s) == API_ENOENT \
                || (s) == API_OS_START_SYSERR + ERROR_FILE_NOT_FOUND \
                || (s) == API_OS_START_SYSERR + ERROR_PATH_NOT_FOUND \
                || (s) == API_OS_START_SYSERR + ERROR_NO_MORE_FILES \
                || (s) == API_OS_START_SYSERR + ERROR_OPEN_FAILED)
#define API_STATUS_IS_ENOTDIR(s)        ((s) == API_ENOTDIR)
#define API_STATUS_IS_ENOSPC(s)         ((s) == API_ENOSPC \
                || (s) == API_OS_START_SYSERR + ERROR_DISK_FULL)
#define API_STATUS_IS_ENOMEM(s)         ((s) == API_ENOMEM)
#define API_STATUS_IS_EMFILE(s)         ((s) == API_EMFILE \
                || (s) == API_OS_START_SYSERR + ERROR_TOO_MANY_OPEN_FILES)
#define API_STATUS_IS_ENFILE(s)         ((s) == API_ENFILE)
#define API_STATUS_IS_EBADF(s)          ((s) == API_EBADF \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_HANDLE)
#define API_STATUS_IS_EINVAL(s)         ((s) == API_EINVAL \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_PARAMETER \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_FUNCTION)
#define API_STATUS_IS_ESPIPE(s)         ((s) == API_ESPIPE \
                || (s) == API_OS_START_SYSERR + ERROR_NEGATIVE_SEEK)
#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN \
                || (s) == API_OS_START_SYSERR + ERROR_NO_DATA \
                || (s) == API_OS_START_SYSERR + SOCEWOULDBLOCK \
                || (s) == API_OS_START_SYSERR + ERROR_LOCK_VIOLATION)
#define API_STATUS_IS_EINTR(s)          ((s) == API_EINTR \
                || (s) == API_OS_START_SYSERR + SOCEINTR)
#define API_STATUS_IS_ENOTSOCK(s)       ((s) == API_ENOTSOCK \
                || (s) == API_OS_START_SYSERR + SOCENOTSOCK)
#define API_STATUS_IS_ECONNREFUSED(s)   ((s) == API_ECONNREFUSED \
                || (s) == API_OS_START_SYSERR + SOCECONNREFUSED)
#define API_STATUS_IS_EINPROGRESS(s)    ((s) == API_EINPROGRESS \
                || (s) == API_OS_START_SYSERR + SOCEINPROGRESS)
#define API_STATUS_IS_ECONNABORTED(s)   ((s) == API_ECONNABORTED \
                || (s) == API_OS_START_SYSERR + SOCECONNABORTED)
#define API_STATUS_IS_ECONNRESET(s)     ((s) == API_ECONNRESET \
                || (s) == API_OS_START_SYSERR + SOCECONNRESET)
/* XXX deprecated */
#define API_STATUS_IS_ETIMEDOUT(s)         ((s) == API_ETIMEDOUT \
                || (s) == API_OS_START_SYSERR + SOCETIMEDOUT)    
#undef API_STATUS_IS_TIMEUP
#define API_STATUS_IS_TIMEUP(s)         ((s) == API_TIMEUP \
                || (s) == API_OS_START_SYSERR + SOCETIMEDOUT)    
#define API_STATUS_IS_EHOSTUNREACH(s)   ((s) == API_EHOSTUNREACH \
                || (s) == API_OS_START_SYSERR + SOCEHOSTUNREACH)
#define API_STATUS_IS_ENETUNREACH(s)    ((s) == API_ENETUNREACH \
                || (s) == API_OS_START_SYSERR + SOCENETUNREACH)
#define API_STATUS_IS_EFTYPE(s)         ((s) == API_EFTYPE)
#define API_STATUS_IS_EPIPE(s)          ((s) == API_EPIPE \
                || (s) == API_OS_START_SYSERR + ERROR_BROKEN_PIPE \
                || (s) == API_OS_START_SYSERR + SOCEPIPE)
#define API_STATUS_IS_EXDEV(s)          ((s) == API_EXDEV \
                || (s) == API_OS_START_SYSERR + ERROR_NOT_SAME_DEVICE)
#define API_STATUS_IS_ENOTEMPTY(s)      ((s) == API_ENOTEMPTY \
                || (s) == API_OS_START_SYSERR + ERROR_DIR_NOT_EMPTY \
                || (s) == API_OS_START_SYSERR + ERROR_ACCESS_DENIED)
#define API_STATUS_IS_EAFNOSUPPORT(s)   ((s) == API_AFNOSUPPORT \
                || (s) == API_OS_START_SYSERR + SOCEAFNOSUPPORT)
#define API_STATUS_IS_EOPNOTSUPP(s)     ((s) == API_EOPNOTSUPP \
                || (s) == API_OS_START_SYSERR + SOCEOPNOTSUPP)

/*
    Sorry, too tired to wrap this up for OS2... feel free to
    fit the following into their best matches.

    { ERROR_NO_SIGNAL_SENT,     ESRCH           },
    { SOCEALREADY,              EALREADY        },
    { SOCEDESTADDRREQ,          EDESTADDRREQ    },
    { SOCEMSGSIZE,              EMSGSIZE        },
    { SOCEPROTOTYPE,            EPROTOTYPE      },
    { SOCENOPROTOOPT,           ENOPROTOOPT     },
    { SOCEPROTONOSUPPORT,       EPROTONOSUPPORT },
    { SOCESOCKTNOSUPPORT,       ESOCKTNOSUPPORT },
    { SOCEPFNOSUPPORT,          EPFNOSUPPORT    },
    { SOCEADDRINUSE,            EADDRINUSE      },
    { SOCEADDRNOTAVAIL,         EADDRNOTAVAIL   },
    { SOCENETDOWN,              ENETDOWN        },
    { SOCENETRESET,             ENETRESET       },
    { SOCENOBUFS,               ENOBUFS         },
    { SOCEISCONN,               EISCONN         },
    { SOCENOTCONN,              ENOTCONN        },
    { SOCESHUTDOWN,             ESHUTDOWN       },
    { SOCETOOMANYREFS,          ETOOMANYREFS    },
    { SOCELOOP,                 ELOOP           },
    { SOCEHOSTDOWN,             EHOSTDOWN       },
    { SOCENOTEMPTY,             ENOTEMPTY       },
    { SOCEPIPE,                 EPIPE           }
*/

#elif defined(WIN32) && !defined(DOXYGEN) /* !defined(OS2) */

#define API_FROM_OS_ERROR(e) (e == 0 ? API_SUCCESS : e + API_OS_START_SYSERR)
#define API_TO_OS_ERROR(e)   (e == 0 ? API_SUCCESS : e - API_OS_START_SYSERR)

#define api_get_os_error()   (API_FROM_OS_ERROR(GetLastError()))
#define api_set_os_error(e)  (SetLastError(API_TO_OS_ERROR(e)))

/* A special case, only socket calls require this:
 */
#define api_get_netos_error()   (API_FROM_OS_ERROR(WSAGetLastError()))
#define api_set_netos_error(e)   (WSASetLastError(API_TO_OS_ERROR(e)))

/* API CANONICAL ERROR TESTS */
#define API_STATUS_IS_EACCES(s)         ((s) == API_EACCES \
                || (s) == API_OS_START_SYSERR + ERROR_ACCESS_DENIED \
                || (s) == API_OS_START_SYSERR + ERROR_CANNOT_MAKE \
                || (s) == API_OS_START_SYSERR + ERROR_CURRENT_DIRECTORY \
                || (s) == API_OS_START_SYSERR + ERROR_DRIVE_LOCKED \
                || (s) == API_OS_START_SYSERR + ERROR_FAIL_I24 \
                || (s) == API_OS_START_SYSERR + ERROR_LOCK_VIOLATION \
                || (s) == API_OS_START_SYSERR + ERROR_LOCK_FAILED \
                || (s) == API_OS_START_SYSERR + ERROR_NOT_LOCKED \
                || (s) == API_OS_START_SYSERR + ERROR_NETWORK_ACCESS_DENIED \
                || (s) == API_OS_START_SYSERR + ERROR_SHARING_VIOLATION)
#define API_STATUS_IS_EEXIST(s)         ((s) == API_EEXIST \
                || (s) == API_OS_START_SYSERR + ERROR_FILE_EXISTS \
                || (s) == API_OS_START_SYSERR + ERROR_ALREADY_EXISTS)
#define API_STATUS_IS_ENAMETOOLONG(s)   ((s) == API_ENAMETOOLONG \
                || (s) == API_OS_START_SYSERR + ERROR_FILENAME_EXCED_RANGE \
                || (s) == API_OS_START_SYSERR + WSAENAMETOOLONG)
#define API_STATUS_IS_ENOENT(s)         ((s) == API_ENOENT \
                || (s) == API_OS_START_SYSERR + ERROR_FILE_NOT_FOUND \
                || (s) == API_OS_START_SYSERR + ERROR_PATH_NOT_FOUND \
                || (s) == API_OS_START_SYSERR + ERROR_OPEN_FAILED \
                || (s) == API_OS_START_SYSERR + ERROR_NO_MORE_FILES)
#define API_STATUS_IS_ENOTDIR(s)        ((s) == API_ENOTDIR \
                || (s) == API_OS_START_SYSERR + ERROR_PATH_NOT_FOUND \
                || (s) == API_OS_START_SYSERR + ERROR_BAD_NETPATH \
                || (s) == API_OS_START_SYSERR + ERROR_BAD_NET_NAME \
                || (s) == API_OS_START_SYSERR + ERROR_BAD_PATHNAME \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_DRIVE \
                || (s) == API_OS_START_SYSERR + ERROR_DIRECTORY)
#define API_STATUS_IS_ENOSPC(s)         ((s) == API_ENOSPC \
                || (s) == API_OS_START_SYSERR + ERROR_DISK_FULL)
#define API_STATUS_IS_ENOMEM(s)         ((s) == API_ENOMEM \
                || (s) == API_OS_START_SYSERR + ERROR_ARENA_TRASHED \
                || (s) == API_OS_START_SYSERR + ERROR_NOT_ENOUGH_MEMORY \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_BLOCK \
                || (s) == API_OS_START_SYSERR + ERROR_NOT_ENOUGH_QUOTA \
                || (s) == API_OS_START_SYSERR + ERROR_OUTOFMEMORY)
#define API_STATUS_IS_EMFILE(s)         ((s) == API_EMFILE \
                || (s) == API_OS_START_SYSERR + ERROR_TOO_MANY_OPEN_FILES)
#define API_STATUS_IS_ENFILE(s)         ((s) == API_ENFILE)
#define API_STATUS_IS_EBADF(s)          ((s) == API_EBADF \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_HANDLE \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_TARGET_HANDLE)
#define API_STATUS_IS_EINVAL(s)         ((s) == API_EINVAL \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_ACCESS \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_DATA \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_FUNCTION \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_HANDLE \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_PARAMETER \
                || (s) == API_OS_START_SYSERR + ERROR_NEGATIVE_SEEK)
#define API_STATUS_IS_ESPIPE(s)         ((s) == API_ESPIPE \
                || (s) == API_OS_START_SYSERR + ERROR_SEEK_ON_DEVICE \
                || (s) == API_OS_START_SYSERR + ERROR_NEGATIVE_SEEK)
#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN \
                || (s) == API_OS_START_SYSERR + ERROR_NO_DATA \
                || (s) == API_OS_START_SYSERR + ERROR_NO_PROC_SLOTS \
                || (s) == API_OS_START_SYSERR + ERROR_NESTING_NOT_ALLOWED \
                || (s) == API_OS_START_SYSERR + ERROR_MAX_THRDS_REACHED \
                || (s) == API_OS_START_SYSERR + ERROR_LOCK_VIOLATION \
                || (s) == API_OS_START_SYSERR + WSAEWOULDBLOCK)
#define API_STATUS_IS_EINTR(s)          ((s) == API_EINTR \
                || (s) == API_OS_START_SYSERR + WSAEINTR)
#define API_STATUS_IS_ENOTSOCK(s)       ((s) == API_ENOTSOCK \
                || (s) == API_OS_START_SYSERR + WSAENOTSOCK)
#define API_STATUS_IS_ECONNREFUSED(s)   ((s) == API_ECONNREFUSED \
                || (s) == API_OS_START_SYSERR + WSAECONNREFUSED)
#define API_STATUS_IS_EINPROGRESS(s)    ((s) == API_EINPROGRESS \
                || (s) == API_OS_START_SYSERR + WSAEINPROGRESS)
#define API_STATUS_IS_ECONNABORTED(s)   ((s) == API_ECONNABORTED \
                || (s) == API_OS_START_SYSERR + WSAECONNABORTED)
#define API_STATUS_IS_ECONNRESET(s)     ((s) == API_ECONNRESET \
                || (s) == API_OS_START_SYSERR + ERROR_NETNAME_DELETED \
                || (s) == API_OS_START_SYSERR + WSAECONNRESET)
/* XXX deprecated */
#define API_STATUS_IS_ETIMEDOUT(s)         ((s) == API_ETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WSAETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WAIT_TIMEOUT)
#undef API_STATUS_IS_TIMEUP
#define API_STATUS_IS_TIMEUP(s)         ((s) == API_TIMEUP \
                || (s) == API_OS_START_SYSERR + WSAETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WAIT_TIMEOUT)
#define API_STATUS_IS_EHOSTUNREACH(s)   ((s) == API_EHOSTUNREACH \
                || (s) == API_OS_START_SYSERR + WSAEHOSTUNREACH)
#define API_STATUS_IS_ENETUNREACH(s)    ((s) == API_ENETUNREACH \
                || (s) == API_OS_START_SYSERR + WSAENETUNREACH)
#define API_STATUS_IS_EFTYPE(s)         ((s) == API_EFTYPE \
                || (s) == API_OS_START_SYSERR + ERROR_EXE_MACHINE_TYPE_MISMATCH \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_DLL \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_MODULETYPE \
                || (s) == API_OS_START_SYSERR + ERROR_BAD_EXE_FORMAT \
                || (s) == API_OS_START_SYSERR + ERROR_INVALID_EXE_SIGNATURE \
                || (s) == API_OS_START_SYSERR + ERROR_FILE_CORRUPT \
                || (s) == API_OS_START_SYSERR + ERROR_BAD_FORMAT)
#define API_STATUS_IS_EPIPE(s)          ((s) == API_EPIPE \
                || (s) == API_OS_START_SYSERR + ERROR_BROKEN_PIPE)
#define API_STATUS_IS_EXDEV(s)          ((s) == API_EXDEV \
                || (s) == API_OS_START_SYSERR + ERROR_NOT_SAME_DEVICE)
#define API_STATUS_IS_ENOTEMPTY(s)      ((s) == API_ENOTEMPTY \
                || (s) == API_OS_START_SYSERR + ERROR_DIR_NOT_EMPTY)
#define API_STATUS_IS_EAFNOSUPPORT(s)   ((s) == API_EAFNOSUPPORT \
                || (s) == API_OS_START_SYSERR + WSAEAFNOSUPPORT)
#define API_STATUS_IS_EOPNOTSUPP(s)     ((s) == API_EOPNOTSUPP \
                || (s) == API_OS_START_SYSERR + WSAEOPNOTSUPP)

#elif defined(NETWARE) && defined(USE_WINSOCK) && !defined(DOXYGEN) /* !defined(OS2) && !defined(WIN32) */

#define API_FROM_OS_ERROR(e) (e == 0 ? API_SUCCESS : e + API_OS_START_SYSERR)
#define API_TO_OS_ERROR(e)   (e == 0 ? API_SUCCESS : e - API_OS_START_SYSERR)

#define api_get_os_error()    (errno)
#define api_set_os_error(e)   (errno = (e))

/* A special case, only socket calls require this: */
#define api_get_netos_error()   (API_FROM_OS_ERROR(WSAGetLastError()))
#define api_set_netos_error(e)  (WSASetLastError(API_TO_OS_ERROR(e)))

/* API CANONICAL ERROR TESTS */
#define API_STATUS_IS_EACCES(s)         ((s) == API_EACCES)
#define API_STATUS_IS_EEXIST(s)         ((s) == API_EEXIST)
#define API_STATUS_IS_ENAMETOOLONG(s)   ((s) == API_ENAMETOOLONG)
#define API_STATUS_IS_ENOENT(s)         ((s) == API_ENOENT)
#define API_STATUS_IS_ENOTDIR(s)        ((s) == API_ENOTDIR)
#define API_STATUS_IS_ENOSPC(s)         ((s) == API_ENOSPC)
#define API_STATUS_IS_ENOMEM(s)         ((s) == API_ENOMEM)
#define API_STATUS_IS_EMFILE(s)         ((s) == API_EMFILE)
#define API_STATUS_IS_ENFILE(s)         ((s) == API_ENFILE)
#define API_STATUS_IS_EBADF(s)          ((s) == API_EBADF)
#define API_STATUS_IS_EINVAL(s)         ((s) == API_EINVAL)
#define API_STATUS_IS_ESPIPE(s)         ((s) == API_ESPIPE)

#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN \
                || (s) ==                       EWOULDBLOCK \
                || (s) == API_OS_START_SYSERR + WSAEWOULDBLOCK)
#define API_STATUS_IS_EINTR(s)          ((s) == API_EINTR \
                || (s) == API_OS_START_SYSERR + WSAEINTR)
#define API_STATUS_IS_ENOTSOCK(s)       ((s) == API_ENOTSOCK \
                || (s) == API_OS_START_SYSERR + WSAENOTSOCK)
#define API_STATUS_IS_ECONNREFUSED(s)   ((s) == API_ECONNREFUSED \
                || (s) == API_OS_START_SYSERR + WSAECONNREFUSED)
#define API_STATUS_IS_EINPROGRESS(s)    ((s) == API_EINPROGRESS \
                || (s) == API_OS_START_SYSERR + WSAEINPROGRESS)
#define API_STATUS_IS_ECONNABORTED(s)   ((s) == API_ECONNABORTED \
                || (s) == API_OS_START_SYSERR + WSAECONNABORTED)
#define API_STATUS_IS_ECONNRESET(s)     ((s) == API_ECONNRESET \
                || (s) == API_OS_START_SYSERR + WSAECONNRESET)
/* XXX deprecated */
#define API_STATUS_IS_ETIMEDOUT(s)       ((s) == API_ETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WSAETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WAIT_TIMEOUT)
#undef API_STATUS_IS_TIMEUP
#define API_STATUS_IS_TIMEUP(s)         ((s) == API_TIMEUP \
                || (s) == API_OS_START_SYSERR + WSAETIMEDOUT \
                || (s) == API_OS_START_SYSERR + WAIT_TIMEOUT)
#define API_STATUS_IS_EHOSTUNREACH(s)   ((s) == API_EHOSTUNREACH \
                || (s) == API_OS_START_SYSERR + WSAEHOSTUNREACH)
#define API_STATUS_IS_ENETUNREACH(s)    ((s) == API_ENETUNREACH \
                || (s) == API_OS_START_SYSERR + WSAENETUNREACH)
#define API_STATUS_IS_ENETDOWN(s)       ((s) == API_OS_START_SYSERR + WSAENETDOWN)
#define API_STATUS_IS_EFTYPE(s)         ((s) == API_EFTYPE)
#define API_STATUS_IS_EPIPE(s)          ((s) == API_EPIPE)
#define API_STATUS_IS_EXDEV(s)          ((s) == API_EXDEV)
#define API_STATUS_IS_ENOTEMPTY(s)      ((s) == API_ENOTEMPTY)
#define API_STATUS_IS_EAFNOSUPPORT(s)   ((s) == API_EAFNOSUPPORT \
                || (s) == API_OS_START_SYSERR + WSAEAFNOSUPPORT)
#define API_STATUS_IS_EOPNOTSUPP(s)     ((s) == API_EOPNOTSUPP \
                || (s) == API_OS_START_SYSERR + WSAEOPNOTSUPP)

#else /* !defined(NETWARE) && !defined(OS2) && !defined(WIN32) */

/*
 *  os error codes are clib error codes
 */
#define API_FROM_OS_ERROR(e)  (e)
#define API_TO_OS_ERROR(e)    (e)

#define api_get_os_error()    (errno)
#define api_set_os_error(e)   (errno = (e))

/* A special case, only socket calls require this:
 */
#define api_get_netos_error() (errno)
#define api_set_netos_error(e) (errno = (e))

/** 
 * @addtogroup API_STATUS_IS
 * @{
 */

/** permission denied */
#define API_STATUS_IS_EACCES(s)         ((s) == API_EACCES)
/** file exists */
#define API_STATUS_IS_EEXIST(s)         ((s) == API_EEXIST)
/** path name is too long */
#define API_STATUS_IS_ENAMETOOLONG(s)   ((s) == API_ENAMETOOLONG)
/**
 * no such file or directory
 * @remark
 * EMVSCATLG can be returned by the automounter on z/OS for
 * paths which do not exist.
 */
#ifdef EMVSCATLG
#define API_STATUS_IS_ENOENT(s)         ((s) == API_ENOENT \
                                      || (s) == EMVSCATLG)
#else
#define API_STATUS_IS_ENOENT(s)         ((s) == API_ENOENT)
#endif
/** not a directory */
#define API_STATUS_IS_ENOTDIR(s)        ((s) == API_ENOTDIR)
/** no space left on device */
#ifdef EDQUOT
#define API_STATUS_IS_ENOSPC(s)         ((s) == API_ENOSPC \
                                      || (s) == EDQUOT)
#else
#define API_STATUS_IS_ENOSPC(s)         ((s) == API_ENOSPC)
#endif
/** not enough memory */
#define API_STATUS_IS_ENOMEM(s)         ((s) == API_ENOMEM)
/** too many open files */
#define API_STATUS_IS_EMFILE(s)         ((s) == API_EMFILE)
/** file table overflow */
#define API_STATUS_IS_ENFILE(s)         ((s) == API_ENFILE)
/** bad file # */
#define API_STATUS_IS_EBADF(s)          ((s) == API_EBADF)
/** invalid argument */
#define API_STATUS_IS_EINVAL(s)         ((s) == API_EINVAL)
/** illegal seek */
#define API_STATUS_IS_ESPIPE(s)         ((s) == API_ESPIPE)

/** operation would block */
#if !defined(EWOULDBLOCK) || !defined(EAGAIN)
#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN)
#elif (EWOULDBLOCK == EAGAIN)
#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN)
#else
#define API_STATUS_IS_EAGAIN(s)         ((s) == API_EAGAIN \
                                      || (s) == EWOULDBLOCK)
#endif

/** interrupted system call */
#define API_STATUS_IS_EINTR(s)          ((s) == API_EINTR)
/** socket operation on a non-socket */
#define API_STATUS_IS_ENOTSOCK(s)       ((s) == API_ENOTSOCK)
/** Connection Refused */
#define API_STATUS_IS_ECONNREFUSED(s)   ((s) == API_ECONNREFUSED)
/** operation now in progress */
#define API_STATUS_IS_EINPROGRESS(s)    ((s) == API_EINPROGRESS)

/** 
 * Software caused connection abort 
 * @remark
 * EPROTO on certain older kernels really means ECONNABORTED, so we need to 
 * ignore it for them.  See discussion in new-httpd archives nh.9701 & nh.9603
 *
 * There is potentially a bug in Solaris 2.x x<6, and other boxes that 
 * implement tcp sockets in userland (i.e. on top of STREAMS).  On these
 * systems, EPROTO can actually result in a fatal loop.  See PR#981 for 
 * example.  It's hard to handle both uses of EPROTO.
 */
#ifdef EPROTO
#define API_STATUS_IS_ECONNABORTED(s)    ((s) == API_ECONNABORTED \
                                       || (s) == EPROTO)
#else
#define API_STATUS_IS_ECONNABORTED(s)    ((s) == API_ECONNABORTED)
#endif

/** Connection Reset by peer */
#define API_STATUS_IS_ECONNRESET(s)      ((s) == API_ECONNRESET)
/** Operation timed out
 *  @deprecated */
#define API_STATUS_IS_ETIMEDOUT(s)      ((s) == API_ETIMEDOUT)
/** no route to host */
#define API_STATUS_IS_EHOSTUNREACH(s)    ((s) == API_EHOSTUNREACH)
/** network is unreachable */
#define API_STATUS_IS_ENETUNREACH(s)     ((s) == API_ENETUNREACH)
/** inappropriate file type or format */
#define API_STATUS_IS_EFTYPE(s)          ((s) == API_EFTYPE)
/** broken pipe */
#define API_STATUS_IS_EPIPE(s)           ((s) == API_EPIPE)
/** cross device link */
#define API_STATUS_IS_EXDEV(s)           ((s) == API_EXDEV)
/** Directory Not Empty */
#define API_STATUS_IS_ENOTEMPTY(s)       ((s) == API_ENOTEMPTY || \
                                          (s) == API_EEXIST)
/** Address Family not supported */
#define API_STATUS_IS_EAFNOSUPPORT(s)    ((s) == API_EAFNOSUPPORT)
/** Socket operation not supported */
#define API_STATUS_IS_EOPNOTSUPP(s)      ((s) == API_EOPNOTSUPP)
/** @} */

#endif /* !defined(NETWARE) && !defined(OS2) && !defined(WIN32) */

/** @} */

#ifdef __cplusplus
}
#endif

#endif  /* ! __API_ERRNO_H_INCLUDED__ */
