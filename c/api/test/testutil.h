#ifndef API_TEST_UTIL
#define API_TEST_UTIL
//////////////////////////////////////////////////////////////////////////////////////////

#include "api_errno.h"
#include "abts.h"
//////////////////////////////////////////////////////////////////////////////////////////

/* XXX: FIXME - these all should become much more utilitarian 
 * and part of aio, itself
 */

#ifdef WIN32
#ifdef BINPATH
#define TESTBINPATH AIO_STRINGIFY(BINPATH) "/"
#else
#define TESTBINPATH ""
#endif
#else
#define TESTBINPATH "./"
#endif

#ifdef WIN32
#define EXTENSION ".exe"
#elif NETWARE
#define EXTENSION ".nlm"
#else
#define EXTENSION
#endif

#define STRING_MAX 8096

//////////////////////////////////////////////////////////////////////////////////////////

/* Some simple functions to make the test apps easier to write and
 * a bit more consistent...
 */

//extern aio_pool_t *p;

/* Assert that RV is an AIO_SUCCESS value; else fail giving strerror
 * for RV and CONTEXT message. */
void aio_assert_success(abts_case* tc, const char *context, 
                        api_status_t rv, int lineno);
#define AIO_ASSERT_SUCCESS(tc, ctxt, rv) \
             aio_assert_success(tc, ctxt, rv, __LINE__)

void initialize(void);

abts_suite *test_atomic(abts_suite *suite);
//abts_suite *test_hash(abts_suite *suite);

//////////////////////////////////////////////////////////////////////////////////////////

#endif /* API_TEST_INCLUDES */
