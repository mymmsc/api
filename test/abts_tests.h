#ifndef __API_TEST_INCLUDES__
#define __API_TEST_INCLUDES__

#include "abts.h"
#include "testutil.h"

const struct testlist {
    abts_suite *(*func)(abts_suite *suite);
} alltests[] = {
    {test_atomic}
};

#endif /* __API_TEST_INCLUDES__ */
