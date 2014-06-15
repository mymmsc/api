#ifndef ATOMIC_H
#define ATOMIC_H

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

#if defined(WINCE) || defined(_WIN32_WCE)
#   define API_OS_WINCE
#elif defined(WIN64) || defined(_WIN64) || defined(__WIN64__)
#   define API_OS_WIN64
#elif defined(WIN32) || defined(_WIN32) || defined(__WIN32__) || defined(__NT__)
#   define API_OS_WIN32
#elif defined(__linux__) || defined(__linux)
#   define API_OS_LINUX
#else
#   error "This OS is unsupported"
#endif

#if defined(API_OS_WIN32) || defined(API_OS_WIN64) || defined(API_OS_WINCE)
#   define API_OS_WIN
#else
#   define API_OS_UNIX
#endif

#if defined(_MSC_VER)
#   if (_MSC_VER <= 1200)
#       define API_CC_MSVC6
#   endif
#   define API_CC_MSVC
#elif defined(__GNUC__)
#   define API_CC_GNUC
#else
#   error "This CC is unsupported"
#endif

#if defined(API_OS_WIN32) || defined(API_OS_WINCE)
    typedef LONG            atomic_int_t;
#elif defined(API_OS_WIN64)
    typedef LONG64          atomic_int_t;
#else
    typedef long            atomic_int_t;
#endif
typedef volatile atomic_int_t atomic_t;

//////////////////////////////////////////////////////////////////////////

#if defined(API_CC_GNUC)
#   define atomic_inc(var)           __sync_fetch_and_add        (&(var), 1)
#   define atomic_dec(var)           __sync_fetch_and_sub        (&(var), 1)
#   define atomic_add(var, val)      __sync_fetch_and_add        (&(var), (val))
#   define atomic_sub(var, val)      __sync_fetch_and_sub        (&(var), (val))
#   define atomic_set(var, val)      __sync_lock_test_and_set    (&(var), (val))
#   define atomic_cas(var, cmp, val) __sync_bool_compare_and_swap(&(var), (cmp), (val))
#elif defined(API_OS_WIN32) || defined(API_OS_WINCE)
#   define atomic_inc(var)           InterlockedExchangeAdd      (&(var), 1)
#   define atomic_dec(var)           InterlockedExchangeAdd      (&(var),-1)
#   define atomic_add(var, val)      InterlockedExchangeAdd      (&(var), (val))
#   define atomic_sub(var, val)      InterlockedExchangeAdd      (&(var),-(val))
#   define atomic_set(var, val)      InterlockedExchange         (&(var), (val))
#   define atomic_cas(var, cmp, val) ((cmp) == InterlockedCompareExchange(&(var), (val), (cmp)))
#elif defined(API_OS_WIN64)
#   define atomic_inc(var)           InterlockedExchangeAdd64    (&(var), 1)
#   define atomic_dec(var)           InterlockedExchangeAdd64    (&(var),-1)
#   define atomic_add(var, val)      InterlockedExchangeAdd64    (&(var), (val))
#   define atomic_sub(var, val)      InterlockedExchangeAdd64    (&(var),-(val))
#   define atomic_set(var, val)      InterlockedExchange64       (&(var), (val))
#   define atomic_cas(var, cmp, val) ((cmp) == InterlockedCompareExchange64(&(var), (val), (cmp)))
#else
#   error "This platform is unsupported"
#endif

#ifdef __cplusplus
	}
#endif

#endif /* ! ATOMIC_H */
