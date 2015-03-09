// -*- Mode: C++; tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
// vim:tabstop=4:shiftwidth=4:expandtab:

#ifndef __API_BITSET_H_INCLUDED__
#define __API_BITSET_H_INCLUDED__
//////////////////////////////////////////////////////////////////////////////////////////

#include "api.h"
#include "api/errno.h"
//////////////////////////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @defgroup API Bitset
 * @ingroup  API
 * @{
 */

/** 单字节按照从右到左(低位到高位)的顺序 */
//#define API_BITSET_L2H 
/** 单字节按照从左到右(高位到低位)的顺序 */
//#define API_BITSET_H2L

#define API_BITSET_BIT_SIZE      API_SIZE_FROM_KB(128)
 
#define API_BITSET_BYTE_SIZE(s)  ((s) * 8)
 
#define API_BITSET_ALIGN(size, boundary) ((((api_off_t)(size) + (boundary) - 1) / (boundary)) * (boundary))

/** 统计可计算的BIT总数 */
#define API_BITSET_COUNT(size, boundary) ((int)((api_off_t)(size) / (boundary) - 1))

/** 获得bit数在ptr中的字节 */
#define API_BITSET_ELEM(ptr, bit)   ((unsigned char *)(ptr))[(bit)/8]

//#define API_BITSET_MASK(ptr, bit) (1u << ((bit) & (CHAR_BIT - 1)))
/** 计算bit的掩码, 单字节按照从右到左(低位到高位)的顺序 */
#define API_BITSET_MASK_L2H(bit) (1 << ((bit) % 8))

/** 计算bit的掩码, 单字节按照从左到右(高位到低位)的顺序 */
#define API_BITSET_MASK_H2L(bit) (1 << (7 - (bit) % 8))

/** 指定次序, 计算bit的掩码 */
#define API_BITSET_MASK(bit, op) API_BITSET_MASK_##op(bit)

/** 判断bit位是否为1 */
#define API_BITSET_ISSET(ptr, bit, op)  ((API_BITSET_ELEM(ptr, bit) &   API_BITSET_MASK(bit, op)) != 0)
/** 设置bit位为1 */
#define API_BITSET_SET(ptr, bit, op)     (API_BITSET_ELEM(ptr, bit) |=  API_BITSET_MASK(bit, op))
/** 设置bit位为0 */
#define API_BITSET_UNSET(ptr, bit, op)   (API_BITSET_ELEM(ptr, bit) &= ~API_BITSET_MASK(bit, op))
/** bit位按位异或 */
#define API_BITSET_TOGGLE(ptr, bit, op)  (API_BITSET_ELEM(ptr, bit) ^=  API_BITSET_MASK(bit, op))

#pragma pack(1) // 字节对齐

typedef struct __data_block_struct {
    api_uint8_t  guid[16];
    api_off_t    offset;
    api_int32_t  seed;
    api_uint8_t  data[1024];
}data_block_t;

typedef enum __bitset_enum {
    API_BITSET_MEM,   /**< 内存 */
    API_BITSET_NOMEM  /**< 非内存, 如文件等等 */
}api_bitset_e;

typedef struct __bitset_struct 
{
    api_bool_t    bH2L;      /**< 是否由高到低 */
    api_uint8_t   guid[16];  /**< GUID */
    api_off_t     length;    /**< 内容总长度 */
    api_size_t    sizeOfBit; /**< bit代表的尺寸 */
    api_uint32_t  size;      /**< 位图尺寸 */
    api_bitset_e  type;      /**< 是否为文件共享内存 */
    api_uint8_t  *bmap;      /**< 任务块位图描述 */
    api_uint8_t  *bmem;      /**< 位图地址 */
}api_bitset_t;

#pragma pack() // 恢复原来状态

/**
 * 创建数据块描述位图
 * @param[out] bitset 位图对象
 * @param[in] is_h2l 单字节是否从高位到低位
 * @param[in] uuid GUID
 * @param[in] length 数据长度
 * @param[in] sizeOfBit 1bit表示的尺寸
 * @remark 以已经写入的偏移量为计算标准
 */
API api_status_t api_bitset_create(api_bitset_t **bitset, api_bool_t is_h2l,
                                            api_uint8_t uuid[16], api_off_t length, 
                                            api_size_t sizeOfBit);

/**
 * 关闭位图对象
 * @param[in] bitset 位图对象
 */
API status_t api_bitset_close(api_bitset_t **bitset);

/**
 * 设定内存地址
 * @param[in] bitset 位图对象
 * @param[in] mem 内存地址
 */
API void api_bitset_setmem(api_bitset_t *bitset, char *mem);

/**
 * 判断偏移是否为1
 * @param[in] bitset 位图对象
 * @param[in] offset 偏移量
 * @remark 每个块大小API_BLOCK_SIZE_MIN, 目前设定128K, 1个字节代表1MB
 */
API api_bool_t api_bitset_get(const api_bitset_t *bitset, api_off_t offset);

/**
 * 设定位图所在偏移为1
 * @param[in] bitset 位图对象
 * @param[in] offset 偏移量
 * @param[in] size 尺寸
 * @remark 每个块大小API_BLOCK_SIZE_MIN, 目前设定128K, 1个字节代表1MB
 */
API api_status_t api_bitset_set(api_bitset_t *bitset, api_off_t offset);

API api_status_t api_bitset_count(uint32_t n);

/** @} */

#ifdef __cplusplus
}
#endif

//////////////////////////////////////////////////////////////////////////////////////////

#endif /* ! __API_BITSET_H_INCLUDED__ */
