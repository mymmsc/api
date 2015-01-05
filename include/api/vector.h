#ifndef __API_VECTOR_H_INCLUDED__
#define __API_VECTOR_H_INCLUDED__

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __vector_struct api_vector_t;

API api_vector_t * api_vector_new(int size);
API void api_vector_destroy(api_vector_t *vector);
API int api_vector_size(api_vector_t *vector);
API int api_vector_length(api_vector_t *vector);
API void * api_vector_get(api_vector_t *vector, int index);
API void * api_vector_set(api_vector_t *vector, int index, void *elem);
API void api_vector_remove(api_vector_t *vector, int index);
API void   api_vector_push(api_vector_t *vector, void *elem);
API void * api_vector_pop(api_vector_t *vector);
API void api_vector_resize(api_vector_t *vector);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_VECTOR_H_INCLUDED__ */
