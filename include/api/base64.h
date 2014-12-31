/**
 * @file base64.h
 * @brief API Base64 Encoding
 */
#ifndef __API_BASE64_H_INCLUDED__
#define __API_BASE64_H_INCLUDED__

#include "api.h"
#include "api/lib.h"

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @defgroup API_Base64 Base64 Encoding
 * @ingroup API
 * @{
 */
#if 1
/* Simple BASE64 encode/decode functions.
 * 
 * As we might encode binary strings, hence we require the length of
 * the incoming plain source. And return the length of what we decoded.
 *
 * The decoding function takes any non valid char (i.e. whitespace, \0
 * or anything non A-Z,0-9 etc) as terminal.
 * 
 * The handling of terminating \0 characters differs from function to
 * function.
 *
 */

/**
 * Given the length of an un-encoded string, get the length of the
 * encoded string.
 * @param len the length of an unencoded string.
 * @return the length of the string after it is encoded, including the
 * trailing \0
 */ 
API int api_base64_encode_length(int len);

/**
 * Encode a text string using base64encoding. On EBCDIC machines, the input
 * is first converted to ASCII.
 * @param coded_dst The destination string for the encoded string. A \0 is
 * appended.
 * @param plain_src The original string in plain text
 * @param len_plain_src The length of the plain text string
 * @return the length of the encoded string, including the trailing \0
 */ 
API int api_base64_encode(char * coded_dst, const char *plain_src, 
                                   int len_plain_src);

/**
 * Encode an text string using base64encoding. This is the same as
 * api_base64_encode() except on EBCDIC machines, where the conversion of the
 * input to ASCII is left out.
 * @param coded_dst The destination string for the encoded string. A \0 is
 * appended.
 * @param plain_src The original string in plain text
 * @param len_plain_src The length of the plain text string
 * @return the length of the encoded string, including the trailing \0
 */ 
API int api_base64_encode_internal(uint8_t * coded_dst,
                                          const uint8_t *plain_src,
                                          int len_plain_src, 
                                          const uint8_t *basis, int padding);

/**
 * Determine the maximum buffer length required to decode the plain text
 * string given the encoded string.
 * @param coded_src The encoded string
 * @return the maximum required buffer length for the plain text string
 */ 
API int api_base64_decode_length(const char * coded_src);
API int api_base64_decode_length_internal(const char * coded_src, const uint8_t *basis);

/**
 * Decode a string to plain text. On EBCDIC machines, the result is then
 * converted to EBCDIC.
 * @param plain_dst The destination string for the plain text. A \0 is
 * appended.
 * @param coded_src The encoded string 
 * @return the length of the plain text string (excluding the trailing \0)
 */ 
API int api_base64_decode(char * plain_dst, const char *coded_src);

/**
 * Decode an string to plain text. This is the same as api_base64_decode()
 * except no \0 is appended and on EBCDIC machines, the conversion of the
 * output to EBCDIC is left out.
 * @param plain_dst The destination string for the plain text. The string is
 * not \0-terminated.
 * @param coded_src The encoded string 
 * @return the length of the plain text string
 */ 
API int api_base64_decode_internal(unsigned char * plain_dst, 
                                          const char *coded_src, const uint8_t *basis);
#else

API int api_base64_encode_internal(uint8_t *dst, const uint8_t *src, int src_len, 
                                   const uint8_t *basis, int padding);

#define api_base64_encode_length(len)  (((len + 2) / 3) * 4 + 1)

API int api_base64_encode(uint8_t * coded_dst, const uint8_t *plain_src,
                          int len_plain_src);

API int api_base64_encode_url(uint8_t *coded_dst, const uint8_t *plain_src,
                              int len_plain_src);

#define api_base64_decode_length(len)  (((len + 3) / 4) * 3 + 1)

API int api_base64_decode_internal(uint8_t *dst, const uint8_t *src, int src_len,
                                   const uint8_t *basis);
API int api_base64_decode(uint8_t * plain_dst, const uint8_t *coded_src, int len_coded_src);
API int api_base64_decode_url(uint8_t *plain_dst, const uint8_t *coded_src, int len_coded_src);

#endif
/** @} */

#ifdef __cplusplus
}
#endif

#endif	/* ! __API_BASE64_H_INCLUDED__ */

