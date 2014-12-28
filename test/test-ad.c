/*
 * test-ad.c
 *
 *  Created on: 2014年11月9日
 *      Author: wangfeng
 */

#include "dsp/core.h"

int main(int arhc, const char *argv[])
{
	const char *s = "{\"creative_trade_id\":12345678}";
	bid_adinfo_t *ad = dsp_creative_parse(s, strlen(s));
	if(ad == NULL) {
		printf("parse failed!\n");
	} else {
		printf("ad#creative_trade_id=%d\n", ad->creative_trade_id);
	}
	return 0x00;
}
