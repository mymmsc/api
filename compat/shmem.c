#include <api/shmem.h>

#include <sys/mman.h>
#include <sys/types.h>
#include <fcntl.h>

#if 0
api_int_t
api_shm_zone_init(api_shm_zone_t *shm_zone, size_t size, void *tag)
{
	api_shm_zone_t *shm_zone = NULL;
	if (shm_zone == NULL) {
        return NULL;
    }
	
	api_shm_t shm;
	shm.size = size;
	api_shm_alloc(&shm);
	shm_zone->shm.addr = api_shm_alloc(api_shm_t * shm)
    shm_zone->data = NULL;
    //shm_zone->shm.log = cf->cycle->log;
    shm_zone->shm.size = size;
    //shm_zone->shm.name = *name;
    shm_zone->shm.exists = 0;
    shm_zone->init = NULL;
    shm_zone->tag = tag;

    return shm_zone;
}
#endif

#if (API_HAVE_SHMEM_MMAP_ANON)

api_int_t
api_shm_alloc(api_shm_t *shm)
{
    shm->addr = (uint8_t *) mmap(NULL, shm->size,
                                PROT_READ|PROT_WRITE,
                                MAP_ANON|MAP_SHARED, -1, 0);

    if (shm->addr == MAP_FAILED) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "mmap(MAP_ANON|MAP_SHARED, %uz) failed", shm->size);
        return API_ERROR;
    }

    return API_SUCCESS;
}


void
api_shm_free(api_shm_t *shm)
{
    if (munmap((void *) shm->addr, shm->size) == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "munmap(%p, %uz) failed", shm->addr, shm->size);
    }
}

#elif (API_HAVE_SHMEM_MMAP_ZERO)

api_int_t
api_shm_alloc(api_shm_t *shm)
{
    api_fd_t  fd;
	
    fd = open("/dev/zero", O_RDWR);

    if (fd == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "open(\"/dev/zero\") failed");
        return API_ERROR;
    }

    shm->addr = (u_char *) mmap(NULL, shm->size, PROT_READ|PROT_WRITE,
                                MAP_SHARED, fd, 0);

    if (shm->addr == MAP_FAILED) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "mmap(/dev/zero, MAP_SHARED, %uz) failed", shm->size);
    }

    if (close(fd) == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "close(\"/dev/zero\") failed");
    }

    return (shm->addr == MAP_FAILED) ? API_ERROR : API_SUCCESS;
}


void
api_shm_free(api_shm_t *shm)
{
    if (munmap((void *) shm->addr, shm->size) == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "munmap(%p, %uz) failed", shm->addr, shm->size);
    }
}

#elif (API_HAVE_SHMEM_SHMGET)

#include <sys/ipc.h>
#include <sys/shm.h>


api_int_t
api_shm_alloc(api_shm_t *shm)
{
    int  id;

    id = shmget(IPC_PRIVATE, shm->size, (SHM_R|SHM_W|IPC_CREAT));

    if (id == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
                      "shmget(%uz) failed", shm->size);
        return API_ERROR;
    }

    //api_log_debug1(API_LOG_DEBUG_CORE, shm->log, 0, "shmget id: %d", id);

    shm->addr = shmat(id, NULL, 0);
	
    if (shm->addr == (void *) -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno, "shmat() failed");
    }

    if (shmctl(id, IPC_RMID, NULL) == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "shmctl(IPC_RMID) failed");
    }

    return (shm->addr == (void *) -1) ? API_ERROR : API_SUCCESS;
}


void
api_shm_free(api_shm_t *shm)
{
    if (shmdt(shm->addr) == -1) {
        //api_log_error(API_LOG_ALERT, shm->log, api_errno,
        //              "shmdt(%p) failed", shm->addr);
    }
}

#endif
