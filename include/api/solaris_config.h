#ifndef __API_SOLARIS_CONFIG_H_INCLUDED__
#define __API_SOLARIS_CONFIG_H_INCLUDED__

#ifndef _REENTRANT
#define _REENTRANT
#endif

#define _FILE_OFFSET_BITS  64   /* must be before <sys/types.h> */

#include <sys/types.h>
#include <sys/time.h>
#include <unistd.h>
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
#include <sys/statvfs.h>        /* statvfs() */

#include <sys/filio.h>          /* FIONBIO */
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

#include <sys/systeminfo.h>
#include <limits.h>             /* IOV_MAX */
#include <inttypes.h>
#include <crypt.h>

#define API_ALIGNMENT  _MAX_ALIGNMENT

#if (API_HAVE_POSIX_SEM)
#include <semaphore.h>
#endif


#if (API_HAVE_POLL)
#include <poll.h>
#endif


#if (API_HAVE_DEVPOLL)
#include <sys/ioctl.h>
#include <sys/devpoll.h>
#endif


#if (API_HAVE_EVENTPORT)
#include <port.h>
#endif


#if (API_HAVE_SENDFILE)
#include <sys/sendfile.h>
#endif


#define API_LISTEN_BACKLOG           511


#ifndef API_HAVE_INHERITED_NONBLOCK
#define API_HAVE_INHERITED_NONBLOCK  1
#endif


#ifndef API_HAVE_SO_SNDLOWAT
/* setsockopt(SO_SNDLOWAT) returns ENOPROTOOPT */
#define API_HAVE_SO_SNDLOWAT         0
#endif


#define API_HAVE_OS_SPECIFIC_INIT    1
#define api_debug_init()


extern char **environ;


#endif /*! __API_SOLARIS_CONFIG_H_INCLUDED__ */
