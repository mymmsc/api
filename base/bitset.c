#include "api/bitset.h"
//////////////////////////////////////////////////////////////////////////////////////////

api_status_t api_bitset_create(api_bitset_t **bitset, api_bool_t is_h2l, api_uint8_t uuid[16], api_off_t length, api_size_t sizeOfBit)
{
    api_status_t iRet = API_SUCCESS;
    
    (*bitset) = (api_bitset_t *)api_calloc(1, sizeof(**bitset));
    (*bitset)->bH2L = is_h2l;
    (*bitset)->type = API_BITSET_MEM;
    (*bitset)->sizeOfBit = sizeOfBit;
    (*bitset)->length = length;
    (*bitset)->size = (api_uint32_t)API_BITSET_ALIGN((*bitset)->length, API_BITSET_BYTE_SIZE((*bitset)->sizeOfBit)) / (API_BITSET_BYTE_SIZE((*bitset)->sizeOfBit));
    (*bitset)->bmem = (api_uint8_t *)api_calloc((*bitset)->size, sizeof(api_uint8_t));
    if ((*bitset)->bmem == NULL)
    {
        return API_ENOMEM;
    }
    (*bitset)->bmap = (*bitset)->bmem;
    if (uuid != NULL)
    {
        memcpy((*bitset)->guid, uuid, sizeof((*bitset)->guid));
    }
    
    return iRet;
}

api_status_t api_bitset_close(api_bitset_t **bitset)
{
    api_status_t ret = API_SUCCESS;
    //if ((*bitset)->type == AIO_BITSET_MEM)
    {
        api_safefree((*bitset)->bmem);
    }
    api_safefree((*bitset));
    
    return ret;
}

void api_bitset_setmem(api_bitset_t *bitset, char *mem)
{
    if (bitset->bmem != NULL)
    {
        api_safefree(bitset->bmem);
    }
    bitset->bmap = mem;
    bitset->type = API_BITSET_NOMEM;
}

api_bool_t api_bitset_get(const api_bitset_t *bitset, api_off_t offset)
{
    api_bool_t bRet      = API_FALSE;
    api_off_t  tmpOffset = offset + bitset->sizeOfBit;
    int        tmpBits   = API_BITSET_COUNT(tmpOffset, bitset->sizeOfBit);
    if (tmpBits >= 0)
    {
    	if(bitset->bH2L) {
        	bRet = API_BITSET_ISSET(bitset->bmap, tmpBits, H2L);
    	} else {
    		bRet = API_BITSET_ISSET(bitset->bmap, tmpBits, L2H);
    	}
    }
    
    return bRet;
}

api_status_t api_bitset_set(api_bitset_t *bitset, api_off_t offset)
{
    api_bool_t bRet      = API_FALSE;
    api_off_t  tmpOffset = offset;// + bitset->sizeOfBit;
    int        tmpBits   = API_BITSET_COUNT(tmpOffset, bitset->sizeOfBit);
    if (tmpBits >= 0)
    {
    	if(bitset->bH2L) {
        	bRet = API_BITSET_SET(bitset->bmap, tmpBits, H2L);
    	} else {
    		bRet = API_BITSET_SET(bitset->bmap, tmpBits, L2H);
    	}
    }
    
    return bRet;
}

//////////////////////////////////////////////////////////////////////////////////////////
