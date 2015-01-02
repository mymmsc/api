/*
 * Copyright (C) agile6v
 */

#ifndef __API_CONSISTENT_HASH_H_INCLUDED__
#define __API_CONSISTENT_HASH_H_INCLUDED__

#include <api/slab.h>
#include <api/queue.h>
#include <api/rbtree.h>
<<<<<<< HEAD

#ifdef __cplusplus
extern "C" {
#endif

#define API_CONHASH_NAME_SIZE   64

typedef struct conhash_vnode_s api_conhash_vnode_t;

typedef uint32_t (*api_conhash_hashfunc_pt) (u_char *data, size_t len);
typedef void (*api_conhash_oper_pt) (api_conhash_vnode_t *, void *);

typedef struct {
    api_conhash_hashfunc_pt     hash_func;
    void                       *data;
} api_conhash_ctx_t;

typedef struct {
    api_rbtree_t                vnode_tree;
    api_rbtree_node_t           vnode_sentinel;
    api_queue_t                 hnode_queue;
    api_uint_t                  vnodes;
} api_conhash_sh_t;

typedef struct {
    api_conhash_sh_t           *sh;
    api_slab_pool_t            *shpool;
    api_conhash_hashfunc_pt     hash_func;
    api_shm_zone_t             *shm_zone;
    api_uint_t                  vnodecnt;
} api_conhash_t;

typedef struct {
    api_str_t               name;
    api_queue_t             queue;
    void                   *data;
} api_conhash_node_t;

struct conhash_vnode_s {
    api_rbtree_node_t       node;
    api_conhash_node_t     *hnode;
    api_str_t               name;
};

api_int_t api_conhash_node_traverse(api_conhash_t *conhash, api_conhash_oper_pt func, void *data);
api_int_t api_conhash_add_node(api_conhash_t *conhash, u_char *name, size_t len, void *data);
api_int_t api_conhash_del_node(api_conhash_t *conhash, u_char *name, size_t len);
api_int_t api_conhash_lookup_node(api_conhash_t *conhash, u_char *name, size_t len,
    api_conhash_oper_pt func, void *data);
char *api_conhash_shm_set_slot(api_conf_t *cf, api_command_t *cmd, void *conf);
void api_conhash_clear(api_conhash_t *conhash);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_CONSISTENT_HASH_H_INCLUDED__ */