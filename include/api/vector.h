#ifndef __API_VECTOR_H_INCLUDED__
#define __API_VECTOR_H_INCLUDED__

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __vector_struct api_vector_t;

API api_vector_t * api_vector_new(int size);
API void api_vector_destroy(vector_t *vector);
API int api_vector_size(vector_t *vector);
API int api_vector_length(vector_t *vector);
API void * api_vector_get(vector_t *vector, int index);
API void * api_vector_set(vector_t *vector, int index, void *elem);
API void   api_vector_push(vector_t *vector, void *elem);
API void * api_vector_pop(vector_t *vector);
API void api_vector_resize(vector_t *vector);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_VECTOR_H_INCLUDED__ */
