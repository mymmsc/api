#include "api/atomic.h"
#include "api/core.h"
  
void
api_spinlock(atomic_t *lock, atomic_int_t value, uint32_t spin)
{
	
#if (API_HAVE_ATOMIC_OPS)
    uint32_t  i = 0, n = 0;
    for ( ;; ) {
        if (*lock == 0 && api_atomic_cmp_set(lock, 0, value)) {
            return;
        }
		
        if (api_ncpu > 1) {
            for (n = 1; n < spin; n <<= 1) {
                for (i = 0; i < n; i++) {
                    api_cpu_pause();
                }
				
                if (*lock == 0 && api_atomic_cmp_set(lock, 0, value)) {
                    return;
                }
            }
        }
        api_sched_yield();
    }
#else

#if (API_HAS_THREADS)

#error api_spinlock() or api_atomic_cmp_set() are not defined !

#endif

#endif

}

