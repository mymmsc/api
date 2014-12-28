#include <api/file.h>
#include <api/strings.h>
#if API_HAVE_DIRENT_H
#include <dirent.h>
#endif
#if API_HAVE_SYS_SYSLIMITS_H
#include <sys/syslimits.h>
#endif
#if API_HAVE_LIMITS_H
#include <limits.h>
#endif
#if API_HAVE_UNISTD_H
#include <unistd.h>
#endif
//////////////////////////////////////////////////////////////////////////////////////////

#ifndef F_OK
#define F_OK (0)
#endif

status_t api_dir_exist(const char *dname)
{
	status_t rc = API_SUCCESS;
	if(access(dname, F_OK) != 0 ) {
		rc = errno;
	}
	return rc;
}
	

status_t api_mkdirs(const char *pathname)
{
	status_t rc = API_SUCCESS;
	char dName[1024];
	int i, len;
	
	memset(dName, 0x00, sizeof(dName));
	api_cpystrn(dName, pathname, sizeof(dName));
	len = api_strlen(dName);
	if(dName[len - 1] != '/') {
		dName[len] = '/';
	}
	len = api_strlen(dName);
	for(i = 1; i < len; i++) {
		if(dName[i] == '/') {
			dName[i] = 0;
			if(access(dName, F_OK) != 0 ) {
				if(mkdir(dName
				#ifndef _WIN32
					, S_IRWXU
				#endif
					) == -1) {
					rc = -1;
				}
			}
			dName[i] = '/';
		}
	}
	return 0;
}

//////////////////////////////////////////////////////////////////////////////////////////

