noinst_PROGRAMS  = abts test-spinlock

abts_SOURCES   = abts.c testutil.c test_atomic.c
abts_INCLUDES  = -I$(top_srcdir)/include
abts_CFLAGS    =  $(CFLAGS) $(abts_INCLUDES)
abts_LDADD     = $(top_srcdir)/base/libbase.la $(top_srcdir)/compat/libcompat.la -lpthread

test_spinlock_SOURCES = test_spinlock.c
test_spinlock_CFLAGS  = $(CFLAGS) $(abts_INCLUDES)
test_spinlock_LDADD   = $(top_srcdir)/base/libbase.la  $(top_srcdir)/compat/libcompat.la -lpthread
