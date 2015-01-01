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
}api_cache_t;

API status_t api_cache_open(api_cache_t **cache, const char *filename, size_t size);
API status_t api_cache_close(api_cache_t *cache);

API status_t api_cache_offset(void **addr, api_cache_t *cache, int64_t offset);

#ifdef __cplusplus
}
#endif


#endif /* ! __API_CACHE_H_INCLUDED__ */
