#include <string.h>
#include <stdio.h>

#define MAX_LINE	1024
#include "api/conhash.h"

int main(int argc, char* argv[])
{
	api_init();
	int ec = 0
	api_conhash_t *conhash = calloc(1, sizeof(api_conhash_t));
	rc = api_conhash_init(conhash, API_SIZE_FROM_MB(32), 100);
	do_assert(rc == API_SUCCESS);
	int index = 0;
	for(index = 0; index < 6; index++)
	{
		struct node_s* nodep = (struct node_s*)malloc(sizeof(struct node_s));
		memset(nodep, 0, sizeof(struct node_s));
		nodep->index = 0;
		nodep->replicas = 500;
		nodep->flag = NODE_FLAG_INIT;
		snprintf(nodep->iden, 64, "10.1.15.1%d", index);
		nodep->iden[63] = '\0';
		conhash_add_node(conhashp, nodep);
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
	
	char buf[MAX_LINE];
	FILE *fp;
	int len;
	if((fp = fopen("./ts.list","r")) == NULL)
    {
        perror("fail to read");
        exit(1);
    }
	
    while(fgets(buf,MAX_LINE,fp) != NULL)
    {
        len = strlen(buf);
        buf[len-1] = '\0'; 
		struct node_s* findp = conhash_lookup(conhashp, buf, len-1);
        fprintf(stdout, "%s,%s\n", buf, findp->iden);
    }
	
	fclose(fp);
    
	return 0;
}

