#ifndef __API_DBCP_H_INCLUDED__
#define __API_DBCP_H_INCLUDED__

#include <api/memory.h>

#ifdef __cplusplus
extern "C" {
#endif

API status_t api_dbcp_init(api_pool_t *pool);
API int64_t api_dbcp_exec_int(api_str_t *host, const char *command, const char *key);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_DBCP_H_INCLUDED__ */

