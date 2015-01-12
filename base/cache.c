#include "api/cache.h"
#include "api/errno.h"
#include "api/strings.h"
#include "api/lib.h"
#include "api/memory.h"

#include <sys/mman.h>
#include <sys/types.h>
#include <fcntl.h>

status_t api_cache_open(api_cache_t **cache, const char *filename, size_t size)
{
	status_t rc = API_SUCCESS;
	(*cache) = NULL;
	int fd = open(filename, O_CREAT|O_RDWR, 0666);
	if(fd < 0) {
		rc = errno;
	} else {
		int64_t offset = size;
		//offset = API_ALIGN(size, API_SIZE_FROM_KB(4))
		(void)ftruncate(fd, offset);
		void *mm = mmap(NULL, offset, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
		if (mm == (void *)-1) {
        	rc = errno;
    	} else {
    		(*cache) = api_calloc(1, sizeof(api_cache_t));
			(*cache)->fname = strdup(filename);
			(*cache)->size = offset;
			(*cache)->fd = fd;
			(*cache)->mm = mm;
    	}
	}
	return rc;
}


status_t api_cache_close(api_cache_t *cache)
{
	status_t iRet = API_SUCCESS;
	if(cache != NULL) {
		api_safefree(cache->fname);
		if(cache->fd > 0) {
			close(cache->fd);
		}
		cache->fd = -1;
		if(cache->mm != NULL) {
			munmap(cache->mm, cache->size);
			cache->mm = (void *)-1;
		}
	}
	api_safefree(cache);
	return iRet;
}

status_t api_cache_offset(void **addr, api_cache_t *cache, int64_t offset)
{
    if (offset < 0 || offset > cache->size)
    {
		(*addr) = NULL;
        return API_EINVAL;
    }
    (*addr) = (char *) cache->mm + offset;
    return API_SUCCESS;
}

