# Microsoft Developer Studio Project File - Name="libapi" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=libapi - Win32 Release
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "libapi.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "libapi.mak" CFG="libapi - Win32 Release"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "libapi - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "libapi - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "libapi - x64 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "libapi - x64 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "libapi - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MD /W3 /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /FD /c
# ADD CPP /nologo /MD /W3 /Zi /O2 /Oy- /I "./include" /I "./include/private" /I "./include/arch/win32" /I "./include/arch/unix" /I "../expat/lib" /D "NDEBUG" /D "API_DECLARE_EXPORT" /D "WIN32" /D "WINNT" /D "_WINDOWS" /Fo"$(INTDIR)\" /Fd"$(INTDIR)\libapi_src" /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /o /win32 "NUL"
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /o /win32 "NUL"
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /i "./include" /d "NDEBUG" /d "API_VERSION_ONLY"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /opt:ref
# ADD LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib libexpat.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /libpath:"..\expat\win32\bin\Release" /out:"Release\libapi-2.dll" /pdb:"Release\libapi-2.pdb" /implib:"Release\libapi-2.lib" /MACHINE:X86 /opt:ref
# Begin Special Build Tool
TargetPath=Release\libapi-2.dll
SOURCE="$(InputPath)"
PostBuild_Desc=Embed .manifest
PostBuild_Cmds=if exist $(TargetPath).manifest mt.exe -manifest $(TargetPath).manifest -outputresource:$(TargetPath);2
# End Special Build Tool

!ELSEIF  "$(CFG)" == "libapi - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Zi /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /FD /EHsc /c
# ADD CPP /nologo /MDd /W3 /Zi /Od /I "./include" /I "./include/private" /I "./include/arch/win32" /I "./include/arch/unix" /I "../expat/lib" /D "_DEBUG" /D "API_DECLARE_EXPORT" /D "WIN32" /D "WINNT" /D "_WINDOWS" /Fo"$(INTDIR)\" /Fd"$(INTDIR)\libapi_src" /FD /EHsc /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /o /win32 "NUL"
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /o /win32 "NUL"
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /i "./include" /d "_DEBUG" /d "API_VERSION_ONLY"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug
# ADD LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib libexpat.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /libpath:"..\expat\win32\bin\Debug" /out:"Debug\libapi-2.dll" /pdb:"Debug\libapi-2.pdb" /implib:"Debug\libapi-2.lib" /MACHINE:X86
# Begin Special Build Tool
TargetPath=Debug\libapi-2.dll
SOURCE="$(InputPath)"
PostBuild_Desc=Embed .manifest
PostBuild_Cmds=if exist $(TargetPath).manifest mt.exe -manifest $(TargetPath).manifest -outputresource:$(TargetPath);2
# End Special Build Tool

!ELSEIF  "$(CFG)" == "libapi - x64 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "x64\Release"
# PROP BASE Intermediate_Dir "x64\Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "x64\Release"
# PROP Intermediate_Dir "x64\Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MD /W3 /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /FD /c
# ADD CPP /nologo /MD /W3 /Zi /O2 /Oy- /I "./include" /I "./include/private" /I "./include/arch/win32" /I "./include/arch/unix" /I "../expat/lib" /D "NDEBUG" /D "API_DECLARE_EXPORT" /D "WIN32" /D "WINNT" /D "_WINDOWS" /Fo"$(INTDIR)\" /Fd"$(INTDIR)\libapi_src" /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /o /win32 "NUL"
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /o /win32 "NUL"
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /i "./include" /d "NDEBUG" /d "API_VERSION_ONLY"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /opt:ref
# ADD LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib libexpat.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /libpath:"..\expat\win32\bin\x64\Release" /out:"x64\Release\libapi-2.dll" /pdb:"x64\Release\libapi-2.pdb" /implib:"x64\Release\libapi-2.lib" /MACHINE:X64 /opt:ref
# Begin Special Build Tool
TargetPath=x64\Release\libapi-2.dll
SOURCE="$(InputPath)"
PostBuild_Desc=Embed .manifest
PostBuild_Cmds=if exist $(TargetPath).manifest mt.exe -manifest $(TargetPath).manifest -outputresource:$(TargetPath);2
# End Special Build Tool

!ELSEIF  "$(CFG)" == "libapi - x64 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "x64\Debug"
# PROP BASE Intermediate_Dir "x64\Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "x64\Debug"
# PROP Intermediate_Dir "x64\Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Zi /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /FD /EHsc /c
# ADD CPP /nologo /MDd /W3 /Zi /Od /I "./include" /I "./include/private" /I "./include/arch/win32" /I "./include/arch/unix" /I "../expat/lib" /D "_DEBUG" /D "API_DECLARE_EXPORT" /D "WIN32" /D "WINNT" /D "_WINDOWS" /Fo"$(INTDIR)\" /Fd"$(INTDIR)\libapi_src" /FD /EHsc /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /o /win32 "NUL"
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /o /win32 "NUL"
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /i "./include" /d "_DEBUG" /d "API_VERSION_ONLY"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug
# ADD LINK32 kernel32.lib advapi32.lib ws2_32.lib mswsock.lib ole32.lib shell32.lib rpcrt4.lib libexpat.lib /nologo /base:"0x6EEC0000" /subsystem:windows /dll /incremental:no /debug /libpath:"..\expat\win32\bin\x64\Debug" /out:"x64\Debug\libapi-2.dll" /pdb:"x64\Debug\libapi-2.pdb" /implib:"x64\Debug\libapi-2.lib" /MACHINE:X64
# Begin Special Build Tool
TargetPath=x64\Debug\libapi-2.dll
SOURCE="$(InputPath)"
PostBuild_Desc=Embed .manifest
PostBuild_Cmds=if exist $(TargetPath).manifest mt.exe -manifest $(TargetPath).manifest -outputresource:$(TargetPath);2
# End Special Build Tool

!ENDIF 

# Begin Target

# Name "libapi - Win32 Release"
# Name "libapi - Win32 Debug"
# Name "libapi - x64 Release"
# Name "libapi - x64 Debug"
# Begin Group "Source Files"

# PROP Default_Filter ".c"
# Begin Group "atomic"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\atomic\win32\api_atomic.c
# End Source File
# End Group
# Begin Group "buckets"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\buckets\api_brigade.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_alloc.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_eos.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_file.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_flush.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_heap.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_mmap.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_pipe.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_pool.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_refcount.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_simple.c
# End Source File
# Begin Source File

SOURCE=.\buckets\api_buckets_socket.c
# End Source File
# End Group
# Begin Group "crypto"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\crypto\api_crypto.c
# End Source File
# Begin Source File

SOURCE=.\crypto\api_md4.c
# End Source File
# Begin Source File

SOURCE=.\crypto\api_md5.c
# End Source File
# Begin Source File

SOURCE=.\crypto\api_passwd.c
# End Source File
# Begin Source File

SOURCE=.\crypto\api_sha1.c
# End Source File
# Begin Source File

SOURCE=.\crypto\crypt_blowfish.c
# End Source File
# Begin Source File

SOURCE=.\crypto\crypt_blowfish.h
# End Source File
# Begin Source File

SOURCE=.\crypto\getuuid.c
# End Source File
# Begin Source File

SOURCE=.\crypto\uuid.c
# End Source File
# End Group
# Begin Group "dbd"
# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dbd\api_dbd.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_mysql.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_odbc.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_oracle.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_pgsql.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_sqlite2.c
# End Source File
# Begin Source File

SOURCE=.\dbd\api_dbd_sqlite3.c
# End Source File
# End Group
# Begin Group "dbm"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dbm\api_dbm.c
# End Source File
# Begin Source File

SOURCE=.\dbm\api_dbm_berkeleydb.c
# End Source File
# Begin Source File

SOURCE=.\dbm\api_dbm_gdbm.c
# End Source File
# Begin Source File

SOURCE=.\dbm\api_dbm_sdbm.c
# End Source File
# End Group
# Begin Group "dso"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dso\win32\dso.c
# End Source File
# End Group
# Begin Group "encoding"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\encoding\api_base64.c
# End Source File
# End Group
# Begin Group "file_io"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\file_io\win32\buffer.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\copy.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\dir.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\fileacc.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\filedup.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\filepath.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\filepath_util.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\filestat.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\filesys.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\flock.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\fullrw.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\mktemp.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\open.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\pipe.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\readwrite.c
# End Source File
# Begin Source File

SOURCE=.\file_io\win32\seek.c
# End Source File
# Begin Source File

SOURCE=.\file_io\unix\tempdir.c
# End Source File
# End Group
# Begin Group "hooks"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\hooks\api_hooks.c
# End Source File
# End Group
# Begin Group "locks"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\locks\win32\proc_mutex.c
# End Source File
# Begin Source File

SOURCE=.\locks\win32\thread_cond.c
# End Source File
# Begin Source File

SOURCE=.\locks\win32\thread_mutex.c
# End Source File
# Begin Source File

SOURCE=.\locks\win32\thread_rwlock.c
# End Source File
# End Group
# Begin Group "memcache"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\memcache\api_memcache.c
# End Source File
# End Group
# Begin Group "memory"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\memory\unix\api_pools.c
# End Source File
# End Group
# Begin Group "misc"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\misc\win32\api_app.c
# PROP Exclude_From_Build 1
# End Source File
# Begin Source File

SOURCE=.\misc\win32\charset.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\env.c
# End Source File
# Begin Source File

SOURCE=.\misc\unix\errorcodes.c
# End Source File
# Begin Source File

SOURCE=.\misc\unix\getopt.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\internal.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\misc.c
# End Source File
# Begin Source File

SOURCE=.\misc\unix\otherchild.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\rand.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\start.c
# End Source File
# Begin Source File

SOURCE=.\misc\win32\utf8.c
# End Source File
# Begin Source File

SOURCE=.\misc\unix\version.c
# End Source File
# End Group
# Begin Group "mmap"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\mmap\unix\common.c
# End Source File
# Begin Source File

SOURCE=.\mmap\win32\mmap.c
# End Source File
# End Group
# Begin Group "network_io"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\network_io\unix\inet_ntop.c
# End Source File
# Begin Source File

SOURCE=.\network_io\unix\inet_pton.c
# End Source File
# Begin Source File

SOURCE=.\network_io\unix\multicast.c
# End Source File
# Begin Source File

SOURCE=.\network_io\win32\sendrecv.c
# End Source File
# Begin Source File

SOURCE=.\network_io\unix\sockaddr.c
# End Source File
# Begin Source File

SOURCE=.\network_io\win32\sockets.c
# End Source File
# Begin Source File

SOURCE=.\network_io\unix\socket_util.c
# End Source File
# Begin Source File

SOURCE=.\network_io\win32\sockopt.c
# End Source File
# End Group
# Begin Group "passwd"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\passwd\api_getpass.c
# End Source File
# End Group
# Begin Group "poll"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\poll\unix\pollcb.c
# End Source File
# Begin Source File

SOURCE=.\poll\unix\pollset.c
# End Source File
# Begin Source File

SOURCE=.\poll\unix\poll.c
# End Source File
# Begin Source File

SOURCE=.\poll\unix\select.c
# End Source File
# Begin Source File

SOURCE=.\poll\unix\wakeup.c
# End Source File
# End Group
# Begin Group "random"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\random\unix\api_random.c
# End Source File
# Begin Source File

SOURCE=.\random\unix\sha2.c
# End Source File
# Begin Source File

SOURCE=.\random\unix\sha2_glue.c
# End Source File
# End Group
# Begin Group "sdbm"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm.c
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_hash.c
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_lock.c
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_pair.c
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_pair.h
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_private.h
# End Source File
# Begin Source File

SOURCE=.\dbm\sdbm\sdbm_tune.h
# End Source File
# End Group
# Begin Group "shmem"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\shmem\win32\shm.c
# End Source File
# End Group
# Begin Group "strings"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\strings\api_cpystrn.c
# End Source File
# Begin Source File

SOURCE=.\strings\api_fnmatch.c
# End Source File
# Begin Source File

SOURCE=.\strings\api_snprintf.c
# End Source File
# Begin Source File

SOURCE=.\strings\api_strings.c
# End Source File
# Begin Source File

SOURCE=.\strings\api_strnatcmp.c
# End Source File
# Begin Source File

SOURCE=.\strings\api_strtok.c
# End Source File
# End Group
# Begin Group "strmatch"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\strmatch\api_strmatch.c
# End Source File
# End Group
# Begin Group "tables"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\tables\api_hash.c
# Begin Source File

SOURCE=.\tables\api_tables.c
# End Source File
# Begin Source File

SOURCE=.\tables\api_skiplist.c
# End Source File
# End Group
# Begin Group "threadproc"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\threadproc\win32\proc.c
# End Source File
# Begin Source File

SOURCE=.\threadproc\win32\signals.c
# End Source File
# Begin Source File

SOURCE=.\threadproc\win32\thread.c
# End Source File
# Begin Source File

SOURCE=.\threadproc\win32\threadpriv.c
# End Source File
# End Group
# Begin Group "time"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\time\win32\time.c
# End Source File
# Begin Source File

SOURCE=.\time\win32\timestr.c
# End Source File
# End Group
# Begin Group "uri"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\uri\api_uri.c
# End Source File
# End Group
# Begin Group "user"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\user\win32\groupinfo.c
# End Source File
# Begin Source File

SOURCE=.\user\win32\userinfo.c
# End Source File
# End Group
# Begin Group "util-misc"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\util-misc\api_date.c
# End Source File
# Begin Source File

SOURCE=.\util-misc\apu_dso.c
# End Source File
# Begin Source File

SOURCE=.\util-misc\api_queue.c
# End Source File
# Begin Source File

SOURCE=.\util-misc\api_reslist.c
# End Source File
# Begin Source File

SOURCE=.\util-misc\api_rmm.c
# End Source File
# Begin Source File

SOURCE=.\util-misc\api_thread_pool.c
# End Source File
# End Group
# Begin Group "xlate"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\xlate\xlate.c
# End Source File
# End Group
# Begin Group "xml"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\xml\api_xml.c
# End Source File
# Begin Source File

SOURCE=.\xml\api_xml_expat.c
# End Source File
# End Group
# End Group
# Begin Group "Private Header Files"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_atime.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_dso.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_file_io.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_inherit.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_misc.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_networkio.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_thread_mutex.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_thread_rwlock.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_threadproc.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_arch_utf8.h
# End Source File
# Begin Source File

SOURCE=.\include\arch\win32\api_private.h
# End Source File
# End Group
# Begin Group "Public Header Files"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\include\api.h.in
# PROP Exclude_From_Build 1
# End Source File
# Begin Source File

SOURCE=.\include\api.hnw
# PROP Exclude_From_Build 1
# End Source File
# Begin Source File

SOURCE=.\include\api.hw

!IF  "$(CFG)" == "libapi - Win32 Release"

# Begin Custom Build - Creating api.h from api.hw
InputPath=.\include\api.hw

".\include\api.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\api.hw > .\include\api.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - Win32 Debug"

# Begin Custom Build - Creating api.h from api.hw
InputPath=.\include\api.hw

".\include\api.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\api.hw > .\include\api.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Release"

# Begin Custom Build - Creating api.h from api.hw
InputPath=.\include\api.hw

".\include\api.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\api.hw > .\include\api.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Debug"

# Begin Custom Build - Creating api.h from api.hw
InputPath=.\include\api.hw

".\include\api.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\api.hw > .\include\api.h

# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\include\api_allocator.h
# End Source File
# Begin Source File

SOURCE=.\include\api_atomic.h
# End Source File
# Begin Source File

SOURCE=.\include\api_dso.h
# End Source File
# Begin Source File

SOURCE=.\include\api_env.h
# End Source File
# Begin Source File

SOURCE=.\include\api_errno.h
# End Source File
# Begin Source File

SOURCE=.\include\api_file_info.h
# End Source File
# Begin Source File

SOURCE=.\include\api_file_io.h
# End Source File
# Begin Source File

SOURCE=.\include\api_fnmatch.h
# End Source File
# Begin Source File

SOURCE=.\include\api_general.h
# End Source File
# Begin Source File

SOURCE=.\include\api_getopt.h
# End Source File
# Begin Source File

SOURCE=.\include\api_global_mutex.h
# End Source File
# Begin Source File

SOURCE=.\include\api_hash.h
# End Source File
# Begin Source File

SOURCE=.\include\api_inherit.h
# End Source File
# Begin Source File

SOURCE=.\include\api_lib.h
# End Source File
# Begin Source File

SOURCE=.\include\api_mmap.h
# End Source File
# Begin Source File

SOURCE=.\include\api_network_io.h
# End Source File
# Begin Source File

SOURCE=.\include\api_poll.h
# End Source File
# Begin Source File

SOURCE=.\include\api_pools.h
# End Source File
# Begin Source File

SOURCE=.\include\api_portable.h
# End Source File
# Begin Source File

SOURCE=.\include\api_proc_mutex.h
# End Source File
# Begin Source File

SOURCE=.\include\api_random.h
# End Source File
# Begin Source File

SOURCE=.\include\api_ring.h
# End Source File
# Begin Source File

SOURCE=.\include\api_shm.h
# End Source File
# Begin Source File

SOURCE=.\include\api_signal.h
# End Source File
# Begin Source File

SOURCE=.\include\api_skiplist.h
# End Source File
# Begin Source File

SOURCE=.\include\api_strings.h
# End Source File
# Begin Source File

SOURCE=.\include\api_support.h
# End Source File
# Begin Source File

SOURCE=.\include\api_tables.h
# End Source File
# Begin Source File

SOURCE=.\include\api_thread_cond.h
# End Source File
# Begin Source File

SOURCE=.\include\api_thread_mutex.h
# End Source File
# Begin Source File

SOURCE=.\include\api_thread_proc.h
# End Source File
# Begin Source File

SOURCE=.\include\api_thread_rwlock.h
# End Source File
# Begin Source File

SOURCE=.\include\api_time.h
# End Source File
# Begin Source File

SOURCE=.\include\api_user.h
# End Source File
# Begin Source File

SOURCE=.\include\api_version.h
# End Source File
# Begin Source File

SOURCE=.\include\api_want.h
# End Source File
# Begin Source File

SOURCE=.\include\apu.h
# End Source File
# Begin Source File

SOURCE=.\include\private\apu_select_dbm.h.in
# End Source File
# Begin Source File

SOURCE=.\include\private\apu_select_dbm.hw

!IF  "$(CFG)" == "libapi - Win32 Release"

# Begin Custom Build - Creating apu_select_dbm.h from apu_select_dbm.hw
InputPath=.\include\private\apu_select_dbm.hw

".\include\private\apu_select_dbm.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\private\apu_select_dbm.hw > .\include\private\apu_select_dbm.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - Win32 Debug"

# Begin Custom Build - Creating apu_select_dbm.h from apu_select_dbm.hw
InputPath=.\include\private\apu_select_dbm.hw

".\include\private\apu_select_dbm.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\private\apu_select_dbm.hw > .\include\private\apu_select_dbm.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Release"

# Begin Custom Build - Creating apu_select_dbm.h from apu_select_dbm.hw
InputPath=.\include\private\apu_select_dbm.hw

".\include\private\apu_select_dbm.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\private\apu_select_dbm.hw > .\include\private\apu_select_dbm.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Debug"

# Begin Custom Build - Creating apu_select_dbm.h from apu_select_dbm.hw
InputPath=.\include\private\apu_select_dbm.hw

".\include\private\apu_select_dbm.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\private\apu_select_dbm.hw > .\include\private\apu_select_dbm.h

# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\include\apu_want.h.in
# End Source File
# Begin Source File

SOURCE=.\include\apu_want.hnw
# End Source File
# Begin Source File

SOURCE=.\include\apu_want.hw

!IF  "$(CFG)" == "libapi - Win32 Release"

# Begin Custom Build - Creating apu_want.h from apu_want.hw
InputPath=.\include\apu_want.hw

".\include\apu_want.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\apu_want.hw > .\include\apu_want.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - Win32 Debug"

# Begin Custom Build - Creating apu_want.h from apu_want.hw
InputPath=.\include\apu_want.hw

".\include\apu_want.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\apu_want.hw > .\include\apu_want.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Release"

# Begin Custom Build - Creating apu_want.h from apu_want.hw
InputPath=.\include\apu_want.hw

".\include\apu_want.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\apu_want.hw > .\include\apu_want.h

# End Custom Build

!ELSEIF  "$(CFG)" == "libapi - x64 Debug"

# Begin Custom Build - Creating apu_want.h from apu_want.hw
InputPath=.\include\apu_want.hw

".\include\apu_want.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	type .\include\apu_want.hw > .\include\apu_want.h

# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Source File

SOURCE=.\libapi.rc
# End Source File
# End Target
# End Project
