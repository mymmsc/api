#ifndef API_TRIE_H
#define API_TRIE_H

#include "api.h"

#define API_TRIE_WIDTH (256)
static const int API_TRIE_SIZE = API_TRIE_WIDTH;

#ifdef __cplusplus
extern "C" {
#endif

typedef union trie_t {
    void *sentinel;
    union trie_t *chars[API_TRIE_WIDTH];
} trie_t;

API trie_t *trie_init(void);
API void trie_add(trie_t *, char *);
API int trie_exists(trie_t *, char *);
API int trie_load(trie_t *, char *);
API void trie_strip(trie_t *, char *, char *);
API void trie_free(trie_t *);

#define trie_step(t,c) (t = (t == NULL || t->chars[c] == NULL ? NULL : t->chars[c]))
#define trie_word(t) (t != NULL && t->sentinel != NULL)

#ifdef __cplusplus
}
#endif

#endif /* ! API_TRIE_H */

