dnl Hard-coded top of config.h:
AH_TOP([
#ifndef API_PRIVATE_H
#define API_PRIVATE_H

/* Pick up publicly advertised headers and symbols before the
 * API internal private headers and symbols
 */
#include <api.h>
])

dnl Hard-coded inclusion at the tail end of api_private.h:
AH_BOTTOM([
/* switch this on if we have a BeOS version below BONE */
#if defined(BEOS) && !defined(HAVE_BONE_VERSION)
#define BEOS_R5 1
#else
#define BEOS_BONE 1
#endif

/*
 * Darwin 10's default compiler (gcc42) builds for both 64 and
 * 32 bit architectures unless specifically told not to.
 * In those cases, we need to override types depending on how
 * we're being built at compile time.
 * NOTE: This is an ugly work-around for Darwin's
 * concept of universal binaries, a single package
 * (executable, lib, etc...) which contains both 32
 * and 64 bit versions. The issue is that if API is
 * built universally, if something else is compiled
 * against it, some bit sizes will depend on whether
 * it is 32 or 64 bit. This is determined by the __LP64__
 * flag. Since we need to support both, we have to
 * handle OS X unqiuely.
 */
#ifdef DARWIN_10

#undef API_OFF_T_STRFN
#undef API_INT64_STRFN
#undef SIZEOF_LONG
#undef SIZEOF_SIZE_T
#undef SIZEOF_SSIZE_T
#undef SIZEOF_VOIDP
#undef SIZEOF_STRUCT_IOVEC

#ifdef __LP64__
 #define API_INT64_STRFN strtol
 #define SIZEOF_LONG    8
 #define SIZEOF_SIZE_T  8
 #define SIZEOF_SSIZE_T 8
 #define SIZEOF_VOIDP   8
 #define SIZEOF_STRUCT_IOVEC 16
#else
 #define API_INT64_STRFN strtoll
 #define SIZEOF_LONG    4
 #define SIZEOF_SIZE_T  4
 #define SIZEOF_SSIZE_T 4
 #define SIZEOF_VOIDP   4
 #define SIZEOF_STRUCT_IOVEC 8
#endif

#undef API_OFF_T_STRFN
#define API_OFF_T_STRFN API_INT64_STRFN
 

#undef SETPGRP_VOID
#ifdef __DARWIN_UNIX03
 #define SETPGRP_VOID 1
#else
/* #undef SETPGRP_VOID */
#endif
 
#endif /* DARWIN_10 */

#endif /* API_PRIVATE_H */
])

