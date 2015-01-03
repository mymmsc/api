#ifndef __API_POSIX_CONFIG_H_INCLUDED__
#define __API_POSIX_CONFIG_H_INCLUDED__

#if (API_HPUX)
#define _XOPEN_SOURCE
#define _XOPEN_SOURCE_EXTENDED  1
#define _HPUX_ALT_XOPEN_SOCKET_API
#endif


#if (API_TRU64)
#define _REENTRANT
#endif


#ifdef __CYGWIN__
#define timezonevar             /* timezone is variable */
#define API_BROKEN_SCM_RIGHTS   1
#endif


#include <sys/types.h>
#include <sys/time.h>
#if (API_HAVE_UNISTD_H)
#include <unistd.h>
#endif
#if (API_HAVE_INTTYPES_H)
#include <inttypes.h>
#endif
#include <stdarg.h>
#include <stddef.h>             /* offsetof() */
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <string.h>
#include <signal.h>
#include <pwd.h>
#include <grp.h>
#include <dirent.h>
#include <glob.h>
#include <time.h>
#if (API_HAVE_SYS_PARAM_H)
#include <sys/param.h>          /* statfs() */
#endif
#if (API_HAVE_SYS_MOUNT_H)
#include <sys/mount.h>          /* statfs() */
#endif
#if (API_HAVE_SYS_STATVFS_H)
#include <sys/statvfs.h>        /* statvfs() */
#endif

#if (API_HAVE_SYS_FILIO_H)
#include <sys/filio.h>          /* FIONBIO */
#endif
#include <sys/ioctl.h>          /* FIONBIO */

#include <sys/uio.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/wait.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <sched.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>        /* TCP_NODELAY */
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/un.h>

#if (API_HAVE_LIMITS_H)
#include <limits.h>             /* IOV_MAX */
#endif

#ifdef __CYGWIN__
#include <malloc.h>             /* memalign() */
#endif

#if (API_HAVE_CRYPT_H)
#include <crypt.h>
#endif


#ifndef IOV_MAX
#define IOV_MAX   16
#endif

#if (API_HAVE_POSIX_SEM)
#include <semaphore.h>
#endif


#if (API_HAVE_POLL)
#include <poll.h>
#endif


#if (API_HAVE_KQUEUE)
#include <sys/event.h>
#endif


#if (API_HAVE_DEVPOLL)
#include <sys/ioctl.h>
#include <sys/devpoll.h>
#endif


#if (API_HAVE_FILE_AIO)
#include <aio.h>
typedef struct aiocb  api_aiocb_t;
#endif


#define API_LISTEN_BACKLOG  511

#define api_debug_init()


#if (__FreeBSD__) && (__FreeBSD_version < 400017)

#include <sys/param.h>          /* ALIGN() */

/*
 * FreeBSD 3.x has no CMSG_SPACE() and CMSG_LEN() and has the broken CMSG_DATA()
 */

#undef  CMSG_SPACE
#define CMSG_SPACE(l)       (ALIGN(sizeof(struct cmsghdr)) + ALIGN(l))

#undef  CMSG_LEN
#define CMSG_LEN(l)         (ALIGN(sizeof(struct cmsghdr)) + (l))

#undef  CMSG_DATA
#define CMSG_DATA(cmsg)     ((uint8_t *)(cmsg) + ALIGN(sizeof(struct cmsghdr)))

#endif


extern char **environ;


#endif /* ! __API_POSIX_CONFIG_H_INCLUDED__ */
