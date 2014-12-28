#include "api/strings.h"

#if API_HAVE_NETDB_H
#include <netdb.h>
#endif
#ifdef HAVE_DLFCN_H
#include <dlfcn.h>
#endif

/*
 * stuffbuffer - like api_cpystrn() but returns the address of the
 * dest buffer instead of the address of the terminating '\0'
 */
static char *stuffbuffer(char *buf, api_size_t bufsize, const char *s)
{
    api_cpystrn(buf,s,bufsize);
    return buf;
}

static char *api_error_string(api_status_t statcode)
{
    switch (statcode) {
    case API_ENOSTAT:
        return "Could not perform a stat on the file.";
    case API_ENOPOOL:
        return "A new pool could not be created.";
    case API_EBADDATE:
        return "An invalid date has been provided";
    case API_EINVALSOCK:
        return "An invalid socket was returned";
    case API_ENOPROC:
        return "No process was provided and one was required.";
    case API_ENOTIME:
        return "No time was provided and one was required.";
    case API_ENODIR:
        return "No directory was provided and one was required.";
    case API_ENOLOCK:
        return "No lock was provided and one was required.";
    case API_ENOPOLL:
        return "No poll structure was provided and one was required.";
    case API_ENOSOCKET:
        return "No socket was provided and one was required.";
    case API_ENOTHREAD:
        return "No thread was provided and one was required.";
    case API_ENOTHDKEY:
        return "No thread key structure was provided and one was required.";
    case API_ENOSHMAVAIL:
        return "No shared memory is currently available";
    case API_EDSOOPEN:
#if API_HAS_DSO && defined(HAVE_LIBDL)
        return dlerror();
#else
        return "DSO load failed";
#endif /* HAVE_LIBDL */
    case API_EBADIP:
        return "The specified IP address is invalid.";
    case API_EBADMASK:
        return "The specified network mask is invalid.";
    case API_ESYMNOTFOUND:
        return "Could not find the requested symbol.";
    case API_ENOTENOUGHENTROPY:
        return "Not enough entropy to continue.";
    case API_INCHILD:
        return
	    "Your code just forked, and you are currently executing in the "
	    "child process";
    case API_INPARENT:
        return
	    "Your code just forked, and you are currently executing in the "
	    "parent process";
    case API_DETACH:
        return "The specified thread is detached";
    case API_NOTDETACH:
        return "The specified thread is not detached";
    case API_CHILD_DONE:
        return "The specified child process is done executing";
    case API_CHILD_NOTDONE:
        return "The specified child process is not done executing";
    case API_TIMEUP:
        return "The timeout specified has expired";
    case API_INCOMPLETE:
        return "Partial results are valid but processing is incomplete";
    case API_BADCH:
        return "Bad character specified on command line";
    case API_BADARG:
        return "Missing parameter for the specified command line option";
    case API_EOF:
        return "End of file found";
    case API_NOTFOUND:
        return "Could not find specified socket in poll list.";
    case API_ANONYMOUS:
        return "Shared memory is implemented anonymously";
    case API_FILEBASED:
        return "Shared memory is implemented using files";
    case API_KEYBASED:
        return "Shared memory is implemented using a key system";
    case API_EINIT:
        return
	    "There is no error, this value signifies an initialized "
	    "error code";
    case API_ENOTIMPL:
        return "This function has not been implemented on this platform";
    case API_EMISMATCH:
        return "passwords do not match";
    case API_EABSOLUTE:
        return "The given path is absolute";
    case API_ERELATIVE:
        return "The given path is relative";
    case API_EINCOMPLETE:
        return "The given path is incomplete";
    case API_EABOVEROOT:
        return "The given path was above the root path";
    case API_EBADPATH:
        return "The given path is misformatted or contained invalid characters";
    case API_EPATHWILD:
        return "The given path contained wildcard characters";
    case API_EBUSY:
        return "The given lock was busy.";
    case API_EPROC_UNKNOWN:
        return "The process is not recognized.";
    case API_EGENERAL:
        return "Internal error (specific information not available)";
    default:
        return "Error string not specified yet";
    }
}


#ifdef OS2
#include <ctype.h>

int api_canonical_error(api_status_t err);

static char *api_os_strerror(char* buf, api_size_t bufsize, int err)
{
  char result[200];
  unsigned char message[HUGE_STRING_LEN];
  ULONG len;
  char *pos;
  int c;
  
  if (err >= 10000 && err < 12000) {  /* socket error codes */
      return stuffbuffer(buf, bufsize,
                         strerror(api_canonical_error(err+API_OS_START_SYSERR)));
  } 
  else if (DosGetMessage(NULL, 0, message, HUGE_STRING_LEN, err,
			 "OSO001.MSG", &len) == 0) {
      len--;
      message[len] = 0;
      pos = result;
  
      if (len >= sizeof(result))
        len = sizeof(result) - 1;

      for (c=0; c<len; c++) {
	  /* skip multiple whitespace */
          while (api_isspace(message[c]) && api_isspace(message[c+1]))
              c++;
          *(pos++) = api_isspace(message[c]) ? ' ' : message[c];
      }
  
      *pos = 0;
  } 
  else {
      sprintf(result, "OS/2 error %d", err);
  }

  /* Stuff the string into the caller supplied buffer, then return 
   * a pointer to it.
   */
  return stuffbuffer(buf, bufsize, result);  
}

#elif defined(WIN32) || (defined(NETWARE) && defined(USE_WINSOCK))

static const struct {
    api_status_t code;
    const char *msg;
} gaErrorList[] = {
    {WSAEINTR,           "Interrupted system call"},
    {WSAEBADF,           "Bad file number"},
    {WSAEACCES,          "Permission denied"},
    {WSAEFAULT,          "Bad address"},
    {WSAEINVAL,          "Invalid argument"},
    {WSAEMFILE,          "Too many open sockets"},
    {WSAEWOULDBLOCK,     "Operation would block"},
    {WSAEINPROGRESS,     "Operation now in progress"},
    {WSAEALREADY,        "Operation already in progress"},
    {WSAENOTSOCK,        "Socket operation on non-socket"},
    {WSAEDESTADDRREQ,    "Destination address required"},
    {WSAEMSGSIZE,        "Message too long"},
    {WSAEPROTOTYPE,      "Protocol wrong type for socket"},
    {WSAENOPROTOOPT,     "Bad protocol option"},
    {WSAEPROTONOSUPPORT, "Protocol not supported"},
    {WSAESOCKTNOSUPPORT, "Socket type not supported"},
    {WSAEOPNOTSUPP,      "Operation not supported on socket"},
    {WSAEPFNOSUPPORT,    "Protocol family not supported"},
    {WSAEAFNOSUPPORT,    "Address family not supported"},
    {WSAEADDRINUSE,      "Address already in use"},
    {WSAEADDRNOTAVAIL,   "Can't assign requested address"},
    {WSAENETDOWN,        "Network is down"},
    {WSAENETUNREACH,     "Network is unreachable"},
    {WSAENETRESET,       "Net connection reset"},
    {WSAECONNABORTED,    "Software caused connection abort"},
    {WSAECONNRESET,      "Connection reset by peer"},
    {WSAENOBUFS,         "No buffer space available"},
    {WSAEISCONN,         "Socket is already connected"},
    {WSAENOTCONN,        "Socket is not connected"},
    {WSAESHUTDOWN,       "Can't send after socket shutdown"},
    {WSAETOOMANYREFS,    "Too many references, can't splice"},
    {WSAETIMEDOUT,       "Connection timed out"},
    {WSAECONNREFUSED,    "Connection refused"},
    {WSAELOOP,           "Too many levels of symbolic links"},
    {WSAENAMETOOLONG,    "File name too long"},
    {WSAEHOSTDOWN,       "Host is down"},
    {WSAEHOSTUNREACH,    "No route to host"},
    {WSAENOTEMPTY,       "Directory not empty"},
    {WSAEPROCLIM,        "Too many processes"},
    {WSAEUSERS,          "Too many users"},
    {WSAEDQUOT,          "Disc quota exceeded"},
    {WSAESTALE,          "Stale NFS file handle"},
    {WSAEREMOTE,         "Too many levels of remote in path"},
    {WSASYSNOTREADY,     "Network system is unavailable"},
    {WSAVERNOTSUPPORTED, "Winsock version out of range"},
    {WSANOTINITIALISED,  "WSAStartup not yet called"},
    {WSAEDISCON,         "Graceful shutdown in progress"},
    {WSAHOST_NOT_FOUND,  "Host not found"},
    {WSANO_DATA,         "No host data of that type was found"},
    {0,                  NULL}
};


static char *api_os_strerror(char *buf, api_size_t bufsize, api_status_t errcode)
{
    api_size_t len=0, i;

#ifndef NETWARE
#ifndef _WIN32_WCE
    len = FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM 
                      | FORMAT_MESSAGE_IGNORE_INSERTS,
                        NULL,
                        errcode,
                        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), /* Default language */
                        buf,
                        (DWORD)bufsize,
                        NULL);
#else /* _WIN32_WCE speaks unicode */
     LPTSTR msg = (LPTSTR) buf;
     len = FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM 
                       | FORMAT_MESSAGE_IGNORE_INSERTS,
                         NULL,
                         errcode,
                         MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), /* Default language */
                         msg,
                         (DWORD) (bufsize/sizeof(TCHAR)),
                         NULL);
     /* in-place convert to US-ASCII, substituting '?' for non ASCII   */
     for(i = 0; i <= len; i++) {
        if (msg[i] < 0x80 && msg[i] >= 0) {
            buf[i] = (char) msg[i];
        } else {
            buf[i] = '?';
        }
    }
#endif
#endif

    if (!len) {
        for (i = 0; gaErrorList[i].msg; ++i) {
            if (gaErrorList[i].code == errcode) {
                api_cpystrn(buf, gaErrorList[i].msg, bufsize);
                len = strlen(buf);
                break;
            }
        }
    }

    if (len) {
        /* FormatMessage put the message in the buffer, but it may
         * have embedded a newline (\r\n), and possible more than one.
         * Remove the newlines replacing them with a space. This is not
         * as visually perfect as moving all the remaining message over,
         * but more efficient.
         */
        i = len;
        while (i) {
            i--;
            if ((buf[i] == '\r') || (buf[i] == '\n'))
                buf[i] = ' ';
        }
    }
    else {
        /* Windows didn't provide us with a message.  Even stuff like 
         * WSAECONNREFUSED won't get a message.
         */
        api_snprintf(buf, bufsize, "Unrecognized Win32 error code %d", errcode);
    }

    return buf;
}

#else
/* On Unix, api_os_strerror() handles error codes from the resolver 
 * (h_errno). 
 */
static char *api_os_strerror(char* buf, api_size_t bufsize, int err) 
{
#ifdef HAVE_HSTRERROR
    return stuffbuffer(buf, bufsize, hstrerror(err));
#else /* HAVE_HSTRERROR */
    const char *msg;

    switch(err) {
    case HOST_NOT_FOUND:
        msg = "Unknown host";
        break;
#if defined(NO_DATA)
    case NO_DATA:
#if defined(NO_ADDRESS) && (NO_DATA != NO_ADDRESS)
    case NO_ADDRESS:
#endif
        msg = "No address for host";
        break;
#elif defined(NO_ADDRESS)
    case NO_ADDRESS:
        msg = "No address for host";
        break;
#endif /* NO_DATA */
    default:
        msg = "Unrecognized resolver error";
    }
    return stuffbuffer(buf, bufsize, msg);
#endif /* HAVE_STRERROR */
}
#endif

#if defined(HAVE_STRERROR_R) && defined(STRERROR_R_RC_INT) && !defined(BEOS)
/* AIX and Tru64 style */
static char *native_strerror(api_status_t statcode, char *buf,
                             api_size_t bufsize)
{
    if (strerror_r(statcode, buf, bufsize) < 0) {
        return stuffbuffer(buf, bufsize, 
                           "API does not understand this error code");
    }
    else {
        return buf;
    }
}
#elif defined(HAVE_STRERROR_R)
/* glibc style */

/* BeOS has the function available, but it doesn't provide
 * the prototype publically (doh!), so to avoid a build warning
 * we add a suitable prototype here.
 */
#if defined(BEOS)
const char *strerror_r(api_status_t, char *, api_size_t);
#endif

static char *native_strerror(api_status_t statcode, char *buf,
                             api_size_t bufsize)
{
    const char *msg;

    buf[0] = '\0';
    msg = strerror_r(statcode, buf, bufsize);
    if (buf[0] == '\0') { /* libc didn't use our buffer */
        return stuffbuffer(buf, bufsize, msg);
    }
    else {
        return buf;
    }
}
#else
/* plain old strerror(); 
 * thread-safe on some platforms (e.g., Solaris, OS/390)
 */
static char *native_strerror(api_status_t statcode, char *buf,
                             api_size_t bufsize)
{
#ifdef _WIN32_WCE
    static char err[32];
    sprintf(err, "Native Error #%d", statcode);
    return stuffbuffer(buf, bufsize, err);
#else
    const char *err = strerror(statcode);
    if (err) {
        return stuffbuffer(buf, bufsize, err);
    } else {
        return stuffbuffer(buf, bufsize, 
                           "API does not understand this error code");
    }
#endif
}
#endif

API_DECLARE(char *) api_strerror(api_status_t statcode, char *buf,
                                 api_size_t bufsize)
{
    if (statcode < API_OS_START_ERROR) {
        return native_strerror(statcode, buf, bufsize);
    }
    else if (statcode < API_OS_START_USERERR) {
        return stuffbuffer(buf, bufsize, api_error_string(statcode));
    }
    else if (statcode < API_OS_START_EAIERR) {
        return stuffbuffer(buf, bufsize, "API does not understand this error code");
    }
    else if (statcode < API_OS_START_SYSERR) {
#if defined(HAVE_GAI_STRERROR)
        statcode -= API_OS_START_EAIERR;
#if defined(NEGATIVE_EAI)
        statcode = -statcode;
#endif
        return stuffbuffer(buf, bufsize, gai_strerror(statcode));
#else
        return stuffbuffer(buf, bufsize, "API does not understand this error code");
#endif
    }
    else {
        return api_os_strerror(buf, bufsize, statcode - API_OS_START_SYSERR);
    }
}

