AC_CHECK_FUNCS(strnicmp, have_strnicmp="1", have_strnicmp="0")
AC_CHECK_FUNCS(strncasecmp, have_strncasecmp="1", have_strncasecmp="0")
AC_CHECK_FUNCS(stricmp, have_stricmp="1", have_stricmp="0")
AC_CHECK_FUNCS(strcasecmp, have_strcasecmp="1", have_strcasecmp="0")
AC_CHECK_FUNCS(strdup, have_strdup="1", have_strdup="0")
AC_CHECK_FUNCS(strstr, have_strstr="1", have_strstr="0")
AC_CHECK_FUNCS(memchr, have_memchr="1", have_memchr="0")
AC_CHECK_FUNC($int64_strfn, have_int64_strfn="1", have_int64_strfn="0")

dnl ----------------------------- We have a fallback position
if test "$have_int64_strfn" = "0" && test "$int64_strfn" = "strtoll"; then
    int64_strfn="strtoq"
    AC_CHECK_FUNC(strtoq, [have_int64_strfn=1], [have_int64_strfn=0])
fi

if test "$have_int64_strfn" = "1"; then
  AC_DEFINE_UNQUOTED(API_INT64_STRFN, [$int64_strfn],
      [Define as function which can be used for conversion of strings to api_int64_t])
fi

AC_SUBST(have_strnicmp)
AC_SUBST(have_strncasecmp)
AC_SUBST(have_stricmp)
AC_SUBST(have_strcasecmp)
AC_SUBST(have_strdup)
AC_SUBST(have_strstr)
AC_SUBST(have_memchr)

if test "$off_t_strfn" = "api_strtoi64" && test "$have_int64_strfn" = "1"; then
    off_t_strfn=$int64_strfn
fi
AC_DEFINE_UNQUOTED(API_OFF_T_STRFN, [$off_t_strfn],
          [Define as function used for conversion of strings to api_off_t])

API_FLAG_FUNCS([asprintf \
                bcopy \
				explicit_bzero \
				snprintf \
				strerror \
				strlcat \
				strlcpy \
				strmode \
				strnlen \
				strnvis \
				strptime \
				strtonum \
				strtoll \
				strtoul \
				strtoull\ 
				vasprintf \
				vsnprintf 
])

AC_SUBST(have_asprintf)
AC_SUBST(have_bcopy)
AC_SUBST(have_explicit_bzero)
AC_SUBST(have_snprintf)
AC_SUBST(have_strerror)
AC_SUBST(have_strlcat)
AC_SUBST(have_strlcpy)
AC_SUBST(have_strmode)
AC_SUBST(have_strnlen)
AC_SUBST(have_strnvis)
AC_SUBST(have_strptime)
AC_SUBST(have_strtonum)
AC_SUBST(have_strtoll)
AC_SUBST(have_strtoul)
AC_SUBST(have_strtoull)
AC_SUBST(have_vasprintf)
AC_SUBST(have_vsnprintf)

# Check for broken snprintf
if test "x$ac_cv_func_snprintf" = "xyes" ; then
	AC_MSG_CHECKING([whether snprintf correctly terminates long strings])
	AC_RUN_IFELSE(
		[AC_LANG_PROGRAM([[ #include <stdio.h> ]],
		[[
	char b[5];
	snprintf(b,5,"123456789");
	exit(b[4]!='\0'); 
		]])],
		[AC_MSG_RESULT([yes])],
		[
			AC_MSG_RESULT([no])
			AC_DEFINE([BROKEN_SNPRINTF], [1],
				[Define if your snprintf is busted])
			AC_MSG_WARN([****** Your snprintf() function is broken, complain to your vendor])
		],
		[ AC_MSG_WARN([cross compiling: Assuming working snprintf()]) ]
	)
fi

# If we don't have a working asprintf, then we strongly depend on vsnprintf
# returning the right thing on overflow: the number of characters it tried to
# create (as per SUSv3)
if test "x$ac_cv_func_asprintf" != "xyes" && \
   test "x$ac_cv_func_vsnprintf" = "xyes" ; then
	AC_MSG_CHECKING([whether vsnprintf returns correct values on overflow])
	AC_RUN_IFELSE(
		[AC_LANG_PROGRAM([[
#include <sys/types.h>
#include <stdio.h>
#include <stdarg.h>

int x_snprintf(char *str,size_t count,const char *fmt,...)
{
	size_t ret; va_list ap;
	va_start(ap, fmt); ret = vsnprintf(str, count, fmt, ap); va_end(ap);
	return ret;
}
		]], [[
	char x[1];
	exit(x_snprintf(x, 1, "%s %d", "hello", 12345) == 11 ? 0 : 1);
		]])],
		[AC_MSG_RESULT([yes])],
		[
			AC_MSG_RESULT([no])
			AC_DEFINE([BROKEN_SNPRINTF], [1],
				[Define if your snprintf is busted])
			AC_MSG_WARN([****** Your vsnprintf() function is broken, complain to your vendor])
		],
		[ AC_MSG_WARN([cross compiling: Assuming working vsnprintf()]) ]
	)
fi

# On systems where [v]snprintf is broken, but is declared in stdio,
# check that the fmt argument is const char * or just char *.
# This is only useful for when BROKEN_SNPRINTF
AC_MSG_CHECKING([whether snprintf can declare const char *fmt])
AC_COMPILE_IFELSE([AC_LANG_PROGRAM([[
#include <stdio.h>
int snprintf(char *a, size_t b, const char *c, ...) { return 0; }
		]], [[
	snprintf(0, 0, 0);
		]])],
   [AC_MSG_RESULT([yes])
    AC_DEFINE([SNPRINTF_CONST], [const],
              [Define as const if snprintf() can declare const char *fmt])],
   [AC_MSG_RESULT([no])
    AC_DEFINE([SNPRINTF_CONST], [/* not const */])])

