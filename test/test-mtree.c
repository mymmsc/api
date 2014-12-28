
#include "all_defines.h"
#include "mutate_dtree.h"

void example(){

	DResult result;
        tree_factors * factors = NULL;
        char * path ="/home/fang/344";
        int rc = MTInit(path, &factors);
        if(rc<0)
        	goto EndProgess;

        //如果是算CTR,最后的权值设为1.4613
        //如果是算CPM和CPC，最后的权值是:0.4515
        //这里需要提供外部设置的接口，可以随时调整
        rc = MTSetThreshold(factors,I_MEDIA,1.4615);

        KEY_PARAM keyParam = CTR;//CTR，CPC，CPM，CPE，CPA
        DParameters params;

        params._ids[CAL_INDEX_EX] = 2;//exchage id
        params._ids[CAL_INDEX_TIME] = 12;//时间 0~23
        params._ids[CAL_INDEX_MEDIA] = 21;//媒体分类ID yoyi标准
        params._ids[CAL_INDEX_POS] = 2;//广告位位置ID
        params._ids[CAL_INDEX_REG] = 2;//用户地域ID
        params._ids[CAL_INDEX_SIZE] = 2;//广告为尺寸ID
        params._ids[CAL_INDEX_VIEW] = 2;//广告类型ID，目前没用

        double goal=0.002;

        if (MTCalculteWeight(factors, params, keyParam, goal, &result) < 0)
        	goto EndProgess;
        else{
        	//出价
        }

        //result._estimation 是预估的goal值
        //result._weight 是加权参考goal值
EndProgess:
        MTDestory(factors);
}
