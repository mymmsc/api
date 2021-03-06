#include <api/rbtree.h>

/*
 * The red-black tree code is based on the algorithm described in
 * the "Introduction to Algorithms" by Cormen, Leiserson and Rivest.
 */


static API_INLINE void api_rbtree_left_rotate(api_rbtree_node_t **root,
    api_rbtree_node_t *sentinel, api_rbtree_node_t *node);
static API_INLINE void api_rbtree_right_rotate(api_rbtree_node_t **root,
    api_rbtree_node_t *sentinel, api_rbtree_node_t *node);


void
api_rbtree_insert(api_rbtree_t *tree, api_rbtree_node_t *node)
{
    api_rbtree_node_t  **root, *temp, *sentinel;

    /* a binary tree insert */

    root = (api_rbtree_node_t **) &tree->root;
    sentinel = tree->sentinel;

    if (*root == sentinel) {
        node->parent = NULL;
        node->left = sentinel;
        node->right = sentinel;
        api_rbt_black(node);
        *root = node;

        return;
    }

    tree->insert(*root, node, sentinel);

    /* re-balance tree */

    while (node != *root && api_rbt_is_red(node->parent)) {

        if (node->parent == node->parent->parent->left) {
            temp = node->parent->parent->right;

            if (api_rbt_is_red(temp)) {
                api_rbt_black(node->parent);
                api_rbt_black(temp);
                api_rbt_red(node->parent->parent);
                node = node->parent->parent;

            } else {
                if (node == node->parent->right) {
                    node = node->parent;
                    api_rbtree_left_rotate(root, sentinel, node);
                }

                api_rbt_black(node->parent);
                api_rbt_red(node->parent->parent);
                api_rbtree_right_rotate(root, sentinel, node->parent->parent);
            }

        } else {
            temp = node->parent->parent->left;

            if (api_rbt_is_red(temp)) {
                api_rbt_black(node->parent);
                api_rbt_black(temp);
                api_rbt_red(node->parent->parent);
                node = node->parent->parent;

            } else {
                if (node == node->parent->left) {
                    node = node->parent;
                    api_rbtree_right_rotate(root, sentinel, node);
                }

                api_rbt_black(node->parent);
                api_rbt_red(node->parent->parent);
                api_rbtree_left_rotate(root, sentinel, node->parent->parent);
            }
        }
    }

    api_rbt_black(*root);
}


void
api_rbtree_insert_value(api_rbtree_node_t *temp, api_rbtree_node_t *node,
    api_rbtree_node_t *sentinel)
{
    api_rbtree_node_t  **p;

    for ( ;; ) {

        p = (node->key < temp->key) ? &temp->left : &temp->right;

        if (*p == sentinel) {
            break;
        }

        temp = *p;
    }

    *p = node;
    node->parent = temp;
    node->left = sentinel;
    node->right = sentinel;
    api_rbt_red(node);
}


void
api_rbtree_insert_timer_value(api_rbtree_node_t *temp, api_rbtree_node_t *node,
    api_rbtree_node_t *sentinel)
{
    api_rbtree_node_t  **p;

    for ( ;; ) {

        /*
         * Timer values
         * 1) are spread in small range, usually several minutes,
         * 2) and overflow each 49 days, if milliseconds are stored in 32 bits.
         * The comparison takes into account that overflow.
         */

        /*  node->key < temp->key */

        p = ((api_rbtree_key_int_t) (node->key - temp->key) < 0)
            ? &temp->left : &temp->right;

        if (*p == sentinel) {
            break;
        }

        temp = *p;
    }

    *p = node;
    node->parent = temp;
    node->left = sentinel;
    node->right = sentinel;
    api_rbt_red(node);
}


void
api_rbtree_delete(api_rbtree_t *tree, api_rbtree_node_t *node)
{
    api_uint_t           red;
    api_rbtree_node_t  **root, *sentinel, *subst, *temp, *w;

    /* a binary tree delete */

    root = (api_rbtree_node_t **) &tree->root;
    sentinel = tree->sentinel;

    if (node->left == sentinel) {
        temp = node->right;
        subst = node;

    } else if (node->right == sentinel) {
        temp = node->left;
        subst = node;

    } else {
        subst = api_rbtree_min(node->right, sentinel);

        if (subst->left != sentinel) {
            temp = subst->left;
        } else {
            temp = subst->right;
        }
    }

    if (subst == *root) {
        *root = temp;
        api_rbt_black(temp);

        /* DEBUG stuff */
        node->left = NULL;
        node->right = NULL;
        node->parent = NULL;
        node->key = 0;

        return;
    }

    red = api_rbt_is_red(subst);

    if (subst == subst->parent->left) {
        subst->parent->left = temp;

    } else {
        subst->parent->right = temp;
    }

    if (subst == node) {

        temp->parent = subst->parent;

    } else {

        if (subst->parent == node) {
            temp->parent = subst;

        } else {
            temp->parent = subst->parent;
        }

        subst->left = node->left;
        subst->right = node->right;
        subst->parent = node->parent;
        api_rbt_copy_color(subst, node);

        if (node == *root) {
            *root = subst;

        } else {
            if (node == node->parent->left) {
                node->parent->left = subst;
            } else {
                node->parent->right = subst;
            }
        }

        if (subst->left != sentinel) {
            subst->left->parent = subst;
        }

        if (subst->right != sentinel) {
            subst->right->parent = subst;
        }
    }

    /* DEBUG stuff */
    node->left = NULL;
    node->right = NULL;
    node->parent = NULL;
    node->key = 0;

    if (red) {
        return;
    }

    /* a delete fixup */

    while (temp != *root && api_rbt_is_black(temp)) {

        if (temp == temp->parent->left) {
            w = temp->parent->right;

            if (api_rbt_is_red(w)) {
                api_rbt_black(w);
                api_rbt_red(temp->parent);
                api_rbtree_left_rotate(root, sentinel, temp->parent);
                w = temp->parent->right;
            }
            if (api_rbt_is_black(w->left) && api_rbt_is_black(w->right)) {
                api_rbt_red(w);
                temp = temp->parent;
            } else {
                if (api_rbt_is_black(w->right)) {
                    api_rbt_black(w->left);
                    api_rbt_red(w);
                    api_rbtree_right_rotate(root, sentinel, w);
                    w = temp->parent->right;
                }

                api_rbt_copy_color(w, temp->parent);
                api_rbt_black(temp->parent);
                api_rbt_black(w->right);
                api_rbtree_left_rotate(root, sentinel, temp->parent);
                temp = *root;
            }
        } else {
            w = temp->parent->left;

            if (api_rbt_is_red(w)) {
                api_rbt_black(w);
                api_rbt_red(temp->parent);
                api_rbtree_right_rotate(root, sentinel, temp->parent);
                w = temp->parent->left;
            }

            if (api_rbt_is_black(w->left) && api_rbt_is_black(w->right)) {
                api_rbt_red(w);
                temp = temp->parent;

            } else {
                if (api_rbt_is_black(w->left)) {
                    api_rbt_black(w->right);
                    api_rbt_red(w);
                    api_rbtree_left_rotate(root, sentinel, w);
                    w = temp->parent->left;
                }

                api_rbt_copy_color(w, temp->parent);
                api_rbt_black(temp->parent);
                api_rbt_black(w->left);
                api_rbtree_right_rotate(root, sentinel, temp->parent);
                temp = *root;
            }
        }
    }

    api_rbt_black(temp);
}


static API_INLINE void
api_rbtree_left_rotate(api_rbtree_node_t **root, api_rbtree_node_t *sentinel,
    api_rbtree_node_t *node)
{
    api_rbtree_node_t  *temp;

    temp = node->right;
    node->right = temp->left;

    if (temp->left != sentinel) {
        temp->left->parent = node;
    }

    temp->parent = node->parent;

    if (node == *root) {
        *root = temp;

    } else if (node == node->parent->left) {
        node->parent->left = temp;

    } else {
        node->parent->right = temp;
    }

    temp->left = node;
    node->parent = temp;
}


static API_INLINE void
api_rbtree_right_rotate(api_rbtree_node_t **root, api_rbtree_node_t *sentinel,
    api_rbtree_node_t *node)
{
    api_rbtree_node_t  *temp;

    temp = node->left;
    node->left = temp->right;

    if (temp->right != sentinel) {
        temp->right->parent = node;
    }

    temp->parent = node->parent;

    if (node == *root) {
        *root = temp;

    } else if (node == node->parent->right) {
        node->parent->right = temp;

    } else {
        node->parent->left = temp;
    }

    temp->right = node;
    node->parent = temp;
}
