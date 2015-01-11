dnl 
dnl Autoconf configuration file for API
dnl 
dnl Process this file with autoconf to produce a configure script.
dnl Use ./autogen.sh to prepare build files and run autoconf for API.

AC_PREREQ(2.59)
AC_INIT(api, 1.0.5)

AC_CONFIG_SRCDIR([build/api_common.m4])
AC_CONFIG_HEADER([include/api/arch/unix/api_private.h])
AC_CONFIG_AUX_DIR(build)
AC_CONFIG_MACRO_DIR(build)

dnl 
dnl Include our own M4 macros along with those for libtool
dnl 
sinclude(build/api_common.m4)
sinclude(build/api_network.m4)
sinclude(build/api_threads.m4)
sinclude(build/api_win32.m4)
sinclude(build/api_hints.m4)
sinclude(build/api_iconv.m4)

sinclude(build/libtool.m4)
sinclude(build/ltsugar.m4)
sinclude(build/argz.m4)
sinclude(build/ltoptions.m4)
sinclude(build/ltversion.m4)
sinclude(build/lt~obsolete.m4)

sinclude(build/api_hard.m4)
dnl ----------------------------- Check system

dnl Save user-defined environment settings for later restoration
dnl
API_SAVE_THE_ENVIRONMENT(CPPFLAGS)
API_SAVE_THE_ENVIRONMENT(CFLAGS)
API_SAVE_THE_ENVIRONMENT(LDFLAGS)
API_SAVE_THE_ENVIRONMENT(LIBS)
API_SAVE_THE_ENVIRONMENT(INCLUDES)

dnl Generate ./config.nice for reproducing runs of configure
dnl
API_CONFIGURE_PART([Check system])
AC_CANONICAL_SYSTEM
API_CONFIGURE_PART([Configuring API library])
API_CONFIGURE_PART([Platform: $host])

AM_INIT_AUTOMAKE([libapi],[1.0.5])
AM_MAINTAINER_MODE

dnl Some initial steps for configuration.  We setup the default directory
dnl and which files are to be configured.

dnl Setup the directory macros now

# Absolute source/build directory
api_srcdir=`(cd $srcdir && pwd)`
api_builddir=`pwd`
AC_SUBST(api_srcdir)
AC_SUBST(api_builddir)

if test "$api_builddir" != "$api_srcdir"; then
  USE_VPATH=1
  API_CONFIG_LOCATION=build
else
  API_CONFIG_LOCATION=source
fi

AC_SUBST(API_CONFIG_LOCATION)

# Libtool might need this symbol -- it must point to the location of
# the generated libtool script (not necessarily the "top" build dir).
#
top_builddir="$api_builddir"
AC_SUBST(top_builddir)

# Directory containing api build macros, helpers, and make rules
# NOTE: make rules (apr_rules.mk) will be in the builddir for vpath
#
api_buildout=$api_builddir/build
api_builders=$api_srcdir/build
AC_SUBST(api_builders)

# get our version information
get_version="$api_builders/get-version.sh"
version_hdr="$api_srcdir/include/api/version.h"
API_MAJOR_VERSION="`$get_version major $version_hdr API`"
API_DOTTED_VERSION="`$get_version all $version_hdr API`"

AC_SUBST(API_DOTTED_VERSION)
AC_SUBST(API_MAJOR_VERSION)

AC_MSG_NOTICE([API Version: ${API_DOTTED_VERSION}])

dnl ----------------------------- Check C compiler
API_CONFIGURE_PART([Checking C compiler])

dnl Set optional CC hints here in case autoconf makes an inappropriate choice.
dnl This allows us to suggest what the compiler should be, but still
dnl allows the user to override CC externally.
API_CC_HINTS

dnl Do the various CC checks *before* preloading values. The preload code
dnl may need to use compiler characteristics to make decisions. This macro
dnl can only be used once within a configure script, so this prevents a
dnl preload section from invoking the macro to get compiler info.
AC_PROG_CC
AM_PROG_CC_C_O
AC_PROG_CXX

dnl AC_PROG_SED is only avaliable in recent autoconf versions.
dnl Use AC_CHECK_PROG instead if AC_PROG_SED is not present.
ifdef([AC_PROG_SED],
      [AC_PROG_SED],
      [AC_CHECK_PROG(SED, sed, sed)])

dnl ----------------------------- Preload
API_CONFIGURE_PART(Preload...)
API_PRELOAD

dnl Checks for programs.
AC_PROG_MAKE_SET
AC_PROG_CPP
AC_PROG_AWK
AC_PROG_LN_S
AC_PROG_INSTALL
dnl AC_CHECK_PROG(RM, rm, rm)
AC_CHECK_PROG(AS, as, as)
AC_CHECK_PROG(ASCPP, cpp, cpp)
AC_CHECK_TOOL(AR, ar, ar)

dnl Various OS checks that apparently set required flags
ifdef([AC_USE_SYSTEM_EXTENSIONS], [
AC_USE_SYSTEM_EXTENSIONS
], [
AC_AIX
AC_MINIX
])

AC_ISC_POSIX
API_EBCDIC

dnl this is our library name
API_LIBNAME="api${libsuffix}"
AC_SUBST(API_LIBNAME)

dnl ----------------------------- Check base program
API_CONFIGURE_PART([Checking program install])
AC_PROG_INSTALL
API_CONFIGURE_PART([Checking program libtool])
AC_PROG_LIBTOOL

AC_ARG_ENABLE([libapi-install],
     AS_HELP_STRING([--disable-libapi-install, disable installation of libapi]),
	[], [enable_libapi_install=yes])
AM_CONDITIONAL([INSTALL_LIBAPI], [test "$enable_libapi_install" = "yes"])

dnl ----------------------------- Checks for compiler flags
nl='
'
API_CONFIGURE_PART([Check for compiler flags...])
sinclude(build/api_cflags.m4)
sinclude(build/api_options.m4)

dnl ----------------------------- Check event options
API_CONFIGURE_PART([Checking event options])
sinclude([build/api_event.m4])

dnl ----------------------------- Checking for Threads
API_CONFIGURE_PART([Checking for Threads...])
if test -z "$enable_threads"; then
	AC_ARG_ENABLE(threads,
	[  --enable-threads        Enable threading support in API.],
	[ enable_threads=$enableval] ,
	[ API_CHECK_PTHREADS_H([ enable_threads="pthread" ] ,
	                       [ enable_threads="no" ] ) ] )
fi

if test "$enable_threads" = "no"; then
    threads="0"
    pthreadh="0"
    pthreadser="0"
else
    if test "$enable_threads" = "pthread"; then
# We have specified pthreads for our threading library, just make sure
# that we have everything we need
      API_PTHREADS_CHECK_SAVE
      API_PTHREADS_CHECK
      API_CHECK_PTHREADS_H([
          threads="1"
          pthreadh="1"
          pthreadser="1" ], [
          threads="0"
          pthreadh="0"
          pthreadser="0"
          API_PTHREADS_CHECK_RESTORE ] )
    elif test "$enable_threads" = "system_threads"; then
        threads="1"
        pthreadh="0"
        pthreadser="0"
    else
# We basically specified that we wanted threads, but not how to implement
# them.  In this case, just look for pthreads.  In the future, we can check
# for other threading libraries as well.
      API_PTHREADS_CHECK_SAVE
      API_PTHREADS_CHECK
      API_CHECK_PTHREADS_H([
          threads="1"
          pthreadh="1"
          pthreadser="1" ], [
          threads="0"
          pthreadser="0"
          pthreadh="0"
          API_PTHREADS_CHECK_RESTORE ] )
    fi
    if test "$pthreadh" = "1"; then
        API_CHECK_PTHREAD_GETSPECIFIC_TWO_ARGS
        API_CHECK_PTHREAD_ATTR_GETDETACHSTATE_ONE_ARG
        API_CHECK_PTHREAD_RECURSIVE_MUTEX
        AC_CHECK_FUNCS([pthread_key_delete pthread_rwlock_init \
                        pthread_attr_setguardsize pthread_yield])

        if test "$ac_cv_func_pthread_rwlock_init" = "yes"; then
            dnl ----------------------------- Checking for pthread_rwlock_t
            AC_CACHE_CHECK([for pthread_rwlock_t], [api_cv_type_rwlock_t],
            AC_TRY_COMPILE([#include <sys/types.h>
#include <pthread.h>], [pthread_rwlock_t *rwlock;],
              [api_cv_type_rwlock_t=yes], [api_cv_type_rwlock_t=no], 
              [api_cv_type_rwlock_t=no]))
            if test "$api_cv_type_rwlock_t" = "yes"; then
               AC_DEFINE(HAVE_PTHREAD_RWLOCKS, 1, [Define if pthread rwlocks are available])
            fi
        fi
        
		have_schedh="0"
		have_sched_yield="0"
        if test "$ac_cv_func_pthread_yield" = "no"; then
            have_pthread_yield="0"
        else
            have_pthread_yield="1"
        fi
        AC_SUBST(have_pthread_yield)
    fi
fi

ac_cv_define_READDIR_IS_THREAD_SAFE=no
ac_cv_define_GETHOSTBYNAME_IS_THREAD_SAFE=no
ac_cv_define_GETHOSTBYADDR_IS_THREAD_SAFE=no
ac_cv_define_GETSERVBYNAME_IS_THREAD_SAFE=no
if test "$threads" = "1"; then
    AC_MSG_NOTICE([API will use threads])
    AC_CHECK_LIB(c_r, readdir,
        AC_DEFINE(READDIR_IS_THREAD_SAFE, 1, 
                  [Define if readdir is thread safe]))
    if test "x$api_gethostbyname_is_thread_safe" = "x"; then
        AC_CHECK_LIB(c_r, gethostbyname, api_gethostbyname_is_thread_safe=yes)
    fi
    if test "$api_gethostbyname_is_thread_safe" = "yes"; then
        AC_DEFINE(GETHOSTBYNAME_IS_THREAD_SAFE, 1,
                  [Define if gethostbyname is thread safe])
    fi
    if test "x$api_gethostbyaddr_is_thread_safe" = "x"; then
        AC_CHECK_LIB(c_r, gethostbyaddr, api_gethostbyaddr_is_thread_safe=yes)
    fi
    if test "$api_gethostbyaddr_is_thread_safe" = "yes"; then
        AC_DEFINE(GETHOSTBYADDR_IS_THREAD_SAFE, 1, 
                  [Define if gethostbyaddr is thread safe])
    fi
    if test "x$api_getservbyname_is_thread_safe" = "x"; then
        AC_CHECK_LIB(c_r, getservbyname, api_getservbyname_is_thread_safe=yes)
    fi
    if test "$api_getservbyname_is_thread_safe" = "yes"; then
        AC_DEFINE(GETSERVBYNAME_IS_THREAD_SAFE, 1, 
                  [Define if getservbyname is thread safe])
    fi
    AC_CHECK_FUNCS(gethostbyname_r gethostbyaddr_r getservbyname_r)
else
    AC_MSG_NOTICE([API will be non-threaded])
fi

dnl ----------------------------- Checking for signal
API_CONFIGURE_PART([Checking for signal])
have_sigsuspend="0"
AC_CHECK_FUNCS(sigsuspend, [ have_sigsuspend="1" ], [ have_sigsuspend="0" ])
AC_CHECK_FUNCS(sigwait, [ have_sigwait="1" ], [ have_sigwait="0" ]) 

dnl AC_CHECK_FUNCS doesn't work for this on Tru64 since the function
dnl is renamed in signal.h.  Todo: Autodetect
case $host in
    *alpha*-dec-osf* )
        have_sigwait="1"
        ;;
esac

AC_SUBST(threads)
AC_SUBST(have_sigsuspend)
AC_SUBST(have_sigwait)

dnl ----------------------------- Checking for sched_yield
API_CONFIGURE_PART([Checking for sched_yield])
AC_CHECK_HEADERS([sched.h], [ have_schedh="1" ], [ have_schedh="1" ])
AC_CHECK_FUNCS([sched_yield], [ have_sched_yield="1" ], [ have_sched_yield="0" ])
AC_SUBST(have_schedh)
AC_SUBST(have_sched_yield)

dnl ----------------------------- Checks for Any required Headers
API_CONFIGURE_PART([Checks for Any required Headers])
AC_HEADER_STDC
API_FLAG_HEADERS(
    aio.h               \
    ByteOrder.h		\
    conio.h		\
    crypt.h		\
    ctype.h		\
    dir.h		\
    dirent.h		\
    dl.h		\
    dlfcn.h		\
    errno.h		\
    fcntl.h		\
    grp.h		\
    ifaddrs.h           \
    io.h		\
    limits.h		\
    mach-o/dyld.h	\
    malloc.h		\
    memory.h		\
    netdb.h		\
    osreldate.h		\
    poll.h		\
    process.h		\
    pwd.h		\
    semaphore.h		\
    signal.h		\
    stdarg.h		\
    stddef.h		\
    stdio.h		\
    stdlib.h		\
    string.h		\
    strings.h		\
    sysapi.h		\
    sysgtime.h		\
    termios.h		\
    time.h		\
    tpfeq.h		\
    tpfio.h		\
    unistd.h		\
    unix.h		\
    windows.h		\
    winsock2.h		\
    ws2tcpip.h		\
    arpa/inet.h		\
    kernel/OS.h		\
    net/errno.h		\
    netinet/in.h	\
    netinet/sctp.h      \
    netinet/sctp_uio.h  \
    sys/file.h		\
    sys/ioctl.h         \
    sys/mman.h		\
    sys/param.h         \
    sys/poll.h		\
    sys/resource.h	\
    sys/select.h	\
    sys/sem.h		\
    sys/sendfile.h	\
    sys/signal.h	\
    sys/socket.h	\
    sys/sockio.h	\
    sys/stat.h          \
    sys/sysctl.h	\
    sys/syslimits.h	\
    sys/time.h		\
    sys/types.h		\
    sys/uio.h		\
    sys/un.h		\
    sys/wait.h)

# Windows' <mswsock.h> requires <windows.h> first
AC_CACHE_CHECK([for mswsock.h], [api_cv_hdr_mswsock_h],
[AC_TRY_CPP(
[#include <windows.h>
#include <mswsock.h>
], [api_cv_hdr_mswsock_h=yes], [api_cv_hdr_mswsock_h=no])])
if test "$api_cv_hdr_mswsock_h" = "yes"; then
   mswsockh=1
else
   mswsockh=0
fi

# IRIX 6.5 has a problem in <netinet/tcp.h> which prevents it from
# being included by itself.  Check for <netinet/tcp.h> manually,
# including another header file first.
AC_CACHE_CHECK([for netinet/tcp.h], [api_cv_hdr_netinet_tcp_h],
  [AC_TRY_CPP(
    [#ifdef HAVE_NETINET_IN_H
     #include <netinet/in.h>
     #endif
     #include <netinet/tcp.h>
    ], [api_cv_hdr_netinet_tcp_h=yes], [api_cv_hdr_netinet_tcp_h=no])
])

if test "$api_cv_hdr_netinet_tcp_h" = "yes"; then
  netinet_tcph=1
  AC_DEFINE([HAVE_NETINET_TCP_H], 1, [Defined if netinet/tcp.h is present])
else
  netinet_tcph=0
fi

AC_SUBST(aioh)
AC_SUBST(arpa_ineth)
AC_SUBST(conioh)
AC_SUBST(ctypeh)
AC_SUBST(crypth)
AC_SUBST(errnoh)
AC_SUBST(direnth)
AC_SUBST(fcntlh)
AC_SUBST(ioh)
AC_SUBST(ifaddrsh)
AC_SUBST(limitsh)
AC_SUBST(mswsockh)
AC_SUBST(netdbh)
AC_SUBST(netinet_inh)
AC_SUBST(netinet_sctph)
AC_SUBST(netinet_sctp_uioh)
AC_SUBST(netinet_tcph)
AC_SUBST(stdargh)
AC_SUBST(stddefh)
AC_SUBST(stdioh)
AC_SUBST(stdlibh)
AC_SUBST(stringh)
AC_SUBST(stringsh)
AC_SUBST(sys_ioctlh)
AC_SUBST(sys_sendfileh)
AC_SUBST(sys_signalh)
AC_SUBST(sys_socketh)
AC_SUBST(sys_sockioh)
AC_SUBST(sys_syslimitsh)
AC_SUBST(sys_typesh)
AC_SUBST(sys_timeh)
AC_SUBST(sys_uioh)
AC_SUBST(sys_unh)
AC_SUBST(timeh)
AC_SUBST(unistdh)
AC_SUBST(signalh)
AC_SUBST(sys_waith)
AC_SUBST(processh)
AC_SUBST(pthreadh)
AC_SUBST(semaphoreh)
AC_SUBST(windowsh)
AC_SUBST(winsock2h)
AC_SUBST(ws2tcpiph)

dnl ----------------------------- Checks for standard typedefs
API_CONFIGURE_PART([Checks for standard typedefs])
sinclude(build/api_types.m4)

dnl ----------------------------- Checking for string functions
API_CONFIGURE_PART([Checking for string functions...])
sinclude(build/api_strings.m4)
sinclude(build/api_misc.m4)
API_FIND_ICONV

dnl ----------------------------- Checking for Time Support 
API_CONFIGURE_PART([Checking for Time Support...])

AC_CHECK_MEMBERS([struct tm.tm_gmtoff, struct tm.__tm_gmtoff],,,[
#include <sys/types.h>
#include <time.h>])

have_clock_gettime="0"
AC_CHECK_LIB(rt, clock_gettime, have_clock_gettime="1")
# AC_CHECK_FUNCS([clock_gettime], [ have_clock_gettime="1" ], [ have_clock_gettime="0" ])
AC_CHECK_FUNCS([nanosleep], [ have_nanosleep="1" ], [ have_nanosleep="0" ])

AC_SUBST(have_clock_gettime)
AC_SUBST(have_nanosleep)

dnl ----------------------------- Checking for DSO support
API_CONFIGURE_PART([Checking for DSO...])
sinclude(build/api_dso.m4)

dnl ----------------------------- Checking for Processes
API_CONFIGURE_PART([Checking for Processes...])
sinclude(build/api_proc.m4)

dnl ----------------------------- Checking for File Info Support 
API_CONFIGURE_PART([Checking for File Info Support...])

AC_CHECK_MEMBERS([struct stat.st_blocks, struct stat.st_atimensec,
struct stat.st_ctimensec, struct stat.st_mtimensec, struct stat.st_atim.tv_nsec,
struct stat.st_ctim.tv_nsec, struct stat.st_mtim.tv_nsec,
struct stat.st_atime_n, struct stat.st_ctime_n, struct stat.st_mtime_n],,,[
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#ifdef HAVE_SYS_STAT_H
#include <sys/stat.h>
#endif
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif])

API_CHECK_DIRENT_INODE
API_CHECK_DIRENT_TYPE

dnl ----------------------------- Checking for Locking Characteristics 
API_CONFIGURE_PART([Checking for Locking...])
sinclude(build/api_lock.m4)

dnl ----------------------------- Checking for Networking Support 
API_CONFIGURE_PART([Checking for Networking support...])

API_TYPE_IN_ADDR
if test "$ac_cv_type_in_addr" = "yes"; then
  have_in_addr="1"
else
  have_in_addr="0"
fi

AC_MSG_CHECKING([if fd == socket on this platform])
if test "x$file_as_socket" != "x0" ; then
    file_as_socket="1";
    AC_MSG_RESULT([yes])
else
    AC_MSG_RESULT([no])
fi

AC_SUBST(have_in_addr)
AC_SUBST(file_as_socket)

if test "$ac_cv_func_poll $file_as_socket" = "yes 1"; then
    AC_DEFINE(WAITIO_USES_POLL, 1,
              [Define if api_wait_for_io_or_timeout() uses poll(2)])
fi

# Check the types only if we have gethostbyname_r
if test "$ac_cv_func_gethostbyname_r" = "yes"; then
    API_CHECK_GETHOSTBYNAME_R_STYLE
fi

# Check the types only if we have getservbyname_r
if test "$ac_cv_func_getservbyname_r" = "yes"; then
    API_CHECK_GETSERVBYNAME_R_STYLE
fi

API_CHECK_TCP_NODELAY_INHERITED
API_CHECK_O_NONBLOCK_INHERITED
API_CHECK_TCP_NODELAY_WITH_CORK

# Look for a way of corking TCP...
API_CHECK_DEFINE(TCP_CORK, netinet/tcp.h)
API_CHECK_DEFINE(TCP_NOPUSH, netinet/tcp.h)
api_tcp_nopush_flag="0"
have_corkable_tcp="0"
if test "x$ac_cv_define_TCP_CORK" = "xyes"; then
    api_tcp_nopush_flag="TCP_CORK"
    have_corkable_tcp="1"
else
    case $host in
        *linux*)
            AC_EGREP_CPP(yes,[
#include <linux/socket.h>
#ifdef TCP_CORK
yes
#endif
            ],[
                api_tcp_nopush_flag="3"
                have_corkable_tcp="1"
            ])
            ;;
        *)
            ;;
    esac
fi
if test "x$ac_cv_define_TCP_NOPUSH" = "xyes"; then
    api_tcp_nopush_flag="TCP_NOPUSH"
    have_corkable_tcp="1"
fi

API_CHECK_DEFINE(SO_ACCEPTFILTER, sys/socket.h)
if test "x$ac_cv_define_SO_ACCEPTFILTER" = "xyes"; then
    acceptfilter="1"
else
    acceptfilter="0"
fi

API_CHECK_SCTP
API_CHECK_MCAST

AC_SUBST(api_tcp_nopush_flag)
AC_SUBST(have_corkable_tcp)
AC_SUBST(acceptfilter)
AC_SUBST(have_sctp)

AC_CHECK_FUNCS(set_h_errno)

dnl ----------------------------- Start of checking for IPv6 support...
API_CONFIGURE_PART([Checking for IPv6 Networking support...])

AC_ARG_ENABLE(ipv6,
  [  --disable-ipv6          Disable IPv6 support in API.],
  [ if test "$enableval" = "no"; then
        user_disabled_ipv6=1
    fi ],
  [ user_disabled_ipv6=0 ] )

case $host in
  *)
    broken_ipv6=0
esac

AC_SEARCH_LIBS(getaddrinfo, socket inet6)
AC_SEARCH_LIBS(gai_strerror, socket inet6)
AC_SEARCH_LIBS(getnameinfo, socket inet6)
AC_CHECK_FUNCS(gai_strerror)
API_CHECK_WORKING_GETADDRINFO
API_CHECK_NEGATIVE_EAI
API_CHECK_WORKING_GETNAMEINFO
API_CHECK_SOCKADDR_IN6
API_CHECK_SOCKADDR_STORAGE
API_CHECK_SOCKADDR_UN

have_ipv6="0"
if test "$user_disabled_ipv6" = 1; then
    ipv6_result="no -- disabled by user"
else
    if test "x$broken_ipv6" = "x0"; then
        if test "x$have_sockaddr_in6" = "x1"; then
            if test "x$ac_cv_working_getaddrinfo" = "xyes"; then
                if test "x$ac_cv_working_getnameinfo" = "xyes"; then
                    API_CHECK_GETADDRINFO_ADDRCONFIG
                    have_ipv6="1"
                    ipv6_result="yes"
                else
                    ipv6_result="no -- no getnameinfo"
                fi
            else
                ipv6_result="no -- no working getaddrinfo"
            fi
        else
            ipv6_result="no -- no sockaddr_in6"
        fi
    else
        ipv6_result="no -- the platform has known problems supporting IPv6"
    fi
fi

AC_MSG_CHECKING(if API supports IPv6)
AC_MSG_RESULT($ipv6_result)

AC_SUBST(have_ipv6)

# hstrerror is only needed if IPv6 is not enabled,
# so getaddrinfo/gai_strerror are not used.
if test $have_ipv6 = 0; then
  AC_SEARCH_LIBS(hstrerror, resolv,
    [AC_DEFINE(HAVE_HSTRERROR, 1, [Define if hstrerror is present])])
fi

dnl ------------------------------ Check for langinfo support
API_CONFIGURE_PART([Check for langinfo support])
AC_CHECK_HEADERS(langinfo.h)
AC_CHECK_FUNCS(nl_langinfo)

dnl ----------------------------- Checking for poll/kqueue/port_create/epoll
API_CONFIGURE_PART([Checking for poll/kqueue/port_create/epoll])
AC_CHECK_FUNCS(poll kqueue port_create)

# Check for the Linux epoll interface; epoll* may be available in libc
# but return ENOSYS on a pre-2.6 kernel, so do a run-time check.
AC_CACHE_CHECK([for epoll support], [api_cv_epoll],
[AC_TRY_RUN([
#include <sys/epoll.h>
#include <unistd.h>

int main()
{
    return epoll_create(5) == -1;
}], [api_cv_epoll=yes], [api_cv_epoll=no], [api_cv_epoll=no])])

if test "$api_cv_epoll" = "yes"; then
   AC_DEFINE([HAVE_EPOLL], 1, [Define if the epoll interface is supported])
fi

dnl ----------------------------- Checking for extended file descriptor handling
API_CONFIGURE_PART([Checking for extended file descriptor handling])
# test for epoll_create1
AC_CACHE_CHECK([for epoll_create1 support], [api_cv_epoll_create1],
[AC_TRY_RUN([
#include <sys/epoll.h>
#include <unistd.h>

int main()
{
    return epoll_create1(0) == -1;
}], [api_cv_epoll_create1=yes], [api_cv_epoll_create1=no], [api_cv_epoll_create1=no])])

if test "$api_cv_epoll_create1" = "yes"; then
   AC_DEFINE([HAVE_EPOLL_CREATE1], 1, [Define if epoll_create1 function is supported])
fi

# Check for z/OS async i/o support.  
AC_CACHE_CHECK([for asio -> message queue support], [api_cv_aio_msgq],
[AC_TRY_RUN([
#define _AIO_OS390
#include <aio.h>
int main()
{
    struct aiocb a;
    a.aio_notifytype = AIO_MSGQ;  /* use IPC message queue for notification */
    return aio_cancel(2, NULL) == -1;
}], [api_cv_aio_msgq=yes], [api_cv_aio_msgq=no], [api_cv_aio_msgq=no])])

if test "$api_cv_aio_msgq" = "yes"; then
   AC_DEFINE([HAVE_AIO_MSGQ], 1, [Define if async i/o supports message q's])
fi

# test for dup3
AC_CACHE_CHECK([for dup3 support], [api_cv_dup3],
[AC_TRY_RUN([
#include <unistd.h>

int main()
{
    return dup3(STDOUT_FILENO, STDERR_FILENO, 0) == -1;
}], [api_cv_dup3=yes], [api_cv_dup3=no], [api_cv_dup3=no])])

if test "$api_cv_dup3" = "yes"; then
   AC_DEFINE([HAVE_DUP3], 1, [Define if dup3 function is supported])
fi

# Test for accept4().  Create a non-blocking socket, bind it to
# an unspecified port & address (kernel picks), and attempt to
# call accept4() on it.  If the syscall is wired up (i.e. the
# kernel is new enough), it should return EAGAIN.
AC_CACHE_CHECK([for accept4 support], [api_cv_accept4],
[AC_TRY_RUN([
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>

int main(int argc, char **argv)
{
    int fd, flags;
    struct sockaddr_in sin;

    if ((fd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
        return 1;
    flags = fcntl(fd, F_GETFL);
    if (flags == -1 || fcntl(fd, F_SETFL, flags|O_NONBLOCK) == -1)
        return 5;

    memset(&sin, 0, sizeof sin);
    sin.sin_family = AF_INET;
    
    if (bind(fd, (struct sockaddr *) &sin, sizeof sin) == -1)
        return 2;
    
    if (listen(fd, 5) == -1)
        return 3;

    if (accept4(fd, NULL, 0, SOCK_NONBLOCK) == 0
        || errno == EAGAIN || errno == EWOULDBLOCK)
        return 0;

    return 4;
}], [api_cv_accept4=yes], [api_cv_accept4=no], [api_cv_accept4=no])])

if test "$api_cv_accept4" = "yes"; then
   AC_DEFINE([HAVE_ACCEPT4], 1, [Define if accept4 function is supported])
fi

AC_CACHE_CHECK([for SOCK_CLOEXEC support], [api_cv_sock_cloexec],
[AC_TRY_RUN([
#include <sys/types.h>
#include <sys/socket.h>

int main()
{
    return socket(AF_INET, SOCK_STREAM|SOCK_CLOEXEC, 0) == -1;
}], [api_cv_sock_cloexec=yes], [api_cv_sock_cloexec=no], [api_cv_sock_cloexec=no])])

if test "$api_cv_sock_cloexec" = "yes"; then
   AC_DEFINE([HAVE_SOCK_CLOEXEC], 1, [Define if the SOCK_CLOEXEC flag is supported])
fi

dnl ----------------------------- Checking for fdatasync: OS X doesn't have it
API_CONFIGURE_PART([Checking for fdatasync: OS X doesn't have it])
AC_CHECK_FUNCS(fdatasync)

dnl ----------------------------- Checking for missing POSIX thread functions
AC_CHECK_FUNCS([getpwnam_r getpwuid_r getgrnam_r getgrgid_r])

dnl ----------------------------- Checking for Shared Memory Support 
API_CONFIGURE_PART([Checking for Shared Memory Support])
sinclude(build/api_shm.m4)

have_posix_memalign="0"
AC_CACHE_CHECK([for posix_memalign support], [api_cv_posix_memalign],
[AC_TRY_RUN([
#include <stdlib.h>

int main()
{
    void *p;
    int n;
    n = posix_memalign(&p, 4096, 4096);
    if (n != 0) return 1;
    return 0x0;
}], [api_cv_posix_memalign=yes], [api_cv_posix_memalign=no], [api_cv_posix_memalign=no])])

if test "$api_cv_posix_memalign" = "yes"; then
   AC_DEFINE([HAVE_POSIX_MEMALIGN], 1, [Define if the posix_memalign interface is supported])
   have_posix_memalign="1"
fi
AC_SUBST(have_posix_memalign)

have_memalign="0"
AC_CACHE_CHECK([for memalign support], [api_cv_memalign],
[AC_TRY_RUN([
#include <stdlib.h>
#include <malloc.h>

int main()
{
   	void *p;
   	p = memalign(4096, 4096);
    if (p == NULL) return 1;
    return 0x0;
}], [api_cv_memalign=yes], [api_cv_memalign=no], [api_cv_memalign=no])])

if test "$api_cv_memalign" = "yes"; then
   AC_DEFINE([HAVE_MEMALIGN], 1, [Define if the memalign interface is supported])
   have_memalign="1"
fi
AC_SUBST(have_memalign)

dnl ----------------------------- Checks for Any required Functions
dnl Checks for library functions. (N.B. poll is further down)

AC_FUNC_ALLOCA

AC_CHECK_FUNCS([calloc setsid isinf isnan \
                getenv putenv setenv unsetenv \
                writev getifaddrs utime utimes])
AC_CHECK_FUNCS(setrlimit, [ have_setrlimit="1" ], [ have_setrlimit="0" ]) 
AC_CHECK_FUNCS(getrlimit, [ have_getrlimit="1" ], [ have_getrlimit="0" ]) 
sendfile="0"
AC_CHECK_LIB(sendfile, sendfilev)
AC_CHECK_FUNCS(sendfile send_file sendfilev, [ sendfile="1" ])

dnl THIS MUST COME AFTER THE THREAD TESTS - FreeBSD doesn't always have a
dnl threaded poll() and we don't want to use sendfile on early FreeBSD 
dnl systems if we are also using threads.

AC_ARG_WITH(sendfile, [  --with-sendfile         Override decision to use sendfile],
  [ if test "$withval" = "yes"; then
        sendfile="1"
    else
        sendfile="0"
    fi ], [
    orig_sendfile=$sendfile
    case $host in
        *freebsd*)
            # FreeBSD < 4.2 has issues with threads+sendfile
            if test $os_version -le "401999"; then
                if test "$threads" = "1"; then
                    sendfile="0"
                fi
            fi
            ;;
        *alpha*-dec-osf* )
            sendfile="0"
            ;;
        s390-*-linux-gnu)
            # disable sendfile support for 2.2 on S/390
            if test $os_pre24linux -eq 1; then
                AC_MSG_WARN([Disabled sendfile support for Linux 2.2 on S/390])
                sendfile="0"
            fi
            ;;
        *aix*)
            # compiler-independent check for 64-bit build
            AC_CHECK_SIZEOF(void*, 4)
            if test "x$ac_cv_sizeof_voidp" = "x8"; then
                # sendfile not working for 64-bit build
                sendfile="0"
            fi
            ;;
    esac       
    if test "$orig_sendfile" != "$sendfile"; then
      AC_MSG_NOTICE([sendfile support disabled to avoid system problem])
    fi ] )
AC_SUBST(sendfile)

AC_CHECK_FUNCS(sigaction, [ have_sigaction="1" ], [ have_sigaction="0" ]) 
AC_DECL_SYS_SIGLIST

AC_CHECK_FUNCS(fork, [ fork="1" ], [ fork="0" ])
API_CHECK_INET_ADDR
API_CHECK_INET_NETWORK
AC_SUBST(api_inaddr_none)
AC_CHECK_FUNC(_getch)
AC_CHECK_FUNCS(strerror_r, [ strerror_r="1" ], [ strerror_r="0" ])
if test "$strerror_r" = "1"; then
  API_CHECK_STRERROR_R_RC
fi
AC_CHECK_FUNCS(mmap, [ mmap="1" ], [ mmap="0" ])
if test "$native_mmap_emul" = "1"; then
    mmap="1"
fi
AC_CHECK_FUNCS(memmove, [ have_memmove="1" ], [have_memmove="0" ])
AC_CHECK_FUNCS([getpass getpassphrase gmtime_r localtime_r mkstemp])

AC_SUBST(fork)
AC_SUBST(have_inet_addr)
AC_SUBST(tcp_nodelay_inherited)
AC_SUBST(o_nonblock_inherited)
AC_SUBST(have_inet_network)
AC_SUBST(have_sigaction)
AC_SUBST(have_setrlimit)
AC_SUBST(have_getrlimit)
AC_SUBST(mmap)
AC_SUBST(have_memmove)

API_CHECK_SIGWAIT_ONE_ARG

dnl ------------------------------ Defaults for some platform nuances
API_CONFIGURE_PART([Defaults for some platform nuances])

dnl Do we have a Win32-centric Unicode FS?
API_SETIFNULL(have_unicode_fs, [0])
AC_SUBST(have_unicode_fs)

API_SETIFNULL(api_has_xthread_files, [0])
AC_SUBST(api_has_xthread_files)

API_SETIFNULL(api_procattr_user_set_requires_password, [0])
AC_SUBST(api_procattr_user_set_requires_password)

API_SETIFNULL(api_thread_func, [])
AC_SUBST(api_thread_func)

API_SETIFNULL(api_has_user, [1])
AC_SUBST(api_has_user)

dnl XXX FIXME; used for -lexpat, -liconv etc?
AC_SUBST(API_EXPORT_LIBS)

dnl
dnl Prep all the flags and stuff for compilation and export to other builds
dnl
API_ADDTO(LIBS, [$API_LIBS])

AC_SUBST(LDFLAGS)


dnl ----------------------------- Configure options
API_CONFIGURE_PART([Configure options])
echo ""
echo "API has been configured with the following options:"
echo "              Host: ${host}"
echo "          Compiler: ${CC}"
echo "    Compiler flags: ${CFLAGS}"
echo "Preprocessor flags: ${CPPFLAGS}"
echo "      Linker flags: ${LDFLAGS}"
echo "         Libraries: ${LIBS}"

dnl ----------------------------- Config files
API_CONFIGURE_PART([Configure files])
AC_CONFIG_FILES(Makefile \
                include/api/platform.h \
                base/Makefile \
                event/Makefile \
                compat/Makefile \
                samples/Makefile \
                samples/conhash/Makefile \
                samples/socks5/Makefile \
                test/Makefile \
                libapi.pc )

AC_OUTPUT

