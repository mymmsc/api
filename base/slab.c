#include "api/slab.h"
#include "api/log.h"

#define API_SLAB_PAGE_MASK   3
#define API_SLAB_PAGE        0
#define API_SLAB_BIG         1
#define API_SLAB_EXACT       2
#define API_SLAB_SMALL       3

#if (API_PTRSIZE == 4)

#define API_SLAB_PAGE_FREE   0
#define API_SLAB_PAGE_BUSY   0xffffffff
#define API_SLAB_PAGE_START  0x80000000

#define API_SLAB_SHIFT_MASK  0x0000000f
#define API_SLAB_MAP_MASK    0xffff0000
#define API_SLAB_MAP_SHIFT   16

#define API_SLAB_BUSY        0xffffffff

#else /* (API_PTRSIZE == 8) */

#define API_SLAB_PAGE_FREE   0
#define API_SLAB_PAGE_BUSY   0xffffffffffffffff
#define API_SLAB_PAGE_START  0x8000000000000000

#define API_SLAB_SHIFT_MASK  0x000000000000000f
#define API_SLAB_MAP_MASK    0xffffffff00000000
#define API_SLAB_MAP_SHIFT   32

#define API_SLAB_BUSY        0xffffffffffffffff

#endif


static api_slab_page_t *api_slab_alloc_pages(api_slab_pool_t *pool,
    api_uint_t pages);
static void api_slab_free_pages(api_slab_pool_t *pool, api_slab_page_t *page,
    api_uint_t pages);
static void api_slab_error(api_slab_pool_t *pool, const char *text);

static api_uint_t  api_slab_max_size;
static api_uint_t  api_slab_exact_size;
static api_uint_t  api_slab_exact_shift;

static void slab_init(api_slab_pool_t *pool)
{
    uint8_t           *p;
    size_t            size;
    api_int_t         m;
    api_uint_t        i, n, pages;
    api_slab_page_t  *slots;
	
    /* STUB */
    if (api_slab_max_size == 0) {
        api_slab_max_size = api_pagesize / 2;
        api_slab_exact_size = api_pagesize / (8 * sizeof(uintptr_t));
        for (n = api_slab_exact_size; n >>= 1; api_slab_exact_shift++) {
            /* void */
        }
    }
    /**/

    pool->min_size = 1 << pool->min_shift;

    p = (uint8_t *) pool + sizeof(api_slab_pool_t);
    size = pool->end - p;

    slots = (api_slab_page_t *) p;
    n = api_pagesize_shift - pool->min_shift;

    for (i = 0; i < n; i++) {
        slots[i].slab = 0;
        slots[i].next = &slots[i];
        slots[i].prev = 0;
    }

    p += n * sizeof(api_slab_page_t);

    pages = (api_uint_t) (size / (api_pagesize + sizeof(api_slab_page_t)));

    api_memzero(p, pages * sizeof(api_slab_page_t));

    pool->pages = (api_slab_page_t *) p;

    pool->free.prev = 0;
    pool->free.next = (api_slab_page_t *) p;

    pool->pages->slab = pages;
    pool->pages->next = &pool->free;
    pool->pages->prev = (uintptr_t) &pool->free;

    pool->start = (uint8_t *)
                  api_align_ptr((uintptr_t) p + pages * sizeof(api_slab_page_t),
                                 api_pagesize);

    m = pages - (pool->end - pool->start) / api_pagesize;
    if (m > 0) {
        pages -= m;
        pool->pages->slab = pages;
    }

    pool->last = pool->pages + pages;

    pool->log_nomem = 1;
    pool->log_ctx = &pool->zero;
    pool->zero = '\0';
}

api_slab_pool_t *api_slab_init(void *addr, size_t size)
{
	api_slab_pool_t *pool = NULL;
	if(addr != NULL && size > sizeof(api_slab_pool_t)) {
		pool = (api_slab_pool_t *) addr;
		
		pool->addr = addr;
		pool->min_shift = 3;
		pool->end = (char *)addr + size;
		if (api_shmtx_create(&pool->mutex, &pool->lock) == API_SUCCESS) {
        	slab_init(pool);
		}
	}

	return pool;
}

void *
api_slab_alloc(api_slab_pool_t *pool, size_t size)
{
    void  *p;

    api_shmtx_lock(&pool->mutex);

    p = api_slab_alloc_locked(pool, size);

    api_shmtx_unlock(&pool->mutex);

    return p;
}


void *
api_slab_alloc_locked(api_slab_pool_t *pool, size_t size)
{
    size_t            s;
    uintptr_t         p, n, m, mask, *bitmap;
    api_uint_t        i, slot, shift, map;
    api_slab_page_t  *page, *prev, *slots;

    if (size > api_slab_max_size) {
        page = api_slab_alloc_pages(pool, (size >> api_pagesize_shift)
                                          + ((size % api_pagesize) ? 1 : 0));
        if (page) {
            p = (page - pool->pages) << api_pagesize_shift;
            p += (uintptr_t) pool->start;
        } else {
            p = 0;
        }
        goto done;
    }

    if (size > pool->min_size) {
        shift = 1;
        for (s = size - 1; s >>= 1; shift++) { /* void */ }
        slot = shift - pool->min_shift;

    } else {
        size = pool->min_size;
        shift = pool->min_shift;
        slot = 0;
    }

    slots = (api_slab_page_t *) ((uint8_t *) pool + sizeof(api_slab_pool_t));
    page = slots[slot].next;
	
    if (page->next != page) {
        if (shift < api_slab_exact_shift) {
            do {
                p = (page - pool->pages) << api_pagesize_shift;
                bitmap = (uintptr_t *) (pool->start + p);

                map = (1 << (api_pagesize_shift - shift))
                          / (sizeof(uintptr_t) * 8);

                for (n = 0; n < map; n++) {

                    if (bitmap[n] != API_SLAB_BUSY) {

                        for (m = 1, i = 0; m; m <<= 1, i++) {
                            if ((bitmap[n] & m)) {
                                continue;
                            }

                            bitmap[n] |= m;

                            i = ((n * sizeof(uintptr_t) * 8) << shift)
                                + (i << shift);

                            if (bitmap[n] == API_SLAB_BUSY) {
                                for (n = n + 1; n < map; n++) {
                                     if (bitmap[n] != API_SLAB_BUSY) {
                                         p = (uintptr_t) bitmap + i;

                                         goto done;
                                     }
                                }

                                prev = (api_slab_page_t *)
                                            (page->prev & ~API_SLAB_PAGE_MASK);
                                prev->next = page->next;
                                page->next->prev = page->prev;

                                page->next = NULL;
                                page->prev = API_SLAB_SMALL;
                            }

                            p = (uintptr_t) bitmap + i;

                            goto done;
                        }
                    }
                }

                page = page->next;

            } while (page);

        } else if (shift == api_slab_exact_shift) {

            do {
                if (page->slab != API_SLAB_BUSY) {

                    for (m = 1, i = 0; m; m <<= 1, i++) {
                        if ((page->slab & m)) {
                            continue;
                        }

                        page->slab |= m;

                        if (page->slab == API_SLAB_BUSY) {
                            prev = (api_slab_page_t *)
                                            (page->prev & ~API_SLAB_PAGE_MASK);
                            prev->next = page->next;
                            page->next->prev = page->prev;

                            page->next = NULL;
                            page->prev = API_SLAB_EXACT;
                        }

                        p = (page - pool->pages) << api_pagesize_shift;
                        p += i << shift;
                        p += (uintptr_t) pool->start;

                        goto done;
                    }
                }

                page = page->next;

            } while (page);

        } else { /* shift > api_slab_exact_shift */

            n = api_pagesize_shift - (page->slab & API_SLAB_SHIFT_MASK);
            n = 1 << n;
            n = ((uintptr_t) 1 << n) - 1;
            mask = n << API_SLAB_MAP_SHIFT;

            do {
                if ((page->slab & API_SLAB_MAP_MASK) != mask) {

                    for (m = (uintptr_t) 1 << API_SLAB_MAP_SHIFT, i = 0;
                         m & mask;
                         m <<= 1, i++)
                    {
                        if ((page->slab & m)) {
                            continue;
                        }

                        page->slab |= m;

                        if ((page->slab & API_SLAB_MAP_MASK) == mask) {
                            prev = (api_slab_page_t *)
                                            (page->prev & ~API_SLAB_PAGE_MASK);
                            prev->next = page->next;
                            page->next->prev = page->prev;

                            page->next = NULL;
                            page->prev = API_SLAB_BIG;
                        }

                        p = (page - pool->pages) << api_pagesize_shift;
                        p += i << shift;
                        p += (uintptr_t) pool->start;

                        goto done;
                    }
                }

                page = page->next;

            } while (page);
        }
    }

    page = api_slab_alloc_pages(pool, 1);

    if (page) {
        if (shift < api_slab_exact_shift) {
            p = (page - pool->pages) << api_pagesize_shift;
            bitmap = (uintptr_t *) (pool->start + p);

            s = 1 << shift;
            n = (1 << (api_pagesize_shift - shift)) / 8 / s;

            if (n == 0) {
                n = 1;
            }

            bitmap[0] = (2 << n) - 1;

            map = (1 << (api_pagesize_shift - shift)) / (sizeof(uintptr_t) * 8);

            for (i = 1; i < map; i++) {
                bitmap[i] = 0;
            }

            page->slab = shift;
            page->next = &slots[slot];
            page->prev = (uintptr_t) &slots[slot] | API_SLAB_SMALL;

            slots[slot].next = page;

            p = ((page - pool->pages) << api_pagesize_shift) + s * n;
            p += (uintptr_t) pool->start;

            goto done;

        } else if (shift == api_slab_exact_shift) {

            page->slab = 1;
            page->next = &slots[slot];
            page->prev = (uintptr_t) &slots[slot] | API_SLAB_EXACT;

            slots[slot].next = page;

            p = (page - pool->pages) << api_pagesize_shift;
            p += (uintptr_t) pool->start;

            goto done;

        } else { /* shift > api_slab_exact_shift */

            page->slab = ((uintptr_t) 1 << API_SLAB_MAP_SHIFT) | shift;
            page->next = &slots[slot];
            page->prev = (uintptr_t) &slots[slot] | API_SLAB_BIG;

            slots[slot].next = page;

            p = (page - pool->pages) << api_pagesize_shift;
            p += (uintptr_t) pool->start;

            goto done;
        }
    }

    p = 0;

done:

    return (void *) p;
}


void *
api_slab_calloc(api_slab_pool_t *pool, size_t size)
{
    void  *p;

    api_shmtx_lock(&pool->mutex);

    p = api_slab_calloc_locked(pool, size);

    api_shmtx_unlock(&pool->mutex);

    return p;
}


void *
api_slab_calloc_locked(api_slab_pool_t *pool, size_t size)
{
    void  *p;

    p = api_slab_alloc_locked(pool, size);
    if (p) {
        api_memzero(p, size);
    }

    return p;
}


void
api_slab_free(api_slab_pool_t *pool, void *p)
{
    api_shmtx_lock(&pool->mutex);

    api_slab_free_locked(pool, p);

    api_shmtx_unlock(&pool->mutex);
}


void
api_slab_free_locked(api_slab_pool_t *pool, void *p)
{
    size_t            size;
    uintptr_t         slab, m, *bitmap;
    api_uint_t        n, type, slot, shift, map;
    api_slab_page_t  *slots, *page;

    if ((uint8_t *) p < pool->start || (uint8_t *) p > pool->end) {
        api_slab_error(pool, "api_slab_free(): outside of pool");
        goto fail;
    }

    n = ((uint8_t *) p - pool->start) >> api_pagesize_shift;
    page = &pool->pages[n];
    slab = page->slab;
    type = page->prev & API_SLAB_PAGE_MASK;

    switch (type) {

    case API_SLAB_SMALL:

        shift = slab & API_SLAB_SHIFT_MASK;
        size = 1 << shift;

        if ((uintptr_t) p & (size - 1)) {
            goto wrong_chunk;
        }

        n = ((uintptr_t) p & (api_pagesize - 1)) >> shift;
        m = (uintptr_t) 1 << (n & (sizeof(uintptr_t) * 8 - 1));
        n /= (sizeof(uintptr_t) * 8);
        bitmap = (uintptr_t *)
                             ((uintptr_t) p & ~((uintptr_t) api_pagesize - 1));

        if (bitmap[n] & m) {

            if (page->next == NULL) {
                slots = (api_slab_page_t *)
                                   ((uint8_t *) pool + sizeof(api_slab_pool_t));
                slot = shift - pool->min_shift;

                page->next = slots[slot].next;
                slots[slot].next = page;

                page->prev = (uintptr_t) &slots[slot] | API_SLAB_SMALL;
                page->next->prev = (uintptr_t) page | API_SLAB_SMALL;
            }

            bitmap[n] &= ~m;

            n = (1 << (api_pagesize_shift - shift)) / 8 / (1 << shift);

            if (n == 0) {
                n = 1;
            }

            if (bitmap[0] & ~(((uintptr_t) 1 << n) - 1)) {
                goto done;
            }

            map = (1 << (api_pagesize_shift - shift)) / (sizeof(uintptr_t) * 8);

            for (n = 1; n < map; n++) {
                if (bitmap[n]) {
                    goto done;
                }
            }

            api_slab_free_pages(pool, page, 1);

            goto done;
        }

        goto chunk_already_free;

    case API_SLAB_EXACT:

        m = (uintptr_t) 1 <<
                (((uintptr_t) p & (api_pagesize - 1)) >> api_slab_exact_shift);
        size = api_slab_exact_size;

        if ((uintptr_t) p & (size - 1)) {
            goto wrong_chunk;
        }

        if (slab & m) {
            if (slab == API_SLAB_BUSY) {
                slots = (api_slab_page_t *)
                                   ((uint8_t *) pool + sizeof(api_slab_pool_t));
                slot = api_slab_exact_shift - pool->min_shift;

                page->next = slots[slot].next;
                slots[slot].next = page;

                page->prev = (uintptr_t) &slots[slot] | API_SLAB_EXACT;
                page->next->prev = (uintptr_t) page | API_SLAB_EXACT;
            }

            page->slab &= ~m;

            if (page->slab) {
                goto done;
            }

            api_slab_free_pages(pool, page, 1);

            goto done;
        }

        goto chunk_already_free;

    case API_SLAB_BIG:

        shift = slab & API_SLAB_SHIFT_MASK;
        size = 1 << shift;

        if ((uintptr_t) p & (size - 1)) {
            goto wrong_chunk;
        }

        m = (uintptr_t) 1 << ((((uintptr_t) p & (api_pagesize - 1)) >> shift)
                              + API_SLAB_MAP_SHIFT);

        if (slab & m) {

            if (page->next == NULL) {
                slots = (api_slab_page_t *)
                                   ((uint8_t *) pool + sizeof(api_slab_pool_t));
                slot = shift - pool->min_shift;

                page->next = slots[slot].next;
                slots[slot].next = page;

                page->prev = (uintptr_t) &slots[slot] | API_SLAB_BIG;
                page->next->prev = (uintptr_t) page | API_SLAB_BIG;
            }

            page->slab &= ~m;

            if (page->slab & API_SLAB_MAP_MASK) {
                goto done;
            }

            api_slab_free_pages(pool, page, 1);

            goto done;
        }

        goto chunk_already_free;

    case API_SLAB_PAGE:

        if ((uintptr_t) p & (api_pagesize - 1)) {
            goto wrong_chunk;
        }

        if (slab == API_SLAB_PAGE_FREE) {
            api_slab_error(pool, "api_slab_free(): page is already free");
            goto fail;
        }

        if (slab == API_SLAB_PAGE_BUSY) {
            api_slab_error(pool, "api_slab_free(): pointer to wrong page");
            goto fail;
        }

        n = ((uint8_t *) p - pool->start) >> api_pagesize_shift;
        size = slab & ~API_SLAB_PAGE_START;

        api_slab_free_pages(pool, &pool->pages[n], size);

        return;
    }

    /* not reached */

    return;

done:

    return;

wrong_chunk:

    api_slab_error(pool, "api_slab_free(): pointer to wrong chunk");

    goto fail;

chunk_already_free:

    api_slab_error(pool, "api_slab_free(): chunk is already free");

fail:

    return;
}


static api_slab_page_t *
api_slab_alloc_pages(api_slab_pool_t *pool, api_uint_t pages)
{
    api_slab_page_t  *page, *p;

    for (page = pool->free.next; page != &pool->free; page = page->next) {

        if (page->slab >= pages) {

            if (page->slab > pages) {
                page[page->slab - 1].prev = (uintptr_t) &page[pages];

                page[pages].slab = page->slab - pages;
                page[pages].next = page->next;
                page[pages].prev = page->prev;

                p = (api_slab_page_t *) page->prev;
                p->next = &page[pages];
                page->next->prev = (uintptr_t) &page[pages];

            } else {
                p = (api_slab_page_t *) page->prev;
                p->next = page->next;
                page->next->prev = page->prev;
            }

            page->slab = pages | API_SLAB_PAGE_START;
            page->next = NULL;
            page->prev = API_SLAB_PAGE;

            if (--pages == 0) {
                return page;
            }

            for (p = page + 1; pages; pages--) {
                p->slab = API_SLAB_PAGE_BUSY;
                p->next = NULL;
                p->prev = API_SLAB_PAGE;
                p++;
            }

            return page;
        }
    }

    if (pool->log_nomem) {
        api_slab_error(pool, "api_slab_alloc() failed: no memory");
    }

    return NULL;
}


static void
api_slab_free_pages(api_slab_pool_t *pool, api_slab_page_t *page,
    api_uint_t pages)
{
    api_uint_t        type;
    api_slab_page_t  *prev, *join;

    page->slab = pages--;

    if (pages) {
        api_memzero(&page[1], pages * sizeof(api_slab_page_t));
    }

    if (page->next) {
        prev = (api_slab_page_t *) (page->prev & ~API_SLAB_PAGE_MASK);
        prev->next = page->next;
        page->next->prev = page->prev;
    }

    join = page + page->slab;

    if (join < pool->last) {
        type = join->prev & API_SLAB_PAGE_MASK;

        if (type == API_SLAB_PAGE) {

            if (join->next != NULL) {
                pages += join->slab;
                page->slab += join->slab;

                prev = (api_slab_page_t *) (join->prev & ~API_SLAB_PAGE_MASK);
                prev->next = join->next;
                join->next->prev = join->prev;

                join->slab = API_SLAB_PAGE_FREE;
                join->next = NULL;
                join->prev = API_SLAB_PAGE;
            }
        }
    }

    if (page > pool->pages) {
        join = page - 1;
        type = join->prev & API_SLAB_PAGE_MASK;

        if (type == API_SLAB_PAGE) {

            if (join->slab == API_SLAB_PAGE_FREE) {
                join = (api_slab_page_t *) (join->prev & ~API_SLAB_PAGE_MASK);
            }

            if (join->next != NULL) {
                pages += join->slab;
                join->slab += page->slab;

                prev = (api_slab_page_t *) (join->prev & ~API_SLAB_PAGE_MASK);
                prev->next = join->next;
                join->next->prev = join->prev;

                page->slab = API_SLAB_PAGE_FREE;
                page->next = NULL;
                page->prev = API_SLAB_PAGE;

                page = join;
            }
        }
    }

    if (pages) {
        page[pages].prev = (uintptr_t) page;
    }

    page->prev = (uintptr_t) &pool->free;
    page->next = pool->free.next;

    page->next->prev = (uintptr_t) page;

    pool->free.next = page;
}


static void
api_slab_error(api_slab_pool_t *pool, const char *text)
{
    do_error("%s%s", text, pool->log_ctx);
}

