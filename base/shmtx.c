#include "api/shmtx.h"

static void api_shmtx_wakeup(api_shmtx_t *mtx);

api_int_t
api_shmtx_create(api_shmtx_t *mtx, api_shmtx_sh_t *addr)
{
    mtx->lock = &addr->lock;

    if (mtx->spin == (api_uint_t) -1) {
        return API_SUCCESS;
    }
	
    mtx->spin = 2048;

    return API_SUCCESS;
}


void
api_shmtx_destroy(api_shmtx_t *mtx)
{
#if (API_HAVE_POSIX_SEM)

    if (mtx->semaphore) {
        if (sem_destroy(&mtx->sem) == -1) {
            api_log_error(API_LOG_ALERT, api_cycle->log, api_errno,
                          "sem_destroy() failed");
        }
    }

#endif
}


api_uint_t
api_shmtx_trylock(api_shmtx_t *mtx)
{
    return (*mtx->lock == 0 && api_atomic_cmp_set(mtx->lock, 0, api_pid));
}


void
api_shmtx_lock(api_shmtx_t *mtx)
{
    api_uint_t         i, n;

    for ( ;; ) {
		printf("api_pid: %d, %d\n", api_pid, *mtx->lock);
        if (*mtx->lock == 0 && api_atomic_cmp_set(mtx->lock, 0, api_pid)) {
            return;
        }

        if (api_ncpu > 1) {

            for (n = 1; n < mtx->spin; n <<= 1) {

                for (i = 0; i < n; i++) {
                    api_cpu_pause();
                }

                if (*mtx->lock == 0
                    && api_atomic_cmp_set(mtx->lock, 0, api_pid))
                {
                    return;
                }
            }
        }

        api_sched_yield();
    }
}


void
api_shmtx_unlock(api_shmtx_t *mtx)
{
    if (mtx->spin != (api_uint_t) -1) {
        //
    }

    if (api_atomic_cmp_set(mtx->lock, api_pid, 0)) {
        api_shmtx_wakeup(mtx);
    }
}


api_uint_t
api_shmtx_force_unlock(api_shmtx_t *mtx, pid_t pid)
{
    if (api_atomic_cmp_set(mtx->lock, pid, 0)) {
        api_shmtx_wakeup(mtx);
        return 1;
    }

    return 0;
}


static void
api_shmtx_wakeup(api_shmtx_t *mtx)
{
#if (API_HAVE_POSIX_SEM)
    api_atomic_uint_t  wait;

    if (!mtx->semaphore) {
        return;
    }

    for ( ;; ) {

        wait = *mtx->wait;

        if (wait == 0) {
            return;
        }

        if (api_atomic_cmp_set(mtx->wait, wait, wait - 1)) {
            break;
        }
    }

    api_log_debug1(API_LOG_DEBUG_CORE, api_cycle->log, 0,
                   "shmtx wake %uA", wait);

    if (sem_post(&mtx->sem) == -1) {
        api_log_error(API_LOG_ALERT, api_cycle->log, api_errno,
                      "sem_post() failed while wake shmtx");
    }

#endif
}

