#include <string.h>
#include <stdio.h>

#define MAX_LINE	1024
#include "api/conhash.h"

static void server_get (api_conhash_vnode_t *vnode, void *data)
{
	char name[vnode->hnode->name.len + 1];
	memset(name, 0x00, sizeof(name));
	api_snprintf(name, sizeof(name), "%V", vnode->hnode->name);
	printf("node = [%s]\n", name);
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
	for(index = 0; index < 6; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "10.1.15.1%d", index);
		api_conhash_add_node(conhash, serverId, api_strlen(serverId), NULL);
	}

#if 0
	char* uri = "/videos/v/20110926/205763500/205763500/1/f3a89defde4e580e4058149e9059d1d4.ts";
	int len = strlen(uri);
	struct node_s* findp = conhash_lookup(conhashp, uri, len);
	fprintf(stdout, "find [%d][%s] by %s\n", findp->index, findp->iden, uri);

	uri = "/videos/v/20110926/205763500/205763500/2/c59965f441f474c6b0dfbe36232bd614.ts";
	len = strlen(uri);
    findp = conhash_lookup(conhashp, uri, len);       
    fprintf(stdout, "find [%d][%s] by %s\n", findp->index, findp->iden, uri);  

	uri = "abcdefsdfgdsaklj";
	len = strlen(uri);
    findp = conhash_lookup(conhashp, uri, len);       
    fprintf(stdout, "find [%d][%s] by %s\n", findp->index, findp->iden, uri);
#endif
	
	char serverId[100];
	for(index = 0; index < 60; index++)
	{
		memset(serverId, 0x00, sizeof(serverId));
		snprintf(serverId, sizeof(serverId), "10.1.15.1%d", index);
		api_conhash_lookup_node(conhash, serverId, api_strlen(serverId), server_get, NULL);
	}
    
	return 0;
}

