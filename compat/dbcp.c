#include "api/dbcp.h"
#include "api/hashtable.h"
#include "hiredis/hiredis.h"
#include "api/log.h"

static api_hashtable_t g_servers;

static void
dbcp_cleanup(void *data)
{
	//
}

static redisContext *redis_connect(api_str_t *hostport)
{
	char hp[hostport->len + 1];
	char host[32]={0};
	int port = 0;
	memset(hp, 0x00, sizeof(hp));
	api_snprintf(hp, sizeof(hp), "%V", hostport);
	sscanf(hp, "%[0-9,.]:%d", host, &port);
	redisContext *conn = NULL;
    struct timeval timeout = {0, 10000}; // 0.5 seconds
    conn = redisConnectWithTimeout(host, port, timeout);
    if (conn->err) {
		conn = redisConnectWithTimeout(host, port, timeout);
		if(conn->err)
		{
			do_debug("Connection error: %s,hp: %s", conn->errstr, hp);
			return NULL;
		} else {
			int rc = redisEnableKeepAlive(conn);
			if(rc != REDIS_OK) {
				do_debug("redisEnableKeepAlive error: %s,hp: %s", conn->errstr, hp);
			}
		}
    }
	return conn;
}

status_t api_dbcp_init(api_pool_t *pool)
{
	status_t rc = API_ENOTIMPL;
	api_hashtable_init(&g_servers, HT_KEY_CONST | HT_VALUE_CONST, 0.05);
	api_pool_cleanup_t *cln = api_pool_cleanup_add(pool, 0);
	cln->data = &g_servers;
	cln->handler = dbcp_cleanup;
	
	return rc;
}

static redisReply *redis_exec(api_str_t *host, const char *command, const char *keywords)
{
	redisReply *reply = NULL;
	size_t tsize = 0;
	redisContext *conn = NULL;
	conn = api_hashtable_get(&g_servers, host->data, host->len, &tsize);
	int tries = 2;
	while( tries-- > 0) {
		if(conn == NULL) {
			conn = redis_connect(host);
			if(conn == NULL) {
				break;
			} else {
				api_hashtable_insert(&g_servers, host->data, host->len, conn, sizeof(conn));
			}
		}
		reply = redisCommand(conn, command);
		if(reply)
		{
			do_debug("#%d:%s => [%s], %d", api_getpid(), command, reply->str, reply->type);
			break;
		} else {
			do_debug("#%d:redis-server[%V] connect error.", api_getpid(), &(node->name));
			api_hashtable_remove(&g_servers, host->data, host->len);
			redisFree(conn);
			conn = NULL;
		}
	}
	return reply;
}

int64_t api_dbcp_exec_int(api_str_t *host, const char *command, const char *key)
{
	int64_t iRet = -1;
	redisReply *reply = NULL;
	reply = redis_exec(host, command, key);
	if(reply == NULL) {
		//
	} else if(reply->type == REDIS_REPLY_INTEGER){
		iRet = reply->integer;
		freeReplyObject(reply);
		reply = NULL;
	}
	return iRet;
}

