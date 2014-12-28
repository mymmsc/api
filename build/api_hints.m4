dnl -------------------------------------------------------- -*- autoconf -*-
dnl
dnl api_hints.m4: API's autoconf macros for platform-specific hints
dnl
dnl  We preload various configure settings depending
dnl  on previously obtained platform knowledge.
dnl  We allow all settings to be overridden from
dnl  the command-line.
dnl
dnl  We maintain the "format" that we've used
dnl  under 1.3.x, so we don't exactly follow
dnl  what is "recommended" by autoconf.

dnl
dnl API_PRELOAD
dnl
dnl  Preload various ENV/makefile params such as CC, CFLAGS, etc
dnl  based on outside knowledge
dnl
dnl  Generally, we force the setting of CC, and add flags
dnl  to CFLAGS, CPPFLAGS, LIBS and LDFLAGS. 
dnl
AC_DEFUN([API_PRELOAD], [
if test "x$api_preload_done" != "xyes" ; then

  api_preload_done="yes"

  echo "Applying API hints file rules for $host"

  case "$host" in
    *mint)
	API_ADDTO(CPPFLAGS, [-DMINT -D_GNU_SOURCE])
	;;
    *MPE/iX*)
	API_ADDTO(CPPFLAGS, [-DMPE -D_POSIX_SOURCE -D_SOCKET_SOURCE])
	API_ADDTO(LIBS, [-lsvipc -lcurses])
	API_ADDTO(LDFLAGS, [-Xlinker \"-WL,cap=ia,ba,ph;nmstack=1024000\"])
	;;
    *-apple-aux3*)
	API_ADDTO(CPPFLAGS, [-DAUX3 -D_POSIX_SOURCE])
	API_ADDTO(LIBS, [-lposix -lbsd])
	API_ADDTO(LDFLAGS, [-s])
	API_SETVAR(SHELL, [/bin/ksh])
	;;
    *-ibm-aix*)
	API_ADDTO(CPPFLAGS, [-U__STR__ -D_THREAD_SAFE])
        dnl _USR_IRS gets us the hstrerror() proto in netdb.h
        case $host in
            *-ibm-aix4.3)
	        API_ADDTO(CPPFLAGS, [-D_USE_IRS])
	        ;;
            *-ibm-aix5*)
	        API_ADDTO(CPPFLAGS, [-D_USE_IRS])
	        ;;
            *-ibm-aix4.3.*)
                API_ADDTO(CPPFLAGS, [-D_USE_IRS])
                ;;
        esac
        dnl If using xlc, remember it, and give it the right options.
        if $CC 2>&1 | grep 'xlc' > /dev/null; then
          API_SETIFNULL(AIX_XLC, [yes])
          API_ADDTO(CFLAGS, [-qHALT=E])
        fi
	API_SETIFNULL(api_sysvsem_is_global, [yes])
	API_SETIFNULL(api_lock_method, [USE_SYSVSEM_SERIALIZE])
        case $host in
            *-ibm-aix3* | *-ibm-aix4.1.*)
                ;;
            *)
                API_ADDTO(LDFLAGS, [-Wl,-brtl])
                ;;
	esac
        ;;
    *-apollo-*)
	API_ADDTO(CPPFLAGS, [-DAPOLLO])
	;;
    *-dg-dgux*)
	API_ADDTO(CPPFLAGS, [-DDGUX])
	;;
    *-os2*)
	API_SETVAR(SHELL, [sh])
	API_SETIFNULL(api_gethostbyname_is_thread_safe, [yes])
	API_SETIFNULL(api_gethostbyaddr_is_thread_safe, [yes])
	API_SETIFNULL(api_getservbyname_is_thread_safe, [yes])
	;;
    *-hi-hiux)
	API_ADDTO(CPPFLAGS, [-DHIUX])
	;;
    *-hp-hpux11.*)
	API_ADDTO(CPPFLAGS, [-DHPUX11 -D_REENTRANT -D_HPUX_SOURCE])
	;;
    *-hp-hpux10.*)
 	case $host in
 	  *-hp-hpux10.01)
dnl	       # We know this is a problem in 10.01.
dnl	       # Not a problem in 10.20.  Otherwise, who knows?
	       API_ADDTO(CPPFLAGS, [-DSELECT_NEEDS_CAST])
	       ;;	     
 	esac
	API_ADDTO(CPPFLAGS, [-D_REENTRANT])
	;;
    *-hp-hpux*)
	API_ADDTO(CPPFLAGS, [-DHPUX -D_REENTRANT])
	;;
    *-linux*)
	API_ADDTO(CPPFLAGS, [-DLINUX -D_REENTRANT -D_GNU_SOURCE])
	;;
    *-lynx-lynxos)
	API_ADDTO(CPPFLAGS, [-D__NO_INCLUDE_WARN__ -DLYNXOS])
	API_ADDTO(LIBS, [-lbsd])
	;;
    *486-*-bsdi*)
	API_ADDTO(CFLAGS, [-m486])
	;;
    *-*-bsdi*)
        case $host in
            *bsdi4.1)
                API_ADDTO(CFLAGS, [-D_REENTRANT])
                ;;
        esac
        ;;
    *-openbsd*)
	API_ADDTO(CPPFLAGS, [-D_POSIX_THREADS])
        # binding to an ephemeral port fails on OpenBSD so override
        # the test for O_NONBLOCK inheritance across accept().
        API_SETIFNULL(ac_cv_o_nonblock_inherited, [yes])
	;;
    *-netbsd*)
	API_ADDTO(CPPFLAGS, [-DNETBSD])
        # fcntl() lies about O_NONBLOCK on an accept()ed socket (PR kern/26950)
        API_SETIFNULL(ac_cv_o_nonblock_inherited, [yes])
	;;
    *-freebsd*)
        API_SETIFNULL(api_lock_method, [USE_FLOCK_SERIALIZE])
        if test -x /sbin/sysctl; then
            os_version=`/sbin/sysctl -n kern.osreldate`
        else
            os_version=000000
        fi
        # 502102 is when libc_r switched to libpthread (aka libkse).
        if test $os_version -ge "502102"; then
          api_cv_pthreads_cflags="none"
          api_cv_pthreads_lib="-lpthread"
        else
          API_ADDTO(CPPFLAGS, [-D_THREAD_SAFE -D_REENTRANT])
          API_SETIFNULL(enable_threads, [no])
        fi
        # prevent use of KQueue before FreeBSD 4.8
        if test $os_version -lt "480000"; then
          API_SETIFNULL(ac_cv_func_kqueue, no)
        fi
	;;
    *-k*bsd*-gnu)
        API_ADDTO(CPPFLAGS, [-D_REENTRANT -D_GNU_SOURCE])
        ;;
    *-gnu*|*-GNU*)
        API_ADDTO(CPPFLAGS, [-D_REENTRANT -D_GNU_SOURCE -DHURD])
        ;;
    *-next-nextstep*)
	API_SETIFNULL(CFLAGS, [-O])
	API_ADDTO(CPPFLAGS, [-DNEXT])
	;;
    *-next-openstep*)
	API_SETIFNULL(CFLAGS, [-O])
	API_ADDTO(CPPFLAGS, [-DNEXT])
	;;
    *-apple-rhapsody*)
	API_ADDTO(CPPFLAGS, [-DRHAPSODY])
	;;
    *-apple-darwin*)
        API_ADDTO(CPPFLAGS, [-DDARWIN -DSIGPROCMASK_SETS_THREAD_MASK -no-cpp-precomp])
        API_SETIFNULL(api_posixsem_is_global, [yes])
        case $host in
            *-apple-darwin[[1-9]].*)
                # API's use of kqueue has triggered kernel panics for some
                # 10.5.x (Darwin 9.x) users when running the entire test suite.
                # In 10.4.x, use of kqueue would cause the socket tests to hang.
                # 10.6+ (Darwin 10.x is supposed to fix the KQueue issues
                API_SETIFNULL(ac_cv_func_kqueue, [no]) 
                API_SETIFNULL(ac_cv_func_poll, [no]) # See issue 34332
            ;;
            *-apple-darwin1?.*)
                API_ADDTO(CPPFLAGS, [-DDARWIN_10])
            ;;
        esac
	;;
    *-dec-osf*)
	API_ADDTO(CPPFLAGS, [-DOSF1])
        # process-shared mutexes don't seem to work in Tru64 5.0
        API_SETIFNULL(api_cv_process_shared_works, [no])
	;;
    *-nto-qnx*)
	;;
    *-qnx)
	API_ADDTO(CPPFLAGS, [-DQNX])
	API_ADDTO(LIBS, [-N128k -lunix])
	;;
    *-qnx32)
	API_ADDTO(CPPFLAGS, [-DQNX])
	API_ADDTO(CFLAGS, [-mf -3])
	API_ADDTO(LIBS, [-N128k -lunix])
	;;
    *-isc4*)
	API_ADDTO(CPPFLAGS, [-posix -DISC])
	API_ADDTO(LDFLAGS, [-posix])
	API_ADDTO(LIBS, [-linet])
	;;
    *-sco3.2v[[234]]*)
	API_ADDTO(CPPFLAGS, [-DSCO -D_REENTRANT])
	if test "$GCC" = "no"; then
	    API_ADDTO(CFLAGS, [-Oacgiltz])
	fi
	API_ADDTO(LIBS, [-lPW -lmalloc])
	;;
    *-sco3.2v5*)
	API_ADDTO(CPPFLAGS, [-DSCO5 -D_REENTRANT])
	;;
    *-sco_sv*|*-SCO_SV*)
	API_ADDTO(CPPFLAGS, [-DSCO -D_REENTRANT])
	API_ADDTO(LIBS, [-lPW -lmalloc])
	;;
    *-solaris2*)
    	PLATOSVERS=`echo $host | sed 's/^.*solaris2.//'`
	API_ADDTO(CPPFLAGS, [-DSOLARIS2=$PLATOSVERS -D_POSIX_PTHREAD_SEMANTICS -D_REENTRANT])
        if test $PLATOSVERS -ge 10; then
            API_SETIFNULL(api_lock_method, [USE_PROC_PTHREAD_SERIALIZE])
        else
            API_SETIFNULL(api_lock_method, [USE_FCNTL_SERIALIZE])
        fi
        # readdir64_r error handling seems broken on Solaris (at least
        # up till 2.8) -- it will return -1 at end-of-directory.
        API_SETIFNULL(ac_cv_func_readdir64_r, [no])
	;;
    *-sunos4*)
	API_ADDTO(CPPFLAGS, [-DSUNOS4])
	;;
    *-unixware1)
	API_ADDTO(CPPFLAGS, [-DUW=100])
	;;
    *-unixware2)
	API_ADDTO(CPPFLAGS, [-DUW=200])
	API_ADDTO(LIBS, [-lgen])
	;;
    *-unixware211)
	API_ADDTO(CPPFLAGS, [-DUW=211])
	API_ADDTO(LIBS, [-lgen])
	;;
    *-unixware212)
	API_ADDTO(CPPFLAGS, [-DUW=212])
	API_ADDTO(LIBS, [-lgen])
	;;
    *-unixware7)
	API_ADDTO(CPPFLAGS, [-DUW=700])
	API_ADDTO(LIBS, [-lgen])
	;;
    maxion-*-sysv4*)
	API_ADDTO(CPPFLAGS, [-DSVR4])
	API_ADDTO(LIBS, [-lc -lgen])
	;;
    *-*-powermax*)
	API_ADDTO(CPPFLAGS, [-DSVR4])
	API_ADDTO(LIBS, [-lgen])
	;;
    TPF)
       API_ADDTO(CPPFLAGS, [-DTPF -D_POSIX_SOURCE])
       ;;
    bs2000*-siemens-sysv*)
	API_SETIFNULL(CFLAGS, [-O])
	API_ADDTO(CPPFLAGS, [-DSVR4 -D_XPG_IV -D_KMEMUSER])
	API_ADDTO(LIBS, [-lsocket])
	API_SETIFNULL(enable_threads, [no])
	;;
    *-siemens-sysv4*)
	API_ADDTO(CPPFLAGS, [-DSVR4 -D_XPG_IV -DHAS_DLFCN -DUSE_MMAP_FILES -DUSE_SYSVSEM_SERIALIZED_ACCEPT])
	API_ADDTO(LIBS, [-lc])
	;;
    pyramid-pyramid-svr4)
	API_ADDTO(CPPFLAGS, [-DSVR4 -DNO_LONG_DOUBLE])
	API_ADDTO(LIBS, [-lc])
	;;
    DS/90\ 7000-*-sysv4*)
	API_ADDTO(CPPFLAGS, [-DUXPDS])
	;;
    *-tandem-sysv4*)
	API_ADDTO(CPPFLAGS, [-DSVR4])
	;;
    *-ncr-sysv4)
	API_ADDTO(CPPFLAGS, [-DSVR4 -DMPRAS])
	API_ADDTO(LIBS, [-lc -L/usr/ucblib -lucb])
	;;
    *-sysv4*)
	API_ADDTO(CPPFLAGS, [-DSVR4])
	API_ADDTO(LIBS, [-lc])
	;;
    88k-encore-sysv4)
	API_ADDTO(CPPFLAGS, [-DSVR4 -DENCORE])
	API_ADDTO(LIBS, [-lPW])
	;;
    *-uts*)
	PLATOSVERS=`echo $host | sed 's/^.*,//'`
	case $PLATOSVERS in
	    2*) API_ADDTO(CPPFLAGS, [-DUTS21])
	        API_ADDTO(CFLAGS, [-Xa -eft])
	        API_ADDTO(LIBS, [-lbsd -la])
	        ;;
	    *)  API_ADDTO(CPPFLAGS, [-DSVR4])
	        API_ADDTO(CFLAGS, [-Xa])
	        ;;
	esac
	;;
    *-ultrix)
	API_ADDTO(CPPFLAGS, [-DULTRIX])
	API_SETVAR(SHELL, [/bin/sh5])
	;;
    *powerpc-tenon-machten*)
	API_ADDTO(LDFLAGS, [-Xlstack=0x14000 -Xldelcsect])
	;;
    *-machten*)
	API_ADDTO(LDFLAGS, [-stack 0x14000])
	;;
    *convex-v11*)
	API_ADDTO(CPPFLAGS, [-DCONVEXOS11])
	API_SETIFNULL(CFLAGS, [-O1])
	API_ADDTO(CFLAGS, [-ext])
	;;
    i860-intel-osf1)
	API_ADDTO(CPPFLAGS, [-DPARAGON])
	;;
    *-sequent-ptx2.*.*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=20])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-linet -lc -lseq])
	;;
    *-sequent-ptx4.0.*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=40])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-linet -lc])
	;;
    *-sequent-ptx4.[[123]].*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=41])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-lc])
	;;
    *-sequent-ptx4.4.*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=44])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-lc])
	;;
    *-sequent-ptx4.5.*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=45])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-lc])
	;;
    *-sequent-ptx5.0.*)
	API_ADDTO(CPPFLAGS, [-DSEQUENT=50])
	API_ADDTO(CFLAGS, [-Wc,-pw])
	API_ADDTO(LIBS, [-lc])
	;;
    *NEWS-OS*)
	API_ADDTO(CPPFLAGS, [-DNEWSOS])
	;;
    *-riscix)
	API_ADDTO(CPPFLAGS, [-DRISCIX])
	API_SETIFNULL(CFLAGS, [-O])
	;;
    *-irix*)
	API_ADDTO(CPPFLAGS, [-D_POSIX_THREAD_SAFE_FUNCTIONS])
	;;
    *beos*)
        API_ADDTO(CPPFLAGS, [-DBEOS])
        PLATOSVERS=`uname -r`
        API_SETIFNULL(api_process_lock_is_global, [yes])
        case $PLATOSVERS in
            5.0.4)
                API_ADDTO(LDFLAGS, [-L/boot/beos/system/lib])
                API_ADDTO(LIBS, [-lbind -lsocket])
                API_ADDTO(CPPFLAGS,[-DBONE7])
                ;;
            5.1)
                API_ADDTO(LDFLAGS, [-L/boot/beos/system/lib])
                API_ADDTO(LIBS, [-lbind -lsocket])
                ;;
	esac
	API_ADDTO(CPPFLAGS, [-DSIGPROCMASK_SETS_THREAD_MASK])
        ;;
    4850-*.*)
	API_ADDTO(CPPFLAGS, [-DSVR4 -DMPRAS])
	API_ADDTO(LIBS, [-lc -L/usr/ucblib -lucb])
	;;
    drs6000*)
	API_ADDTO(CPPFLAGS, [-DSVR4])
	API_ADDTO(LIBS, [-lc -L/usr/ucblib -lucb])
	;;
    m88k-*-CX/SX|CYBER)
	API_ADDTO(CPPFLAGS, [-D_CX_SX])
	API_ADDTO(CFLAGS, [-Xa])
	;;
    *-tandem-oss)
	API_ADDTO(CPPFLAGS, [-D_TANDEM_SOURCE -D_XOPEN_SOURCE_EXTENDED=1])
	;;
    *-ibm-os390)
        API_SETIFNULL(api_lock_method, [USE_SYSVSEM_SERIALIZE])
        API_SETIFNULL(api_sysvsem_is_global, [yes])
        API_SETIFNULL(api_gethostbyname_is_thread_safe, [yes])
        API_SETIFNULL(api_gethostbyaddr_is_thread_safe, [yes])
        API_SETIFNULL(api_getservbyname_is_thread_safe, [yes])
        AC_DEFINE(HAVE_ZOS_PTHREADS, 1, [Define for z/OS pthread API nuances])
        API_ADDTO(CPPFLAGS, [-U_NO_PROTO -DSIGPROCMASK_SETS_THREAD_MASK -DTCP_NODELAY=1])
        ;;
    *-ibm-as400)
        API_SETIFNULL(api_lock_method, [USE_SYSVSEM_SERIALIZE])
        API_SETIFNULL(api_process_lock_is_global, [yes])
        API_SETIFNULL(api_gethostbyname_is_thread_safe, [yes])
        API_SETIFNULL(api_gethostbyaddr_is_thread_safe, [yes])
        API_SETIFNULL(api_getservbyname_is_thread_safe, [yes])
        ;;
    *mingw*)
        API_ADDTO(INTERNAL_CPPFLAGS, -DBINPATH=$api_builddir/test/.libs)
        API_ADDTO(CPPFLAGS, [-DWIN32 -D__MSVCRT__])
        API_ADDTO(LDFLAGS, [-Wl,--enable-auto-import,--subsystem,console])
        API_SETIFNULL(have_unicode_fs, [1])
        API_SETIFNULL(have_proc_invoked, [1])
        API_SETIFNULL(api_lock_method, [win32])
        API_SETIFNULL(api_process_lock_is_global, [yes])
        API_SETIFNULL(api_cv_use_lfs64, [yes])
        API_SETIFNULL(api_cv_osuuid, [yes])
        API_SETIFNULL(api_cv_tcp_nodelay_with_cork, [no])
        API_SETIFNULL(api_thread_func, [__stdcall])
        API_SETIFNULL(ac_cv_o_nonblock_inherited, [yes])
        API_SETIFNULL(ac_cv_tcp_nodelay_inherited, [yes])
        API_SETIFNULL(ac_cv_file__dev_zero, [no])
        API_SETIFNULL(ac_cv_func_setpgrp_void, [no])
        API_SETIFNULL(ac_cv_func_mmap, [yes])
        API_SETIFNULL(ac_cv_define_sockaddr_in6, [yes])
        API_SETIFNULL(ac_cv_working_getaddrinfo, [yes])
        API_SETIFNULL(ac_cv_working_getnameinfo, [yes])
        API_SETIFNULL(ac_cv_func_gai_strerror, [yes])
        case $host in
            *mingw32*)
                API_SETIFNULL(api_has_xthread_files, [1])
                API_SETIFNULL(api_has_user, [1])
                API_SETIFNULL(api_procattr_user_set_requires_password, [1])
                dnl The real function is TransmitFile(), not sendfile(), but
                dnl this bypasses the Linux/Solaris/AIX/etc. test and enables
                dnl the TransmitFile() implementation.
                API_SETIFNULL(ac_cv_func_sendfile, [yes])
                ;;
            *mingwce)
                API_SETIFNULL(api_has_xthread_files, [0])
                API_SETIFNULL(api_has_user, [0])
                API_SETIFNULL(api_procattr_user_set_requires_password, [0])
                API_SETIFNULL(ac_cv_func_sendfile, [no])
                ;;
        esac
        ;;
  esac

fi
])

dnl
dnl API_CC_HINTS
dnl
dnl  Allows us to provide a default choice of compiler which
dnl  the user can override.
AC_DEFUN([API_CC_HINTS], [
case "$host" in
  *-apple-aux3*)
      API_SETIFNULL(CC, [gcc])
      ;;
  bs2000*-siemens-sysv*)
      API_SETIFNULL(CC, [c89 -XLLML -XLLMK -XL -Kno_integer_overflow])
      ;;
  *convex-v11*)
      API_SETIFNULL(CC, [cc])
      ;;
  *-ibm-os390)
      API_SETIFNULL(CC, [cc])
      ;;
  *-ibm-as400)
      API_SETIFNULL(CC, [icc])
      ;;
  *-isc4*)
      API_SETIFNULL(CC, [gcc])
      ;;
  m88k-*-CX/SX|CYBER)
      API_SETIFNULL(CC, [cc])
      ;;
  *-next-openstep*)
      API_SETIFNULL(CC, [cc])
      ;;
  *-qnx32)
      API_SETIFNULL(CC, [cc -F])
      ;;
  *-tandem-oss)
      API_SETIFNULL(CC, [c89])
      ;;
  TPF)
      API_SETIFNULL(CC, [c89])
      ;;
esac
])
