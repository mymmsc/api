#ifndef API_VECTOR_H
#define API_VECTOR_H

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __vector_struct vector_t;

API vector_t * vector_new(int size);
API void vector_destroy(vector_t *vector);
API int vector_size(vector_t *vector);
API int vector_length(vector_t *vector);
API void * vector_get(vector_t *vector, int index);
API void * vector_set(vector_t *vector, int index, void *elem);
API void   vector_push(vector_t *vector, void *elem);
API void * vector_pop(vector_t *vector);
API void vector_resize(vector_t *vector);

#ifdef __cplusplus
}
#endif

#endif /* ! API_VECTOR_H */
