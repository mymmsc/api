#include "api/lib.h"

pid_t    api_pid = -1;
uint32_t api_ncpu = 0;
uint32_t api_pagesize;
uint32_t api_pagesize_shift;
uint32_t api_cacheline_size;

#if (( __i386__ || __amd64__ ) && ( __GNUC__ || __INTEL_COMPILER ))

static API_INLINE void api_cpuid(uint32_t i, uint32_t *buf);

#if ( __i386__ )

static API_INLINE void
api_cpuid(uint32_t i, uint32_t *buf)
{

    /*
     * we could not use %ebx as output parameter if gcc builds PIC,
     * and we could not save %ebx on stack, because %esp is used,
     * when the -fomit-frame-pointer optimization is specified.
     */

    __asm__ (

    "    mov    %%ebx, %%esi;  "

    "    cpuid;                "
    "    mov    %%eax, (%1);   "
    "    mov    %%ebx, 4(%1);  "
    "    mov    %%edx, 8(%1);  "
    "    mov    %%ecx, 12(%1); "

    "    mov    %%esi, %%ebx;  "

    : : "a" (i), "D" (buf) : "ecx", "edx", "esi", "memory" );
}


#else /* __amd64__ */


static API_INLINE void
api_cpuid(uint32_t i, uint32_t *buf)
{
    uint32_t eax = 0, ebx = 0, ecx = 0, edx = 0;
    __asm__ (

        "cpuid"

    : "=a" (eax), "=b" (ebx), "=c" (ecx), "=d" (edx) : "a" (i) );

    buf[0] = eax;
    buf[1] = ebx;
    buf[2] = edx;
    buf[3] = ecx;
}


#endif


/* auto detect the L2 cache line size of modern and widespread CPUs */

void
api_cpuinfo(void)
{
    uint8_t *vendor;
    uint32_t vbuf[5], cpu[4], model;
	
    vbuf[0] = 0;
    vbuf[1] = 0;
    vbuf[2] = 0;
    vbuf[3] = 0;
    vbuf[4] = 0;
	
    api_cpuid(0, vbuf);
	
    vendor = (uint8_t *) &vbuf[1];
	
    if (vbuf[0] == 0) {
        return;
    }
	
    api_cpuid(1, cpu);
	
    if (strcmp(vendor, "GenuineIntel") == 0) {
        switch ((cpu[0] & 0xf00) >> 8) {
        /* Pentium */
        case 5:
            api_cacheline_size = 32;
            break;
        /* Pentium Pro, II, III */
        case 6:
            api_cacheline_size = 32;
            model = ((cpu[0] & 0xf0000) >> 8) | (cpu[0] & 0xf0);
            if (model >= 0xd0) {
                /* Intel Core, Core 2, Atom */
                api_cacheline_size = 64;
            }
            break;
        /*
         * Pentium 4, although its cache line size is 64 bytes,
         * it prefetches up to two cache lines during memory read
         */
        case 15:
            api_cacheline_size = 128;
            break;
        }
    } else if (strcmp(vendor, "AuthenticAMD") == 0) {
        api_cacheline_size = 64;
    }
}

#else

void
api_cpuinfo(void)
{
	//
}

#endif

//////////////////////////////////////////////////////////////////////////////////////////

#ifdef API_SOLARIS2
#include <kstat.h>
#endif

#if API_HAVE_FCNTL_H
#include <fcntl.h>
#endif

#if API_HAVE_UNISTD_H
#include <unistd.h>
#endif

#if API_HAVE_CTYPE_H
#include <ctype.h>
#endif
//////////////////////////////////////////////////////////////////////////////////////////

#if defined(API_FREEBSD)
typedef unsigned int u_int;
#include <sys/cdefs.h>
#include <sys/sysctl.h>
#include <sys/procfs.h>
#include <sys/dkstat.h>
#endif

#if defined(API_UNIX)
#include <sys/sched.h>
#endif

//////////////////////////////////////////////////////////////////////////////////////////

#if defined(API_LINUX)
uint32_t api_cpu_getnumber(void)
{
    int nNumProc = sysconf(_SC_NPROCESSORS_ONLN);
    if (nNumProc < 1)
    {
        nNumProc = 1;
    }
    
    return nNumProc;
}

//////////////////////////////////////////////////////////////////////////////////////////
#elif defined(API_FREEBSD) || defined(API_APPLE)
uint32_t api_cpu_getnumber(void)
{
    int mib[2];
    int nNumProc;
    size_t nLen;

    mib[0] = CTL_HW;
    mib[1] = HW_NCPU;
    nLen = sizeof(nNumProc);
    sysctl(mib, 2, &nNumProc, &nLen, NULL, 0);

    if (nNumProc < 1)
    {
        nNumProc = 1;
    }
    return nNumProc;
}

//////////////////////////////////////////////////////////////////////////////////////////
#elif defined(API_HPUX)
#include <sys/pstat.h>
#include <sys/dk.h>

uint32_t api_cpu_getnumber(void)
{
    struct pst_dynamic pst;
    int numproc = 1;

    if (pstat_getdynamic(&pst, sizeof(pst), (size_t)1, 0) != -1)
    {
        numproc = pst.psd_proc_cnt;
    }
    return numproc;
}

//////////////////////////////////////////////////////////////////////////////////////////
#elif defined(API_AIX)
#ifdef _DEBUG
#undef _DEBUG
#endif
#include <procinfo.h>
#include <sys/types.h>
#include <sys/trcctl.h>
#include <nlist.h>
#include <sys/sysinfo.h>

#define KMEM "/dev/kmem"

uint32_t api_cpu_getnumber(void)
{
    return __TRC_SYSCPUS;
}

//////////////////////////////////////////////////////////////////////////////////////////
#elif defined(API_SOLARIS2)
#include <sys/fcntl.h>
#include <sys/sysinfo.h>

uint32_t api_cpu_getnumber(void)
{
    int nNumProc = sysconf(_SC_NPROCESSORS_ONLN);
    if (nNumProc < 1)
    {
        nNumProc = 1;
    }
    
    return nNumProc;
}

//////////////////////////////////////////////////////////////////////////////////////////
#elif defined(API_WIN64) || defined(API_WIN32) || defined(API_WIN32_WCE)
uint32_t api_cpu_getnumber(void)
{
    SYSTEM_INFO info;
    
    GetSystemInfo(&info);
    
    return info.dwNumberOfProcessors;
}
#endif

//////////////////////////////////////////////////////////////////////////////////////////

void api_init(void)
{
	if(api_ncpu < 1) {
		return;
	}
	uint32_t n = 0;
	api_ncpu = api_cpu_getnumber();
	
	if (api_ncpu < 1) {
		api_ncpu = 1;
	}
	api_pagesize = getpagesize();
	
	for (n = api_pagesize; n >>= 1; api_pagesize_shift++) { /* void */ }
	api_cpuinfo();

	api_pid = api_getpid();
	
	fprintf(stdout, "cpu number:%d pagesize:%d pagezie_shift:%d cacheline_size:%d\n", 
		api_ncpu, api_pagesize, api_pagesize_shift, api_cacheline_size);
}
