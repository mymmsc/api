#!/bin/sh
if [ -x "`which autoreconf 2>/dev/null`" ] ; then
   exec autoreconf -imvf
fi

mkdir -p include/api/arch/unix
LIBTOOLIZE=libtoolize
SYSNAME=`uname`
if [ "x$SYSNAME" = "xDarwin" ] ; then
  LIBTOOLIZE=glibtoolize
fi
aclocal -I build && \
	autoheader && \
	$LIBTOOLIZE && \
	autoconf && \
	automake --add-missing --force-missing --copy
