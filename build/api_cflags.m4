dnl AC_PROG_CC sets -g in CFLAGS (and -O2 for gcc) by default.
dnl On OS/390 this causes the compiler to insert extra debugger
dnl hook instructions.  That's fine for debug/maintainer builds, not fine
dnl otherwise.

case $host in
    *os390)
        if test "$ac_test_CFLAGS" != set; then
           API_REMOVEFROM(CFLAGS,-g)
        fi
        ;;
esac

AC_ARG_ENABLE(debug,[  --enable-debug          Turn on debugging and compile time warnings],
  [API_ADDTO(CFLAGS,-g)
   if test "$GCC" = "yes"; then
     API_ADDTO(CFLAGS,-Wall)
   elif test "$AIX_XLC" = "yes"; then
     API_ADDTO(CFLAGS,-qfullpath)
   fi
])dnl

AC_ARG_ENABLE(maintainer-mode,[  --enable-maintainer-mode  Turn on debugging and compile time warnings],
  [API_ADDTO(CFLAGS,-g)
   if test "$GCC" = "yes"; then
     API_ADDTO(CFLAGS,[-Wall -Wmissing-prototypes -Wstrict-prototypes -Wmissing-declarations])
   elif test "$AIX_XLC" = "yes"; then
     API_ADDTO(CFLAGS,-qfullpath -qinitauto=FE -qcheck=all -qinfo=pro)
   fi
])dnl

AC_ARG_ENABLE(profile,[  --enable-profile        Turn on profiling for the build (GCC)],
  if test "$GCC" = "yes"; then
    API_ADDTO(CFLAGS, -pg)
    API_REMOVEFROM(CFLAGS, -g)
    if test "$host" = "i586-pc-beos"; then
        API_REMOVEFROM(CFLAGS, -O2)
        API_ADDTO(CFLAGS, -O1)
        API_ADDTO(LDFLAGS, -p)
    fi
  fi
)dnl

if test "$host" = "i586-pc-beos"; then
  AC_ARG_ENABLE(malloc-debug,[  --enable-malloc-debug   Switch on malloc_debug for BeOS],
    API_REMOVEFROM(CFLAGS, -O2)
    API_ADDTO(CPPFLAGS, -fcheck-memory-usage -D_KERNEL_MODE)
  ) dnl
fi

# this is the place to put specific options for platform/compiler
# combinations
case "$host:$CC" in
    *-hp-hpux*:cc )
	API_ADDTO(CFLAGS,[-Ae +Z])
	case $host in
	  ia64-* )
	    ;;
          * )
	    if echo "$CFLAGS " | grep '+DA' >/dev/null; then :
	    else
	      API_ADDTO(CFLAGS,[+DAportable])
	    fi 
	    ;;
        esac
	;;
    powerpc-*-beos:mwcc* )
	API_SETVAR(CPP,[mwcc -E])
	API_SETVAR(CC,mwcc)
	API_SETVAR(AR,ar)
	;;

    dnl If building static API, both the API build and the app build
    dnl need -DAPI_DECLARE_STATIC to generate the right linkage from    
    dnl API_DECLARE et al.
    dnl If building dynamic API, the API build needs API_DECLARE_EXPORT
    dnl and the app build should have neither define.
    *-mingw* | *-cygwin*)
        if test "$enable_shared" = "yes"; then
            API_ADDTO(INTERNAL_CPPFLAGS, -DAPI_DECLARE_EXPORT)
        else
            API_ADDTO(CPPFLAGS, -DAPI_DECLARE_STATIC)
        fi
        ;;
esac

AC_CACHE_CHECK([whether the compiler provides atomic builtins], [ap_cv_atomic_builtins],
[AC_TRY_RUN([
int main()
{
    unsigned long val = 1010, tmp, *mem = &val;

    if (__sync_fetch_and_add(&val, 1010) != 1010 || val != 2020)
        return 1;

    tmp = val;

    if (__sync_fetch_and_sub(mem, 1010) != tmp || val != 1010)
        return 1;

    if (__sync_sub_and_fetch(&val, 1010) != 0 || val != 0)
        return 1;

    tmp = 3030;

    if (__sync_val_compare_and_swap(mem, 0, tmp) != 0 || val != tmp)
        return 1;

    if (__sync_lock_test_and_set(&val, 4040) != 3030)
        return 1;

    mem = &tmp;

    if (__sync_val_compare_and_swap(&mem, &tmp, &val) != &tmp)
        return 1;

    __sync_synchronize();

    if (mem != &val)
        return 1;

    return 0;
}], [ap_cv_atomic_builtins=yes], [ap_cv_atomic_builtins=no], [ap_cv_atomic_builtins=no])])

if test "$ap_cv_atomic_builtins" = "yes"; then
    AC_DEFINE(HAVE_ATOMIC_BUILTINS, 1, [Define if compiler provides atomic builtins])
fi

case $host in
    powerpc-405-*)
        # The IBM ppc405cr processor has a bugged stwcx instruction.
        AC_DEFINE(PPC405_ERRATA, 1, [Define on PowerPC 405 where errata 77 applies])
        ;;
    *)
        ;;
esac

dnl Check the depend program we can use
API_CHECK_DEPEND

proc_mutex_is_global=0

config_subdirs="none"
INSTALL_SUBDIRS="none"
OBJECTS_PLATFORM='$(OBJECTS_unix)'

case $host in
   i386-ibm-aix* | *-ibm-aix[[1-2]].* | *-ibm-aix3.* | *-ibm-aix4.1 | *-ibm-aix4.1.* | *-ibm-aix4.2 | *-ibm-aix4.2.*)
       OSDIR="aix"
       API_ADDTO(LDFLAGS,-lld)
       eolstr="\\n"
       OBJECTS_PLATFORM='$(OBJECTS_aix)'
       ;;
   *-os2*)
       API_ADDTO(CPPFLAGS,-DOS2)
       API_ADDTO(CFLAGS,-Zmt)
       AC_CHECK_LIB(bsd, random)
       OSDIR="os2"
       enable_threads="system_threads"
       eolstr="\\r\\n"
       file_as_socket="0"
       proc_mutex_is_global=1
       OBJECTS_PLATFORM='$(OBJECTS_os2)'
       ;;
   *beos*)
       OSDIR="beos"
       API_ADDTO(CPPFLAGS,-DBEOS)
       enable_threads="system_threads"
       native_mmap_emul="1"
       API_CHECK_DEFINE(BONE_VERSION, sys/socket.h)
       eolstr="\\n"
       osver=`uname -r`
       proc_mutex_is_global=1
       OBJECTS_PLATFORM='$(OBJECTS_beos)'
       case $osver in
          5.0.4)
             file_as_socket="1"
             ;;
          *)
             file_as_socket="0"
             ;;
       esac
       ;;
   *os390)
       OSDIR="os390"
       OBJECTS_PLATFORM='$(OBJECTS_os390)'
       eolstr="\\n"
       ;;
   *os400)
       OSDIR="as400"
       eolstr="\\n"
       ;;
   *mingw*)
       OSDIR="win32"
       enable_threads="system_threads"
       eolstr="\\r\\n"
       file_as_socket=0
       proc_mutex_is_global=1
       OBJECTS_PLATFORM='$(OBJECTS_win32)'
       ;;
   *cygwin*)
       OSDIR="unix"
       enable_threads="no"
       eolstr="\\n"
       ;;
   *hpux10* ) 
       enable_threads="no"
       OSDIR="unix"
       eolstr="\\n"
       ;;
   *)
       OSDIR="unix"
       eolstr="\\n"
       ;;
esac

AC_SUBST(OBJECTS_PLATFORM)

# Check whether LFS has explicitly been disabled
AC_ARG_ENABLE(lfs,[  --disable-lfs           Disable large file support on 32-bit platforms],
[api_lfs_choice=$enableval], [api_lfs_choice=yes])

if test "$api_lfs_choice" = "yes"; then
   # Check whether the transitional LFS API is sufficient
   AC_CACHE_CHECK([whether to enable -D_LARGEFILE64_SOURCE], [api_cv_use_lfs64], [
   api_save_CPPFLAGS=$CPPFLAGS
   CPPFLAGS="$CPPFLAGS -D_LARGEFILE64_SOURCE"
   AC_TRY_RUN([
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

void main(void)
{
    int fd, ret = 0;
    struct stat64 st;
    off64_t off = 4242;

    if (sizeof(off64_t) != 8 || sizeof(off_t) != 4)
       exit(1);
    if ((fd = open("conftest.lfs", O_LARGEFILE|O_CREAT|O_WRONLY, 0644)) < 0)
       exit(2);
    if (ftruncate64(fd, off) != 0)
       ret = 3;
    else if (fstat64(fd, &st) != 0 || st.st_size != off)
       ret = 4;
    else if (lseek64(fd, off, SEEK_SET) != off)
       ret = 5;
    else if (close(fd) != 0)
       ret = 6;
    else if (lstat64("conftest.lfs", &st) != 0 || st.st_size != off)
       ret = 7;
    else if (stat64("conftest.lfs", &st) != 0 || st.st_size != off)
       ret = 8;
    unlink("conftest.lfs");

    exit(ret);
}], [api_cv_use_lfs64=yes], [api_cv_use_lfs64=no], [api_cv_use_lfs64=no])
   CPPFLAGS=$api_save_CPPFLAGS])
   if test "$api_cv_use_lfs64" = "yes"; then
      API_ADDTO(CPPFLAGS, [-D_LARGEFILE64_SOURCE])
   fi
fi

AC_ARG_ENABLE(nonportable-atomics,
[  --enable-nonportable-atomics  Use optimized atomic code which may produce nonportable binaries],
[if test $enableval = yes; then
   force_generic_atomics=no
 else
   force_generic_atomics=yes
 fi
],
[case $host_cpu in
   i[[456]]86) force_generic_atomics=yes ;;
   *) force_generic_atomics=no ;;
esac
])

if test $force_generic_atomics = yes; then
   AC_DEFINE([USE_ATOMICS_GENERIC], 1,
             [Define if use of generic atomics is requested])
fi

AC_SUBST(proc_mutex_is_global)
AC_SUBST(eolstr)
AC_SUBST(INSTALL_SUBDIRS)

# For some platforms we need a version string which allows easy numeric
# comparisons.
case $host in
    *freebsd*)
        if test -x /sbin/sysctl; then
            os_version=`/sbin/sysctl -n kern.osreldate`
        else
            os_version=000000
        fi
        ;;
    *linux*)
        os_major=[`uname -r | sed -e 's/\([1-9][0-9]*\)\..*/\1/'`]
        os_minor=[`uname -r | sed -e 's/[1-9][0-9]*\.\([0-9]\+\)\..*/\1/'`]
        if test $os_major -lt 2 -o \( $os_major -eq 2 -a $os_minor -lt 4 \); then
            AC_MSG_WARN([Configured for pre-2.4 Linux $os_major.$os_minor])
            os_pre24linux=1
        else
            os_pre24linux=0
            AC_MSG_NOTICE([Configured for Linux $os_major.$os_minor])
        fi
        ;;
    *os390)
        os_version=`uname -r | sed -e 's/\.//g'`
        ;;
    *)
        os_version=OS_VERSION_IS_NOT_SET
        ;;
esac
