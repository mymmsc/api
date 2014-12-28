# The real-time POSIX extensions (e.g. shm_*, sem_*) may only
# be available if linking against librt.
AC_SEARCH_LIBS(shm_open, rt)

case $host in
 *-sysv*)
   ac_includes_default="$ac_includes_default
#if HAVE_SYS_MUTEX_H /* needed to define lock_t for sys/shm.h */
# include <sys/mutex.h>
#endif";;
esac

AC_CHECK_HEADERS([sys/types.h sys/mman.h sys/ipc.h sys/mutex.h sys/shm.h sys/file.h kernel/OS.h os2.h windows.h])
AC_CHECK_FUNCS([mmap munmap shm_open shm_unlink shmget shmat shmdt shmctl \
                create_area mprotect])

API_CHECK_DEFINE(MAP_ANON, sys/mman.h)
AC_CHECK_FILE(/dev/zero)

# Not all systems can mmap /dev/zero (such as HP-UX).  Check for that.
if test "$ac_cv_func_mmap" = "yes" &&
   test "$ac_cv_file__dev_zero" = "yes"; then
    AC_MSG_CHECKING(for mmap that can map /dev/zero)
    AC_TRY_RUN([
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#ifdef HAVE_SYS_MMAN_H
#include <sys/mman.h>
#endif
    int main()
    {
        int fd;
        void *m;
        fd = open("/dev/zero", O_RDWR);
        if (fd < 0) {
            return 1;
        }
        m = mmap(0, sizeof(void*), PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
        if (m == (void *)-1) {  /* aka MAP_FAILED */
            return 2;
        }
        if (munmap(m, sizeof(void*)) < 0) {
            return 3;
        }
        return 0;
    }], [], [ac_cv_file__dev_zero=no], [ac_cv_file__dev_zero=no])

    AC_MSG_RESULT($ac_cv_file__dev_zero)
fi

# Now we determine which one is our anonymous shmem preference.
haveshmgetanon="0"
havemmapzero="0"
havemmapanon="0"
API_BEGIN_DECISION([anonymous shared memory allocation method])
API_IFALLYES(header:sys/ipc.h header:sys/shm.h header:sys/file.h dnl
             func:shmget func:shmat func:shmdt func:shmctl,
             [haveshmgetanon="1"
              API_DECIDE(USE_SHMEM_SHMGET_ANON, [SysV IPC shmget()])])
API_IFALLYES(header:sys/mman.h func:mmap func:munmap file:/dev/zero,
             [havemmapzero="1"
              API_DECIDE(USE_SHMEM_MMAP_ZERO, 
                  [SVR4-style mmap() on /dev/zero])])
API_IFALLYES(header:sys/mman.h func:mmap func:munmap define:MAP_ANON,
             [havemmapanon="1"
              API_DECIDE(USE_SHMEM_MMAP_ANON, 
                  [4.4BSD-style mmap() via MAP_ANON])])
API_IFALLYES(header:os2.h,
             [haveos2shm="1"
              API_DECIDE(USE_SHMEM_OS2_ANON, [OS/2 DosAllocSharedMem()])])
API_IFALLYES(header:kernel/OS.h func:create_area,
             [havebeosshm="1"
              API_DECIDE(USE_SHMEM_BEOS_ANON,
                         [BeOS areas])])
API_IFALLYES(header:windows.h func:CreateFileMapping,
             [havewin32shm="1"
              API_DECIDE(USE_SHMEM_WIN32_ANON,
                         [Windows CreateFileMapping()])])
case $host in
    *linux* ) 
        # Linux has problems with MM_SHMT_MMANON even though it reports
        # that it has it.
        # FIXME - find exact 2.3 version that MMANON was fixed in.  It is
        # confirmed fixed in 2.4 series.
        if test $os_pre24linux -eq 1; then
            AC_MSG_WARN([Disabling anon mmap() support for Linux pre-2.4])
            API_DECISION_OVERRIDE(USE_SHMEM_MMAP_ZERO USE_SHMEM_SHMGET_ANON)
        fi
        ;;
    *hpux11* ) 
        API_DECISION_OVERRIDE(USE_SHMEM_SHMGET_ANON)
        ;;
esac
API_END_DECISION
AC_DEFINE_UNQUOTED($ac_decision)

useshmgetanon="0"
usemmapzero="0"
usemmapanon="0"

case $ac_decision in
    USE_SHMEM_SHMGET_ANON )
        useshmgetanon="1"
        ;;
    USE_SHMEM_MMAP_ZERO )
        usemmapzero="1"
        ;;
    USE_SHMEM_MMAP_ANON )
        usemmapanon="1"
        ;;
esac

AC_SUBST(useshmgetanon)
AC_SUBST(usemmapzero)
AC_SUBST(usemmapanon)
AC_SUBST(haveshmgetanon)
AC_SUBST(havemmapzero)
AC_SUBST(havemmapanon)

# Now we determine which one is our name-based shmem preference.
havemmaptmp="0"
havemmapshm="0"
haveshmget="0"
havebeosarea="0"
haveos2shm="0"
havewin32shm="0"
API_BEGIN_DECISION([namebased memory allocation method])
API_IFALLYES(header:sys/mman.h func:mmap func:munmap,
             [havemmaptmp="1"
              API_DECIDE(USE_SHMEM_MMAP_TMP, 
                  [Classical mmap() on temporary file])])
API_IFALLYES(header:sys/mman.h func:mmap func:munmap func:shm_open dnl
             func:shm_unlink,
             [havemmapshm="1"
              API_DECIDE(USE_SHMEM_MMAP_SHM, 
                  [mmap() via POSIX.1 shm_open() on temporary file])])
API_IFALLYES(header:sys/ipc.h header:sys/shm.h header:sys/file.h dnl
             func:shmget func:shmat func:shmdt func:shmctl,
             [haveshmget="1"
              API_DECIDE(USE_SHMEM_SHMGET, [SysV IPC shmget()])])
API_IFALLYES(header:kernel/OS.h func:create_area,
             [havebeosshm="1"
              API_DECIDE(USE_SHMEM_BEOS, [BeOS areas])])
API_IFALLYES(header:os2.h,
             [haveos2shm="1"
              API_DECIDE(USE_SHMEM_OS2, [OS/2 DosAllocSharedMem()])])
API_IFALLYES(header:windows.h,
             [havewin32shm="1"
              API_DECIDE(USE_SHMEM_WIN32, [Windows shared memory])])
AC_ARG_ENABLE(posix-shm,
[  --enable-posix-shm      Use POSIX shared memory (shm_open) if available],
[
if test "$havemmapshm" = "1"; then
  API_DECISION_OVERRIDE(USE_SHMEM_MMAP_SHM)
fi
])
case $host in
    *linux* ) 
        # Linux pre-2.4 had problems with MM_SHMT_MMANON even though
        # it reports that it has it.
        if test $os_pre24linux -eq 1; then
            API_DECISION_OVERRIDE(USE_SHMEM_MMAP_TMP USE_SHMEM_MMAP_SHM dnl
                                  USE_SHMEM_SHMGET)
        fi
        ;;
esac
API_END_DECISION
AC_DEFINE_UNQUOTED($ac_decision)

usemmaptmp="0"
usemmapshm="0"
useshmget="0"
usebeosarea="0"
useos2shm="0"
usewin32shm="0"

case $ac_decision in
    USE_SHMEM_MMAP_TMP )
        usemmaptmp="1"
        ;;
    USE_SHMEM_MMAP_SHM )
        usemmapshm="1"
        ;;
    USE_SHMEM_SHMGET )
        useshmget="1"
        ;;
    USE_SHMEM_BEOS )
        usebeosarea="1"
        ;;
    USE_SHMEM_OS2 )
        useos2shm="1"
        ;;
    USE_SHMEM_WIN32 )
        usewin32shm="1"
        ;;
esac

# Do we have any shared memory support?
if test "$usemmaptmp$usemmapshm$usemmapzero$useshmget$usemmapanon$usebeosarea$useos2shm$usewin32shm" = "00000000"; then
  sharedmem="0"
else
  sharedmem="1"
fi

AC_SUBST(usemmaptmp)
AC_SUBST(usemmapshm)
AC_SUBST(useshmget)
AC_SUBST(usebeosarea)
AC_SUBST(useos2shm)
AC_SUBST(usewin32shm)
AC_SUBST(havemmaptmp)
AC_SUBST(havemmapshm)
AC_SUBST(haveshmget)
AC_SUBST(havebeosarea)
AC_SUBST(haveos2shm)
AC_SUBST(havewin32shm)
AC_SUBST(sharedmem)

