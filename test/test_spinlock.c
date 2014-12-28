#include"spinlock.h"

// gcc -Wall -g -O3 -o myspinlock.out myspinlock.c -lpthread


/////////////////////////                test  

spinlock_t lock =  MY_SPINLOCK_INITIALIZER;
volatile int cnt = 0;

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#define  TOTAL 1000000 * 20
int NR;
int DELAY_CNT = 100;

static uint32_t bar = 13; 
static uint32_t *foo = &bar;

void * fun1(void * arg)
{
        int i = 0, id = *(int*)arg;
        printf("thread:%d\n",id);
        for(; i < NR; i++)
        {
                spinlock_lock(&lock);
                cnt++;
                int j = 0;
                for (; j < DELAY_CNT; j++) {
                        *foo = (*foo * 33) + 17;
                }
                spinlock_unlock(&lock);
        }
        printf("thread:%d over, lock:%d\n",id, lock);
        return 0;
}

pthread_mutex_t mlock = PTHREAD_MUTEX_INITIALIZER;

void * fun2(void * arg)
{
        int i = 0, id = *(int*)arg;
        printf("thread:%d\n",id);
        for(; i < NR; i++)
        {
                pthread_mutex_lock(&mlock);
                cnt++;
                int j = 0;
                for (; j < DELAY_CNT; j++) {
                        *foo = (*foo * 33) + 17;
                }
                pthread_mutex_unlock(&mlock);
        }
        printf("thread:%d over, lock:%d\n",id, lock);
        return 0;
}

pthread_spinlock_t splock;

void * fun3(void * arg)
{
        int i = 0, id = *(int*)arg;
        printf("thread:%d\n",id);
        for(; i < NR; i++)
        {
                pthread_spin_lock(&splock);
                cnt++;
                int j = 0;
                for (; j < DELAY_CNT; j++) {
                        *foo = (*foo * 33) + 17;
                }
                pthread_spin_unlock(&splock);
        }
        printf("thread:%d over, lock:%d\n",id, lock);
        return 0;
}

int N = 20;
int main(int c, char * s[])
{
        int which = 0;
        if(c > 1)
        {
                //线程数
                N = atoi(s[1]);
                if(N > 20 || N <= 1) N = 10;
        }
        if(c > 2)
        {
                //which func?
                which = atoi(s[2]);
                if(which > 2 || which < 0) which = 0;
        }
        if(c > 3)
        {
                //delay param
                DELAY_CNT = atoi(s[3]);
                if(DELAY_CNT > 10000 || DELAY_CNT < 0) DELAY_CNT= 100;
        }

        pthread_t id[N];
        int args[N];
        int i = 0;
        void * (*fun[])(void*) = { fun1,fun2,fun3};
        pthread_spin_init(&splock,0);
        NR = TOTAL / N;

        for(;i<N;++i){
                args[i] = i;
                pthread_create(&id[i],NULL,fun[which],&args[i]);
        }

        for(i=0;i<N;++i){
                printf("join thread:%d\n", i);
                pthread_join(id[i],NULL);
                printf("join thread:%d done\n", i);
        }

        printf("cnt = %d, should be %d\n",cnt, N * NR);
        return 0;
}

