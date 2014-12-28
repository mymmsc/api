#ifndef API_BUFFER_H
#define API_BUFFER_H

#include "api.h"
//////////////////////////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/**
 * @defgroup api_buffer Buffer routines
 * @ingroup API 
 * @{
 */

/**
 * max size of a buffer which will just be reset
 * to ->used = 0 instead of really freeing the buffer
 *
 * 64kB (no real reason, just a guess)
 */
#define BUFFER_MAX_REUSE_SIZE  (64 * 1024)

typedef struct api_buffer_t
{
    char       *ptr;   /**< 缓冲区指针 */
    api_size_t  used;  /**< 使用尺寸 */
    api_size_t  size;  /**< 缓冲区尺寸 */
} api_buffer_t;

/** 协议数据缓存 */
typedef struct api_packet_t
{
    api_uint8_t data[API_BUFFER_MAX]; /**< 数据区 */
    api_size_t  length;               /**< 数据长度 */
    int         gotheader;            /**< 是否获得数据头 */
}api_packet_t;

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * init the api_buffer_t
 * @return api_buffer_t point
 */
API api_buffer_t * api_buffer_init(void);

/**
 * 释放内存
 * @return api_buffer_t point
 */
API void api_buffer_free(api_buffer_t *b);

/**
 * 重置内存
 * @return api_buffer_t point
 */
API void api_buffer_reset(api_buffer_t *b);

/**
 * 缓冲区是否为空
 * @param[in] b api_buffer_t point
 * @return 1 or 0
 */
API int api_buffer_is_empty(api_buffer_t *b);

/**
 * 缓冲区是否为空
 * @param[in] a api_buffer_t point
 * @param[in] b api_buffer_t point
 * @return 1 or 0
 */
API int api_buffer_is_equal(api_buffer_t *a, api_buffer_t *b);

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * 内存移动
 * @param[in] b 缓冲区
 * @param[in] from 开始位置
 * @param[in] to 结束位置
 */
API api_buffer_t * api_buffer_move(api_buffer_t *b, int from, int to);

/**
 * 内存拷贝
 * @param[in] b 缓冲区
 * @param[in] s 内存地址
 * @param[in] s_len 内存长度
 */
API int api_buffer_memcpy(api_buffer_t *b, const void *s, api_size_t s_len);

/**
 * 追加内存
 * @param[in] b 缓冲区
 * @param[in] s 内存地址
 * @param[in] s_len 内存长度
 */
API int api_buffer_memcat(api_buffer_t *b, const void *s, api_size_t s_len);

/**
 * 字符串拷贝
 * @param[in] b 缓冲区
 * @param[in] s 字符串首地址
 */
API int api_buffer_strcpy(api_buffer_t *b, const char *s);

/**
 * 字符串拷贝
 * @param[in] b 缓冲区
 * @param[in] s 字符串首地址
 * @param[in] s_len 字符串长度
 */
API int api_buffer_strncpy(api_buffer_t *b, const char *s, api_size_t s_len);

/**
 * 连接字符串
 * @param[in] b 缓冲区
 * @param[in] s 字符串首地址
 */
API int api_buffer_strcat(api_buffer_t *b, const char *s);

/**
 * append a string to the end of the api_buffer_t
 *
 * the resulting api_buffer_t is terminated with a '\0'
 * s is treated as a un-terminated string (a \0 is handled a normal character)
 *
 * @param b a api_buffer_t
 * @param s the string
 * @param s_len size of the string (without the terminating \0)
 */
API int api_buffer_strncat(api_buffer_t *b, const char *s, api_size_t s_len);

/**
 * 检索字符串
 * @param[in] b the specail api_buffer_t object
 * @param[in] needle 需要检索的字符串
 * @param[in] len 字符串长度
 */
API char * api_buffer_strnstr(api_buffer_t *b, const char *needle, api_size_t len);

/**
 * 转换成小写
 * @param[in] b the specail api_buffer_t object
 */
API int api_buffer_to_lower(api_buffer_t *b);

/**
 * 转换成大写
 * @param[in] b the specail api_buffer_t object
 */
API int api_buffer_to_upper(api_buffer_t *b);

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * 复制十六进制串到缓冲区
 * @param[in] b 缓冲区
 * @param[in] in 十六进制字符串
 * @param[in] in_len 十六进制字符串长度
 */
API int api_buffer_cpyhex(api_buffer_t *b, const char *in, api_size_t in_len);

//////////////////////////////////////////////////////////////////////////////////////////
API api_size_t api_buffer_put_int(api_buffer_t *buf, int64_t val);
API api_size_t api_buffer_put_uint(api_buffer_t *buf, uint64_t val);
API api_size_t api_buffer_put_byte(api_buffer_t *buf, api_byte_t val);

API api_size_t api_buffer_put_uint16(api_buffer_t *buf, api_uint16_t val);

API api_size_t api_buffer_put_uint32(api_buffer_t *buf, api_uint32_t val);

API api_size_t api_buffer_put_uint64(api_buffer_t *buf, api_uint64_t val);

API api_size_t api_buffer_fix_byte(api_buffer_t *buf, api_size_t pos, api_byte_t val);
API api_size_t api_buffer_fix_uint16(api_buffer_t *buf, api_size_t pos, api_uint16_t val);
API api_size_t api_buffer_fix_uint32(api_buffer_t *buf, api_size_t pos, api_uint32_t val);
API api_size_t api_buffer_fix_uint64(api_buffer_t *buf, api_size_t pos, api_uint64_t val);
//////////////////////////////////////////////////////////////////////////////////////////

/** @} */

#ifdef __cplusplus
}
#endif

//////////////////////////////////////////////////////////////////////////////////////////

#endif /* ! API_BUFFER_H */
