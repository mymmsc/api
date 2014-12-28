dnl -------------------------------------------------------- -*- autoconf -*-
dnl
dnl api_common.m4: API's general-purpose autoconf macros
dnl

AC_DEFUN([API_CONFIGURE_PART],[
  AC_MSG_RESULT()
  echo -e "\033\01331;37m$1 \033\01330m"
])

dnl API_CHECK_CFLAG_COMPILE(check_flag[, define_flag])
dnl Check that $CC accepts a flag 'check_flag'. If it is supported append
dnl 'define_flag' to $CFLAGS. If 'define_flag' is not specified, then append
dnl 'check_flag'.
AC_DEFUN([API_CHECK_CFLAG_COMPILE], [{
	AC_MSG_CHECKING([if $CC supports compile flag $1])
	saved_CFLAGS="$CFLAGS"
	CFLAGS="$CFLAGS $WERROR $1"
	_define_flag="$2"
	test "x$_define_flag" = "x" && _define_flag="$1"
	AC_COMPILE_IFELSE([AC_LANG_SOURCE([[
#include <stdlib.h>
#include <stdio.h>
int main(int argc, char **argv) {
	/* Some math to catch -ftrapv problems in the toolchain */
	int i = 123 * argc, j = 456 + argc, k = 789 - argc;
	float l = i * 2.1;
	double m = l / 0.5;
	long long int n = argc * 12345LL, o = 12345LL * (long long int)argc;
	printf("%d %d %d %f %f %lld %lld\n", i, j, k, l, m, n, o);
	exit(0);
}
	]])],
		[
if `grep -i "unrecognized option" conftest.err >/dev/null`
then
		AC_MSG_RESULT([no])
		CFLAGS="$saved_CFLAGS"
else
		AC_MSG_RESULT([yes])
		 CFLAGS="$saved_CFLAGS $_define_flag"
fi],
		[ AC_MSG_RESULT([no])
		  CFLAGS="$saved_CFLAGS" ]
	)
}])

dnl API_CHECK_CFLAG_LINK(check_flag[, define_flag])
dnl Check that $CC accepts a flag 'check_flag'. If it is supported append
dnl 'define_flag' to $CFLAGS. If 'define_flag' is not specified, then append
dnl 'check_flag'.
AC_DEFUN([API_CHECK_CFLAG_LINK], [{
	AC_MSG_CHECKING([if $CC supports compile flag $1 and linking succeeds])
	saved_CFLAGS="$CFLAGS"
	CFLAGS="$CFLAGS $WERROR $1"
	_define_flag="$2"
	test "x$_define_flag" = "x" && _define_flag="$1"
	AC_LINK_IFELSE([AC_LANG_SOURCE([[
#include <stdlib.h>
#include <stdio.h>
int main(int argc, char **argv) {
	/* Some math to catch -ftrapv problems in the toolchain */
	int i = 123 * argc, j = 456 + argc, k = 789 - argc;
	float l = i * 2.1;
	double m = l / 0.5;
	long long int n = argc * 12345LL, o = 12345LL * (long long int)argc;
	printf("%d %d %d %f %f %lld %lld\n", i, j, k, l, m, n, o);
	exit(0);
}
	]])],
		[
if `grep -i "unrecognized option" conftest.err >/dev/null`
then
		AC_MSG_RESULT([no])
		CFLAGS="$saved_CFLAGS"
else
		AC_MSG_RESULT([yes])
		 CFLAGS="$saved_CFLAGS $_define_flag"
fi],
		[ AC_MSG_RESULT([no])
		  CFLAGS="$saved_CFLAGS" ]
	)
}])

dnl API_CHECK_LDFLAG_LINK(check_flag[, define_flag])
dnl Check that $LD accepts a flag 'check_flag'. If it is supported append
dnl 'define_flag' to $LDFLAGS. If 'define_flag' is not specified, then append
dnl 'check_flag'.
AC_DEFUN([API_CHECK_LDFLAG_LINK], [{
	AC_MSG_CHECKING([if $LD supports link flag $1])
	saved_LDFLAGS="$LDFLAGS"
	LDFLAGS="$LDFLAGS $WERROR $1"
	_define_flag="$2"
	test "x$_define_flag" = "x" && _define_flag="$1"
	AC_LINK_IFELSE([AC_LANG_SOURCE([[
#include <stdlib.h>
#include <stdio.h>
int main(int argc, char **argv) {
	/* Some math to catch -ftrapv problems in the toolchain */
	int i = 123 * argc, j = 456 + argc, k = 789 - argc;
	float l = i * 2.1;
	double m = l / 0.5;
	long long int n = argc * 12345LL, o = 12345LL * (long long int)argc;
	printf("%d %d %d %f %f %lld %lld\n", i, j, k, l, m, n, o);
	exit(0);
}
		]])],
		[ AC_MSG_RESULT([yes])
		  LDFLAGS="$saved_LDFLAGS $_define_flag"],
		[ AC_MSG_RESULT([no])
		  LDFLAGS="$saved_LDFLAGS" ]
	)
}])

dnl API_CHECK_HEADER_FOR_FIELD(field, header, symbol)
dnl Does AC_EGREP_HEADER on 'header' for the string 'field'
dnl If found, set 'symbol' to be defined. Cache the result.
dnl TODO: This is not foolproof, better to compile and read from there
AC_DEFUN([API_CHECK_HEADER_FOR_FIELD], [
# look for field '$1' in header '$2'
	dnl This strips characters illegal to m4 from the header filename
	ossh_safe=`echo "$2" | sed 'y%./+-%__p_%'`
	dnl
	ossh_varname="ossh_cv_$ossh_safe""_has_"$1
	AC_MSG_CHECKING(for $1 field in $2)
	AC_CACHE_VAL($ossh_varname, [
		AC_EGREP_HEADER($1, $2, [ dnl
			eval "$ossh_varname=yes" dnl
		], [ dnl
			eval "$ossh_varname=no" dnl
		]) dnl
	])
	ossh_result=`eval 'echo $'"$ossh_varname"`
	if test -n "`echo $ossh_varname`"; then
		AC_MSG_RESULT($ossh_result)
		if test "x$ossh_result" = "xyes"; then
			AC_DEFINE($3, 1, [Define if you have $1 in $2])
		fi
	else
		AC_MSG_RESULT(no)
	fi
])

dnl
dnl API_SAVE_THE_ENVIRONMENT(variable_name)
dnl
dnl Stores the variable (usually a Makefile macro) for later restoration
dnl
AC_DEFUN([API_SAVE_THE_ENVIRONMENT], [
  api_ste_save_$1="$$1"
])dnl

dnl
dnl API_RESTORE_THE_ENVIRONMENT(variable_name, prefix_)
dnl
dnl Uses the previously saved variable content to figure out what configure
dnl has added to the variable, moving the new bits to prefix_variable_name
dnl and restoring the original variable contents.  This makes it possible
dnl for a user to override configure when it does something stupid.
dnl
AC_DEFUN([API_RESTORE_THE_ENVIRONMENT], [
dnl Check whether $api_ste_save_$1 is empty or
dnl only whitespace. The verbatim "X" is token number 1,
dnl the following whitespace will be ignored.
set X $api_ste_save_$1
if test ${#} -eq 1; then
  $2$1="$$1"
  $1=
else
  if test "x$api_ste_save_$1" = "x$$1"; then
    $2$1=
  else
    $2$1=`echo "$$1" | sed -e "s%${api_ste_save_$1}%%"`
    $1="$api_ste_save_$1"
  fi
fi
if test "x$silent" != "xyes"; then
  echo "  restoring $1 to \"$$1\""
  echo "  setting $2$1 to \"$$2$1\""
fi
AC_SUBST($2$1)
])dnl

dnl
dnl API_SETIFNULL(variable, value)
dnl
dnl  Set variable iff it's currently null
dnl
AC_DEFUN([API_SETIFNULL], [
  if test -z "$$1"; then
    test "x$silent" != "xyes" && echo "  setting $1 to \"$2\""
    $1="$2"
  fi
])dnl

dnl
dnl API_SETVAR(variable, value)
dnl
dnl  Set variable no matter what
dnl
AC_DEFUN([API_SETVAR], [
  test "x$silent" != "xyes" && echo "  forcing $1 to \"$2\""
  $1="$2"
])dnl

dnl
dnl API_ADDTO(variable, value)
dnl
dnl  Add value to variable
dnl
AC_DEFUN([API_ADDTO], [
  if test "x$$1" = "x"; then
    test "x$silent" != "xyes" && echo "  setting $1 to \"$2\""
    $1="$2"
  else
    api_addto_bugger="$2"
    for i in $api_addto_bugger; do
      api_addto_duplicate="0"
      for j in $$1; do
        if test "x$i" = "x$j"; then
          api_addto_duplicate="1"
          break
        fi
      done
      if test $api_addto_duplicate = "0"; then
        test "x$silent" != "xyes" && echo "  adding \"$i\" to $1"
        $1="$$1 $i"
      fi
    done
  fi
])dnl

dnl
dnl API_REMOVEFROM(variable, value)
dnl
dnl Remove a value from a variable
dnl
AC_DEFUN([API_REMOVEFROM], [
  if test "x$$1" = "x$2"; then
    test "x$silent" != "xyes" && echo "  nulling $1"
    $1=""
  else
    api_new_bugger=""
    api_removed=0
    for i in $$1; do
      if test "x$i" != "x$2"; then
        api_new_bugger="$api_new_bugger $i"
      else
        api_removed=1
      fi
    done
    if test $api_removed = "1"; then
      test "x$silent" != "xyes" && echo "  removed \"$2\" from $1"
      $1=$api_new_bugger
    fi
  fi
]) dnl

dnl
dnl API_CHECK_DEFINE_FILES( symbol, header_file [header_file ...] )
dnl
AC_DEFUN([API_CHECK_DEFINE_FILES], [
  AC_CACHE_CHECK([for $1 in $2],ac_cv_define_$1,[
    ac_cv_define_$1=no
    for curhdr in $2
    do
      AC_EGREP_CPP(YES_IS_DEFINED, [
#include <$curhdr>
#ifdef $1
YES_IS_DEFINED
#endif
      ], ac_cv_define_$1=yes)
    done
  ])
  if test "$ac_cv_define_$1" = "yes"; then
    AC_DEFINE(HAVE_$1, 1, [Define if $1 is defined])
  fi
])


dnl
dnl API_CHECK_DEFINE(symbol, header_file)
dnl
AC_DEFUN([API_CHECK_DEFINE], [
  AC_CACHE_CHECK([for $1 in $2],ac_cv_define_$1,[
    AC_EGREP_CPP(YES_IS_DEFINED, [
#include <$2>
#ifdef $1
YES_IS_DEFINED
#endif
    ], ac_cv_define_$1=yes, ac_cv_define_$1=no)
  ])
  if test "$ac_cv_define_$1" = "yes"; then
    AC_DEFINE(HAVE_$1, 1, [Define if $1 is defined in $2])
  fi
])

dnl
dnl API_CHECK_API_DEFINE( symbol )
dnl
AC_DEFUN([API_CHECK_API_DEFINE], [
api_old_cppflags=$CPPFLAGS
CPPFLAGS="$CPPFLAGS $INCLUDES"
AC_EGREP_CPP(YES_IS_DEFINED, [
#include <api.h>
#if $1
YES_IS_DEFINED
#endif
], ac_cv_define_$1=yes, ac_cv_define_$1=no)
CPPFLAGS=$api_old_cppflags
])

dnl API_CHECK_FILE(filename); set ac_cv_file_filename to
dnl "yes" if 'filename' is readable, else "no".
dnl @deprecated! - use AC_CHECK_FILE instead
AC_DEFUN([API_CHECK_FILE], [
dnl Pick a safe variable name
define([api_cvname], ac_cv_file_[]translit([$1], [./+-], [__p_]))
AC_CACHE_CHECK([for $1], [api_cvname],
[if test -r $1; then
   api_cvname=yes
 else
   api_cvname=no
 fi])
])

define(API_IFALLYES,[dnl
ac_rc=yes
for ac_spec in $1; do
    ac_type=`echo "$ac_spec" | sed -e 's/:.*$//'`
    ac_item=`echo "$ac_spec" | sed -e 's/^.*://'`
    case $ac_type in
        header )
            ac_item=`echo "$ac_item" | sed 'y%./+-%__p_%'`
            ac_var="ac_cv_header_$ac_item"
            ;;
        file )
            ac_item=`echo "$ac_item" | sed 'y%./+-%__p_%'`
            ac_var="ac_cv_file_$ac_item"
            ;;
        func )   ac_var="ac_cv_func_$ac_item"   ;;
        struct ) ac_var="ac_cv_struct_$ac_item" ;;
        define ) ac_var="ac_cv_define_$ac_item" ;;
        custom ) ac_var="$ac_item" ;;
    esac
    eval "ac_val=\$$ac_var"
    if test ".$ac_val" != .yes; then
        ac_rc=no
        break
    fi
done
if test ".$ac_rc" = .yes; then
    :
    $2
else
    :
    $3
fi
])


define(API_BEGIN_DECISION,[dnl
ac_decision_item='$1'
ac_decision_msg='FAILED'
ac_decision=''
])


AC_DEFUN([API_DECIDE],[dnl
dnl Define the flag (or not) in api_private.h via autoheader
AH_TEMPLATE($1, [Define if $2 will be used])
ac_decision='$1'
ac_decision_msg='$2'
ac_decision_$1=yes
ac_decision_$1_msg='$2'
])


define(API_DECISION_OVERRIDE,[dnl
    ac_decision=''
    for ac_item in $1; do
         eval "ac_decision_this=\$ac_decision_${ac_item}"
         if test ".$ac_decision_this" = .yes; then
             ac_decision=$ac_item
             eval "ac_decision_msg=\$ac_decision_${ac_item}_msg"
         fi
    done
])


define(API_DECISION_FORCE,[dnl
ac_decision="$1"
eval "ac_decision_msg=\"\$ac_decision_${ac_decision}_msg\""
])


define(API_END_DECISION,[dnl
if test ".$ac_decision" = .; then
    echo "[$]0:Error: decision on $ac_decision_item failed" 1>&2
    exit 1
else
    if test ".$ac_decision_msg" = .; then
        ac_decision_msg="$ac_decision"
    fi
    AC_DEFINE_UNQUOTED(${ac_decision_item})
    AC_MSG_RESULT([decision on $ac_decision_item... $ac_decision_msg])
fi
])


dnl
dnl API_CHECK_SIZEOF_EXTENDED(INCLUDES, TYPE [, CROSS_SIZE])
dnl
dnl A variant of AC_CHECK_SIZEOF which allows the checking of
dnl sizes of non-builtin types
dnl
AC_DEFUN([API_CHECK_SIZEOF_EXTENDED],
[changequote(<<, >>)dnl
dnl The name to #define.
define(<<AC_TYPE_NAME>>, translit(sizeof_$2, [a-z *], [A-Z_P]))dnl
dnl The cache variable name.
define(<<AC_CV_NAME>>, translit(ac_cv_sizeof_$2, [ *], [_p]))dnl
changequote([, ])dnl
AC_MSG_CHECKING(size of $2)
AC_CACHE_VAL(AC_CV_NAME,
[AC_TRY_RUN([#include <stdio.h>
$1
#ifdef WIN32
#define binmode "b"
#else
#define binmode
#endif
main()
{
  FILE *f=fopen("conftestval", "w" binmode);
  if (!f) exit(1);
  fprintf(f, "%d\n", sizeof($2));
  exit(0);
}], AC_CV_NAME=`cat conftestval`, AC_CV_NAME=0, ifelse([$3],,,
AC_CV_NAME=$3))])dnl
AC_MSG_RESULT($AC_CV_NAME)
AC_DEFINE_UNQUOTED(AC_TYPE_NAME, $AC_CV_NAME, [The size of ]$2)
undefine([AC_TYPE_NAME])dnl
undefine([AC_CV_NAME])dnl
])


dnl
dnl API_TRY_COMPILE_NO_WARNING(INCLUDES, FUNCTION-BODY,
dnl             [ACTIONS-IF-NO-WARNINGS], [ACTIONS-IF-WARNINGS])
dnl
dnl Tries a compile test with warnings activated so that the result
dnl is false if the code doesn't compile cleanly.  For compilers
dnl where it is not known how to activate a "fail-on-error" mode,
dnl it is undefined which of the sets of actions will be run.
dnl
AC_DEFUN([API_TRY_COMPILE_NO_WARNING],
[api_save_CFLAGS=$CFLAGS
 CFLAGS="$CFLAGS $CFLAGS_WARN"
 if test "$ac_cv_prog_gcc" = "yes"; then 
   CFLAGS="$CFLAGS -Werror"
 fi
 AC_COMPILE_IFELSE(
  [AC_LANG_SOURCE(
   [#include "confdefs.h"
   ]
   [[$1]]
   [int main(int argc, const char *const *argv) {]
   [[$2]]
   [   return 0; }]
  )],
  [$3], [$4])
 CFLAGS=$api_save_CFLAGS
])

dnl
dnl API_CHECK_STRERROR_R_RC
dnl
dnl  Decide which style of retcode is used by this system's 
dnl  strerror_r().  It either returns int (0 for success, -1
dnl  for failure), or it returns a pointer to the error 
dnl  string.
dnl
dnl
AC_DEFUN([API_CHECK_STRERROR_R_RC], [
AC_MSG_CHECKING(for type of return code from strerror_r)
AC_TRY_RUN([
#include <errno.h>
#include <string.h>
#include <stdio.h>
main()
{
  char buf[1024];
  if (strerror_r(ERANGE, buf, sizeof buf) < 1) {
    exit(0);
  }
  else {
    exit(1);
  }
}], [
    ac_cv_strerror_r_rc_int=yes ], [
    ac_cv_strerror_r_rc_int=no ], [
    ac_cv_strerror_r_rc_int=no ] )
if test "x$ac_cv_strerror_r_rc_int" = xyes; then
  AC_DEFINE(STRERROR_R_RC_INT, 1, [Define if strerror returns int])
  msg="int"
else
  msg="pointer"
fi
AC_MSG_RESULT([$msg])
] )

dnl
dnl API_CHECK_DIRENT_INODE
dnl
dnl  Decide if d_fileno or d_ino are available in the dirent
dnl  structure on this platform.  Single UNIX Spec says d_ino,
dnl  BSD uses d_fileno.  Undef to find the real beast.
dnl
AC_DEFUN([API_CHECK_DIRENT_INODE], [
AC_CACHE_CHECK([for inode member of struct dirent], api_cv_dirent_inode, [
api_cv_dirent_inode=no
AC_TRY_COMPILE([
#include <sys/types.h>
#include <dirent.h>
],[
#ifdef d_ino
#undef d_ino
#endif
struct dirent de; de.d_fileno;
], api_cv_dirent_inode=d_fileno)
if test "$api_cv_dirent_inode" = "no"; then
AC_TRY_COMPILE([
#include <sys/types.h>
#include <dirent.h>
],[
#ifdef d_fileno
#undef d_fileno
#endif
struct dirent de; de.d_ino;
], api_cv_dirent_inode=d_ino)
fi
])
if test "$api_cv_dirent_inode" != "no"; then
  AC_DEFINE_UNQUOTED(DIRENT_INODE, $api_cv_dirent_inode, 
    [Define if struct dirent has an inode member])
fi
])

dnl
dnl API_CHECK_DIRENT_TYPE
dnl
dnl  Decide if d_type is available in the dirent structure 
dnl  on this platform.  Not part of the Single UNIX Spec.
dnl  Note that this is worthless without DT_xxx macros, so
dnl  look for one while we are at it.
dnl
AC_DEFUN([API_CHECK_DIRENT_TYPE], [
AC_CACHE_CHECK([for file type member of struct dirent], api_cv_dirent_type,[
api_cv_dirent_type=no
AC_TRY_COMPILE([
#include <sys/types.h>
#include <dirent.h>
],[
struct dirent de; de.d_type = DT_REG;
], api_cv_dirent_type=d_type)
])
if test "$api_cv_dirent_type" != "no"; then
  AC_DEFINE_UNQUOTED(DIRENT_TYPE, $api_cv_dirent_type, 
    [Define if struct dirent has a d_type member]) 
fi
])

dnl API_FLAG_HEADERS(HEADER-FILE ... [, FLAG-TO-SET ] [, "yes" ])
dnl  we set FLAG-TO-SET to 1 if we find HEADER-FILE, otherwise we set to 0
dnl  if FLAG-TO-SET is null, we automagically determine it's name
dnl  by changing all "/" to "_" in the HEADER-FILE and dropping
dnl  all "." and "-" chars. If the 3rd parameter is "yes" then instead of
dnl  setting to 1 or 0, we set FLAG-TO-SET to yes or no.
dnl  
AC_DEFUN([API_FLAG_HEADERS], [
AC_CHECK_HEADERS($1)
for apit_i in $1
do
    ac_safe=`echo "$apit_i" | sed 'y%./+-%__p_%'`
    apit_2=`echo "$apit_i" | sed -e 's%/%_%g' -e 's/\.//g' -e 's/-//g'`
    if eval "test \"`echo '$ac_cv_header_'$ac_safe`\" = yes"; then
       eval "ifelse($2,,$apit_2,$2)=ifelse($3,yes,yes,1)"
    else
       eval "ifelse($2,,$apit_2,$2)=ifelse($3,yes,no,0)"
    fi
done
])

dnl API_FLAG_FUNCS(FUNC ... [, FLAG-TO-SET] [, "yes" ])
dnl  if FLAG-TO-SET is null, we automagically determine it's name
dnl  prepending "have_" to the function name in FUNC, otherwise
dnl  we use what's provided as FLAG-TO-SET. If the 3rd parameter
dnl  is "yes" then instead of setting to 1 or 0, we set FLAG-TO-SET
dnl  to yes or no.
dnl
AC_DEFUN([API_FLAG_FUNCS], [
AC_CHECK_FUNCS($1)
for apit_j in $1
do
    apit_3="have_$apit_j"
    if eval "test \"`echo '$ac_cv_func_'$apit_j`\" = yes"; then
       eval "ifelse($2,,$apit_3,$2)=ifelse($3,yes,yes,1)"
    else
       eval "ifelse($2,,$apit_3,$2)=ifelse($3,yes,no,0)"
    fi
done
])

dnl Iteratively interpolate the contents of the second argument
dnl until interpolation offers no new result. Then assign the
dnl final result to $1.
dnl
dnl Example:
dnl
dnl foo=1
dnl bar='${foo}/2'
dnl baz='${bar}/3'
dnl API_EXPAND_VAR(fraz, $baz)
dnl   $fraz is now "1/2/3"
dnl 
AC_DEFUN([API_EXPAND_VAR], [
ap_last=
ap_cur="$2"
while test "x${ap_cur}" != "x${ap_last}";
do
  ap_last="${ap_cur}"
  ap_cur=`eval "echo ${ap_cur}"`
done
$1="${ap_cur}"
])

dnl
dnl API_CHECK_DEPEND
dnl
dnl Determine what program we can use to generate .deps-style dependencies
dnl
AC_DEFUN([API_CHECK_DEPEND], [
dnl Try to determine what depend program we can use
dnl All GCC-variants should have -MM.
dnl If not, then we can check on those, too.
if test "$GCC" = "yes"; then
  MKDEP='$(CC) -MM'
else
  rm -f conftest.c
dnl <sys/types.h> should be available everywhere!
  cat > conftest.c <<EOF
#include <sys/types.h>
  int main() { return 0; }
EOF
  MKDEP="true"
  for i in "$CC -MM" "$CC -M" "$CPP -MM" "$CPP -M" "cpp -M"; do
    AC_MSG_CHECKING([if $i can create proper make dependencies])
    if $i conftest.c 2>/dev/null | grep 'conftest.o: conftest.c' >/dev/null; then
      MKDEP=$i
      AC_MSG_RESULT(yes)
      break;
    fi
    AC_MSG_RESULT(no)
  done
  rm -f conftest.c
fi

AC_SUBST(MKDEP)
])

dnl
dnl API_CHECK_TYPES_COMPATIBLE(TYPE-1, TYPE-2, [ACTION-IF-TRUE])
dnl
dnl Try to determine whether two types are the same. Only works
dnl for gcc and icc.
dnl
AC_DEFUN([API_CHECK_TYPES_COMPATIBLE], [
define([api_cvname], api_cv_typematch_[]translit([$1], [ ], [_])_[]translit([$2], [ ], [_]))
AC_CACHE_CHECK([whether $1 and $2 are the same], api_cvname, [
AC_TRY_COMPILE(AC_INCLUDES_DEFAULT, [
    int foo[0 - !__builtin_types_compatible_p($1, $2)];
], [api_cvname=yes
$3], [api_cvname=no])])
])

