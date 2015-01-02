#ifndef _API_SHMEM_H_INCLUDED_
#define _API_SHMEM_H_INCLUDED_

#include <api.h>

#include <sys/mman.h>
#include <sys/types.h>
#include <fcntl.h>

typedef struct {
    uint8_t     *addr;
    size_t       size;
    api_str_t    name;
    //api_log_t   *log;
    api_uint_t   exists;   /* unsigned  exists:1;  */
} api_shm_t;


api_int_t api_shm_alloc(api_shm_t *shm);
void api_shm_free(api_shm_t *shm);


#endif /* _API_SHMEM_H_INCLUDED_ */
