/*
 * memory.c
 *
 *  Created on: 2014年11月9日
 *      Author: wangfeng
 */

#include "api/memory.h"
#include "api/log.h"

void* api_mallocz(size_t size)
{
	void *ptr = api_malloc(size);
    if (ptr) {
        memset(ptr, 0, size);
    }
    return ptr;
}

void api_freep(void *arg)
{
    void **ptr = (void **)arg;
    api_free(*ptr);
    *ptr = NULL;
}

#define DEF_OPT_FLAG_NONE   0
#define DEF_OPT_FLAG_NOPT   1

#define DEF_OPT_FLAG_MMX    5
#define DEF_OPT_FLAG_SSE    6
#define DEF_OPT_FLAG_SSE2   7

static int opt_flag = DEF_OPT_FLAG_NONE;

void * _memcpy(void *to, const void *from, size_t len);

void *(* nmemcpy)(void *to, const void *from, size_t len) = _memcpy;

typedef struct {
    unsigned int eax;
    unsigned int ebx;
    unsigned int ecx;
    unsigned int edx;
} cpuid_regs_t;

static int check_opt_flag(void)
{
    cpuid_regs_t regs;

#define CPUID   ".byte 0x0f, 0xa2; "
    asm(CPUID
            : "=a" (regs.eax), "=b" (regs.ebx), "=c" (regs.ecx), "=d" (regs.edx)
            : "0" (1));

    if (regs.edx & 0x4000000) {
        return (DEF_OPT_FLAG_SSE2);
    }
    if (regs.edx & 0x2000000) {
        return (DEF_OPT_FLAG_SSE);
    }
    if (regs.edx & 0x800000) {
        return (DEF_OPT_FLAG_MMX);
    }
    return (DEF_OPT_FLAG_NONE);
}

#define small_memcpy(to,from,n)\
{\
    register unsigned long int dummy;\
    __asm__ __volatile__(\
            "rep; movsb"\
            :"=&D"(to), "=&S"(from), "=&c"(dummy)\
            :"0" (to), "1" (from),"2" (n)\
            : "memory");\
}

/* From Linux. */
static inline void * __memcpy(void * to, const void * from, size_t len)
{  
    int d0, d1, d2;
	
    if (len < 4 ) {
        small_memcpy(to,from,len);
    } else
        __asm__ __volatile__(
                "rep ; movsl\n\t"
                "testb $2,%b4\n\t"
                "je 1f\n\t"
                "movsw\n"
                "1:\ttestb $1,%b4\n\t"
                "je 2f\n\t"
                "movsb\n"
                "2:"
                : "=&c" (d0), "=&D" (d1), "=&S" (d2)
                :"0" (len/4), "q" (len),"1" ((long) to),"2" ((long) from)
                : "memory");

    return(to);
}  

#define MIN_LEN         0x40
#define SSE_MMREG_SIZE  16
#define MMX_MMREG_SIZE  8

void *sse_memcpy_32(void *to, const void *from, size_t len)
{
    void *const save = to;

    __asm__ __volatile__ (
            "prefetchnta (%0)\n"
            "prefetchnta 32(%0)\n"
            "prefetchnta 64(%0)\n"
            "prefetchnta 96(%0)\n"
            "prefetchnta 128(%0)\n"
            "prefetchnta 160(%0)\n"
            "prefetchnta 192(%0)\n"
            "prefetchnta 224(%0)\n"
            "prefetchnta 256(%0)\n"
            "prefetchnta 288(%0)\n"
            :: "r" (from) );

    if (len >= MIN_LEN) {
        register int i;
        register int j;
        register unsigned int delta;
		
        delta = ((uintptr_t)to) & (SSE_MMREG_SIZE - 1);
        if (delta) {
            delta = SSE_MMREG_SIZE - delta;
            len -= delta;
            small_memcpy(to, from, delta);
        }
        j = len >> 6;
        len &= 63;

        for(i=0; i<j; i++) {
            __asm__ __volatile__ (
                    "prefetchnta 320(%0)\n"
                    "prefetchnta 352(%0)\n"
                    "movups (%0), %%xmm0\n"
                    "movups 16(%0), %%xmm1\n"
                    "movups 32(%0), %%xmm2\n"
                    "movups 48(%0), %%xmm3\n"
                    "movntps %%xmm0, (%1)\n"
                    "movntps %%xmm1, 16(%1)\n"
                    "movntps %%xmm2, 32(%1)\n"
                    "movntps %%xmm3, 48(%1)\n"
                    ::"r" (from), "r" (to) : "memory");
            from+=64;
            to+=64;
        }
        __asm__ __volatile__ ("sfence":::"memory");
    }
    if (len != 0) {
        __memcpy(to, from, len);
    }
    return save;
}

void *sse_memcpy_64(void *to, const void *from, size_t len)
{
    void *const save = to;

    __asm__ __volatile__ (
            "prefetchnta (%0)\n"
            "prefetchnta 64(%0)\n"
            "prefetchnta 128(%0)\n"
            "prefetchnta 192(%0)\n"
            "prefetchnta 256(%0)\n"
            :: "r" (from) );

    if (len >= MIN_LEN) {
        register int i;
        register int j;
        register unsigned int delta;

        delta = ((uintptr_t)to) & (SSE_MMREG_SIZE - 1);
        if (delta) {
            delta = SSE_MMREG_SIZE - delta;
            len -= delta;
            small_memcpy(to, from, delta);
        }
        j = len >> 6;
        len &= 63;

        for(i = 0; i < j; i++) {
            __asm__ __volatile__ (
                    "prefetchnta 320(%0)\n"
                    "movups (%0), %%xmm0\n"
                    "movups 16(%0), %%xmm1\n"
                    "movups 32(%0), %%xmm2\n"
                    "movups 48(%0), %%xmm3\n"
                    "movntps %%xmm0, (%1)\n"
                    "movntps %%xmm1, 16(%1)\n"
                    "movntps %%xmm2, 32(%1)\n"
                    "movntps %%xmm3, 48(%1)\n"
                    ::"r" (from), "r" (to) : "memory");
            from += 64;
            to += 64;
        }
        __asm__ __volatile__ ("sfence":::"memory");
    }
    if (len != 0) {
        __memcpy(to, from, len);
    }
    return save;
}  

void *mmx_memcpy_32(void *to, const void *from, size_t len)
{
    void *const save = to;
    register int i;
    register int j;

    __asm__ __volatile__ (
            "prefetchnta (%0)\n"
            "prefetchnta 32(%0)\n"
            "prefetchnta 64(%0)\n"
            "prefetchnta 96(%0)\n"
            "prefetchnta 128(%0)\n"
            "prefetchnta 160(%0)\n"
            "prefetchnta 192(%0)\n"
            "prefetchnta 224(%0)\n"
            "prefetchnta 256(%0)\n"
            "prefetchnta 288(%0)\n"
            :: "r" (from) );
  
    j = len >> 6;
    len &= 63;
    for(i = 0; i < j; i++) {
        __asm__ __volatile__ (
                "prefetchnta 320(%0)\n"
                "prefetchnta 352(%0)\n"
                "movq (%0), %%mm0\n"
                "movq 8(%0), %%mm1\n"
                "movq 16(%0), %%mm2\n"
                "movq 24(%0), %%mm3\n"
                "movq 32(%0), %%mm4\n"
                "movq 40(%0), %%mm5\n"
                "movq 48(%0), %%mm6\n"
                "movq 56(%0), %%mm7\n"
                "movq %%mm0, (%1)\n"
                "movq %%mm1, 8(%1)\n"
                "movq %%mm2, 16(%1)\n"
                "movq %%mm3, 24(%1)\n"
                "movq %%mm4, 32(%1)\n"
                "movq %%mm5, 40(%1)\n"
                "movq %%mm6, 48(%1)\n"
                "movq %%mm7, 56(%1)\n"
                :: "r" (from), "r" (to) : "memory");
        from += 64;
        to += 64;
    }
    __asm__ __volatile__ ("sfence":::"memory");
    __asm__ __volatile__ ("emms":::"memory");

    if (len != 0) {
        __memcpy(to, from, len);
    }
    return (save);
}  

void *mmx_memcpy_64(void *to, const void *from, size_t len)
{
    void *const save = to;
    register int i;
    register int j;

    __asm__ __volatile__ (
            "prefetchnta (%0)\n"
            "prefetchnta 64(%0)\n"
            "prefetchnta 128(%0)\n"
            "prefetchnta 192(%0)\n"
            "prefetchnta 256(%0)\n"
            :: "r" (from) );

    j = len >> 6;
    len &= 63;
    for(i = 0; i < j; i++) {
        __asm__ __volatile__ (
                "prefetchnta 320(%0)\n"
                "movq (%0), %%mm0\n"
                "movq 8(%0), %%mm1\n"
                "movq 16(%0), %%mm2\n"
                "movq 24(%0), %%mm3\n"
                "movq 32(%0), %%mm4\n"
                "movq 40(%0), %%mm5\n"
                "movq 48(%0), %%mm6\n"
                "movq 56(%0), %%mm7\n"
                "movq %%mm0, (%1)\n"
                "movq %%mm1, 8(%1)\n"
                "movq %%mm2, 16(%1)\n"
                "movq %%mm3, 24(%1)\n"
                "movq %%mm4, 32(%1)\n"
                "movq %%mm5, 40(%1)\n"
                "movq %%mm6, 48(%1)\n"
                "movq %%mm7, 56(%1)\n"
                :: "r" (from), "r" (to) : "memory");
        from += 64;
        to += 64;
    }  
    __asm__ __volatile__ ("sfence":::"memory");
    __asm__ __volatile__ ("emms":::"memory");
  
    if (len != 0) {
        __memcpy(to, from, len);
    }
    return (save);
}

void *_memcpy(void *to, const void *from, size_t len)
{
    if (opt_flag == DEF_OPT_FLAG_NONE) {
        opt_flag = check_opt_flag();
    }
    if (opt_flag == DEF_OPT_FLAG_SSE2) {
        nmemcpy = sse_memcpy_64;
    }
    if (opt_flag == DEF_OPT_FLAG_SSE) {
        nmemcpy = sse_memcpy_32;
    } else if (opt_flag == DEF_OPT_FLAG_MMX) {
        nmemcpy = mmx_memcpy_32;
    } else {
        nmemcpy = memcpy;
    }
    return (nmemcpy(to, from, len));  
}

//----------------< Memory Pool >----------------

static void *api_palloc_block(api_pool_t *pool, size_t size);
static void *api_palloc_large(api_pool_t *pool, size_t size);


#if (API_HAVE_POSIX_MEMALIGN)

void *
api_memalign(size_t alignment, size_t size)
{
    void  *p;
    int    err;
	
    err = posix_memalign(&p, alignment, size);

    if (err) {
        do_error("posix_memalign(%ld, %ld) failed[%d]", alignment, size, err);
        p = NULL;
    }

    return p;
}

#elif (API_HAVE_MEMALIGN)

void *
api_memalign(size_t alignment, size_t size)
{
    void  *p;

    p = memalign(alignment, size);
    if (p == NULL) {
        do_error("memalign(%ld, %ld) failed[%d]", alignment, size, api_errno);
    }

    return p;
}
#else

void *
api_memalign(size_t alignment, size_t size)
{
    return api_mallocz(size);
}

#endif


api_pool_t *
api_create_pool(size_t size)
{
    api_pool_t  *p;

    p = api_memalign(API_POOL_ALIGNMENT, size);
    if (p == NULL) {
        return NULL;
    }

    p->d.last = (uint8_t *) p + sizeof(api_pool_t);
    p->d.end = (uint8_t *) p + size;
    p->d.next = NULL;
    p->d.failed = 0;
	
    size = size - sizeof(api_pool_t);
    p->max = (size < API_MAX_ALLOC_FROM_POOL) ? size : API_MAX_ALLOC_FROM_POOL;
	
    p->current = p;
    //p->chain = NULL;
    p->large = NULL;
    p->cleanup = NULL;
    //p->log = log;

    return p;
}


void
api_destroy_pool(api_pool_t *pool)
{
    api_pool_t          *p, *n;
    api_pool_large_t    *l;
    api_pool_cleanup_t  *c;

    for (c = pool->cleanup; c; c = c->next) {
        if (c->handler) {
            c->handler(c->data);
        }
    }
	
    for (l = pool->large; l; l = l->next) {
     	if (l->alloc) {
            api_free(l->alloc);
        }
    }


    for (p = pool, n = pool->d.next; /* void */; p = n, n = n->d.next) {
        api_free(p);
        if (n == NULL) {
            break;
        }
    }
}


void api_reset_pool(api_pool_t *pool)
{
    api_pool_t        *p;
    api_pool_large_t  *l;

    for (l = pool->large; l; l = l->next) {
        if (l->alloc) {
            api_free(l->alloc);
        }
    }

    for (p = pool; p; p = p->d.next) {
        p->d.last = (uint8_t *) p + sizeof(api_pool_t);
        p->d.failed = 0;
    }
	
    pool->current = pool;
    //pool->chain = NULL;
    pool->large = NULL;
}


void *
api_palloc(api_pool_t *pool, size_t size)
{
    uint8_t *m;
    api_pool_t  *p;
	
    if (size <= pool->max) {
        p = pool->current;
        do {
            m = api_align_ptr(p->d.last, API_ALIGNMENT);
            if ((size_t) (p->d.end - m) >= size) {
                p->d.last = m + size;
                return m;
            }
            p = p->d.next;
        } while (p);
        return api_palloc_block(pool, size);
    }
    return api_palloc_large(pool, size);
}


void *
api_pnalloc(api_pool_t *pool, size_t size)
{
    uint8_t *m;
    api_pool_t  *p;

    if (size <= pool->max) {
        p = pool->current;
        do {
            m = p->d.last;
            if ((size_t) (p->d.end - m) >= size) {
                p->d.last = m + size;
                return m;
            }
            p = p->d.next;
        } while (p);
        return api_palloc_block(pool, size);
    }
    return api_palloc_large(pool, size);
}


static void *
api_palloc_block(api_pool_t *pool, size_t size)
{
    uint8_t *m;
    size_t       psize;
    api_pool_t  *p, *new;

    psize = (size_t) (pool->d.end - (uint8_t *) pool);
    m = api_memalign(API_POOL_ALIGNMENT, psize);
    if (m == NULL) {
        return NULL;
    }
    new = (api_pool_t *) m;
	
    new->d.end = m + psize;
    new->d.next = NULL;
    new->d.failed = 0;

    m += sizeof(api_pool_data_t);
    m = api_align_ptr(m, API_ALIGNMENT);
    new->d.last = m + size;

    for (p = pool->current; p->d.next; p = p->d.next) {
        if (p->d.failed++ > 4) {
            pool->current = p->d.next;
        }
    }
    p->d.next = new;

    return m;
}


static void *
api_palloc_large(api_pool_t *pool, size_t size)
{
    void              *p;
    api_uint_t         n;
    api_pool_large_t  *large;
	
    p = api_mallocz(size);
    if (p == NULL) {
        return NULL;
    }

    n = 0;

    for (large = pool->large; large; large = large->next) {
        if (large->alloc == NULL) {
            large->alloc = p;
            return p;
        }
		
        if (n++ > 3) {
            break;
        }
    }

    large = api_palloc(pool, sizeof(api_pool_large_t));
    if (large == NULL) {
        api_free(p);
        return NULL;
    }

    large->alloc = p;
    large->next = pool->large;
    pool->large = large;

    return p;
}


void *
api_pmemalign(api_pool_t *pool, size_t size, size_t alignment)
{
    void              *p;
    api_pool_large_t  *large;

    p = api_memalign(alignment, size);
    if (p == NULL) {
        return NULL;
    }

    large = api_palloc(pool, sizeof(api_pool_large_t));
    if (large == NULL) {
        api_free(p);
        return NULL;
    }

    large->alloc = p;
    large->next = pool->large;
    pool->large = large;

    return p;
}


api_int_t
api_pfree(api_pool_t *pool, void *p)
{
    api_pool_large_t  *l;

    for (l = pool->large; l; l = l->next) {
        if (p == l->alloc) {
            api_free(l->alloc);
            l->alloc = NULL;
            return API_SUCCESS;
        }
    }
	
    return API_ERROR;
}

void *
api_pcalloc(api_pool_t *pool, size_t size)
{
    void *p;

    p = api_palloc(pool, size);
    if (p) {
        api_memzero(p, size);
    }
	
    return p;
}


api_pool_cleanup_t *
api_pool_cleanup_add(api_pool_t *p, size_t size)
{
    api_pool_cleanup_t  *c;

    c = api_palloc(p, sizeof(api_pool_cleanup_t));
    if (c == NULL) {
        return NULL;
    }

    if (size) {
        c->data = api_palloc(p, size);
        if (c->data == NULL) {
            return NULL;
        }
    } else {
        c->data = NULL;
    }

    c->handler = NULL;
    c->next = p->cleanup;

    p->cleanup = c;

    return c;
}

