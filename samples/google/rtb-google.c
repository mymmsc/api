#include "api/lib.h"
#include "api/log.h"
#include "api/base64.h"
#include "api/memory.h"
#include "api/time.h"
#include "api/bitset.h"

#include "modules/rtb-g.pb-c.h"
#include "dsp/exchanges.h"
#include "dsp/decrypt.h"

static const char *v = "<hello world>";
static const size_t vvlen = 13;
char * xx_replace(api_str_t *key, void *data, size_t *vsize)
{
	char *vRet = NULL;

	if(memcmp(key->data, "key", key->len) == 0) {
		vRet = strdup(v);
		//*vsize = api_strlen(v);
		*vsize = vvlen;
	}
	return vRet;
}

int main(int argc, const char *argv[])
{
	status_t rc = 0;
	bid_adapter_t *adapter = NULL;
	dsp_request_t *req = NULL;
	api_init();
	#if 1
	{
		const char *str = "abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc%%key%%1234567890%%key%%abc";
		int i = 0, num = 10 * 10000;
		char temp[10240];
		//num = 1;
		api_time_t tc = api_tickcount();
		for(i = 0; i < num; i++) {
			memset(temp, 0x00, sizeof(temp));
			memcpy(temp, str, api_strlen(str));
			api_str2rep(temp, xx_replace, NULL);
			//printf("a1=[%s]\n", a1);
			//api_safefree(a1);
		}
		printf("api_str2rep\t[%ld]\n", api_tickcount() - tc);

		tc = api_tickcount();
		//char temp[1024];
		for(i = 0; i < num; i++) {
			memset(temp, 0x00, sizeof(temp));
			memcpy(temp, str, api_strlen(str));
			api_strrep(temp, "%%key%%", v, 0);
			//printf("a2=[%s]\n", temp);
			//api_safefree(a1);
		}
		printf("api_strrep\t[%ld]\n", api_tickcount() - tc);
		
		return 0x00;
	}
	#endif
	#if 0
	{
		api_pool_t *pool = api_create_pool(API_SIZE_FROM_MB(32));
		do_assert(pool != NULL);
		char *a1 = api_pcalloc(pool, 1024);
		do_assert(a1 != NULL);
		api_time_t tc = api_tickcount();
		int i = 0, num = 100 * 10000;
		for(i = 0; i < num; i++) {
			memcpy(a1, "1234567890", 10);
			//do_assert(strncmp(a1, "1234567890", 10) == 0);
		}
		printf("memcpy [%ld]\n", api_tickcount() - tc);
		
		for(i = 0; i < num; i++) {
			sse_memcpy_32(a1, "1234567890", 10);
			//do_assert(strncmp(a1, "1234567890", 10) == 0);
		}
		printf("sse_memcpy_32 [%ld]\n", api_tickcount() - tc);
		
		for(i = 0; i < num; i++) {
			sse_memcpy_64(a1, "1234567890", 10);
			//do_assert(strncmp(a1, "1234567890", 10) == 0);
		}
		printf("sse_memcpy_64 [%ld]\n", api_tickcount() - tc);

		for(i = 0; i < num; i++) {
			mmx_memcpy_32(a1, "1234567890", 10);
			//do_assert(strncmp(a1, "1234567890", 10) == 0);
		}
		printf("mmx_memcpy_32 [%ld]\n", api_tickcount() - tc);
		
		for(i = 0; i < num; i++) {
			nmemcpy(a1, "1234567890", 10);
			//do_assert(strncmp(a1, "1234567890", 10) == 0);
		}
		printf("nmemcpy [%ld]\n", api_tickcount() - tc);

		api_destroy_pool(pool);
		return 0x00;
	}
	#endif
	#if 0
	{
		const char *guid = "CAESEH2sCqLoJCNJH-h14YXWfk0";
		char gt[1024];
		size_t gsize = 0;
		memset(gt, 0x00, sizeof(gt));
		memcpy(gt, guid, api_strlen(guid));
		char *g1 = B64Decode(gt, &gsize);
		printf("x0 = %d, [%s]\n", gsize, g1);
		free(g1);
		printf("x = %s\n", api_strstr("3,", "2,"));
		return 0x00;
	}
	#endif
	#if 0
	{
		const char *s = "0:010101010101010101010101,1:010101010101010101010101,2:010101010101010101010101,3:010101010101010101010101,4:010101010101010101010101,5:010101010101010101010101,6:110101010101010101010101";
		char * tt = filter_time_parse(s);
		api_freep(&tt);
		return 0x00;
	}
	#endif
	const char *str = NULL;
	str = "EhBUSk1iAAGlQgq8FpaOABf9IgPaydkyU01vemlsbGEvNS4wIChjb21wYXRpYmxlOyBNU0lFIDkuMDsgV2luZG93cyBOVCA2LjE7IFRyaWRlbnQvNS4wKSxnemlwKGdmZSksZ3ppcChnZmUpWjNodHRwOi8vc3BvcnRzLmlmZW5nLmNvbS9hLzIwMTQxMDI0LzQyMjg2OTc5XzAuc2h0bWxiBXpoX0NOaggItQgVAACAP2oICNMBFQAAQD9y1AIIARB4GNgEIg9GHiAnFg0ODxAREhMUGTQyjwEKHCorMzw/XF5xfoABggGQAZEBkgGcAbMBtgHGAcwB4QHiAeMB5AG0BOYB5wHoAekB7AHtAe4B/wGEAosCnQKvArsCvALFAssCzALOAs8C1gKeA6wDsAO5A70D2APaA9wD3QPgA+ED5QPmA+kD6gPxA/gDiASRBJYEmQSaBJsEngSfBKYEpwSpBLYErASyBDoIExcKAwUYBxJKEBDl9tWnCSjA7m06BDDA7m1SGXBhY2stYnJhbmQt5Yek5Yew572ROjpBbGxSL3BhY2stYnJhbmQt5Yek5Yew572ROjrlm77niYfpobUsIOWkmuS4quS9jee9riAyYAJqJptOqE7lTudO6U7rTplP1VHpUYxSsFOVW7NctFzXaq9O+k7vaP5pcICnq+oLee3ECLag6MDIiAEAkAEAmAEAeACgAQGqARtDQUVTRURFYTFJZ25wVXltczBxY1dwNmVQTTDAAQLIAeAD0gECGij4AYfSBqACHrgCiYA/yALjFtEC/r6dIHgrIis=";
	str = "EhBUSk1iAAGlQgq8FpaOABf9IgPaydkyU01vemlsbGEvNS4wIChjb21wYXRpYmxlOyBNU0lFIDkuMDsgV2luZG93cyBOVCA2LjE7IFRyaWRlbnQvNS4wKSxnemlwKGdmZSksZ3ppcChnZmUpWjNodHRwOi8vc3BvcnRzLmlmZW5nLmNvbS9hLzIwMTQxMDI0LzQyMjg2OTc5XzAuc2h0bWxiBXpoX0NOaggItQgVAACAP2oICNMBFQAAQD9y1AIIARB4GNgEIg9GHiAnFg0ODxAREhMUGTQyjwEKHCorMzw/XF5xfoABggGQAZEBkgGcAbMBtgHGAcwB4QHiAeMB5AG0BOYB5wHoAekB7AHtAe4B/wGEAosCnQKvArsCvALFAssCzALOAs8C1gKeA6wDsAO5A70D2APaA9wD3QPgA+ED5QPmA+kD6gPxA/gDiASRBJYEmQSaBJsEngSfBKYEpwSpBLYErASyBDoIExcKAwUYBxJKEBDl9tWnCSjA7m06BDDA7m1SGXBhY2stYnJhbmQt5Yek5Yew572ROjpBbGxSL3BhY2stYnJhbmQt5Yek5Yew572ROjrlm77niYfpobUsIOWkmuS4quS9jee9riAyYAJqJptOqE7lTudO6U7rTplP1VHpUYxSsFOVW7NctFzXaq9O+k7vaP5pcICnq+oLee3ECLag6MDIiAEAkAEAmAEAeACgAQGqARtDQUVTRURFYTFJZ25wVXltczBxY1dwNmVQTTDAAQLIAeAD0gECGij4AYfSBqACHrgCiYA/yALjFtEC/r6dIHgrIis=";
	char *buf = NULL;
	int len = 0;
	
	int i, times = 10;
	api_time_t tc = api_tickcount();
	api_time_t tm = api_time_now();
	char stime[100000];
	api_time_exp_t te;
	for(i = 0; i < times; i ++) {
		tc = api_tickcount();
	    tm = api_time_now();
		api_time_exp_lt(&te, tc);
		printf("offset:%d, week: %d, hour:%d\n", te.tm_gmtoff, te.tm_wday, te.tm_hour);
		printf("time = %ld,\t%ld\t%s\n", tc, tm, api_parsetime(stime, sizeof(stime), tm, "0"));
	}
	do_assert(str != NULL);
	len = api_base64_decode_length(str);
	//len = api_base64_decode_length(api_strlen(str));
	printf("len = [%d]\n", len);
	buf = api_mallocz(len);
	
	do_assert(buf != NULL);
	int aa = api_base64_decode(buf, str);
	//int aa = api_base64_decode(buf, str, len);
	printf("aa = [%d]\n", aa);
	do_assert(buf != NULL);
	{
		char *b1 = NULL;
		char *b2 = NULL;
		int blen1 = dsp_base64_encode_length(aa);
		printf("blen1 = [%d]\n", blen1);
		b1 = api_mallocz(blen1);
		int blen2 = dsp_base64_encode(b1, buf, aa);
		printf("blen2 = [%d]\n", blen2);
		int blen3 = dsp_base64_decode_length(b1);
		printf("blen3 = [%d]\n", blen3);
		b2 = api_mallocz(blen2);
		int blen4 = dsp_base64_decode(b2, b1);
		printf("blen4 = [%d]\n", blen4);
		do_assert(memcmp(buf, b2, aa) == 0);
	}
	dsp_init(NULL, NULL, NULL, "/home/runtime/data/dsp");
	adapter = bid_adapter_build(NULL, AD_EXCHANGE_GOOGLE);
	do_assert(adapter != NULL);
	rc = bid_adapter_detect(adapter, buf, aa);
	req = adapter->request;
	do_assert(rc == DSP_EVENT_SUCCESS);
	printf("base64 length = %d,[%s]\n", len, req->user_agent);
	bid_adapter_log(adapter);
	//adapter->response = (dsp_response_t *)api_mallocz(sizeof(dsp_response_t));
	{
		uint8_t *data = NULL;
		size_t len = 0;
		adapter->pack(adapter/*, data, &len*/);
		printf("response: buf=[%02X][%02X], len=%d\n", 
			adapter->out_data[0],adapter->out_data[1], 
			adapter->out_len);
	}
	bid_adapter_close(adapter);
	api_safefree(buf);
	
	return 0x00;
}

