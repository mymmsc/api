#ifndef __API_QUEUE_H_INCLUDED__
#define __API_QUEUE_H_INCLUDED__

typedef struct api_queue_s  api_queue_t;

struct api_queue_s {
    api_queue_t  *prev;
    api_queue_t  *next;
};


#define api_queue_init(q)                                                     \
    (q)->prev = q;                                                            \
    (q)->next = q


#define api_queue_empty(h)                                                    \
    (h == (h)->prev)


#define api_queue_insert_head(h, x)                                           \
    (x)->next = (h)->next;                                                    \
    (x)->next->prev = x;                                                      \
    (x)->prev = h;                                                            \
    (h)->next = x


#define api_queue_insert_after   api_queue_insert_head


#define api_queue_insert_tail(h, x)                                           \
    (x)->prev = (h)->prev;                                                    \
    (x)->prev->next = x;                                                      \
    (x)->next = h;                                                            \
    (h)->prev = x


#define api_queue_head(h)                                                     \
    (h)->next


#define api_queue_last(h)                                                     \
    (h)->prev


#define api_queue_sentinel(h)                                                 \
    (h)


#define api_queue_next(q)                                                     \
    (q)->next


#define api_queue_prev(q)                                                     \
    (q)->prev


#define api_queue_remove(x)                                                   \
    (x)->next->prev = (x)->prev;                                              \
    (x)->prev->next = (x)->next


#define api_queue_split(h, q, n)                                              \
    (n)->prev = (h)->prev;                                                    \
    (n)->prev->next = n;                                                      \
    (n)->next = q;                                                            \
    (h)->prev = (q)->prev;                                                    \
    (h)->prev->next = h;                                                      \
    (q)->prev = n;


#define api_queue_add(h, n)                                                   \
    (h)->prev->next = (n)->next;                                              \
    (n)->next->prev = (h)->prev;                                              \
    (h)->prev = (n)->prev;                                                    \
    (h)->prev->next = h;


#define api_queue_data(q, type, link)                                         \
    (type *) ((u_char *) q - offsetof(type, link))


API api_queue_t *api_queue_middle(api_queue_t *queue);
API void api_queue_sort(api_queue_t *queue,
    api_int_t (*cmp)(const api_queue_t *, const api_queue_t *));


#endif /* ! __API_QUEUE_H_INCLUDED_ */
