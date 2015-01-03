# the samples is tcp sock5

noinst_PROGRAMS  = socks5
socks5_SOURCES   = libconfig.c liblog.c libthread.c socks5.c
socks5_INCLUDES  = -I$(top_srcdir)/include -I$(top_srcdir)/event
socks5_CFLAGS    =  $(CFLAGS) $(socks5_INCLUDES)
socks5_LDADD     = $(top_srcdir)/event/libevent.la $(top_srcdir)/compat/libcompat.la $(top_srcdir)/base/libbase.la -lpthread

