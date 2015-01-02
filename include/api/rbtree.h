#ifndef _API_RBTREE_H_INCLUDED_
#define _API_RBTREE_H_INCLUDED_

#include <api.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef api_uint_t  api_rbtree_key_t;
typedef api_int_t   api_rbtree_key_int_t;


typedef struct api_rbtree_node_s  api_rbtree_node_t;

struct api_rbtree_node_s {
    api_rbtree_key_t       key;
    api_rbtree_node_t     *left;
    api_rbtree_node_t     *right;
    api_rbtree_node_t     *parent;
    u_char                 color;
    u_char                 data;
};


typedef struct api_rbtree_s  api_rbtree_t;

typedef void (*api_rbtree_insert_pt) (api_rbtree_node_t *root,
    api_rbtree_node_t *node, api_rbtree_node_t *sentinel);

struct api_rbtree_s {
    api_rbtree_node_t     *root;
    api_rbtree_node_t     *sentinel;
    api_rbtree_insert_pt   insert;
};


#define api_rbtree_init(tree, s, i)                                           \
    api_rbtree_sentinel_init(s);                                              \
    (tree)->root = s;                                                         \
    (tree)->sentinel = s;                                                     \
    (tree)->insert = i


void api_rbtree_insert(api_rbtree_t *tree, api_rbtree_node_t *node);
void api_rbtree_delete(api_rbtree_t *tree, api_rbtree_node_t *node);
void api_rbtree_insert_value(api_rbtree_node_t *root, api_rbtree_node_t *node,
    api_rbtree_node_t *sentinel);
void api_rbtree_insert_timer_value(api_rbtree_node_t *root,
    api_rbtree_node_t *node, api_rbtree_node_t *sentinel);


#define api_rbt_red(node)               ((node)->color = 1)
#define api_rbt_black(node)             ((node)->color = 0)
#define api_rbt_is_red(node)            ((node)->color)
#define api_rbt_is_black(node)          (!api_rbt_is_red(node))
#define api_rbt_copy_color(n1, n2)      (n1->color = n2->color)


/* a sentinel must be black */

#define api_rbtree_sentinel_init(node)  api_rbt_black(node)


static API_INLINE api_rbtree_node_t *
api_rbtree_min(api_rbtree_node_t *node, api_rbtree_node_t *sentinel)
{
    while (node->left != sentinel) {
        node = node->left;
    }

    return node;
}

#ifdef __cplusplus
}
#endif

#endif /* ! __API_RBTREE_H_INCLUDED__ */
