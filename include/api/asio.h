#ifndef __API_ASIO_H_INCLUDED__
#define __API_ASIO_H_INCLUDED__

#include <api.h>

#ifdef __cplusplus
extern "C" {
#endif

/* eventmask, revents, events... */
enum {
  AE_UNDEF    = (int)0xFFFFFFFF, /* guaranteed to be invalid */
  AE_NONE     =            0x00, /* no events */
  AE_READ     =            0x01, /* ev_io detected read will not block */
  AE_WRITE    =            0x02, /* ev_io detected write will not block */
  AE__IOFDSET =            0x80, /* internal use only */
  AE_IO       =         AE_READ, /* alias for type-detection */
  AE_TIMER    =      0x00000100, /* timer timed out */
  AE_TIMEOUT  =        AE_TIMER, /* pre 4.0 API compatibility */
  AE_PERIODIC =      0x00000200, /* periodic timer timed out */
  AE_SIGNAL   =      0x00000400, /* signal was received */
  AE_CHILD    =      0x00000800, /* child/pid had status change */
  AE_STAT     =      0x00001000, /* stat data changed */
  AE_IDLE     =      0x00002000, /* event loop is idling */
  AE_PREPARE  =      0x00004000, /* event loop about to poll */
  AE_CHECK    =      0x00008000, /* event loop finished poll */
  AE_EMBED    =      0x00010000, /* embedded event loop needs sweep */
  AE_FORK     =      0x00020000, /* event loop resumed in child */
  AE_CLEANUP  =      0x00040000, /* event loop resumed in child */
  AE_ASYNC    =      0x00080000, /* async intra-loop signal */
  AE_CUSTOM   =      0x01000000, /* for use by user code */
  AE_ERROR    = (int)0x80000000  /* sent when an error occurs */
};

typedef struct asio_struct asio_t;
typedef struct io_watcher_struct io_watcher_t;

typedef int (*ae_callback)(io_watcher_t *watcher, int revents, void *context);

API asio_t *asio_init(void);
API void asio_destory(asio_t *asio);
API void asio_loop(asio_t *asio);

API io_watcher_t *io_watcher_new(asio_t *asio);
API void io_watcher_free(io_watcher_t *watcher);
API int io_watcher_fd(io_watcher_t *watcher);
API void io_watcher_add(io_watcher_t *watcher, int fd, ae_callback cb, int revents, void *owner);
API void io_watcher_stop(io_watcher_t *watcher, int toClose);

#ifdef __cplusplus
}
#endif

#endif /* ! __API_ASIO_H_INCLUDED__ */
