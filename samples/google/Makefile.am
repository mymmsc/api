# the samples is RTB-Google

noinst_PROGRAMS  = rtb-google
rtb_google_SOURCES   = rtb-google.c
rtb_google_INCLUDES  = -I$(top_srcdir)/include -I$(top_srcdir) -I$(top_srcdir)/event
rtb_google_CFLAGS    =  $(CFLAGS) $(rtb_google_INCLUDES)
rtb_google_LDADD     = $(top_srcdir)/base/libbase.la $(top_srcdir)/compat/libcompat.la $(top_srcdir)/modules/libproto.la -lpthread -ljson-c -lmm

