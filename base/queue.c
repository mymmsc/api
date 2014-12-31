#include <api/queue.h>

/*
 * find the middle queue element if the queue has odd number of elements
 * or the first element of the queue's second part otherwise
 */

api_queue_t *
api_queue_middle(api_queue_t *queue)
{
    api_queue_t  *middle, *next;

    middle = api_queue_head(queue);

    if (middle == api_queue_last(queue)) {
        return middle;
    }

    next = api_queue_head(queue);

    for ( ;; ) {
        middle = api_queue_next(middle);

        next = api_queue_next(next);

        if (next == api_queue_last(queue)) {
            return middle;
        }

        next = api_queue_next(next);

        if (next == api_queue_last(queue)) {
            return middle;
        }
    }
}


/* the stable insertion sort */

void
api_queue_sort(api_queue_t *queue,
    api_int_t (*cmp)(const api_queue_t *, const api_queue_t *))
{
    api_queue_t  *q, *prev, *next;

    q = api_queue_head(queue);

    if (q == api_queue_last(queue)) {
        return;
    }

    for (q = api_queue_next(q); q != api_queue_sentinel(queue); q = next) {

        prev = api_queue_prev(q);
        next = api_queue_next(q);

        api_queue_remove(q);

        do {
            if (cmp(prev, q) <= 0) {
                break;
            }

            prev = api_queue_prev(prev);

        } while (prev != api_queue_sentinel(queue));

        api_queue_insert_after(prev, q);
    }
}
