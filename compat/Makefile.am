SUBDIRS          = conhash hiredis mtree

noinst_LTLIBRARIES = libcompat.la

private_srcs = protobuf-c.c adapter.c data.c parser.c storage.c filter.c decrypt.c \
				code.c session.c 
SYS_LIBS  = $(top_srcdir)/base/libbase.la
LTLIBOBJS = conhash/libch.la hiredis/libhr.la mtree/libmt.la -lssl

libcompat_la_SOURCES = $(private_srcs)
libcompat_la_LIBADD  = $(LTLIBOBJS) $(SYS_LIBS)
libcompat_la_CFLAGS  = -I$(top_srcdir)/include -I$(prefix)/include

