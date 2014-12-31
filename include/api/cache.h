#ifndef __API_CACHE_H_INCLUDED__
#define __API_CACHE_H_INCLUDED__

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __cache_struct {
	char *fname;
	int fd;
	int64_t size;
	void *mm;
}cache_t;

API status_t cache_open(cache_t **cache, const char *filename, size_t size);
API status_t cache_close(cache_t *cache);

API status_t cache_offset(void **addr, cache_t *cache, int64_t offset);

#ifdef __cplusplus
}
#endif


#endif /* ! __API_CACHE_H_INCLUDED__ */
