AC_TYPE_OFF_T
AC_TYPE_PID_T
AC_TYPE_SIZE_T
AC_TYPE_UID_T
AC_CHECK_TYPE(ssize_t, int)
AC_C_INLINE
AC_C_CONST
AC_FUNC_SETPGRP

API_CHECK_SOCKLEN_T

dnl Checks for pointer size
AC_CHECK_SIZEOF(void*, 4)

if test "x$ac_cv_sizeof_voidp" != "x"; then
    voidp_size=$ac_cv_sizeof_voidp
else
    AC_ERROR([Cannot determine size of void*])
fi

dnl Checks for integer size
AC_CHECK_SIZEOF(char, 1)
AC_CHECK_SIZEOF(int, 4)
AC_CHECK_SIZEOF(long, 4)
AC_CHECK_SIZEOF(short, 2)
AC_CHECK_SIZEOF(long long, 8)

if test "$ac_cv_sizeof_short" = "2"; then
    short_value=short
fi
if test "$ac_cv_sizeof_int" = "4"; then
    int_value=int
fi
# Now we need to find what api_int64_t (sizeof == 8) will be.
# The first match is our preference.
if test "$ac_cv_sizeof_int" = "8"; then
    int64_literal='#define API_INT64_C(val) (val)'
    uint64_literal='#define API_UINT64_C(val) (val##U)'
    int64_t_fmt='#define API_INT64_T_FMT "d"'
    uint64_t_fmt='#define API_UINT64_T_FMT "u"'
    uint64_t_hex_fmt='#define API_UINT64_T_HEX_FMT "x"'
    int64_value="int"
    long_value=int
    int64_strfn="strtoi"
elif test "$ac_cv_sizeof_long" = "8"; then
    int64_literal='#define API_INT64_C(val) (val##L)'
    uint64_literal='#define API_UINT64_C(val) (val##UL)'
    int64_t_fmt='#define API_INT64_T_FMT "ld"'
    uint64_t_fmt='#define API_UINT64_T_FMT "lu"'
    uint64_t_hex_fmt='#define API_UINT64_T_HEX_FMT "lx"'
    int64_value="long"
    long_value=long
    int64_strfn="strtol"
elif test "$ac_cv_sizeof_long_long" = "8"; then
    int64_literal='#define API_INT64_C(val) (val##LL)'
    uint64_literal='#define API_UINT64_C(val) (val##ULL)'
    # Linux, Solaris, FreeBSD all support ll with printf.
    # BSD 4.4 originated 'q'.  Solaris is more popular and 
    # doesn't support 'q'.  Solaris wins.  Exceptions can
    # go to the OS-dependent section.
    int64_t_fmt='#define API_INT64_T_FMT "lld"'
    uint64_t_fmt='#define API_UINT64_T_FMT "llu"'
    uint64_t_hex_fmt='#define API_UINT64_T_HEX_FMT "llx"'
    int64_value="long long"
    long_value="long long"
    int64_strfn="strtoll"
elif test "$ac_cv_sizeof_longlong" = "8"; then
    int64_literal='#define API_INT64_C(val) (val##LL)'
    uint64_literal='#define API_UINT64_C(val) (val##ULL)'
    int64_t_fmt='#define API_INT64_T_FMT "qd"'
    uint64_t_fmt='#define API_UINT64_T_FMT "qu"'
    uint64_t_hex_fmt='#define API_UINT64_T_HEX_FMT "qx"'
    int64_value="__int64"
    long_value="__int64"
    int64_strfn="strtoll"
else
    # int64_literal may be overriden if your compiler thinks you have
    # a 64-bit value but API does not agree.
    AC_ERROR([could not detect a 64-bit integer type])
fi

# If present, allow the C99 macro INT64_C to override our conversion.
#
# HP-UX's ANSI C compiler provides this without any includes, so we
# will first look for INT64_C without adding stdint.h
AC_CACHE_CHECK([for INT64_C], [api_cv_define_INT64_C], [
AC_EGREP_CPP(YES_IS_DEFINED,
[#ifdef INT64_C
YES_IS_DEFINED
#endif], [api_cv_define_INT64_C=yes], [
    # Now check for INT64_C in stdint.h
    AC_EGREP_CPP(YES_IS_DEFINED, [#include <stdint.h>
#ifdef INT64_C
YES_IS_DEFINED
#endif], [api_cv_define_INT64_C=yes], [api_cv_define_INT64_C=no])])])

if test "$api_cv_define_INT64_C" = "yes"; then
    int64_literal='#define API_INT64_C(val) INT64_C(val)'
    uint64_literal='#define API_UINT64_C(val) UINT64_C(val)'
    stdint=1
else
    stdint=0
fi

if test "$ac_cv_type_size_t" = "yes"; then
    size_t_value="size_t"
else
    size_t_value="api_int32_t"
fi
if test "$ac_cv_type_ssize_t" = "yes"; then
    ssize_t_value="ssize_t"
else
    ssize_t_value="api_int32_t"
fi
if test "$ac_cv_socklen_t" = "yes"; then
    socklen_t_value="socklen_t"
    case $host in
        *-hp-hpux*)
            if test "$ac_cv_sizeof_long" = "8"; then
                # 64-bit HP-UX requires 32-bit socklens in
                # kernel, but user-space declarations say
                # 64-bit (socklen_t == size_t == long).
                # This will result in many compile warnings,
                # but we're functionally busted otherwise.
                socklen_t_value="int"
            fi
            ;;
    esac
else
    socklen_t_value="int"
fi

API_CHECK_SIZEOF_EXTENDED([#include <sys/types.h>], pid_t, 8)

if test "$ac_cv_sizeof_pid_t" = "$ac_cv_sizeof_short"; then
    pid_t_fmt='#define API_PID_T_FMT "hd"'
elif test "$ac_cv_sizeof_pid_t" = "$ac_cv_sizeof_int"; then
    pid_t_fmt='#define API_PID_T_FMT "d"'
elif test "$ac_cv_sizeof_pid_t" = "$ac_cv_sizeof_long"; then
    pid_t_fmt='#define API_PID_T_FMT "ld"'
elif test "$ac_cv_sizeof_pid_t" = "$ac_cv_sizeof_long_long"; then
    pid_t_fmt='#define API_PID_T_FMT API_INT64_T_FMT'
else
    pid_t_fmt='#error Can not determine the proper size for pid_t'
fi

# Basically, we have tried to figure out the correct format strings
# for API types which vary between platforms, but we don't always get
# it right.
case $host in
   s390*linux*)
       # uniquely, the 31-bit Linux/s390 uses "unsigned long int"
       # for size_t rather than "unsigned int":
       size_t_fmt="lu"
       ssize_t_fmt="ld"
       ;;
   *-os2*)
       size_t_fmt="lu"
       ;;
   *-solaris*)
       if test "$ac_cv_sizeof_long" = "8"; then
         pid_t_fmt='#define API_PID_T_FMT "d"'
       else
         pid_t_fmt='#define API_PID_T_FMT "ld"'
       fi
       ;;
   *aix4*|*aix5*)
       ssize_t_fmt="ld"
       size_t_fmt="lu"
       ;;
    *beos*)
        ssize_t_fmt="ld"
        size_t_fmt="ld"
        ;;
    *apple-darwin*)
        osver=`uname -r`
        case $osver in
           [[0-7]].*)
              ssize_t_fmt="d"
              ;;
           *)
              ssize_t_fmt="ld"
              ;;
        esac
        size_t_fmt="lu"
        ;;
    *-mingw*)
        int64_t_fmt='#define API_INT64_T_FMT "I64d"'
        uint64_t_fmt='#define API_UINT64_T_FMT "I64u"'
        uint64_t_hex_fmt='#define API_UINT64_T_HEX_FMT "I64x"'
        int64_value="__int64"
        long_value="__int64"
        int64_strfn="_strtoi64"
        ;;
esac

API_CHECK_TYPES_COMPATIBLE(ssize_t, int, [ssize_t_fmt="d"])
API_CHECK_TYPES_COMPATIBLE(ssize_t, long, [ssize_t_fmt="ld"])
API_CHECK_TYPES_COMPATIBLE(size_t, unsigned int, [size_t_fmt="u"])
API_CHECK_TYPES_COMPATIBLE(size_t, unsigned long, [size_t_fmt="lu"])

API_CHECK_SIZEOF_EXTENDED([#include <sys/types.h>], ssize_t, 8)

AC_MSG_CHECKING([which format to use for api_ssize_t])
if test -n "$ssize_t_fmt"; then
    AC_MSG_RESULT(%$ssize_t_fmt)
elif test "$ac_cv_sizeof_ssize_t" = "$ac_cv_sizeof_int"; then
    ssize_t_fmt="d"
    AC_MSG_RESULT(%d)
elif test "$ac_cv_sizeof_ssize_t" = "$ac_cv_sizeof_long"; then
    ssize_t_fmt="ld"
    AC_MSG_RESULT(%ld)
else
    AC_ERROR([could not determine the proper format for api_ssize_t])
fi

ssize_t_fmt="#define API_SSIZE_T_FMT \"$ssize_t_fmt\""

API_CHECK_SIZEOF_EXTENDED([#include <stddef.h>], size_t, 8)

AC_MSG_CHECKING([which format to use for api_size_t])
if test -n "$size_t_fmt"; then
    AC_MSG_RESULT(%$size_t_fmt)
elif test "$ac_cv_sizeof_size_t" = "$ac_cv_sizeof_int"; then
    size_t_fmt="d"
    AC_MSG_RESULT(%d)
elif test "$ac_cv_sizeof_size_t" = "$ac_cv_sizeof_long"; then
    size_t_fmt="ld"
    AC_MSG_RESULT(%ld)
else
    AC_ERROR([could not determine the proper format for api_size_t])
fi

size_t_fmt="#define API_SIZE_T_FMT \"$size_t_fmt\""

API_CHECK_SIZEOF_EXTENDED([#include <sys/types.h>], off_t, 8)

if test "${ac_cv_sizeof_off_t}${api_cv_use_lfs64}" = "4yes"; then
    # Enable LFS
    apilfs=1
    AC_CHECK_FUNCS([mmap64 sendfile64 sendfilev64 mkstemp64 readdir64_r])
elif test "${ac_cv_sizeof_off_t}" != "${ac_cv_sizeof_size_t}"; then
    # unsure of using -gt above is as portable, can can't forsee where
    # off_t can legitimately be smaller than size_t
    apilfs=1
else
    apilfs=0     
fi

AC_MSG_CHECKING([which type to use for api_off_t])
if test "${ac_cv_sizeof_off_t}${api_cv_use_lfs64}" = "4yes"; then
    # LFS is go!
    off_t_fmt='#define API_OFF_T_FMT API_INT64_T_FMT'
    off_t_value='off64_t'
    off_t_strfn='api_strtoi64'
elif test "${ac_cv_sizeof_off_t}x${ac_cv_sizeof_long}" = "4x4"; then
    # Special case: off_t may change size with _FILE_OFFSET_BITS
    # on 32-bit systems with LFS support.  To avoid compatibility
    # issues when other packages do define _FILE_OFFSET_BITS,
    # hard-code api_off_t to long.
    off_t_value=long
    off_t_fmt='#define API_OFF_T_FMT "ld"'
    off_t_strfn='strtol'
elif test "$ac_cv_type_off_t" = "yes"; then
    off_t_value=off_t
    # off_t is more commonly a long than an int; prefer that case
    # where int and long are the same size.
    if test "$ac_cv_sizeof_off_t" = "$ac_cv_sizeof_long"; then
        off_t_fmt='#define API_OFF_T_FMT "ld"'
        off_t_strfn='strtol'
    elif test "$ac_cv_sizeof_off_t" = "$ac_cv_sizeof_int"; then
        off_t_fmt='#define API_OFF_T_FMT "d"'
        off_t_strfn='strtoi'
    elif test "$ac_cv_sizeof_off_t" = "$ac_cv_sizeof_long_long"; then
        off_t_fmt='#define API_OFF_T_FMT API_INT64_T_FMT'
        off_t_strfn='api_strtoi64'
    else
        AC_ERROR([could not determine the size of off_t])
    fi
    # Per OS tuning...
    case $host in
    *-mingw*)
        off_t_value=api_int64_t
        off_t_fmt='#define API_OFF_T_FMT "I64d"'
        off_t_strfn='_strtoi64'
        ;;
    esac
else
    # Fallback on int
    off_t_value=api_int64_t
    off_t_fmt='#define API_OFF_T_FMT "d"'
    off_t_strfn='strtoi'
fi
AC_MSG_RESULT($off_t_value)

# Regardless of whether _LARGEFILE64_SOURCE is used, on some
# platforms _FILE_OFFSET_BITS will affect the size of ino_t and hence
# the build-time ABI may be different from the apparent ABI when using
# API with another package which *does* define _FILE_OFFSET_BITS.
# (Exactly as per the case above with off_t where LFS is *not* used)
#
# To be safe, hard-code api_ino_t as 'unsigned long' or 'unsigned int'
# iff that is exactly the size of ino_t here; otherwise use ino_t as existing
# releases did.  To be correct, api_ino_t should have been made an
# ino64_t as api_off_t is off64_t, but this can't be done now without
# breaking ABI.

# Per OS tuning...
case $host in
*mingw*)
    ino_t_value=api_int64_t
    ;;
*)
    ino_t_value=ino_t
    API_CHECK_SIZEOF_EXTENDED(AC_INCLUDES_DEFAULT, ino_t, $ac_cv_sizeof_long)
    if test $ac_cv_sizeof_ino_t = 4; then
        if test $ac_cv_sizeof_long = 4; then
            ino_t_value="unsigned long"
        else
            ino_t_value="unsigned int"
        fi
    fi
    ;;
esac
AC_MSG_NOTICE([using $ino_t_value for ino_t])

# Checks for endianness
AC_C_BIGENDIAN
if test $ac_cv_c_bigendian = yes; then
    bigendian=1
else
    bigendian=0
fi

API_CHECK_SIZEOF_EXTENDED([#include <sys/types.h>
#include <sys/uio.h>],struct iovec,0)
if test "$ac_cv_sizeof_struct_iovec" = "0"; then
    have_iovec=0
else
    have_iovec=1
fi

AC_SUBST(voidp_size)
AC_SUBST(short_value)
AC_SUBST(int_value)
AC_SUBST(long_value)
AC_SUBST(int64_value)
AC_SUBST(off_t_value)
AC_SUBST(size_t_value)
AC_SUBST(ssize_t_value)
AC_SUBST(socklen_t_value)
AC_SUBST(int64_t_fmt) 
AC_SUBST(uint64_t_fmt) 
AC_SUBST(uint64_t_hex_fmt) 
AC_SUBST(ssize_t_fmt) 
AC_SUBST(size_t_fmt)
AC_SUBST(off_t_fmt) 
AC_SUBST(pid_t_fmt)
AC_SUBST(int64_literal) 
AC_SUBST(uint64_literal) 
AC_SUBST(stdint) 
AC_SUBST(bigendian)
AC_SUBST(apilfs)
AC_SUBST(have_iovec)
AC_SUBST(ino_t_value)

