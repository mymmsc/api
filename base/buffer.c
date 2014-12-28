#include "api/buffer.h"
#include "api/errno.h"
#include "api/strings.h"
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
//////////////////////////////////////////////////////////////////////////////////////////

static const char hex_chars[] = "0123456789abcdef";
//////////////////////////////////////////////////////////////////////////////////////////

/**
 * init the api_buffer_t
 * @return api_buffer_t point
 */
api_buffer_t * api_buffer_init(void)
{
    api_buffer_t *b;
    
    b = (api_buffer_t *)malloc(sizeof(*b));
    assert(b);
    
    b->ptr = NULL;
    b->size = 0;
    b->used = 0;
    
    return b;
}

/**
 * free the api_buffer_t
 */
void api_buffer_free(api_buffer_t *b)
{
    if (!b) return;
    if (b->ptr) {
        free(b->ptr);
        b->ptr = NULL;
    }
    
    free(b);
    b = NULL;
}

void api_buffer_reset(api_buffer_t *b)
{
    if (!b) return;
    
    /* limit don't reuse api_buffer_t larger than ... bytes */
    if (b->size > BUFFER_MAX_REUSE_SIZE)
    {
        free(b->ptr);
        b->ptr = NULL;
        b->size = 0;
    }
    
    b->used = 0;
}

int api_buffer_is_empty(api_buffer_t *b)
{
    if (!b) return 1;
    return (b->used == 0);
}

/**
 * check if two api_buffer_t contain the same data
 *
 * HISTORY: this function was pretty much optimized, but didn't handled
 * alignment properly.
 */

int api_buffer_is_equal(api_buffer_t *a, api_buffer_t *b)
{
    if (a->used != b->used) return 0;
    if (a->used == 0) return 1;
    
    return (0 == strcmp(a->ptr, b->ptr));
}

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * allocate (if neccessary) enough space for 'size' bytes and
 * set the 'used' counter to 0
 */
#define BUFFER_PIECE_SIZE 64

static int api_buffer_prepare_copy(api_buffer_t *b, api_size_t size)
{
    if (!b) return -1;
    
    if ((0 == b->size) || (size > b->size))
    {
        if (b->size) {
            free(b->ptr);
            b->ptr = NULL;
        }
        b->size = size;
        
        /* always allocate a multiply of BUFFER_PIECE_SIZE */
        b->size += BUFFER_PIECE_SIZE - (b->size % BUFFER_PIECE_SIZE);
        
        //b->ptr = malloc(b->size);
        b->ptr = calloc(1, b->size);
        assert(b->ptr);
    }
    b->used = 0;
    return 0;
}

/**
 * increase the internal api_buffer_t (if neccessary) to append another 'size' byte
 * ->used isn't changed
 */
static int api_buffer_prepare_append(api_buffer_t *b, api_size_t size)
{
    if (!b) return -1;
    
    if (0 == b->size)
    {
        b->size = size;
        
        /* always allocate a multiply of BUFFER_PIECE_SIZE */
        b->size += BUFFER_PIECE_SIZE - (b->size % BUFFER_PIECE_SIZE);
        
        //b->ptr = malloc(b->size);
        b->ptr = calloc(1, b->size);
        b->used = 0;
        assert(b->ptr);
    } else if (b->used + size > b->size) {
        api_size_t old_size = b->size;
        b->size += size;
        
        /* always allocate a multiply of BUFFER_PIECE_SIZE */
        b->size += BUFFER_PIECE_SIZE - (b->size % BUFFER_PIECE_SIZE);
        
        b->ptr = realloc(b->ptr, b->size);
        assert(b->ptr);
        memset(b->ptr + old_size, 0x00, b->size - old_size);
    }
    return 0;
}


//////////////////////////////////////////////////////////////////////////////////////////

int api_buffer_strcpy(api_buffer_t *b, const char *s)
{
    api_size_t s_len = 0;
    
    if (!s || !b) return -1;
    
    s_len = strlen(s);
    s_len += 1;
    api_buffer_prepare_copy(b, s_len);
    
    memcpy(b->ptr, s, s_len);
    b->used = s_len;
    
    return 0;
}

int api_buffer_strncpy(api_buffer_t *b, const char *s, api_size_t s_len)
{
    if (!s || !b) return -1;
#if 0
    /* removed optimization as we have to keep the empty string
     * in some cases for the config handling
     *
     * url.access-deny = ("")
     */
    if (s_len == 0) return 0;
#endif
    api_buffer_prepare_copy(b, s_len + 1);
    
    memcpy(b->ptr, s, s_len);
    b->ptr[s_len] = '\0';
    b->used = s_len + 1;
    
    return 0;
}

int api_buffer_strcat(api_buffer_t *b, const char *s)
{
    api_size_t s_len = 0;
    
    if (!s || !b) return -1;
    
    s_len = strlen(s);
    api_buffer_prepare_append(b, s_len + 1);
    if (b->used == 0)
    {
        b->used++;
    }
    memcpy(b->ptr + b->used - 1, s, s_len + 1);
    b->used += s_len;
    
    return 0;
}

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
int api_buffer_strncat(api_buffer_t *b, const char *s, api_size_t s_len)
{
    if (!s || !b) return -1;
    if (s_len == 0) return 0;
    
    api_buffer_prepare_append(b, s_len + 1);
    if (b->used == 0)
    {
        b->used++;
    }
    memcpy(b->ptr + b->used - 1, s, s_len);
    b->used += s_len;
    b->ptr[b->used - 1] = '\0';
    
    return 0;
}

char * api_buffer_strnstr(api_buffer_t *b, const char *needle, api_size_t len)
{
    api_size_t i = 0;
    
    if (len == 0) return NULL;
    if (needle == NULL) return NULL;
    
    if (b->used < len) return NULL;
    
    for(i = 0; i < b->used - len; i++)
    {
        if (0 == memcmp(b->ptr + i, needle, len))
        {
            return b->ptr + i;
        }
    }
    
    return NULL;
}

int api_buffer_to_lower(api_buffer_t *b)
{
    char *c = NULL;
    
    if (b->used == 0) return 0;
    
    for (c = b->ptr; *c; c++)
    {
        if (*c >= 'A' && *c <= 'Z') {
            *c |= 32;
        }
    }
    
    return 0;
}

int api_buffer_to_upper(api_buffer_t *b)
{
    char *c = NULL;
    
    if (b->used == 0) return 0;
    
    for (c = b->ptr; *c; c++)
    {
        if (*c >= 'a' && *c <= 'z') {
            *c &= ~32;
        }
    }
    
    return 0;
}

//////////////////////////////////////////////////////////////////////////////////////////

api_buffer_t * api_buffer_move(api_buffer_t *b, int from, int to)
{
 api_buffer_t *new_buff = NULL;
 api_size_t old_size = b->size;
 api_size_t old_used = b->used;
 
 if (!b || from == to) {
  new_buff = b;
 } else {
  b->size -= (from - to);
  b->used = b->size;
  if (from > to) {
   memmove(b->ptr + to, b->ptr + from, old_size - from);
   // 缓冲区变小
   b->ptr = realloc(b->ptr, b->size);
  } else if (from < to) {
   // 缓冲区变大
   b->ptr = realloc(b->ptr, b->size);
   memmove(b->ptr + to, b->ptr + from, old_size - from);
   // 空白区域清零
   memset(b->ptr + from, 0x00, to - from);
  }
  new_buff = b;
 }
 
 return new_buff;
}

int api_buffer_memcpy(api_buffer_t *b, const void *s, api_size_t s_len)
{
    if (!s || !b) return -1;
    
    b->used = 0;
    
    return api_buffer_memcat(b, s, s_len);
}

int api_buffer_memcat(api_buffer_t *b, const void *s, api_size_t s_len)
{
    if (!s || !b) return -1;
    if (s_len == 0) return 0;
    
    api_buffer_prepare_append(b, s_len + 1);
    memcpy(b->ptr + b->used, s, s_len);
    b->used += s_len;
    
    return 0;
}


//////////////////////////////////////////////////////////////////////////////////////////

int api_buffer_cpyhex(api_buffer_t *b, const char *in, api_size_t in_len)
{
    api_size_t i = 0;
    
    /* BO protection */
    if (in_len * 2 < in_len) return -1;
    
    api_buffer_prepare_copy(b, in_len * 2 + 1);
    
    for (i = 0; i < in_len; i++)
    {
        b->ptr[b->used++] = hex_chars[(in[i] >> 4) & 0x0F];
        b->ptr[b->used++] = hex_chars[in[i] & 0x0F];
    }
    b->ptr[b->used++] = '\0';
    
    return 0;
}

//////////////////////////////////////////////////////////////////////////////////////////

api_size_t api_buffer_put_int(api_buffer_t *buf, int64_t val)
{
	char tmp[128];
	size_t len = api_snprintf(tmp, sizeof(tmp), "%" API_INT64_T_FMT, val);
	api_buffer_strncat(buf, tmp, len);

	return buf->used;
}

api_size_t api_buffer_put_uint(api_buffer_t *buf, uint64_t val)
{
	char tmp[128];
	size_t len = api_snprintf(tmp, sizeof(tmp), "%" API_UINT64_T_FMT, val);
	api_buffer_strncat(buf, tmp, len);

	return buf->used;
}

api_size_t api_buffer_put_byte(api_buffer_t *buf, api_byte_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 1;
    memset(tmpData, 0x00, sizeof(tmpData));
    tmpData[0] = val;
    api_buffer_memcat(buf, tmpData, tmpSize);
    return buf->used;
}

api_size_t api_buffer_put_uint16(api_buffer_t *buf, api_uint16_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 2;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htons(val);
    memcpy(tmpData, &val, tmpSize);
    api_buffer_memcat(buf, tmpData, tmpSize);
    
    return buf->used;
}

api_size_t api_buffer_put_uint32(api_buffer_t *buf, api_uint32_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 4;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htonl(val);
    memcpy(tmpData, &val, tmpSize);
    api_buffer_memcat(buf, tmpData, tmpSize);
    
    return buf->used;
}

api_size_t api_buffer_put_uint64(api_buffer_t *buf, api_uint64_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 8;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htonll(val);
    memcpy(tmpData, &val, tmpSize);
    api_buffer_memcat(buf, tmpData, tmpSize);
    
    return buf->used;
}

api_size_t api_buffer_fix_byte(api_buffer_t *buf, api_size_t pos, api_byte_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 1;
    memset(tmpData, 0x00, sizeof(tmpData));
    tmpData[0] = val;
    memcpy(buf->ptr, tmpData, tmpSize);
    return buf->used;
}

api_size_t api_buffer_fix_uint16(api_buffer_t *buf, api_size_t pos, api_uint16_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 2;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htons(val);
    memcpy(tmpData, &val, tmpSize);
    memcpy(buf->ptr, tmpData, tmpSize);
    
    return buf->used;
}

api_size_t api_buffer_fix_uint32(api_buffer_t *buf, api_size_t pos, api_uint32_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 4;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htonl(val);
    memcpy(tmpData, &val, tmpSize);
    memcpy(buf->ptr, tmpData, tmpSize);
    
    return buf->used;
}

api_size_t api_buffer_fix_uint64(api_buffer_t *buf, api_size_t pos, api_uint64_t val)
{
    char       tmpData[512];
    api_size_t tmpSize = 8;
    
    memset(tmpData, 0x00, sizeof(tmpData));
    val = htonll(val);
    memcpy(tmpData, &val, tmpSize);
    memcpy(buf->ptr, tmpData, tmpSize);
    
    return buf->used;
}

//////////////////////////////////////////////////////////////////////////////////////////

#if 0
API_DECLARE(api_buffer_t *) api_buffer_init_buffer(api_buffer_t *src)
{
    api_buffer_t *b = api_buffer_init();
    api_buffer_copy_string_buffer(b, src);
    return b;
}

API_DECLARE(int) api_buffer_copy_string_buffer(api_buffer_t *b, const api_buffer_t *src)
{
    if (!src) return -1;
    
    if (src->used == 0)
    {
        b->used = 0;
        return 0;
    }
    return api_buffer_strncpy(b, src->ptr, src->used - 1);
}

API_DECLARE(int) api_buffer_append_string_rfill(api_buffer_t *b, const char *s, api_size_t maxlen)
{
    api_size_t s_len;
    
    if (!s || !b) return -1;
    
    s_len = strlen(s);
    api_buffer_prepare_append(b, maxlen + 1);
    if (b->used == 0)
    {
        b->used++;
    }
    memcpy(b->ptr + b->used - 1, s, s_len);
    if (maxlen > s_len)
    {
        memset(b->ptr + b->used - 1 + s_len, ' ', maxlen - s_len);
    }
    
    b->used += maxlen;
    b->ptr[b->used - 1] = '\0';
    return 0;
}

API_DECLARE(int) api_buffer_append_string_buffer(api_buffer_t *b, const api_buffer_t *src)
{
    if (!src) return -1;
    if (src->used == 0) return 0;
    
    return api_buffer_strncat(b, src->ptr, src->used - 1);
}

API_DECLARE(int) api_buffer_append_long_hex(api_buffer_t *b, unsigned long val)
{
    char *buf;
    int shift = 0;
    unsigned long copy = val;
    
    while (copy)
    {
        copy >>= 4;
        shift++;
    }
    if (shift == 0)
    {
        shift++;
    }
    if (shift & 0x01)
    {
        shift++;
    }
    api_buffer_prepare_append(b, shift + 1);
    if (b->used == 0)
    {
        b->used++;
    }
    buf = b->ptr + (b->used - 1);
    b->used += shift;
    
    shift <<= 2;
    while (shift > 0)
    {
        shift -= 4;
        *(buf++) = hex_chars[(val >> shift) & 0x0F];
    }
    *buf = '\0';
    
    return 0;
}

static int buffer_ltostr(char *buf, long val)
{
    char swap;
    char *end;
    int len = 1;
    
    if (val < 0)
    {
        len++;
        *(buf++) = '-';
        val = -val;
    }
    
    end = buf;
    while (val > 9)
    {
        *(end++) = '0' + (val % 10);
        val = val / 10;
    }
    *(end) = '0' + (int)val;
    *(end + 1) = '\0';
    len += end - buf;
    
    while (buf < end)
    {
        swap = *end;
        *end = *buf;
        *buf = swap;
        
        buf++;
        end--;
    }
    
    return len;
}

API_DECLARE(int) api_buffer_append_long(api_buffer_t *b, long val)
{
    if (!b) return -1;
    
    api_buffer_prepare_append(b, 32);
    if (b->used == 0)
    {
        b->used++;
    }
    b->used += buffer_ltostr(b->ptr + (b->used - 1), val);
    return 0;
}

API_DECLARE(int) api_buffer_copy_long(api_buffer_t *b, long val)
{
    if (!b) return -1;
    
    b->used = 0;
    return api_buffer_append_long(b, val);
}

static char int2hex(char c)
{
    return hex_chars[(c & 0x0F)];
}

/* converts hex char (0-9, A-Z, a-z) to decimal.
 * returns 0xFF on invalid input.
 */
static char hex2int(unsigned char hex)
{
    hex = hex - '0';
    if (hex > 9)
    {
        hex = (hex + '0' - 1) | 0x20;
        hex = hex - 'a' + 11;
    }
    if (hex > 15)
    {
        hex = 0xFF;
    }
    return hex;
}

API_DECLARE(api_buffer_t *) api_buffer_init_string(const char *str)
{
    api_buffer_t *b = api_buffer_init();
    
    api_buffer_strcpy(b, str);
    
    return b;
}

API_DECLARE(int) api_buffer_is_equal_string(api_buffer_t *a, const char *s, api_size_t b_len)
{
    api_buffer_t b;
    
    b.ptr = (char *)s;
    b.used = b_len + 1;
    
    return api_buffer_is_equal(a, &b);
}

/* simple-assumption:
 *
 * most parts are equal and doing a case conversion needs time
 *
 */
API_DECLARE(int) api_buffer_caseless_compare(const char *a, api_size_t a_len, const char *b, api_size_t b_len)
{
    api_size_t ndx = 0, max_ndx;
    api_size_t *al, *bl;
    api_size_t mask = sizeof(*al) - 1;
    
    al = (api_size_t *)a;
    bl = (api_size_t *)b;
    
    /* is the alignment correct ? */
    if (((api_size_t)al & mask) == 0 &&
         ((api_size_t)bl & mask) == 0)
    {
        max_ndx = ((a_len < b_len) ? a_len : b_len) & ~mask;
        for (; ndx < max_ndx; ndx += sizeof(*al))
        {
            if (*al != *bl) break;
            al++; bl++;
        }
    }
    
    a = (char *)al;
    b = (char *)bl;
    
    max_ndx = ((a_len < b_len) ? a_len : b_len);
    
    for (; ndx < max_ndx; ndx++)
    {
        char a1 = *a++, b1 = *b++;
        
        if (a1 != b1)
        {
            if ((a1 >= 'A' && a1 <= 'Z') && (b1 >= 'a' && b1 <= 'z'))
            {
                a1 |= 32;
            }
            else if ((a1 >= 'a' && a1 <= 'z') && (b1 >= 'A' && b1 <= 'Z'))
            {
                b1 |= 32;
            }
            if ((a1 - b1) != 0) return (a1 - b1);
        }
    }
    
    /* all chars are the same, and the length match too
     *
     * they are the same */
    if (a_len == b_len) return 0;
    
    /* if a is shorter then b, then b is larger */
    return (a_len - b_len);
}

/**
 * check if the rightmost bytes of the string are equal.
 */
API_DECLARE(int) api_buffer_is_equal_right_len(api_buffer_t *b1, api_buffer_t *b2, api_size_t len)
{
    /* no, len -> equal */
    if (len == 0) return 1;
    
    /* len > 0, but empty buffers -> not equal */
    if (b1->used == 0 || b2->used == 0) return 0;
    
    /* buffers too small -> not equal */
    if (b1->used - 1 < len || b1->used - 1 < len) return 0;
    
    if (0 == strncmp(b1->ptr + b1->used - 1 - len,
             b2->ptr + b2->used - 1 - len, len))
    {
        return 1;
    }
    
    return 0;
}

static const char encoded_chars_rel_uri_part[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  00 -  0F control chars */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  10 -  1F */
    1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1,  /*  20 -  2F space " # $ % & ' + , / */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,  /*  30 -  3F : ; = ? @ < > */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  40 -  4F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  50 -  5F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  60 -  6F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,  /*  70 -  7F DEL */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  80 -  8F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  90 -  9F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  A0 -  AF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  B0 -  BF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  C0 -  CF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  D0 -  DF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  E0 -  EF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  F0 -  FF */
};

static const char encoded_chars_rel_uri[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  00 -  0F control chars */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  10 -  1F */
    1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0,  /*  20 -  2F space " # $ % & ' + , / */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,  /*  30 -  3F : ; = ? @ < > */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  40 -  4F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  50 -  5F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  60 -  6F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,  /*  70 -  7F DEL */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  80 -  8F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  90 -  9F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  A0 -  AF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  B0 -  BF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  C0 -  CF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  D0 -  DF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  E0 -  EF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  F0 -  FF */
};

static const char encoded_chars_html[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  00 -  0F control chars */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  10 -  1F */
    0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  20 -  2F & */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0,  /*  30 -  3F < > */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  40 -  4F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  50 -  5F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  60 -  6F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,  /*  70 -  7F DEL */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  80 -  8F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  90 -  9F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  A0 -  AF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  B0 -  BF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  C0 -  CF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  D0 -  DF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  E0 -  EF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  F0 -  FF */
};

const char encoded_chars_minimal_xml[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  00 -  0F control chars */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  10 -  1F */
    0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  20 -  2F & */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0,  /*  30 -  3F < > */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  40 -  4F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  50 -  5F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  60 -  6F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,  /*  70 -  7F DEL */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  80 -  8F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  90 -  9F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  A0 -  AF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  B0 -  BF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  C0 -  CF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  D0 -  DF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  E0 -  EF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  F0 -  FF */
};

const char encoded_chars_hex[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  00 -  0F control chars */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  10 -  1F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  20 -  2F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  30 -  3F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  40 -  4F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  50 -  5F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  60 -  6F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  70 -  7F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  80 -  8F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  90 -  9F */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  A0 -  AF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  B0 -  BF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  C0 -  CF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  D0 -  DF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  E0 -  EF */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  /*  F0 -  FF */
};

static const char encoded_chars_http_header[] = {
    /*
    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
    */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,  /*  00 -  0F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  10 -  1F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  20 -  2F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  30 -  3F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  40 -  4F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  50 -  5F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  60 -  6F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  70 -  7F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  80 -  8F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  90 -  9F */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  A0 -  AF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  B0 -  BF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  C0 -  CF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  D0 -  DF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  E0 -  EF */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /*  F0 -  FF */
};

#define SEGFAULT() do { fprintf(stderr, "%s.%d: aborted\n", __FILE__, __LINE__); abort(); } while(0)

API_DECLARE(int) api_buffer_append_string_encoded(api_buffer_t *b, const char *s, api_size_t s_len, buffer_encoding_t encoding)
{
    unsigned char *ds, *d;
    api_size_t d_len, ndx;
    const char *map = NULL;
    
    if (!s || !b) return -1;
    
    if (b->ptr[b->used - 1] != '\0')
    {
        SEGFAULT();
    }
    
    if (s_len == 0) return 0;
    
    switch(encoding) {
    case ENCODING_REL_URI:
        map = encoded_chars_rel_uri;
        break;
    case ENCODING_REL_URI_PART:
        map = encoded_chars_rel_uri_part;
        break;
    case ENCODING_HTML:
        map = encoded_chars_html;
        break;
    case ENCODING_MINIMAL_XML:
        map = encoded_chars_minimal_xml;
        break;
    case ENCODING_HEX:
        map = encoded_chars_hex;
        break;
    case ENCODING_HTTP_HEADER:
        map = encoded_chars_http_header;
        break;
    case ENCODING_UNSET:
        break;
    }
    
    assert(map != NULL);
    
    /* count to-be-encoded-characters */
    for (ds = (unsigned char *)s, d_len = 0, ndx = 0; ndx < s_len; ds++, ndx++)
    {
        if (map[*ds])
        {
            switch(encoding)
            {
            case ENCODING_REL_URI:
            case ENCODING_REL_URI_PART:
                d_len += 3;
                break;
            case ENCODING_HTML:
            case ENCODING_MINIMAL_XML:
                d_len += 6;
                break;
            case ENCODING_HTTP_HEADER:
            case ENCODING_HEX:
                d_len += 2;
                break;
            case ENCODING_UNSET:
                break;
            }
        }
        else
        {
            d_len ++;
        }
    }
    
    api_buffer_prepare_append(b, d_len);
    
    for (ds = (unsigned char *)s, d = (unsigned char *)b->ptr + b->used - 1, d_len = 0, ndx = 0;
        ndx < s_len;
        ds++, ndx++)
    {
        if (map[*ds])
        {
            switch(encoding)
            {
            case ENCODING_REL_URI:
            case ENCODING_REL_URI_PART:
                d[d_len++] = '%';
                d[d_len++] = hex_chars[((*ds) >> 4) & 0x0F];
                d[d_len++] = hex_chars[(*ds) & 0x0F];
                break;
            case ENCODING_HTML:
            case ENCODING_MINIMAL_XML:
                d[d_len++] = '&';
                d[d_len++] = '#';
                d[d_len++] = 'x';
                d[d_len++] = hex_chars[((*ds) >> 4) & 0x0F];
                d[d_len++] = hex_chars[(*ds) & 0x0F];
                d[d_len++] = ';';
                break;
            case ENCODING_HEX:
                d[d_len++] = hex_chars[((*ds) >> 4) & 0x0F];
                d[d_len++] = hex_chars[(*ds) & 0x0F];
                break;
            case ENCODING_HTTP_HEADER:
                d[d_len++] = *ds;
                d[d_len++] = '\t';
                break;
            case ENCODING_UNSET:
                break;
            }
        }
        else
        {
            d[d_len++] = *ds;
        }
    }
    
    /* terminate api_buffer_t and calculate new length */
    b->ptr[b->used + d_len - 1] = '\0';
    
    b->used += d_len;
    
    return 0;
}

/* decodes url-special-chars inplace.
 * replaces non-printable characters with '_'
 */
static int buffer_urldecode_internal(api_buffer_t *url, int is_query)
{
    unsigned char high, low;
    const char *src;
    char *dst;
    
    if (!url || !url->ptr) return -1;
    
    src = (const char*) url->ptr;
    dst = (char*) url->ptr;
    
    while ((*src) != '\0')
    {
        if (is_query && *src == '+')
        {
            *dst = ' ';
        }
        else if (*src == '%')
        {
            *dst = '%';
            
            high = hex2int(*(src + 1));
            if (high != 0xFF)
            {
                low = hex2int(*(src + 2));
                if (low != 0xFF)
                {
                    high = (high << 4) | low;
                    
                    /* map control-characters out */
                    if (high < 32 || high == 127) high = '_';
                    
                    *dst = high;
                    src += 2;
                }
            }
        }
        else
        {
            *dst = *src;
        }
        
        dst++;
        src++;
    }
    
    *dst = '\0';
    url->used = (dst - url->ptr) + 1;
    
    return 0;
}

API_DECLARE(int) api_buffer_urldecode_path(api_buffer_t *url)
{
    return buffer_urldecode_internal(url, 0);
}

API_DECLARE(int) api_buffer_urldecode_query(api_buffer_t *url)
{
    return buffer_urldecode_internal(url, 1);
}

/* Remove "/../", "//", "/./" parts from path.
 *
 * /blah/..         gets  /
 * /blah/../foo     gets  /foo
 * /abc/./xyz       gets  /abc/xyz
 * /abc//xyz        gets  /abc/xyz
 *
 * NOTE: src and dest can point to the same api_buffer_t, in which case,
 *       the operation is performed in-place.
 */
API_DECLARE(int) api_buffer_path_simplify(api_buffer_t *dest, api_buffer_t *src)
{
    int toklen;
    char c, pre1;
    char *start, *slash, *walk, *out;
    unsigned short pre;
    
    if (src == NULL || src->ptr == NULL || dest == NULL)
    {
        return -1;
    }
    if (src == dest)
    {
        api_buffer_prepare_append(dest, 1);
    }
    else
    {
        api_buffer_prepare_copy(dest, src->used + 1);
    }
    walk  = src->ptr;
    start = dest->ptr;
    out   = dest->ptr;
    slash = dest->ptr;
    
#if defined(__API_WIN32__) || defined(__API_CYGWIN__)
    /* cygwin is treating \ and / the same, so we have to that too
     */
    for (walk = src->ptr; *walk; walk++)
    {
        if (*walk == '\\') *walk = '/';
    }
    walk = src->ptr;
#endif
    
    while (*walk == ' ')
    {
        walk++;
    }
    
    pre1 = *(walk++);
    c    = *(walk++);
    pre  = pre1;
    if (pre1 != '/')
    {
        pre = ('/' << 8) | pre1;
        *(out++) = '/';
    }
    *(out++) = pre1;
    
    if (pre1 == '\0')
    {
        dest->used = (out - start) + 1;
        return 0;
    }
    
    while (1)
    {
        if (c == '/' || c == '\0')
        {
            toklen = out - slash;
            if (toklen == 3 && pre == (('.' << 8) | '.'))
            {
                out = slash;
                if (out > start)
                {
                    out--;
                    while (out > start && *out != '/')
                    {
                        out--;
                    }
                }
                
                if (c == '\0')
                {
                    out++;
                }
            }
            else if (toklen == 1 || pre == (('/' << 8) | '.'))
            {
                out = slash;
                if (c == '\0')
                {
                    out++;
                }
            }
            
            slash = out;
        }
        
        if (c == '\0')
        {
            break;
        }
        pre1 = c;
        pre  = (pre << 8) | pre1;
        c    = *walk;
        *out = pre1;
        
        out++;
        walk++;
    }
    
    *out = '\0';
    dest->used = (out - start) + 1;
    
    return 0;
}

#if 0
int light_isdigit(int c)
{
    return (c >= '0' && c <= '9');
}

int light_isxdigit(int c)
{
    if (light_isdigit(c)) return 1;
    
    c |= 32;
    return (c >= 'a' && c <= 'f');
}

int light_isalpha(int c)
{
    c |= 32;
    return (c >= 'a' && c <= 'z');
}

int light_isalnum(int c)
{
    return light_isdigit(c) || light_isalpha(c);
}

#endif
#endif
//////////////////////////////////////////////////////////////////////////////////////////
