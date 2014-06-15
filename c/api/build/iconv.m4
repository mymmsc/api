dnl -------------------------------------------------------- -*- autoconf -*-

dnl
dnl API_TRY_ICONV[ IF-SUCCESS, IF-FAILURE ]: try to compile for iconv.
dnl
AC_DEFUN([API_TRY_ICONV], [
  AC_TRY_LINK([
#include <stdlib.h>
#include <iconv.h>
],
[
  iconv_t cd = iconv_open("", "");
  iconv(cd, NULL, NULL, NULL, NULL);
], [$1], [$2])
])

dnl
dnl API_FIND_ICONV: find an iconv library
dnl
AC_DEFUN([API_FIND_ICONV], [

api_iconv_dir="unknown"
want_iconv="1"
AC_ARG_WITH(iconv,[  --with-iconv[=DIR]        path to iconv installation],
  [ api_iconv_dir="$withval"
    if test "$api_iconv_dir" = "no"; then
      have_iconv="0"
      want_iconv="0"
    elif test "$api_iconv_dir" != "yes"; then
      if test -f "$api_iconv_dir/include/iconv.h"; then
        have_iconv="1"
        API_ADDTO(CPPFLAGS,[-I$api_iconv_dir/include])
        API_ADDTO(LDFLAGS,[-L$api_iconv_dir/lib])
      fi
    fi
  ])

if test "$want_iconv" = "1"; then
  AC_CHECK_HEADER(iconv.h, [
    API_TRY_ICONV([ have_iconv="1" ], [

    API_ADDTO(LIBS,[-liconv])

    API_TRY_ICONV([
      API_ADDTO(LIBS,[-liconv])
      API_ADDTO(APIUTIL_EXPORT_LIBS,[-liconv])
      have_iconv="1" ],
      [ have_iconv="0" ])

    API_REMOVEFROM(LIBS,[-liconv])

    ])
  ], [ have_iconv="0" ])
fi

if test "$want_iconv" = "1" -a "$api_iconv_dir" != "unknown"; then
  if test "$have_iconv" != "1"; then
    AC_MSG_ERROR([iconv support requested, but not found])
  fi
  API_REMOVEFROM(CPPFLAGS,[-I$api_iconv_dir/include])
  API_ADDTO(INCLUDES,[-I$api_iconv_dir/include])
  API_ADDTO(LDFLAGS,[-L$api_iconv_dir/lib])
fi

if test "$have_iconv" = "1"; then
  API_CHECK_ICONV_INBUF
fi

API_FLAG_HEADERS(iconv.h langinfo.h)
API_FLAG_FUNCS(nl_langinfo)
API_CHECK_DEFINE(CODESET, langinfo.h, [CODESET defined in langinfo.h])

AC_SUBST(have_iconv)
])dnl

dnl
dnl API_CHECK_ICONV_INBUF
dnl
dnl  Decide whether or not the inbuf parameter to iconv() is const.
dnl
dnl  We try to compile something without const.  If it fails to 
dnl  compile, we assume that the system's iconv() has const.  
dnl  Unfortunately, we won't realize when there was a compile
dnl  warning, so we allow a variable -- api_iconv_inbuf_const -- to
dnl  be set in hints.m4 to specify whether or not iconv() has const
dnl  on this parameter.
dnl
AC_DEFUN([API_CHECK_ICONV_INBUF], [
AC_MSG_CHECKING(for type of inbuf parameter to iconv)
if test "x$api_iconv_inbuf_const" = "x"; then
    API_TRY_COMPILE_NO_WARNING([
    #include <stddef.h>
    #include <iconv.h>
    ],[
    iconv(0,(char **)0,(size_t *)0,(char **)0,(size_t *)0);
    ], api_iconv_inbuf_const="0", api_iconv_inbuf_const="1")
fi
if test "$api_iconv_inbuf_const" = "1"; then
    AC_DEFINE(API_ICONV_INBUF_CONST, 1, [Define if the inbuf parm to iconv() is const char **])
    msg="const char **"
else
    msg="char **"
fi
AC_MSG_RESULT([$msg])
])dnl
