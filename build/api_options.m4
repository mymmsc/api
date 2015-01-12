# We use -Werror for the tests only so that we catch warnings like "this is
# on by default" for things like -fPIE.
AC_MSG_CHECKING([if $CC supports -Werror])
saved_CFLAGS="$CFLAGS"
CFLAGS="$CFLAGS -Werror"
AC_COMPILE_IFELSE([AC_LANG_SOURCE([[int main(void) { return 0; }]])],
	[ AC_MSG_RESULT([yes])
	  WERROR="-Werror"],
	[ AC_MSG_RESULT([no])
	  WERROR="" ]
)
CFLAGS="$saved_CFLAGS"

if test "$GCC" = "yes" || test "$GCC" = "egcs"; then
	API_CHECK_CFLAG_COMPILE([-Wall])
	API_CHECK_CFLAG_COMPILE([-Qunused-arguments])
	API_CHECK_CFLAG_COMPILE([-Wunknown-warning-option])
	API_CHECK_CFLAG_COMPILE([-Wpointer-arith])
	API_CHECK_CFLAG_COMPILE([-Wuninitialized])
	API_CHECK_CFLAG_COMPILE([-Wsign-compare])
	API_CHECK_CFLAG_COMPILE([-Wformat-security])
	API_CHECK_CFLAG_COMPILE([-Wsizeof-pointer-memaccess])
	API_CHECK_CFLAG_COMPILE([-Wpointer-sign], [-Wno-pointer-sign])
	API_CHECK_CFLAG_COMPILE([-Wunused-result], [-Wno-unused-result])
	API_CHECK_CFLAG_COMPILE([-fno-strict-aliasing])
	API_CHECK_CFLAG_COMPILE([-D_FORTIFY_SOURCE=2])
  
	API_CHECK_CFLAG_COMPILE([-Werror])
	API_CHECK_CFLAG_COMPILE([-Wno-unused-parameter], [-Wunused-parameter])
	# 当函数在使用前没有函数原型时
	API_CHECK_CFLAG_COMPILE([-Wmissing-prototypes])
	# 如果函数的声明或定义没有指出参数类型，编译器就发出警告。很有用的警告。
	API_CHECK_CFLAG_COMPILE([-Wstrict-prototypes])
	API_CHECK_CFLAG_COMPILE([-Wmissing-declarations])
	
    if test "x$use_toolchain_hardening" = "x1"; then
	API_CHECK_LDFLAG_LINK([-Wl,-z,relro])
	API_CHECK_LDFLAG_LINK([-Wl,-z,now])
	API_CHECK_LDFLAG_LINK([-Wl,-z,noexecstack])
	# NB. -ftrapv expects certain support functions to be present in
	# the compiler library (libgcc or similar) to detect integer operations
	# that can overflow. We must check that the result of enabling it
	# actually links. The test program compiled/linked includes a number
	# of integer operations that should exercise this.
	API_CHECK_CFLAG_LINK([-ftrapv])
    fi
	AC_MSG_CHECKING([gcc version])
	GCC_VER=`$CC -v 2>&1 | $AWK '/gcc version /{print $3}'`
	case $GCC_VER in
		1.*) no_attrib_nonnull=1 ;;
		2.8* | 2.9*)
		     no_attrib_nonnull=1
		     ;;
		2.*) no_attrib_nonnull=1 ;;
		*) ;;
	esac
	AC_MSG_RESULT([$GCC_VER])

	AC_MSG_CHECKING([if $CC accepts -fno-builtin-memset])
	saved_CFLAGS="$CFLAGS"
	CFLAGS="$CFLAGS -fno-builtin-memset"
	AC_LINK_IFELSE([AC_LANG_PROGRAM([[ #include <string.h> ]],
			[[ char b[10]; memset(b, 0, sizeof(b)); ]])],
		[ AC_MSG_RESULT([yes]) ],
		[ AC_MSG_RESULT([no])
		  CFLAGS="$saved_CFLAGS" ]
	)

	# -fstack-protector-all doesn't always work for some GCC versions
	# and/or platforms, so we test if we can.  If it's not supported
	# on a given platform gcc will emit a warning so we use -Werror.
	if test "x$use_stack_protector" = "x1"; then
	    for t in -fstack-protector-strong -fstack-protector-all \
		    -fstack-protector; do
		AC_MSG_CHECKING([if $CC supports $t])
		saved_CFLAGS="$CFLAGS"
		saved_LDFLAGS="$LDFLAGS"
		CFLAGS="$CFLAGS $t -Werror"
		LDFLAGS="$LDFLAGS $t -Werror"
		AC_LINK_IFELSE(
			[AC_LANG_PROGRAM([[ #include <stdio.h> ]],
			[[
	char x[256];
	snprintf(x, sizeof(x), "XXX");
			 ]])],
		    [ AC_MSG_RESULT([yes])
		      CFLAGS="$saved_CFLAGS $t"
		      LDFLAGS="$saved_LDFLAGS $t"
		      AC_MSG_CHECKING([if $t works])
		      AC_RUN_IFELSE(
			[AC_LANG_PROGRAM([[ #include <stdio.h> ]],
			[[
	char x[256];
	snprintf(x, sizeof(x), "XXX");
			]])],
			[ AC_MSG_RESULT([yes])
			  break ],
			[ AC_MSG_RESULT([no]) ],
			[ AC_MSG_WARN([cross compiling: cannot test])
			  break ]
		      )
		    ],
		    [ AC_MSG_RESULT([no]) ]
		)
		CFLAGS="$saved_CFLAGS"
		LDFLAGS="$saved_LDFLAGS"
	    done
	fi

	if test -z "$have_llong_max"; then
		# retry LLONG_MAX with -std=gnu99, needed on some Linuxes
		unset ac_cv_have_decl_LLONG_MAX
		saved_CFLAGS="$CFLAGS"
		CFLAGS="$CFLAGS -std=gnu99"
		AC_CHECK_DECL([LLONG_MAX],
		    [have_llong_max=1],
		    [CFLAGS="$saved_CFLAGS"],
		    [#include <limits.h>]
		)
	fi
fi
