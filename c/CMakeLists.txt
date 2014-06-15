# Read README.cmake before using this.

PROJECT(API C)

CMAKE_MINIMUM_REQUIRED(VERSION 2.8)

# Either Expat or LibXml2 is required; the others are optional
FIND_PACKAGE(Expat)
FIND_PACKAGE(LibXml2)
FIND_PACKAGE(OpenSSL)

IF(NOT EXPAT_FOUND AND NOT LIBXML2_FOUND)
  MESSAGE(FATAL_ERROR "Either Expat or LibXml2 is required, but neither was found")
ENDIF()

IF(EXPAT_FOUND)
  OPTION(APU_USE_EXPAT      "Use Expat"                    ON)
  IF(LIBXML2_FOUND)
    OPTION(APU_USE_LIBXML2  "Use LibXml2"                  OFF)
  ENDIF()
ELSE()
  OPTION(APU_USE_LIBXML2    "Use LibXml2"                  ON)
ENDIF()

OPTION(API_INSTALL_PRIVATE_H  "Install selected private .h files (for httpd)"  OFF)
OPTION(APU_HAVE_CRYPTO      "Crypto support"               OFF)
OPTION(APU_HAVE_ODBC        "Build ODBC DBD driver"        ON)
OPTION(API_HAVE_IPV6        "IPv6 support"                 ON)
OPTION(INSTALL_PDB          "Install .pdb files (if generated)"  ON)
OPTION(API_BUILD_TESTAPI    "Build the test suite"         OFF)
OPTION(TEST_STATIC_LIBS     "Test programs use API static libraries instead of shared libraries?" OFF)
SET(MIN_WINDOWS_VER             "Vista" 
    CACHE STRING "Minimum Windows version")
SET(LIBXML2_ICONV_INCLUDE_DIR   ""
    CACHE STRING "Directory with iconv include files for libxml2")
SET(LIBXML2_ICONV_LIBRARIES     ""
    CACHE STRING "iconv libraries to link with for libxml2")

IF(NOT APU_USE_EXPAT AND NOT APU_USE_LIBXML2)
  MESSAGE(FATAL_ERROR "Either Expat or LibXml2 must be selected")
ENDIF()
IF(APU_USE_EXPAT AND APU_USE_LIBXML2)
  MESSAGE(FATAL_ERROR "Only one of Expat and LibXml2 can be selected")
ENDIF()

IF(APU_HAVE_CRYPTO)
IF(NOT OPENSSL_FOUND)
  MESSAGE(FATAL_ERROR "OpenSSL is the only supported crypto implementation, and it wasn't found!")
ENDIF()
ENDIF()

# create 1-or-0 representation of feature tests for api.h

SET(api_have_ipv6_10 0)
SET(apu_have_crypto_10 0)
SET(apu_use_libxml2_10 0)
SET(apu_use_expat_10 0)

IF(API_HAVE_IPV6)
  SET(api_have_ipv6_10 1)
ENDIF()

IF(APU_HAVE_CRYPTO)
  SET(apu_have_crypto_10 1)
ENDIF()

IF(APU_USE_EXPAT)
  SET(apu_use_expat_10 1)
ELSE()
  SET(apu_use_libxml2_10 1)
ENDIF()

IF("${MIN_WINDOWS_VER}" STREQUAL "")
  SET(win32_winnt_str "0x0600")
ELSEIF(${MIN_WINDOWS_VER} STREQUAL "Vista")
  SET(win32_winnt_str "0x0600")
ELSEIF(${MIN_WINDOWS_VER} STREQUAL "Windows7")
  SET(win32_winnt_str "0x0601")
ELSE()
  SET(win32_winnt_str ${MIN_WINDOWS_VER})
ENDIF()

CONFIGURE_FILE(include/api.hwc
               ${PROJECT_BINARY_DIR}/api.h)
# "COPYONLY" just because anything else isn't implemented ;)
CONFIGURE_FILE(include/private/apu_select_dbm.hw
               ${PROJECT_BINARY_DIR}/apu_select_dbm.h
               COPYONLY)
CONFIGURE_FILE(include/apu_want.hw
               ${PROJECT_BINARY_DIR}/apu_want.h
               COPYONLY)

ADD_EXECUTABLE(gen_test_char tools/gen_test_char.c)
GET_TARGET_PROPERTY(GEN_TEST_CHAR_EXE gen_test_char LOCATION)
ADD_CUSTOM_COMMAND(
  COMMENT "Generating character tables, api_escape_test_char.h, for current locale"
  DEPENDS gen_test_char
  COMMAND ${GEN_TEST_CHAR_EXE} > ${PROJECT_BINARY_DIR}/api_escape_test_char.h
  OUTPUT ${PROJECT_BINARY_DIR}/api_escape_test_char.h
)
ADD_CUSTOM_TARGET(
  test_char_header ALL
  DEPENDS ${PROJECT_BINARY_DIR}/api_escape_test_char.h
)

IF(APU_USE_EXPAT)
  SET(XMLLIB_INCLUDE_DIR ${EXPAT_INCLUDE_DIRS})
  SET(XMLLIB_LIBRARIES   ${EXPAT_LIBRARIES})
ELSE()
  SET(XMLLIB_INCLUDE_DIR "${LIBXML2_INCLUDE_DIR};${LIBXML2_ICONV_INCLUDE_DIR}")
  SET(XMLLIB_LIBRARIES   "${LIBXML2_LIBRARIES};${LIBXML2_ICONV_LIBRARIES}")
ENDIF()

# Generated .h files are stored in PROJECT_BINARY_DIR, not the
# source tree.
#
# BROKEN: not searching PROJECT_BINARY_DIR first, so you have to
#         manually delete api.h in PROJECT_SOURCE_DIR/include if
#         you've generated api.h before using a different build

SET(API_INCLUDE_DIRECTORIES
  ${PROJECT_BINARY_DIR}
  ${CMAKE_CURRENT_SOURCE_DIR}/include
  ${CMAKE_CURRENT_SOURCE_DIR}/include/arch/win32
  ${CMAKE_CURRENT_SOURCE_DIR}/include/arch/unix
  ${CMAKE_CURRENT_SOURCE_DIR}/include/private
)

SET(API_SYSTEM_LIBS
  ws2_32
  mswsock
  rpcrt4
)

INCLUDE_DIRECTORIES(${API_INCLUDE_DIRECTORIES} ${XMLLIB_INCLUDE_DIR})

SET(API_PUBLIC_HEADERS_STATIC
  include/api_allocator.h
  include/api_anylock.h
  include/api_atomic.h
  include/api_base64.h
  include/api_buckets.h
  include/api_crypto.h
  include/api_date.h
  include/api_dbd.h
  include/api_dbm.h
  include/api_dso.h
  include/api_env.h
  include/api_errno.h
  include/api_escape.h
  include/api_file_info.h
  include/api_file_io.h
  include/api_fnmatch.h
  include/api_general.h
  include/api_getopt.h
  include/api_global_mutex.h
  include/api_hash.h
  include/api_hooks.h
  include/api_inherit.h
  include/api_lib.h
  include/api_md4.h
  include/api_md5.h
  include/api_memcache.h
  include/api_mmap.h
  include/api_network_io.h
  include/api_optional.h
  include/api_optional_hooks.h
  include/api_perms_set.h
  include/api_poll.h
  include/api_pools.h
  include/api_portable.h
  include/api_proc_mutex.h
  include/api_queue.h
  include/api_random.h
  include/api_reslist.h
  include/api_ring.h
  include/api_rmm.h
  include/api_sdbm.h
  include/api_sha1.h
  include/api_shm.h
  include/api_signal.h
  include/api_skiplist.h
  include/api_strings.h
  include/api_strmatch.h
  include/api_tables.h
  include/api_thread_cond.h
  include/api_thread_mutex.h
  include/api_thread_pool.h
  include/api_thread_proc.h
  include/api_thread_rwlock.h
  include/api_time.h
  include/api_uri.h
  include/api_user.h
  include/api_uuid.h
  include/api_version.h
  include/api_want.h
  include/api_xlate.h
  include/api_xml.h
  include/apu.h
  include/apu_errno.h
  include/apu_version.h
)
SET(API_PUBLIC_HEADERS_GENERATED
  ${PROJECT_BINARY_DIR}/api.h
  ${PROJECT_BINARY_DIR}/apu_want.h
)

SET(API_SOURCES
  atomic/win32/api_atomic.c
  buckets/api_brigade.c
  buckets/api_buckets.c
  buckets/api_buckets_alloc.c
  buckets/api_buckets_eos.c
  buckets/api_buckets_file.c
  buckets/api_buckets_flush.c
  buckets/api_buckets_heap.c
  buckets/api_buckets_mmap.c
  buckets/api_buckets_pipe.c
  buckets/api_buckets_pool.c
  buckets/api_buckets_refcount.c
  buckets/api_buckets_simple.c
  buckets/api_buckets_socket.c
  crypto/api_crypto.c
  crypto/api_md4.c
  crypto/api_md5.c
  crypto/api_passwd.c
  crypto/api_sha1.c
  crypto/crypt_blowfish.c
  crypto/getuuid.c
  crypto/uuid.c
  dbd/api_dbd.c
  dbm/api_dbm.c
  dbm/api_dbm_sdbm.c
  dbm/sdbm/sdbm.c
  dbm/sdbm/sdbm_hash.c
  dbm/sdbm/sdbm_lock.c
  dbm/sdbm/sdbm_pair.c
  dso/win32/dso.c
  encoding/api_base64.c
  encoding/api_escape.c
  file_io/unix/copy.c
  file_io/unix/fileacc.c
  file_io/unix/filepath_util.c
  file_io/unix/fullrw.c
  file_io/unix/mktemp.c
  file_io/unix/tempdir.c
  file_io/win32/buffer.c
  file_io/win32/dir.c
  file_io/win32/filedup.c
  file_io/win32/filepath.c
  file_io/win32/filestat.c
  file_io/win32/filesys.c
  file_io/win32/flock.c
  file_io/win32/open.c
  file_io/win32/pipe.c
  file_io/win32/readwrite.c
  file_io/win32/seek.c
  hooks/api_hooks.c
  locks/win32/proc_mutex.c
  locks/win32/thread_cond.c
  locks/win32/thread_mutex.c
  locks/win32/thread_rwlock.c
  memcache/api_memcache.c
  memory/unix/api_pools.c
  misc/unix/errorcodes.c
  misc/unix/getopt.c
  misc/unix/otherchild.c
  misc/unix/version.c
  misc/win32/charset.c
  misc/win32/env.c
  misc/win32/internal.c
  misc/win32/misc.c
  misc/win32/rand.c
  misc/win32/start.c
  misc/win32/utf8.c
  mmap/unix/common.c
  mmap/win32/mmap.c
  network_io/unix/inet_ntop.c
  network_io/unix/inet_pton.c
  network_io/unix/multicast.c
  network_io/unix/sockaddr.c
  network_io/unix/socket_util.c
  network_io/win32/sendrecv.c
  network_io/win32/sockets.c
  network_io/win32/sockopt.c
  passwd/api_getpass.c
  poll/unix/poll.c
  poll/unix/pollcb.c
  poll/unix/pollset.c
  poll/unix/select.c
  poll/unix/wakeup.c
  random/unix/api_random.c
  random/unix/sha2.c
  random/unix/sha2_glue.c
  shmem/win32/shm.c
  strings/api_cpystrn.c
  strings/api_fnmatch.c
  strings/api_snprintf.c
  strings/api_strings.c
  strings/api_strnatcmp.c
  strings/api_strtok.c
  strmatch/api_strmatch.c
  tables/api_hash.c
  tables/api_skiplist.c
  tables/api_tables.c
  threadproc/win32/proc.c
  threadproc/win32/signals.c
  threadproc/win32/thread.c
  threadproc/win32/threadpriv.c
  time/win32/time.c
  time/win32/timestr.c
  uri/api_uri.c
  user/win32/groupinfo.c
  user/win32/userinfo.c
  util-misc/api_date.c
  util-misc/api_queue.c
  util-misc/api_reslist.c
  util-misc/api_rmm.c
  util-misc/api_thread_pool.c
  util-misc/apu_dso.c
  xlate/xlate.c
  xml/api_xml.c
  xml/api_xml_expat.c
  xml/api_xml_libxml2.c
)

SET(API_TEST_SOURCES
  test/abts.c
  test/testargs.c
  test/testatomic.c
  test/testbase64.c
  test/testbuckets.c
  test/testcond.c
  test/testcrypto.c
  test/testdate.c
  test/testdbd.c
  test/testdbm.c
  test/testdir.c
  test/testdso.c
  test/testdup.c
  test/testenv.c
  test/testescape.c
  test/testfile.c
  test/testfilecopy.c
  test/testfileinfo.c
  test/testflock.c
  test/testfmt.c
  test/testfnmatch.c
  test/testglobalmutex.c
  test/testhash.c
  test/testhooks.c
  test/testipsub.c
  test/testlfs.c
  test/testlfsabi.c
  test/testlfsabi32.c
  test/testlfsabi64.c
  test/testlfsabi_include.c
  test/testlock.c
  test/testmd4.c
  test/testmd5.c
  test/testmemcache.c
  test/testmmap.c
  test/testnames.c
  test/testoc.c
  test/testpass.c
  test/testpath.c
  test/testpipe.c
  test/testpoll.c
  test/testpools.c
  test/testproc.c
  test/testprocmutex.c
  test/testqueue.c
  test/testrand.c
  test/testreslist.c
  test/testrmm.c
  test/testshm.c
  test/testsleep.c
  test/testsock.c
  test/testsockets.c
  test/testsockopt.c
  test/teststr.c
  test/teststrmatch.c
  test/teststrnatcmp.c
  test/testtable.c
  test/testtemp.c
  test/testthread.c
  test/testtime.c
  test/testud.c
  test/testuri.c
  test/testuser.c
  test/testutil.c
  test/testuuid.c
  test/testvsn.c
  test/testxlate.c
  test/testxml.c
)

SET(install_targets)
SET(install_bin_pdb)
SET(install_lib_pdb)
SET(dbd_drivers)

# libapi-2 is shared, api-2 is static
ADD_LIBRARY(libapi-2 SHARED ${API_SOURCES} ${API_PUBLIC_HEADERS_GENERATED} libapi.rc)
SET(install_targets ${install_targets} libapi-2)
SET(install_bin_pdb ${install_bin_pdb} ${PROJECT_BINARY_DIR}/libapi-2.pdb)
TARGET_LINK_LIBRARIES(libapi-2 ${XMLLIB_LIBRARIES} ${API_SYSTEM_LIBS})
SET_TARGET_PROPERTIES(libapi-2 PROPERTIES COMPILE_DEFINITIONS "API_DECLARE_EXPORT;API_HAVE_MODULAR_DSO")
ADD_DEPENDENCIES(libapi-2 test_char_header)

ADD_LIBRARY(api-2 STATIC ${API_SOURCES} ${API_PUBLIC_HEADERS_GENERATED})
SET(install_targets ${install_targets} api-2)
SET(install_lib_pdb ${install_lib_pdb} ${PROJECT_BINARY_DIR}/api-2.pdb)
TARGET_LINK_LIBRARIES(api-2 ${XMLLIB_LIBRARIES} ${API_SYSTEM_LIBS})
SET_TARGET_PROPERTIES(api-2 PROPERTIES COMPILE_DEFINITIONS "API_DECLARE_STATIC;API_HAVE_MODULAR_DSO")
ADD_DEPENDENCIES(api-2 test_char_header)

# libapiapp-2 and apiapp-2 are static
ADD_LIBRARY(libapiapp-2 STATIC misc/win32/api_app.c misc/win32/internal.c ${API_PUBLIC_HEADERS_GENERATED})
SET(install_targets ${install_targets} libapiapp-2)
SET(install_lib_pdb ${install_lib_pdb} ${PROJECT_BINARY_DIR}/libapiapp-2.pdb)
SET_TARGET_PROPERTIES(libapiapp-2 PROPERTIES COMPILE_DEFINITIONS API_APP)

ADD_LIBRARY(apiapp-2 STATIC misc/win32/api_app.c misc/win32/internal.c ${API_PUBLIC_HEADERS_GENERATED})
SET(install_targets ${install_targets} apiapp-2)
SET(install_lib_pdb ${install_lib_pdb} ${PROJECT_BINARY_DIR}/apiapp-2.pdb)
SET_TARGET_PROPERTIES(apiapp-2 PROPERTIES COMPILE_DEFINITIONS "API_DECLARE_STATIC;API_APP")

IF(APU_HAVE_CRYPTO)
  IF(NOT OPENSSL_FOUND)
    MESSAGE(FATAL_ERROR "Only OpenSSL-based crypto is currently implemented in the cmake build")
  ENDIF()
  ADD_LIBRARY(api_crypto_openssl-2 SHARED crypto/api_crypto_openssl.c libapi.rc)
  SET(install_targets ${install_targets} api_crypto_openssl-2)
  SET(install_bin_pdb ${install_bin_pdb} ${PROJECT_BINARY_DIR}/api_crypto_openssl-2.pdb)
  SET_TARGET_PROPERTIES(api_crypto_openssl-2 PROPERTIES INCLUDE_DIRECTORIES "${API_INCLUDE_DIRECTORIES};${OPENSSL_INCLUDE_DIR}")
  SET_TARGET_PROPERTIES(api_crypto_openssl-2 PROPERTIES COMPILE_FLAGS "-DDLL_NAME=api_crypto_openssl")
  TARGET_LINK_LIBRARIES(api_crypto_openssl-2 libapi-2 ${API_SYSTEM_LIBS} ${OPENSSL_LIBRARIES})
ENDIF()

IF(APU_HAVE_ODBC)
  ADD_LIBRARY(api_dbd_odbc-2 SHARED dbd/api_dbd_odbc.c libapi.rc)
  SET(install_targets ${install_targets} api_dbd_odbc-2)
  SET(install_bin_pdb ${install_bin_pdb} ${PROJECT_BINARY_DIR}/api_dbd_odbc-2.pdb)
  SET(dbd_drivers ${dbd_drivers} odbc)
  TARGET_LINK_LIBRARIES(api_dbd_odbc-2 libapi-2 ${API_SYSTEM_LIBS} odbc32 odbccp32)
  SET_PROPERTY(TARGET api_dbd_odbc-2 APPEND PROPERTY LINK_FLAGS /export:api_dbd_odbc_driver)
  SET_TARGET_PROPERTIES(api_dbd_odbc-2 PROPERTIES COMPILE_DEFINITIONS "APU_HAVE_ODBC;HAVE_SQL_H;API_DECLARE_EXPORT;APU_DSO_MODULE_BUILD")
  SET_TARGET_PROPERTIES(api_dbd_odbc-2 PROPERTIES COMPILE_FLAGS "-DDLL_NAME=api_dbd_odbc")
ENDIF()

IF(API_BUILD_TESTAPI)
  ENABLE_TESTING()
  # Create a "check" target that displays test program output to the console.
  ADD_CUSTOM_TARGET(check COMMAND ${CMAKE_CTEST_COMMAND} --verbose)

  # copy data files to build directory so that we can run programs from there
  EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND} -E make_directory 
                  ${PROJECT_BINARY_DIR}/data)
  EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND} -E copy_if_different 
                  ${PROJECT_SOURCE_DIR}/test/data/billion-laughs.xml
                  ${PROJECT_BINARY_DIR}/data/billion-laughs.xml)
  EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND} -E copy_if_different 
                  ${PROJECT_SOURCE_DIR}/test/data/file_datafile.txt
                  ${PROJECT_BINARY_DIR}/data/file_datafile.txt)
  EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND} -E copy_if_different
                  ${PROJECT_SOURCE_DIR}/test/data/mmap_datafile.txt
                  ${PROJECT_BINARY_DIR}/data/mmap_datafile.txt)

  IF(TEST_STATIC_LIBS)
    SET(whichapi    api-2)
    SET(whichapiapp apiapp-2)
    SET(apiflag     -DAPI_DECLARE_STATIC)
  ELSE()
    SET(whichapi    libapi-2)
    SET(whichapiapp libapiapp-2)
    SET(apiflag)
  ENDIF()

  ADD_EXECUTABLE(testapp test/testapp.c)
  TARGET_LINK_LIBRARIES(testapp ${whichapi} ${whichapiapp} ${XMLLIB_LIBRARIES} ${API_SYSTEM_LIBS})
  SET_TARGET_PROPERTIES(testapp PROPERTIES LINK_FLAGS /entry:wmainCRTStartup)
  IF(apiflag)
    SET_TARGET_PROPERTIES(testapp PROPERTIES COMPILE_FLAGS ${apiflag})
  ENDIF()
  ADD_TEST(NAME testapp COMMAND testapp)

  ADD_EXECUTABLE(testall ${API_TEST_SOURCES})
  TARGET_LINK_LIBRARIES(testall ${whichapi} ${XMLLIB_LIBRARIES} ${API_SYSTEM_LIBS})
  IF(apiflag)
    SET_TARGET_PROPERTIES(testall PROPERTIES COMPILE_FLAGS ${apiflag})
  ENDIF()
  ADD_TEST(NAME testall COMMAND testall)

  ADD_LIBRARY(mod_test MODULE test/mod_test.c)
  TARGET_LINK_LIBRARIES(mod_test ${whichapi} ${API_SYSTEM_LIBS})
  SET_PROPERTY(TARGET mod_test APPEND PROPERTY LINK_FLAGS /export:print_hello)
  # nasty work-around for difficulties adding more than one additional flag
  # (they get joined in a bad way behind the scenes)
  GET_PROPERTY(link_flags TARGET mod_test PROPERTY LINK_FLAGS)
  SET(link_flags "${link_flags} /export:count_reps")
  SET_TARGET_PROPERTIES(mod_test PROPERTIES LINK_FLAGS ${link_flags})
  IF(apiflag)
    SET_TARGET_PROPERTIES(mod_test PROPERTIES COMPILE_FLAGS ${apiflag})
  ENDIF()

  # Build all the single-source executable files with no special build
  # requirements.
  SET(single_source_programs
    test/dbd.c
    test/echod.c
    test/sendfile.c
    test/sockperf.c
    test/testlockperf.c
    test/testmutexscope.c
    test/globalmutexchild.c
    test/occhild.c
    test/proc_child.c
    test/readchild.c
    test/sockchild.c
    test/testshmproducer.c
    test/testshmconsumer.c
    test/tryread.c
    test/internal/testucs.c
  )

  FOREACH(sourcefile ${single_source_programs})
    STRING(REGEX REPLACE ".*/([^\\]+)\\.c" "\\1" proggie ${sourcefile})
    ADD_EXECUTABLE(${proggie} ${sourcefile})
    TARGET_LINK_LIBRARIES(${proggie} ${whichapi} ${XMLLIB_LIBRARIES} ${API_SYSTEM_LIBS})
    IF(apiflag)
      SET_TARGET_PROPERTIES(${proggie} PROPERTIES COMPILE_FLAGS ${apiflag})
    ENDIF()
  ENDFOREACH()

  # Add tests for programs that run by themselves with no arguments.
  SET(simple_tests
    testmutexscope
    testucs
  )

  FOREACH(simple ${simple_tests})
    ADD_TEST(NAME ${simple} COMMAND ${simple})
  ENDFOREACH()

  # testlockperf takes forever on Windows with default counter limit
  ADD_TEST(NAME testlockperf COMMAND testlockperf -c 50000)

  # dbd and sendfile are run multiple times with different parameters.
  FOREACH(somedbd ${dbd_drivers})
    ADD_TEST(NAME dbd-${somedbd} COMMAND dbd ${somedbd})
  ENDFOREACH()

  FOREACH(sendfile_mode blocking nonblocking timeout)
    ADD_TEST(NAME sendfile-${sendfile_mode} COMMAND sendfile client ${sendfile_mode} startserver)
  ENDFOREACH()

  # No test is added for echod+sockperf.  Those will have to be run manually.

ENDIF (API_BUILD_TESTAPI)

# Installation

INSTALL(TARGETS ${install_targets}
        RUNTIME DESTINATION bin
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
       )

IF(INSTALL_PDB)
  INSTALL(FILES ${install_bin_pdb}
          DESTINATION bin
          CONFIGURATIONS RelWithDebInfo Debug)

  INSTALL(FILES ${install_lib_pdb}
          DESTINATION lib
          CONFIGURATIONS RelWithDebInfo Debug)
ENDIF()

INSTALL(FILES ${API_PUBLIC_HEADERS_STATIC} ${API_PUBLIC_HEADERS_GENERATED} DESTINATION include)
IF(API_INSTALL_PRIVATE_H)
  # Kludges for unexpected dependencies of httpd 2.x, not installed by default
  SET(API_PRIVATE_H_FOR_HTTPD
    include/arch/win32/api_arch_file_io.h
    include/arch/win32/api_arch_misc.h
    include/arch/win32/api_arch_utf8.h
    include/arch/win32/api_private.h
    )
  INSTALL(FILES ${API_PRIVATE_H_FOR_HTTPD} DESTINATION include/arch/win32)
ENDIF()

STRING(TOUPPER "${CMAKE_BUILD_TYPE}" buildtype)
MESSAGE(STATUS "")
MESSAGE(STATUS "")
MESSAGE(STATUS "API configuration summary:")
MESSAGE(STATUS "")

MESSAGE(STATUS "  Build type ...................... : ${CMAKE_BUILD_TYPE}")
MESSAGE(STATUS "  Install .pdb (if available)...... : ${INSTALL_PDB}")
MESSAGE(STATUS "  Install prefix .................. : ${CMAKE_INSTALL_PREFIX}")
MESSAGE(STATUS "  C compiler ...................... : ${CMAKE_C_COMPILER}")
MESSAGE(STATUS "  IPv6 ............................ : ${API_HAVE_IPV6}")
MESSAGE(STATUS "  DBD ODBC driver ................. : ${APU_HAVE_ODBC}")
MESSAGE(STATUS "  Use Expat ....................... : ${APU_USE_EXPAT}")
MESSAGE(STATUS "  Use LibXml2 ..................... : ${APU_USE_LIBXML2}")
MESSAGE(STATUS "  Minimum Windows version ......... : ${MIN_WINDOWS_VER}")
MESSAGE(STATUS "  Library files for XML ........... : ${XMLLIB_LIBRARIES}")
MESSAGE(STATUS "  Build test suite ................ : ${API_BUILD_TESTAPI}")
IF(TEST_STATIC_LIBS)
MESSAGE(STATUS "    (testing static libraries)")
ELSE()
MESSAGE(STATUS "    (testing dynamic libraries)")
ENDIF()
MESSAGE(STATUS "  Install private .h for httpd .... : ${API_INSTALL_PRIVATE_H}")
