SUBDIRS          = 

noinst_LTLIBRARIES = libcompat.la

private_srcs = memwatch.c rbtree.c consistent_hash.c
SYS_LIBS  = 
LTLIBOBJS = 

libcompat_la_SOURCES = $(private_srcs)
libcompat_la_LIBADD  = $(LTLIBOBJS) $(SYS_LIBS)
libcompat_la_CFLAGS  = -I$(top_srcdir)/include -I$(prefix)/include

