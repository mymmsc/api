#ifndef API_VERSION_H
#define API_VERSION_H

/**
 * @file api_version.h
 * @brief API Versioning Interface
 * 
 * API's Version
 *
 * There are several different mechanisms for accessing the version. There
 * is a string form, and a set of numbers; in addition, there are constants
 * which can be compiled into your application, and you can query the library
 * being used for its actual version.
 *
 * Note that it is possible for an application to detect that it has been
 * compiled against a different version of API by use of the compile-time
 * constants and the use of the run-time query function.
 *
 * API version numbering follows the guidelines specified in:
 *
 *     http://api.apache.org/versioning.html
 */


#define API_COPYRIGHT "Copyright (c) 2000-2014 The Apache Software " \
                      "Foundation or its licensors, as applicable."

/* The numeric compile-time version constants. These constants are the
 * authoritative version numbers for API. 
 */

/** major version 
 * Major API changes that could cause compatibility problems for older
 * programs such as structure size changes.  No binary compatibility is
 * possible across a change in the major version.
 */
#define API_MAJOR_VERSION       2

/** minor version
 * Minor API changes that do not cause binary compatibility problems.
 * Reset to 0 when upgrading API_MAJOR_VERSION
 */
#define API_MINOR_VERSION       0

/** patch level 
 * The Patch Level never includes API changes, simply bug fixes.
 * Reset to 0 when upgrading API_MINOR_VERSION
 */
#define API_PATCH_VERSION       0

/** 
 * The symbol API_IS_DEV_VERSION is only defined for internal,
 * "development" copies of API.  It is undefined for released versions
 * of API.
 */
#define API_IS_DEV_VERSION

/**
 * Check at compile time if the API version is at least a certain
 * level.
 * @param major The major version component of the version checked
 * for (e.g., the "1" of "1.3.0").
 * @param minor The minor version component of the version checked
 * for (e.g., the "3" of "1.3.0").
 * @param patch The patch level component of the version checked
 * for (e.g., the "0" of "1.3.0").
 * @remark This macro is available with API versions starting with
 * 1.3.0.
 */
#define API_VERSION_AT_LEAST(major,minor,patch)                    \
(((major) < API_MAJOR_VERSION)                                     \
 || ((major) == API_MAJOR_VERSION && (minor) < API_MINOR_VERSION) \
 || ((major) == API_MAJOR_VERSION && (minor) == API_MINOR_VERSION && (patch) <= API_PATCH_VERSION))

#if defined(API_IS_DEV_VERSION) || defined(DOXYGEN)
/** Internal: string form of the "is dev" flag */
#ifndef API_IS_DEV_STRING
#define API_IS_DEV_STRING "-dev"
#endif
#else
#define API_IS_DEV_STRING ""
#endif

/* API_STRINGIFY is defined here, and also in api_general.h, so wrap it */
#ifndef API_STRINGIFY
/** Properly quote a value as a string in the C preprocessor */
#define API_STRINGIFY(n) API_STRINGIFY_HELPER(n)
/** Helper macro for API_STRINGIFY */
#define API_STRINGIFY_HELPER(n) #n
#endif

/** The formatted string of API's version */
#define API_VERSION_STRING \
     API_STRINGIFY(API_MAJOR_VERSION) "." \
     API_STRINGIFY(API_MINOR_VERSION) "." \
     API_STRINGIFY(API_PATCH_VERSION) \
     API_IS_DEV_STRING

/** An alternative formatted string of API's version */
/* macro for Win32 .rc files using numeric csv representation */
#define API_VERSION_STRING_CSV API_MAJOR_VERSION, \
                               API_MINOR_VERSION, \
                               API_PATCH_VERSION


#ifndef API_VERSION_ONLY

/* The C language API to access the version at run time, 
 * as opposed to compile time.  API_VERSION_ONLY may be defined 
 * externally when preprocessing api_version.h to obtain strictly 
 * the C Preprocessor macro declarations.
 */

#include "api.h"

#ifdef __cplusplus
extern "C" {
#endif

/** 
 * The numeric version information is broken out into fields within this 
 * structure. 
 */
typedef struct {
    int major;      /**< major number */
    int minor;      /**< minor number */
    int patch;      /**< patch number */
    int is_dev;     /**< is development (1 or 0) */
} api_version_t;

/**
 * Return API's version information information in a numeric form.
 *
 *  @param pvsn Pointer to a version structure for returning the version
 *              information.
 */
API_DECLARE(void) api_version(api_version_t *pvsn);

/** Return API's version information as a string. */
API_DECLARE(const char *) api_version_string(void);

#ifdef __cplusplus
}
#endif

#endif /* ndef API_VERSION_ONLY */

#endif /* ndef API_VERSION_H */
