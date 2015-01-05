#include "api/vector.h"
#include "api/memory.h"
#include "api/log.h"

struct __vector_struct {
	int    length;
	int    population;
	void **storage;
};

api_vector_t * api_vector_new(int size) {
	api_vector_t *vector = api_mallocz(sizeof(api_vector_t));
	
	vector->length = size;
	vector->population = 0;
	vector->storage = api_calloc(size, sizeof(void *));
	
	return vector;
}

void api_vector_destroy(api_vector_t *vector) {
	api_safefree(vector->storage);
	api_safefree(vector);
}

int api_vector_size(api_vector_t *vector)
{
	return vector->population;
}

int api_vector_length(api_vector_t *vector)
{
	return vector->length;
}

void * api_vector_get(api_vector_t *vector, int index) {
	if (index >= vector->population || index < 0) {
		return NULL;
	}
	
  	return vector->storage[index];
}

void * api_vector_set(api_vector_t * vector, int index, void *elem) {
	void * old_elem = NULL;
	if (index < vector->population && index >= 0) {
		old_elem = vector->storage[index];
		vector->storage[index] = elem;
	}
	
	return old_elem;
}

void api_vector_remove(api_vector_t *vector, int index)
{
	if (index < vector->population && index >= 0) {
		// step 1
		if(index + 1 < vector->population) {
			memmove(vector->storage + index, vector->storage + index + 1, sizeof(void *) * (vector->population - index - 1));
		}
		vector_pop(vector);
	}
}

void api_vector_push(api_vector_t *vector, void * elem) {
	vector->storage[vector->population++] = elem;
	vector_resize(vector);
}

void * api_vector_pop(api_vector_t *vector) {
	if (vector->population == 0) {
		return NULL;
	}
	
	void * elem = vector->storage[--vector->population];
	vector_resize(vector);
	return elem;
}

void api_vector_resize(api_vector_t *vector) {
	void *new_storage = NULL;
	int new_length = 0;
	
	if (vector->population == vector->length) {
		new_length = vector->length * 2;
		new_storage = realloc(vector->storage, sizeof(void *) * vector->length * 2);
	} else if ((float)vector->population/(float)vector->length < 0.25) {
		new_length = vector->length / 2;
		new_storage = realloc(vector->storage, sizeof(void *) * vector->length / 2);
	} else {
		return;
	}
	
	/* not sure if this is morally correct. On one hand, the system has no more
	 memory to give. On the other, such a trivial error should not bring down
	 the program. Comments? */
	if (new_storage == NULL) {
		vector_destroy(vector);
		//fprintf(stderr, "could not allocate more memory for vector. Sorry\n");
		do_assert(new_storage != NULL);
	} else {
		vector->storage = new_storage;
		vector->length = new_length;
	}
}

void api_vector_sort(api_vector_t *vector, int (*cmp)(const void *, const void *))
{
	qsort(vector->storage, vector->population, sizeof(void *), cmp);
}

