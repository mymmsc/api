#ifndef __API_FILE_H_INCLUDED__
#define __API_FILE_H_INCLUDED__

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

API status_t api_dir_exist(const char *dname);
API status_t api_mkdirs(const char *pathname);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_FILE_H_INCLUDED__ */

