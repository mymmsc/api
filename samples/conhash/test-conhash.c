#include <string.h>
#include <stdio.h>

#define MAX_LINE	1024
#include "api/conhash.h"
#include "api/log.h"

#include "api/vector.h"

static void server_get (api_conhash_vnode_t *vnode, void *data)
{
	char name[vnode->hnode->name.len + 1];
	memset(name, 0x00, sizeof(name));
	api_snprintf(name, sizeof(name), "%V", &vnode->hnode->name);
	printf("node = [%s]\n", name);
}

static int str_sort(const void *s1, const void *s2)
{
	return strcmp(*(char **)s1, *(char **)s2);
}

int main(int argc, char* argv[])
{
	api_init();
	int rc = 0;
	api_conhash_t *conhash = calloc(1, sizeof(api_conhash_t));
	rc = api_conhash_init(conhash, API_SIZE_FROM_MB(32), 100);
	do_assert(rc == API_SUCCESS);
	int index = 0;
	char serverId[100];
	for(index = 0; index < 3; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "10.1.15.1%d", index);
		printf(">>>>>>>>>>>>>>>>>>>>>>>>>>> %s\n", serverId);
		api_conhash_add_node(conhash, serverId, api_strlen(serverId), NULL);
	}

	
	//char serverId[100];
	api_vector_t *slist = api_vector_new(1);
	char *str = NULL;
	for(index = 0; index < 10; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "cookie:%d", 9 - index);
		str = strdup(serverId);
		api_vector_push(slist, str);
		printf("cookie: [%s]\t", serverId);
		api_conhash_lookup_node(conhash, serverId, api_strlen(serverId), server_get, NULL);
	}
	printf("----------------------------------------------------------------\n");
	api_vector_sort(slist, (int (*) (const void *, const void *)) str_sort);
	api_vector_remove(slist, 0);
	for(index = 0; index < api_vector_size(slist); index++)
	{
		str = api_vector_get(slist, index);
		printf("cookie: [%s]\n", str);
	}
	printf("----------------------------------------------------------------\n");
	for(index = 0; index < 1; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "10.1.15.1%d", index + 2);
		api_conhash_del_node(conhash, serverId, api_strlen(serverId));
	}
	printf("----------------------------------------------------------------\n");
	for(index = 0; index < 10; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "cookie:%d", index);
		printf("cookie: [%s]\t", serverId);
		api_conhash_lookup_node(conhash, serverId, api_strlen(serverId), server_get, NULL);
	}
	printf("----------------------------------------------------------------\n");
	api_safefree(conhash);
	return 0;
}

