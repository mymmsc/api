AUTOMAKE_OPTIONS = dist-bzip2 foreign 1.9 subdir-objects
SUBDIRS          = base event compat samples test
ACLOCAL_AMFLAGS  = -I build

RELEASE          = -release 1.0
VERSION_INFO     = 5:0:0

pkgconfigdir     = $(libdir)/pkgconfig
LIBAPI_PKGCONFIG = libapi.pc

LIBAPI_LIBS_LA   = libapi.la

if INSTALL_LIBAPI
lib_LTLIBRARIES = $(LIBAPI_LIBS_LA)
pkgconfig_DATA  = $(LIBAPI_PKGCONFIG)
else
noinst_LTLIBRARIES =  $(LIBAPI_LIBS_LA)
endif

EXTRA_SOURCE    =
noinst_HEADERS  =
noinst_PROGRAMS =
EXTRA_PROGRAMS  =
CLEANFILES      =
DISTCLEANFILES  =
BUILT_SOURCES   =

include include/include.am

GENERIC_LDFLAGS = -version-info $(VERSION_INFO) $(RELEASE) $(NO_UNDEFINED)

SYS_LIBS  =
LTLIBOBJS = base/libbase.la event/libevent.la

libapi_la_SOURCES = $(CORE_SRC)
libapi_la_LIBADD = $(LTLIBOBJS) $(SYS_LIBS)
libapi_la_LDFLAGS = $(GENERIC_LDFLAGS)

API1_HDRS = \
	include/api.h

if INSTALL_LIBAPI
include_HEADERS = $(API1_HDRS)
else
noinst_HEADERS += $(API1_HDRS)
endif

AM_CPPFLAGS = -I$(srcdir)/include $(SYS_INCLUDES)

verify: check

doxygen: FORCE
	doxygen $(srcdir)/Doxyfile
FORCE:

DISTCLEANFILES += *~ libapi.pc

