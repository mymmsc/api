dnl -------------------------------------------------------- -*- autoconf -*-
dnl Licensed to the Apache Software Foundation (ASF) under one or more
dnl contributor license agreements.  See the NOTICE file distributed with
dnl this work for additional information regarding copyright ownership.
dnl The ASF licenses this file to You under the Apache License, Version 2.0
dnl (the "License"); you may not use this file except in compliance with
dnl the License.  You may obtain a copy of the License at
dnl
dnl     http://www.apache.org/licenses/LICENSE-2.0
dnl
dnl Unless required by applicable law or agreed to in writing, software
dnl distributed under the License is distributed on an "AS IS" BASIS,
dnl WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
dnl See the License for the specific language governing permissions and
dnl limitations under the License.

dnl
dnl find_api.m4 : locate the API include files and libraries
dnl
dnl This macro file can be used by applications to find and use the API
dnl library. It provides a standardized mechanism for using API. It supports
dnl embedding API into the application source, or locating an installed
dnl copy of API.
dnl
dnl API_FIND_API(srcdir, builddir, implicit-install-check, acceptable-majors,
dnl              detailed-check)
dnl
dnl   where srcdir is the location of the bundled API source directory, or
dnl   empty if source is not bundled.
dnl
dnl   where builddir is the location where the bundled API will will be built,
dnl   or empty if the build will occur in the srcdir.
dnl
dnl   where implicit-install-check set to 1 indicates if there is no
dnl   --with-api option specified, we will look for installed copies.
dnl
dnl   where acceptable-majors is a space separated list of acceptable major
dnl   version numbers. Often only a single major version will be acceptable.
dnl   If multiple versions are specified, and --with-api=PREFIX or the
dnl   implicit installed search are used, then the first (leftmost) version
dnl   in the list that is found will be used.  Currently defaults to [0 1].
dnl
dnl   where detailed-check is an M4 macro which sets the api_acceptable to
dnl   either "yes" or "no". The macro will be invoked for each installed
dnl   copy of API found, with the api_config variable set appropriately.
dnl   Only installed copies of API which are considered acceptable by
dnl   this macro will be considered found. If no installed copies are
dnl   considered acceptable by this macro, api_found will be set to either
dnl   either "no" or "reconfig".
dnl
dnl Sets the following variables on exit:
dnl
dnl   api_found : "yes", "no", "reconfig"
dnl
dnl   api_config : If the api-config tool exists, this refers to it. If
dnl                api_found is "reconfig", then the bundled directory
dnl                should be reconfigured *before* using api_config.
dnl
dnl Note: this macro file assumes that api-config has been installed; it
dnl       is normally considered a required part of an API installation.
dnl
dnl If a bundled source directory is available and needs to be (re)configured,
dnl then api_found is set to "reconfig". The caller should reconfigure the
dnl (passed-in) source directory, placing the result in the build directory,
dnl as appropriate.
dnl
dnl If api_found is "yes" or "reconfig", then the caller should use the
dnl value of api_config to fetch any necessary build/link information.
dnl

AC_DEFUN([API_FIND_API], [
  api_found="no"

  if test "$target_os" = "os2-emx"; then
    # Scripts don't pass test -x on OS/2
    TEST_X="test -f"
  else
    TEST_X="test -x"
  fi

  ifelse([$4], [], [
         ifdef(AC_WARNING,AC_WARNING([$0: missing argument 4 (acceptable-majors): Defaulting to API 0.x then API 1.x]))
         acceptable_majors="0 1"],
         [acceptable_majors="$4"])

  api_temp_acceptable_api_config=""
  for api_temp_major in $acceptable_majors
  do
    case $api_temp_major in
      0)
      api_temp_acceptable_api_config="$api_temp_acceptable_api_config api-config"
      ;;
      *)
      api_temp_acceptable_api_config="$api_temp_acceptable_api_config api-$api_temp_major-config"
      ;;
    esac
  done

  AC_MSG_CHECKING(for API)
  AC_ARG_WITH(api,
  [  --with-api=PATH         prefix for installed API or the full path to 
                             api-config],
  [
    if test "$withval" = "no" || test "$withval" = "yes"; then
      AC_MSG_ERROR([--with-api requires a directory or file to be provided])
    fi

    for api_temp_api_config_file in $api_temp_acceptable_api_config
    do
      for lookdir in "$withval/bin" "$withval"
      do
        if $TEST_X "$lookdir/$api_temp_api_config_file"; then
          api_config="$lookdir/$api_temp_api_config_file"
          ifelse([$5], [], [], [
          api_acceptable="yes"
          $5
          if test "$api_acceptable" != "yes"; then
            AC_MSG_WARN([Found API in $api_config, but we think it is considered unacceptable])
            continue
          fi])
          api_found="yes"
          break 2
        fi
      done
    done

    if test "$api_found" != "yes" && $TEST_X "$withval" && $withval --help > /dev/null 2>&1 ; then
      api_config="$withval"
      ifelse([$5], [], [api_found="yes"], [
          api_acceptable="yes"
          $5
          if test "$api_acceptable" = "yes"; then
                api_found="yes"
          fi])
    fi

    dnl if --with-api is used, it is a fatal error for its argument
    dnl to be invalid
    if test "$api_found" != "yes"; then
      AC_MSG_ERROR([the --with-api parameter is incorrect. It must specify an install prefix, a build directory, or an api-config file.])
    fi
  ],[
    dnl If we allow installed copies, check those before using bundled copy.
    if test -n "$3" && test "$3" = "1"; then
      for api_temp_api_config_file in $api_temp_acceptable_api_config
      do
        if $api_temp_api_config_file --help > /dev/null 2>&1 ; then
          api_config="$api_temp_api_config_file"
          ifelse([$5], [], [], [
          api_acceptable="yes"
          $5
          if test "$api_acceptable" != "yes"; then
            AC_MSG_WARN([skipped API at $api_config, version not acceptable])
            continue
          fi])
          api_found="yes"
          break
        else
          dnl look in some standard places
          for lookdir in /usr /usr/local /usr/local/api /opt/api; do
            if $TEST_X "$lookdir/bin/$api_temp_api_config_file"; then
              api_config="$lookdir/bin/$api_temp_api_config_file"
              ifelse([$5], [], [], [
              api_acceptable="yes"
              $5
              if test "$api_acceptable" != "yes"; then
                AC_MSG_WARN([skipped API at $api_config, version not acceptable])
                continue
              fi])
              api_found="yes"
              break 2
            fi
          done
        fi
      done
    fi
    dnl if we have not found anything yet and have bundled source, use that
    if test "$api_found" = "no" && test -d "$1"; then
      api_temp_abs_srcdir="`cd \"$1\" && pwd`"
      api_found="reconfig"
      api_bundled_major="`sed -n '/#define.*API_MAJOR_VERSION/s/^[^0-9]*\([0-9]*\).*$/\1/p' \"$1/include/api_version.h\"`"
      case $api_bundled_major in
        "")
          AC_MSG_ERROR([failed to find major version of bundled API])
        ;;
        0)
          api_temp_api_config_file="api-config"
        ;;
        *)
          api_temp_api_config_file="api-$api_bundled_major-config"
        ;;
      esac
      if test -n "$2"; then
        api_config="$2/$api_temp_api_config_file"
      else
        api_config="$1/$api_temp_api_config_file"
      fi
    fi
  ])

  AC_MSG_RESULT($api_found)
])
