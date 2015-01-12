#ifndef __API_CORE_H_INCLUDED__
#define __API_CORE_H_INCLUDED__

#include <api.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifndef LF
# define LF     (unsigned char) '\n'
# define CR     (unsigned char) '\r'
# define CRLF   "\r\n"
#endif

extern uint32_t api_pagesize;
extern uint32_t api_pagesize_shift;
extern uint32_t api_cacheline_size;
extern uint32_t api_ncpu;
extern pid_t    api_pid;

API void api_cpuinfo(void);
API uint32_t api_cpu_getnumber(void);

API void api_init(void);

#define api_getpid   getpid

API status_t api_realpath_get(char *path, size_t size);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_CORE_H_INCLUDED__ */
