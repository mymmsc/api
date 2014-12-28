#include "api/base64.h"

#if 1

/* aaaack but it's fast and const should make it shared text page. */
static const char kBase64EncodeChars[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

static const uint8_t kBase64[256] =
{
    /* ASCII table */
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64,
    64,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64,
    64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64
};

static const char kBase64EncodeUrl[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

static const uint8_t kBasis64Url[256] = {
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64,
    64,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 63,
    64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64,

    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
    64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64
};

int api_base64_decode_length_internal(const char * bufcoded, const uint8_t *basis)

{
    int nbytesdecoded;
    register const unsigned char *bufin;
    register api_size_t nprbytes;

    bufin = (const unsigned char *) bufcoded;
    while (basis[*(bufin++)] <= 63);
	
    nprbytes = (bufin - (const unsigned char *) bufcoded) - 1;
    nbytesdecoded = (((int)nprbytes + 3) / 4) * 3;
	
    return nbytesdecoded + 1;
}

int api_base64_decode_length(const char *bufcoded)
{
    int nbytesdecoded;
    nbytesdecoded = api_base64_decode_length_internal(bufcoded, kBase64);
	
    return nbytesdecoded + 1;
}

int api_base64_decode(char *bufplain, const char *bufcoded)
{
    int len;
    
    len = api_base64_decode_internal((uint8_t *) bufplain, bufcoded, kBase64);
    return len;
}

/* This is the same as api_base64_decode() except:
 * - no \0 is appended
 * - on EBCDIC machines, the conversion of the output to ebcdic is left out
 */
int api_base64_decode_internal(unsigned char *bufplain, const char *bufcoded, const uint8_t *basis)
{
    int nbytesdecoded;
    register const unsigned char *bufin;
    register unsigned char *bufout;
    register api_size_t nprbytes;
	
    bufin = (const unsigned char *) bufcoded;
    while (basis[*(bufin++)] <= 63);
    nprbytes = (bufin - (const unsigned char *) bufcoded) - 1;
    nbytesdecoded = (((int)nprbytes + 3) / 4) * 3;
	
    bufout = (unsigned char *) bufplain;
    bufin = (const unsigned char *) bufcoded;
	
    while (nprbytes > 4) {
		*(bufout++) = (unsigned char) (basis[*bufin] << 2 | basis[bufin[1]] >> 4);
		*(bufout++) = (unsigned char) (basis[bufin[1]] << 4 | basis[bufin[2]] >> 2);
		*(bufout++) = (unsigned char) (basis[bufin[2]] << 6 | basis[bufin[3]]);
		bufin += 4;
		nprbytes -= 4;
    }
	
    /* Note: (nprbytes == 1) would be an error, so just ingore that case */
    if (nprbytes > 1) {
		*(bufout++) = (unsigned char) (basis[*bufin] << 2 | basis[bufin[1]] >> 4);
    }
    if (nprbytes > 2) {
		*(bufout++) = (unsigned char) (basis[bufin[1]] << 4 | basis[bufin[2]] >> 2);
    }
    if (nprbytes > 3) {
		*(bufout++) = (unsigned char) (basis[bufin[2]] << 6 | basis[bufin[3]]);
    }
	
    nbytesdecoded -= (4 - (int)nprbytes) & 3;
    return nbytesdecoded;
}

int api_base64_encode_length(int len)
{
    return ((len + 2) / 3 * 4) + 1;
}

int api_base64_encode(char *encoded, const char *string, int len)
{
    return api_base64_encode_internal(encoded, (const unsigned char *) string, len, kBase64EncodeChars, 1);
}

/* This is the same as api_base64_encode() except on EBCDIC machines, where
 * the conversion of the input to ascii is left out.
 */
int api_base64_encode_internal(uint8_t *encoded, const uint8_t *string, int len, 
									const uint8_t *basis, int padding)
{
    int i;
    uint8_t *p;

    p = encoded;
    for (i = 0; i < len - 2; i += 3) {
		*p++ = basis[(string[i] >> 2) & 0x3F];
		*p++ = basis[((string[i] & 0x3) << 4) |
	                ((int) (string[i + 1] & 0xF0) >> 4)];
		*p++ = basis[((string[i + 1] & 0xF) << 2) |
	                ((int) (string[i + 2] & 0xC0) >> 6)];
		*p++ = basis[string[i + 2] & 0x3F];
    }
    if (i < len) {
		*p++ = basis[(string[i] >> 2) & 0x3F];
		if (i == (len - 1)) {
	    	*p++ = basis[((string[i] & 0x3) << 4)];
	    	if(padding) *p++ = '=';
		} else {
	    	*p++ = basis[((string[i] & 0x3) << 4) |
	                    ((int) (string[i + 1] & 0xF0) >> 4)];
	    	*p++ = basis[((string[i + 1] & 0xF) << 2)];
		}
		if(padding) *p++ = '=';
    }
	
    *p++ = '\0';
    return (int)(p - encoded);
}
#else
/////////////////////////////////////////////////////////////////////////////////////////////////////////

int api_base64_encode_internal(uint8_t *dst, const uint8_t *src, int src_len, const uint8_t *basis,
    int padding)
{
	size_t   iRet = 0;
    uint8_t *d;
	const uint8_t *s = NULL;
    size_t   len = src_len;

    s = src;
    d = dst;
	
    while (len > 2) {
        *d++ = basis[(s[0] >> 2) & 0x3f];
        *d++ = basis[((s[0] & 3) << 4) | (s[1] >> 4)];
        *d++ = basis[((s[1] & 0x0f) << 2) | (s[2] >> 6)];
        *d++ = basis[s[2] & 0x3f];

        s += 3;
        len -= 3;
    }

    if (len) {
        *d++ = basis[(s[0] >> 2) & 0x3f];

        if (len == 1) {
            *d++ = basis[(s[0] & 3) << 4];
            if (padding) {
                *d++ = '=';
            }

        } else {
            *d++ = basis[((s[0] & 3) << 4) | (s[1] >> 4)];
            *d++ = basis[(s[1] & 0x0f) << 2];
        }

        if (padding) {
            *d++ = '=';
        }
    }

    iRet = d - dst;
}


int api_base64_encode(uint8_t *coded_dst, const uint8_t *plain_src, 
                                   int len_plain_src)
{
    static u_char   basis64[] =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    return api_base64_encode_internal(coded_dst, plain_src, len_plain_src, basis64, 1);
}


int
api_base64_encode_url(uint8_t *coded_dst, const uint8_t *plain_src, 
                                   int len_plain_src)
{
    static u_char   basis64[] =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    return api_base64_encode_internal(coded_dst, plain_src, len_plain_src, basis64, 0);
}

int api_base64_decode(uint8_t *plain_dst, const uint8_t *coded_src, int len_coded_src)
{
    static u_char   basis64[] = {
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 62, 77, 77, 77, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 77, 77, 77, 77, 77, 77,
        77,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 77, 77, 77, 77, 77,
        77, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 77, 77, 77, 77, 77,

        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77
    };

    return api_base64_decode_internal(plain_dst, coded_src, len_coded_src, basis64);
}


int
api_base64_decode_url(uint8_t *plain_dst, const uint8_t *coded_src, int len_coded_src)
{
    static uint8_t   basis64[] = {
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 62, 77, 77,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 77, 77, 77, 77, 77, 77,
        77,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 77, 77, 77, 77, 63,
        77, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 77, 77, 77, 77, 77,

        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77,
        77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77, 77
    };

    return api_base64_decode_internal(plain_dst, coded_src, len_coded_src, basis64);
}


int
api_base64_decode_internal(uint8_t *dst, const uint8_t *src, int src_len, const uint8_t *basis)
{
	int      iRet = 0;
    size_t   len, xLen = 0;
    uint8_t *d = NULL;
	const uint8_t *s = NULL;

    for (len = 0; len < src_len; len++) {
        if (src[len] == '=') {
            break;
        }
		
        if (basis[src[len]] == 77) {
            return API_ERROR;
        }
    }
	
    if (len % 4 == 1) {
        return API_ERROR;
    }
	
    s = src;
    d = dst;

    while (len > 3) {
        *d++ = (uint8_t) (basis[s[0]] << 2 | basis[s[1]] >> 4);
        *d++ = (uint8_t) (basis[s[1]] << 4 | basis[s[2]] >> 2);
        *d++ = (uint8_t) (basis[s[2]] << 6 | basis[s[3]]);
		xLen += 3;

        s += 4;
        len -= 4;
    }
	
    if (len > 1) {
        *d++ = (uint8_t) (basis[s[0]] << 2 | basis[s[1]] >> 4);
		xLen ++;
    }
	
    if (len > 2) {
        *d++ = (uint8_t) (basis[s[1]] << 4 | basis[s[2]] >> 2);
		xLen ++;
    }
	
    iRet = d - dst;
	iRet = xLen;
    return iRet;
}
#endif
