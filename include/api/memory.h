#ifndef __API_MEMORY_H_INCLUDED__
#define __API_MEMORY_H_INCLUDED__

#include "api.h"
#include "api/lib.h"

//------------------------------< memory align >------------------------------
#if 0

#ifndef API_BSD
#  ifndef API_WIN32 // Linux
#    include <malloc.h>		// for memalign
#    define allocAlignedMem(bytes)     memalign(16, bytes)
#    define freeAlignedMem(buff)       free(buff)
#  else // Windows
#    include <malloc.h>		// for memalign
#    define memalign(blocksize, bytes) malloc(bytes)
#    define allocAlignedMem(bytes)     _aligned_malloc(bytes, 16)
#    define freeAlignedMem(buff)       _aligned_free(buff)
#  endif
#else // Mac OS X
#  include <stdlib.h>		// for malloc
#  define allocAlignedMem(bytes)       malloc(bytes)
#  define freeAlignedMem(buff)         free(buff)
#endif

#endif

#define api_memmove(a,b,c)     memmove((a),(b),(c))
#define api_malloc(a)  malloc(a)
#define api_calloc(a,b)  calloc((a),(b))
#define api_realloc(a,b) realloc((a),(b))
#define api_free(p)    free(p)

#define api_safefree(x) do { if ((x) != NULL) {api_free(x); (x)=NULL;} } while(0)

//rounded division & shift
#define RSHIFT(a,b) ((a) > 0 ? ((a) + ((1<<(b))>>1))>>(b) : ((a) + ((1<<(b))>>1)-1)>>(b))
/* assume b>0 */
#define ROUNDED_DIV(a,b) (((a)>0 ? (a) + ((b)>>1) : (a) - ((b)>>1))/(b))

#define API_UDIV(a,b) (((a)>0 ?(a):(a)-(b)+1) / (b))
#define API_UMOD(a,b) ((a)-(b)*API_UDIV(a,b))
#define API_ABS(a) ((a) >= 0 ? (a) : (-(a)))
#define API_SIGN(a) ((a) > 0 ? 1 : -1)

#define API_MAX(a,b) ((a) > (b) ? (a) : (b))
#define API_MAX3(a,b,c) API_MAX(API_MAX(a,b),c)
#define API_MIN(a,b) ((a) > (b) ? (b) : (a))
#define API_MIN3(a,b,c) API_MIN(API_MIN(a,b),c)

#define API_SWAP(type,a,b) do{type SWAP_tmp= b; b= a; a= SWAP_tmp;}while(0)
#define API_ARRAY_ELEMS(a) (sizeof(a) / sizeof((a)[0]))
#define API_ALIGN(x, a) (((x)+(a)-1)&~((a)-1))

#ifndef roundup
# define roundup(x, y)   ((((x)+((y)-1))/(y))*(y))
#endif

/*
 * msvc and icc7 compile memset() to the inline "rep stos"
 * while ZeroMemory() and bzero() are the calls.
 * icc7 may also inline several mov's of a zeroed register for small blocks.
 */
#define api_memzero(buf, n)       (void) memset(buf, 0, n)
#define api_memset(buf, c, n)     (void) memset(buf, c, n)

#ifndef API_ALIGNMENT
#define API_ALIGNMENT   sizeof(unsigned long)    /* platform word */
#endif

#define api_align(d, a)     (((d) + (a - 1)) & ~(a - 1))
#define api_align_ptr(p, a) (uint8_t *)(((uintptr_t)(p) + ((uintptr_t)a - 1)) & ~((uintptr_t)a - 1))

/*
 * API_MAX_ALLOC_FROM_POOL should be (api_pagesize - 1), i.e. 4095 on x86.
 * On Windows NT it decreases a number of locked pages in a kernel.
 */
#define API_MAX_ALLOC_FROM_POOL  (api_pagesize - 1)

#define API_DEFAULT_POOL_SIZE    (16 * 1024)

#define API_POOL_ALIGNMENT       16
#define API_MIN_POOL_SIZE        api_align((sizeof(api_pool_t) + 2 * sizeof(api_pool_large_t)), API_POOL_ALIGNMENT)


#ifdef __cplusplus
extern "C" {
#endif

API void* api_mallocz(size_t size);
API void api_freep(void *arg);

extern void *(* nmemcpy)(void *to, const void *from, size_t len);
API void *sse_memcpy_32(void *to, const void *from, size_t len);
API void *sse_memcpy_64(void *to, const void *from, size_t len);
API void *mmx_memcpy_32(void *to, const void *from, size_t len);
API void *mmx_memcpy_64(void *to, const void *from, size_t len);


//------------------------------< memory pool >------------------------------
typedef struct pool_struct        api_pool_t;
//typedef struct api_chain_s       api_chain_t;

typedef void (*api_pool_cleanup_pt)(void *data);

typedef struct pool_cleanup_struct  api_pool_cleanup_t;

struct pool_cleanup_struct {
    api_pool_cleanup_pt   handler;
    void                 *data;
    api_pool_cleanup_t   *next;
};


typedef struct pool_large_struct  api_pool_large_t;

struct pool_large_struct {
    api_pool_large_t     *next;
    void                 *alloc;
};

typedef struct {
    uint8_t    *last;
    uint8_t    *end;
    api_pool_t *next;
    api_uint_t  failed;
} api_pool_data_t;


struct pool_struct {
    api_pool_data_t       d;
    size_t                max;
    api_pool_t           *current;
    //api_chain_t          *chain;
    api_pool_large_t     *large;
    api_pool_cleanup_t   *cleanup;
    //api_log_t            *log;
};


API api_pool_t *api_create_pool(size_t size);
API void api_destroy_pool(api_pool_t *pool);
API void api_reset_pool(api_pool_t *pool);

API void *api_palloc(api_pool_t *pool, size_t size);
API void *api_pnalloc(api_pool_t *pool, size_t size);
API void *api_pcalloc(api_pool_t *pool, size_t size);
API void *api_pmemalign(api_pool_t *pool, size_t size, size_t alignment);
API api_int_t api_pfree(api_pool_t *pool, void *p);

API api_pool_cleanup_t *api_pool_cleanup_add(api_pool_t *p, size_t size);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_MEMORY_H_INCLUDED__ */
