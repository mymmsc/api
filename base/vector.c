#include "api/vector.h"
#include "api/memory.h"
#include "api/log.h"

struct __vector_struct {
	int    length;
	int    population;
	void **storage;
};

vector_t * vector_new(int size) {
	vector_t *vector = api_mallocz(sizeof(vector_t));
	
	vector->length = size;
	vector->population = 0;
	vector->storage = api_calloc(size, sizeof(void *));
	
	return vector;
}

void vector_destroy(vector_t *vector) {
	api_safefree(vector->storage);
	api_safefree(vector);
}

int vector_size(vector_t *vector)
{
	return vector->population;
}

int vector_length(vector_t *vector)
{
	return vector->length;
}

void * vector_get(vector_t *vector, int index) {
	if (index >= vector->population || index < 0) {
		return NULL;
	}
	
  	return vector->storage[index];
}

void * vector_set(vector_t * vector, int index, void *elem) {
	void * old_elem = NULL;
	if (index < vector->population && index >= 0) {
		old_elem = vector->storage[index];
		vector->storage[index] = elem;
	}
	
	return old_elem;
}

void vector_push(vector_t *vector, void * elem) {
	vector->storage[vector->population++] = elem;
	vector_resize(vector);
}

void * vector_pop(vector_t *vector) {
	if (vector->population == 0) {
		return NULL;
	}
	
	void * elem = vector->storage[--vector->population];
	vector_resize(vector);
	return elem;
}

void vector_resize(vector_t *vector) {
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
