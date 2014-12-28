#ifndef API_TEST_INCLUDES
#define API_TEST_INCLUDES

#include "abts.h"
#include "testutil.h"

const struct testlist {
    abts_suite *(*func)(abts_suite *suite);
} alltests[] = {
    {test_atomic}
};

#endif /* API_TEST_INCLUDES */
