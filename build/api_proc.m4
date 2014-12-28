AC_CHECK_FUNCS(waitpid)

AC_ARG_ENABLE(other-child,
  [  --enable-other-child    Enable reliable child processes ],
  [ if test "$enableval" = "yes"; then
        oc="1"
    else
        oc="0"
    fi ],
  [ oc=1 ] ) 
  
AC_SUBST(oc) 

if test -z "$have_proc_invoked"; then
  have_proc_invoked="0"
fi

AC_SUBST(have_proc_invoked)

AC_MSG_CHECKING(for Variable Length Arrays)
API_TRY_COMPILE_NO_WARNING([],
[
    int foo[argc];
    foo[0] = 0;
], vla_msg=yes, vla_msg=no )
AC_MSG_RESULT([$vla_msg])
if test "$vla_msg" = "yes"; then
    AC_DEFINE(HAVE_VLA, 1, [Define if C compiler supports VLA])
fi

AC_CACHE_CHECK(struct rlimit,ac_cv_struct_rlimit,[
AC_TRY_RUN([
#include <sys/types.h>
#include <sys/time.h>
#include <sys/resource.h>
main()
{
    struct rlimit limit;
    limit.rlim_cur = 0;
    limit.rlim_max = 0;
    exit(0);
}], [
    ac_cv_struct_rlimit=yes ], [
    ac_cv_struct_rlimit=no ], [
    ac_cv_struct_rlimit=no ] ) ] )
struct_rlimit=0
test "x$ac_cv_struct_rlimit" = xyes && struct_rlimit=1
AC_SUBST(struct_rlimit)

