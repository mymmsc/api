#include<stdint.h>
#include<unistd.h>

typedef volatile uint32_t spinlock_t;

#define MY_SPINLOCK_INITIALIZER 0

#define do_hash(a) do{  \
(a) = ((a)+0x7ed55d16) + ((a)<<12); \
(a) = ((a)^0xc761c23c) ^ ((a)>>19); \
(a) = ((a)+0x165667b1) + ((a)<<5);  \
(a) = ((a)+0xd3a2646c) ^ ((a)<<9);  \
(a) = ((a)+0xfd7046c5) + ((a)<<3);  \
(a) = ((a)^0xb55a4f09) ^ ((a)>>16); \
}while(0)

#define my_spinlock_lock(lock) do{  \
    while(!__sync_bool_compare_and_swap(lock, 0, 1))    \
    {   \
        while(*lock)    \
        {   \
            do_hash(*foo);  \
            if((*foo % 11) == 1)    \
                sched_yield();  \
        }   \
    }   \
}while(0)

#define my_spinlock_unlock(lock) do{    \
    *lock = 0;  \
}while(0)

#define spinlock_lock(lock) do{  \
    while(!__sync_bool_compare_and_swap(lock, 0, 1))    \
        sched_yield();  \
}while(0)

#define spinlock_unlock(lock) do{    \
    *lock = 0;  \
}while(0)

