#ifndef API_FILE_H
#define API_FILE_H

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

API status_t api_dir_exist(const char *dname);
API status_t api_mkdirs(const char *pathname);

#ifdef __cplusplus
}
#endif

#endif /* ! API_FILE_H */

