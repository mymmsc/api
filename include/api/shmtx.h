#ifndef __API_SHMTX_H_INCLUDED__
#define __API_SHMTX_H_INCLUDED__

#include "api/lib.h"
#include "api/atomic.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    atomic_t   lock;
} api_shmtx_sh_t;

typedef struct {
    atomic_t  *lock;
    api_uint_t spin;
} api_shmtx_t;

API api_int_t api_shmtx_create(api_shmtx_t *mtx, api_shmtx_sh_t *addr);
API void api_shmtx_destroy(api_shmtx_t *mtx);
API api_uint_t api_shmtx_trylock(api_shmtx_t *mtx);
API void api_shmtx_lock(api_shmtx_t *mtx);
API void api_shmtx_unlock(api_shmtx_t *mtx);
API api_uint_t api_shmtx_force_unlock(api_shmtx_t *mtx, pid_t pid);

#ifdef __cplusplus
}
#endif

#endif /* !__API_SHMTX_H_INCLUDED__ */
