#include "testutil.h"
#include <api_atomic.h>


static void test_atomic_set(abts_case *tc, void *data)
{
	int rv = 0;
    atomic_t val = 0;
	if(atomic_cas(val, 0, 1)) {
		rv = 1;
	} else {
		rv = 0;
	}
	ABTS_ASSERT(tc, "set-1", rv == 1);
	ABTS_ASSERT(tc, "set-2", val == 1);
	ABTS_INT_EQUAL(tc, 1, rv);
}

static void test_atomic_add(abts_case *tc, void *data)
{
    int rv = 0;
	atomic_t val = 2;
	rv = atomic_add(val, 3);
	ABTS_ASSERT(tc, "add-1", rv == 2);
	ABTS_ASSERT(tc, "add-2", val == 5);
	ABTS_INT_EQUAL(tc, 2, rv);
}

abts_suite * test_atomic(abts_suite *suite)
{
    suite = ADD_SUITE(suite);
    
    abts_run_test(suite, test_atomic_set, NULL);
	abts_run_test(suite, test_atomic_add, NULL);
    return suite;
}
