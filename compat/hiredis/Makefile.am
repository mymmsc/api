noinst_LTLIBRARIES = libhr.la

libhr_la_SOURCES = hiredis.c hiredis_async.c hiredis_dict.c \
						hiredis_net.c hiredis_sds.c hiredis_locker.c lua_scripts.h

libhr_la_CFLAGS  = -I$(top_srcdir)/include

lua_scripts.h: scripts/lock.lua scripts/unlock.lua mkscripts.py
	python mkscripts.py > $@