AC_ARG_ENABLE(dso,
  [  --disable-dso           Disable DSO support ],
  [if test "x$enableval" = "xyes"; then
      dsotype=any
   else
      dsotype=$enableval
   fi
  ], [dsotype=any])

if test "$dsotype" = "any"; then
    if test "$dsotype" = "any"; then
      case $host in
        *darwin[[0-8]]\.*) 
          # Original Darwin, not for 9.0!:
          AC_CHECK_FUNC(NSLinkModule, [dsotype=dyld]);;
        hppa*-hpux[[1-9]]\.*|hppa*-hpux1[[01]]*)
          # shl is specific to parisc hpux SOM binaries, not used for 64 bit
          AC_CHECK_LIB(dld, shl_load, [have_shl=1])
          if test "$ac_cv_sizeof_voidp$have_shl" = "41"; then
            dsotype=shl; API_ADDTO(LIBS,-ldld)
          fi;;
        *-mingw*|*-os2*)
          # several 'other's below probably belong up here.  If they always
          # use a platform implementation and shouldn't test the dlopen/dlfcn
          # features, then bring them up here.
          # But if they -should- optionally use dlfcn, and/or need the config
          # detection of dlopen/dlsym, do not move them up.
          dsotype=other ;;
      esac
    fi
    # Normal POSIX:
    if test "$dsotype" = "any"; then
      AC_CHECK_FUNC(dlopen, [dsotype=dlfcn])
    fi
    if test "$dsotype" = "any"; then
      AC_CHECK_LIB(dl, dlopen, [dsotype=dlfcn; API_ADDTO(LIBS,-ldl)])
    fi
    if test "$dsotype" = "dlfcn"; then
        # ReliantUnix has dlopen() in libc but dlsym() in libdl :(
        AC_CHECK_FUNC(dlsym, [], 
          [AC_CHECK_LIB(dl, dlsym, 
             [API_ADDTO(LIBS, -ldl)],
             [dsotype=any
              AC_MSG_WARN([Weird: dlopen() was found but dlsym() was not found!])])])
    fi
    if test "$dsotype" = "any"; then
      # BeOS:
      AC_CHECK_LIB(root, load_image, [dsotype=other])
    fi
    # Everything else:
    if test "$dsotype" = "any"; then
        case $host in
        *os390|*os400|*-aix*)
          # Some -aix5 will use dl, no hassles.  Keep that pattern here.
          dsotype=other ;;
        *-hpux*)
          if test "$have_shl" = "1"; then
            dsotype=shl; API_ADDTO(LIBS,-ldld)
          fi;;
        esac
    fi
fi

if test "$dsotype" = "any"; then
    AC_MSG_ERROR([Could not detect suitable DSO implementation])
elif test "$dsotype" = "no"; then
    apidso="0"
else
    case "$dsotype" in
    dlfcn) AC_DEFINE(DSO_USE_DLFCN, 1, [Define if DSO support uses dlfcn.h]);;
    shl)   AC_DEFINE(DSO_USE_SHL, 1, [Define if DSO support uses shl_load]);;
    dyld)  AC_DEFINE(DSO_USE_DYLD, 1, [Define if DSO support uses dyld.h]);;
    other) ;; # Use whatever is in dso/OSDIR
    *) AC_MSG_ERROR([Unknown DSO implementation "$dsotype"]);;
    esac
    apidso="1"
    api_modules="$api_modules dso"
fi

AC_SUBST(apidso)

