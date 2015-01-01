#ifndef __API_CRC64_H_INCLUDED__
#define __API_CRC64_H_INCLUDED__

#include <api.h>

#ifdef __cplusplus
extern "C" {
#endif

API uint64_t api_crc64(uint64_t crc, const unsigned char *s, uint64_t l);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_CRC64_H_INCLUDE__ */
