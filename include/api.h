#ifndef API_H
#define API_H

#include <api/platform.h>

#if defined __DragonFly__ && !defined __FreeBSD__
#define __FreeBSD__        4
#define __FreeBSD_version  480101
#endif

#if defined(WIN64) || defined(_WIN64)
#  define API_WIN64               1
#elif defined(WIN32) || defined(_WIN32)
#  define API_WIN32               1
#elif defined(_WIN32_WCE)
#  define API_WIN32_WCE           1
#elif defined(OS2)
#  define API_OS2                 1
#elif defined(__BEOS__)
#  define API_BEOS                1
#elif defined(NETWARE)
#  define API_NETWARE             1
#else
/* Any other OS should go above this one.  This is the lowest common
 * denominator typedefs for  all UNIX-like systems.  :)
 */
#  if defined(HPUX) || defined(HPUX10) || defined(HPUX11)
#    define API_HPUX              1
#  elif defined(DARWIN)
#    define API_APPLE             1
#  elif defined(__linux__) || defined(__linux) || defined(LINUX)
#    define API_LINUX             1
#  elif defined(FREEBSD)
#    define API_FREEBSD           1
#  elif defined(CYGWIN)
#    define API_CYGWIN            1
#  elif defined(SOLARIS2)
#    define API_SOLARIS2          1
#  elif defined(SUNOS4)
#    define API_SUNOS             1
#  else
#    define API_UNIX              1
#  endif
#endif

#if defined(API_WIN64) || defined(API_WIN32) || defined(API_WIN32_WCE)
#define API_WINDOWS 1
#endif

#if (API_FREEBSD)
#include <api/freebsd_config.h>
#elif (API_LINUX)
#include <api/linux_config.h>
#elif (API_SOLARIS2)
#include <api/solaris_config.h>
#elif (API_APPLE)
#include <api/darwin_config.h>
#elif (API_WIN32)
#include <api/win32_config.h>
#else /* POSIX */
#include <api/posix_config.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

typedef unsigned char byte_t;

/* Typedefs that API needs. */
#ifdef _WIN32
  typedef   signed char   int8_t;
  typedef unsigned char  uint8_t;
  typedef   signed short  int16_t;
  typedef unsigned short uint16_t;
  typedef   signed int    int32_t;
  typedef unsigned int   uint32_t;
  #if __GNUC__
    typedef   signed long long int64_t;
    typedef unsigned long long uint64_t;
  #else /* _MSC_VER || __BORLANDC__ */
    typedef   signed __int64   int64_t;
    typedef unsigned __int64   uint64_t;
  #endif
  #ifdef _WIN64
    #define API_PTRSIZE 8
    typedef uint64_t uintptr_t;
    typedef  int64_t  intptr_t;
  #else
    #define API_PTRSIZE 4
    typedef uint32_t uintptr_t;
    typedef  int32_t  intptr_t;
  #endif
#else
  #include <inttypes.h>
  #if UINTMAX_MAX > 0xffffffffU
    #define API_PTRSIZE 8
  #else
    #define API_PTRSIZE 4
  #endif
#endif

/* Definitions that API programs need to work properly. */

/**
 * API public API wrap for C++ compilers.
 */
#ifdef __cplusplus
#define API_BEGIN_DECLS     extern "C" {
#define API_END_DECLS       }
#else
#define API_BEGIN_DECLS
#define API_END_DECLS
#endif

#if defined(DOXYGEN) || !defined(WIN32)

/**
 * The public API functions are declared with API_DECLARE(), so they may
 * use the most appropriate calling convention.  Public API functions with 
 * variable arguments must use API_DECLARE_NONSTD().
 *
 * @remark Both the declaration and implementations must use the same macro.
 *
 * <PRE>
 * API_DECLARE(rettype) api_func(args)
 * </PRE>
 * @see API_DECLARE_NONSTD @see API_DECLARE_DATA
 * @remark Note that when API compiles the library itself, it passes the 
 * symbol -DAPI_DECLARE_EXPORT to the compiler on some platforms (e.g. Win32) 
 * to export public symbols from the dynamic library build.\n
 * The user must define the API_DECLARE_STATIC when compiling to target
 * the static API library on some platforms (e.g. Win32.)  The public symbols 
 * are neither exported nor imported when API_DECLARE_STATIC is defined.\n
 * By default, compiling an application and including the API public
 * headers, without defining API_DECLARE_STATIC, will prepare the code to be
 * linked to the dynamic library.
 */
#define API_DECLARE(type)            type 

/**
 * The public API functions using variable arguments are declared with 
 * API_DECLARE_NONSTD(), as they must follow the C language calling convention.
 * @see API_DECLARE @see API_DECLARE_DATA
 * @remark Both the declaration and implementations must use the same macro.
 * <PRE>
 *
 * API_DECLARE_NONSTD(rettype) api_func(args, ...);
 *
 * </PRE>
 */
#define API_DECLARE_NONSTD(type)     type

/**
 * The public API variables are declared with API_DECLARE_DATA.
 * This assures the appropriate indirection is invoked at compile time.
 * @see API_DECLARE @see API_DECLARE_NONSTD
 * @remark Note that the declaration and implementations use different forms,
 * but both must include the macro.
 * 
 * <PRE>
 *
 * extern API_DECLARE_DATA type api_variable;\n
 * API_DECLARE_DATA type api_variable = value;
 *
 * </PRE>
 */
#define API_DECLARE_DATA

#elif defined(API_DECLARE_STATIC)
#define API_DECLARE(type)            type __stdcall
#define API_DECLARE_NONSTD(type)     type __cdecl
#define API_DECLARE_DATA
#elif defined(API_DECLARE_EXPORT)
#define API_DECLARE(type)            __declspec(dllexport) type __stdcall
#define API_DECLARE_NONSTD(type)     __declspec(dllexport) type __cdecl
#define API_DECLARE_DATA             __declspec(dllexport)
#else
#define API_DECLARE(type)            __declspec(dllimport) type __stdcall
#define API_DECLARE_NONSTD(type)     __declspec(dllimport) type __cdecl
#define API_DECLARE_DATA             __declspec(dllimport)
#endif

#if defined _WIN32 || defined __CYGWIN__
  #ifdef API_DECLARE_STATIC
    #define API
  #else
    #ifdef API_DECLARE_EXPORT
      #ifdef __GNUC__
        #define API __attribute__((dllexport))
      #else
        #define API __declspec(dllexport)
      #endif
    #else
      #ifdef __GNUC__
        #define API __attribute__((dllimport))
      #else
        #define API __declspec(dllimport)
      #endif
    #endif
  #endif
#else
  #if __GNUC__ >= 4 && !defined(__OS2__)
    #define API __attribute__((visibility("default")))
  #else
    #define API
  #endif
#endif


#if !defined(WIN32) || defined(API_MODULE_DECLARE_STATIC)
/**
 * Declare a dso module's exported module structure as API_MODULE_DECLARE_DATA.
 *
 * Unless API_MODULE_DECLARE_STATIC is defined at compile time, symbols 
 * declared with API_MODULE_DECLARE_DATA are always exported.
 * @code
 * module API_MODULE_DECLARE_DATA mod_tag
 * @endcode
 */
#define API_MODULE_DECLARE_DATA
#else
#define API_MODULE_DECLARE_DATA           __declspec(dllexport)
#endif

#if API_HAVE_SYS_WAIT_H
#ifdef WEXITSTATUS
#define api_wait_t       int
#else
#define api_wait_t       union wait
#define WEXITSTATUS(status)    (int)((status).w_retcode)
#define WTERMSIG(status)       (int)((status).w_termsig)
#endif /* !WEXITSTATUS */
#elif defined(__MINGW32__)
typedef int api_wait_t;
#endif /* HAVE_SYS_WAIT_H */

#if defined(PATH_MAX)
#define API_PATH_MAX       PATH_MAX
#elif defined(_POSIX_PATH_MAX)
#define API_PATH_MAX       _POSIX_PATH_MAX
#else
#error no decision has been made on API_PATH_MAX for your platform
#endif

/* Definitions that only Win32 programs need to compile properly. */

/* XXX These simply don't belong here, perhaps in api_portable.h
 * based on some API_HAVE_PID/GID/UID?
 */
#ifdef WIN32
#ifndef __GNUC__
typedef  int         pid_t;
#endif
typedef  int         uid_t;
typedef  int         gid_t;
#endif

typedef int              api_bool_t;

#define API_FALSE        (0)
#define API_TRUE         (!API_FALSE)

/** function return code */
typedef int status_t;
/** 尺寸单位KB */
#define API_SIZE_FROM_KB(n)    ((n) * 0x400)
/** 尺寸单位MB */
#define API_SIZE_FROM_MB(n)    ((n) * 0x100000)

#define API_BUFFER_MAX 8192

/* ------------------- DEFINITIONS -------------------------- */
/** 64位网络字节需转换 */
#ifndef HTONLL_DEFINED
#define HTONLL_DEFINED
#define htonll(num) ((api_uint64_t)(htonl((api_uint32_t)(num))) << 32) + htonl((api_uint32_t)((num) >> 32))
#define ntohll(num) ((api_uint64_t)(ntohl((api_uint32_t)(num))) << 32) + ntohl((api_uint32_t)((num) >> 32))
#endif

typedef intptr_t        api_int_t;
typedef uintptr_t       api_uint_t;
typedef intptr_t        api_flag_t;

#if API_HAVE_MEMWATCH
#define MEMWATCH
#include <api/memwatch.h>
#endif

#ifdef __cplusplus
}
#endif

#endif /* ! API_H */
