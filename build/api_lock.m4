AC_CHECK_FUNCS(semget semctl flock)
AC_CHECK_HEADERS(semaphore.h OS.h)
AC_SEARCH_LIBS(sem_open, rt)
AC_CHECK_FUNCS(sem_close sem_unlink sem_post sem_wait create_sem)

# Some systems return ENOSYS from sem_open.
AC_CACHE_CHECK(for working sem_open,ac_cv_func_sem_open,[
AC_TRY_RUN([
#include <errno.h>
#include <stdlib.h>
#include <fcntl.h>
#include <semaphore.h>
#ifndef SEM_FAILED
#define SEM_FAILED (-1)
#endif
main()
{
    sem_t *psem;
    const char *sem_name = "/api_autoconf";

    psem = sem_open(sem_name, O_CREAT, 0644, 1);
    if (psem == (sem_t *)SEM_FAILED) {
	exit(1);
    }
    sem_close(psem);
    psem = sem_open(sem_name, O_CREAT | O_EXCL, 0644, 1);
    if (psem != (sem_t *)SEM_FAILED) {
        sem_close(psem);
        exit(1);
    }
    sem_unlink(sem_name);
    exit(0);
}], [ac_cv_func_sem_open=yes], [ac_cv_func_sem_open=no],
[ac_cv_func_sem_open=no])])

# It's stupid, but not all platforms have union semun, even those that need it.
AC_MSG_CHECKING(for union semun in sys/sem.h)
AC_TRY_COMPILE([
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>
],[
union semun arg;
semctl(0, 0, 0, arg);
], [have_union_semun="1" union_semun=yes ]
msg=yes, [
have_union_semun="0"
msg=no ] )
AC_MSG_RESULT([$msg])
AC_SUBST(have_union_semun)

dnl Checks for libraries.
API_CHECK_DEFINE(LOCK_EX, sys/file.h)
API_CHECK_DEFINE(F_SETLK, fcntl.h)
API_CHECK_DEFINE(SEM_UNDO, sys/sem.h)

# We are assuming that if the platform doesn't have POLLIN, it doesn't have
# any POLL definitions.
API_CHECK_DEFINE_FILES(POLLIN, poll.h sys/poll.h)

if test "$threads" = "1"; then
    API_CHECK_DEFINE(PTHREAD_PROCESS_SHARED, pthread.h)
    AC_CHECK_FUNCS(pthread_mutexattr_setpshared)
    # Some systems have setpshared and define PROCESS_SHARED, but don't 
    # really support PROCESS_SHARED locks.  So, we must validate that we 
    # can go through the steps without receiving some sort of system error.
    # Linux and older versions of AIX have this problem.
    API_IFALLYES(header:pthread.h define:PTHREAD_PROCESS_SHARED func:pthread_mutexattr_setpshared, [
      AC_CACHE_CHECK([for working PROCESS_SHARED locks], api_cv_process_shared_works, [
      AC_TRY_RUN([
#include <sys/types.h>
#include <pthread.h>
        int main()
        {
            pthread_mutex_t mutex;
            pthread_mutexattr_t attr;
            if (pthread_mutexattr_init(&attr))
                exit(1);
            if (pthread_mutexattr_setpshared(&attr, PTHREAD_PROCESS_SHARED))
                exit(2);
            if (pthread_mutex_init(&mutex, &attr))
                exit(3);
            if (pthread_mutexattr_destroy(&attr))
                exit(4);
            if (pthread_mutex_destroy(&mutex))
                exit(5);
            exit(0);
        }], [api_cv_process_shared_works=yes], [api_cv_process_shared_works=no])])
      # Override detection of pthread_mutexattr_setpshared
      ac_cv_func_pthread_mutexattr_setpshared=$api_cv_process_shared_works])

    if test "$ac_cv_func_pthread_mutexattr_setpshared" = "yes"; then
        API_CHECK_PTHREAD_ROBUST_SHARED_MUTEX
    fi
fi

# See which lock mechanisms we can support on this system.
API_IFALLYES(header:semaphore.h func:sem_open func:sem_close dnl
             func:sem_unlink func:sem_post func:sem_wait,
             hasposixser="1", hasposixser="0")
API_IFALLYES(func:semget func:semctl define:SEM_UNDO, hassysvser="1", 
             hassysvser="0")
API_IFALLYES(func:flock define:LOCK_EX, hasflockser="1", hasflockser="0")
API_IFALLYES(header:fcntl.h define:F_SETLK, hasfcntlser="1", hasfcntlser="0")
# note: the current API use of shared mutex requires /dev/zero
API_IFALLYES(header:pthread.h define:PTHREAD_PROCESS_SHARED dnl
             func:pthread_mutexattr_setpshared dnl
             file:/dev/zero,
             hasprocpthreadser="1", hasprocpthreadser="0")
API_IFALLYES(header:OS.h func:create_sem, hasbeossem="1", hasbeossem="0")

# See which lock mechanism we'll select by default on this system.
# The last API_DECIDE to execute sets the default.
# At this stage, we match the ordering in Apache 1.3
# which is (highest to lowest): sysvsem -> fcntl -> flock.
# POSIX semaphores and cross-process pthread mutexes are not
# used by default since they have less desirable behaviour when
# e.g. a process holding the mutex segfaults.
# The BEOSSEM decision doesn't require any substitutions but is
# included here to prevent the fcntl() branch being selected
# from the decision making.
API_BEGIN_DECISION([api_lock implementation method])
API_IFALLYES(func:flock define:LOCK_EX,
            API_DECIDE(USE_FLOCK_SERIALIZE, [4.2BSD-style flock()]))
API_IFALLYES(header:fcntl.h define:F_SETLK,
            API_DECIDE(USE_FCNTL_SERIALIZE, [SVR4-style fcntl()]))
API_IFALLYES(func:semget func:semctl define:SEM_UNDO,
            API_DECIDE(USE_SYSVSEM_SERIALIZE, [SysV IPC semget()]))
API_IFALLYES(header:OS.h func:create_sem, 
            API_DECIDE(USE_BEOSSEM, [BeOS Semaphores])) 
if test "x$api_lock_method" != "x"; then
    API_DECISION_FORCE($api_lock_method)
fi
API_END_DECISION
AC_DEFINE_UNQUOTED($ac_decision)

flockser="0"
sysvser="0"
posixser="0"
procpthreadser="0"
fcntlser="0"
case $ac_decision in
    USE_FLOCK_SERIALIZE )
        flockser="1"
        ;;
    USE_FCNTL_SERIALIZE )
        fcntlser="1"
        ;;
    USE_SYSVSEM_SERIALIZE )
        sysvser="1"
        ;;
    USE_POSIXSEM_SERIALIZE )
        posixser="1"
        ;;
    USE_PROC_PTHREAD_SERIALIZE )
        procpthreadser="1"
        ;;
    USE_BEOSSEM )
        beossem="1"
        ;;
esac

if test $hasfcntlser = "1"; then
AC_MSG_CHECKING(if fcntl returns EACCES when F_SETLK is already held)
AC_TRY_RUN([
#ifdef HAVE_STDLIB_H
#include <stdlib.h>
#endif
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#ifdef HAVE_SYS_STAT_H
#include <sys/stat.h>
#endif
#ifdef HAVE_SYS_WAIT_H
#include <sys/wait.h>
#endif
#if defined(HAVE_UNISTD_H)
#include <unistd.h>
#endif
#include <fcntl.h>
#include <errno.h>

int fd;
struct flock proc_mutex_lock_it = {0};
const char *fname = "conftest.fcntl";

int main()
{
    int rc, status;;
    proc_mutex_lock_it.l_whence = SEEK_SET;   /* from current point */
    proc_mutex_lock_it.l_type = F_WRLCK;      /* set exclusive/write lock */

    fd = creat(fname, S_IRWXU);
    unlink(fname);

    if (rc = lockit()) {
        exit(-1);
    }

    if (fork()) {
        wait(&status);
    }
    else {
      return(lockit());
    }

    close(fd);
    exit(WEXITSTATUS(status) != EACCES);
}

int lockit() {
    int rc;
    do {
        rc = fcntl(fd, F_SETLK, &proc_mutex_lock_it);
    } while ( rc < 0 && errno == EINTR);

    return (rc < 0) ? errno : 0;
}], [api_fcntl_tryacquire_eacces=1], [api_fcntl_tryacquire_eacces=0], [api_fcntl_tryacquire_eacces=0])
fi

if test "$api_fcntl_tryacquire_eacces" = "1"; then
  AC_DEFINE(FCNTL_TRYACQUIRE_EACCES, 1, [Define if fcntl returns EACCES when F_SETLK is already held])
  AC_MSG_RESULT(yes)
else
  AC_MSG_RESULT(no)
fi


AC_SUBST(hasflockser)
AC_SUBST(hassysvser)
AC_SUBST(hasposixser)
AC_SUBST(hasfcntlser)
AC_SUBST(hasprocpthreadser)
AC_SUBST(flockser)
AC_SUBST(sysvser)
AC_SUBST(posixser)
AC_SUBST(fcntlser)
AC_SUBST(procpthreadser)
AC_SUBST(pthreadser)

AC_MSG_CHECKING(if all interprocess locks affect threads)
if test "x$api_process_lock_is_global" = "xyes"; then
    proclockglobal="1"
    AC_MSG_RESULT(yes)
else
    proclockglobal="0"
    AC_MSG_RESULT(no)
fi

AC_SUBST(proclockglobal)

AC_MSG_CHECKING(if POSIX sems affect threads in the same process)
if test "x$api_posixsem_is_global" = "xyes"; then
  AC_DEFINE(POSIXSEM_IS_GLOBAL, 1, 
            [Define if POSIX semaphores affect threads within the process])
  AC_MSG_RESULT(yes)
else
  AC_MSG_RESULT(no)
fi

AC_MSG_CHECKING(if SysV sems affect threads in the same process)
if test "x$api_sysvsem_is_global" = "xyes"; then
  AC_DEFINE(SYSVSEM_IS_GLOBAL, 1,
            [Define if SysV semaphores affect threads within the process])
  AC_MSG_RESULT(yes)
else
  AC_MSG_RESULT(no)
fi

AC_MSG_CHECKING(if fcntl locks affect threads in the same process)
if test "x$api_fcntl_is_global" = "xyes"; then
  AC_DEFINE(FCNTL_IS_GLOBAL, 1,
            [Define if fcntl locks affect threads within the process])
  AC_MSG_RESULT(yes)
else
  AC_MSG_RESULT(no)
fi

AC_MSG_CHECKING(if flock locks affect threads in the same process)
if test "x$api_flock_is_global" = "xyes"; then
  AC_DEFINE(FLOCK_IS_GLOBAL, 1,
            [Define if flock locks affect threads within the process])
  AC_MSG_RESULT(yes)
else
  AC_MSG_RESULT(no)
fi

