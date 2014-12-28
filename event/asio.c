#include "api/asio.h"
#include "api/lib.h"

#include "ev.h"

struct asio_struct
{
	struct ev_loop *loop;
};

struct io_watcher_struct
{
	struct ev_loop *loop;
	struct ev_io   *evio;
	ae_callback     cb_read;
	ae_callback     cb_write;
	ae_callback     cb_error;
	ae_callback     cb_close;
	void           *owner;
};

asio_t *asio_init(void)
{
	asio_t *asio = NULL;
	struct ev_loop *loop = ev_default_loop(0);
	if(loop != NULL) {
		asio = (asio_t *)malloc(sizeof(asio_t));
		if(asio != NULL) {
			ZERO_STRUCTP(asio);
			asio->loop = loop;
		}
	}
	return asio;
}

void asio_destory(asio_t *asio)
{
	if(asio != NULL) {
		if(asio->loop != NULL) {
			ev_break(asio->loop, EVBREAK_ALL);
			asio->loop = NULL;			
		}
		ZERO_STRUCTP(asio);
		SAFE_FREE(asio);
	}
}

void asio_loop(asio_t *asio)
{
	ev_loop(asio->loop, 0);
}

static void read_cb(struct ev_loop *loop, struct ev_io *watcher, int revents)
{
	if(watcher != NULL && watcher->data != NULL) {
		io_watcher_t *w = (io_watcher_t *)watcher->data;
		if(w->cb_read != NULL) {
			w->cb_read(w, revents, w->owner);
		}
	}
}

io_watcher_t *io_watcher_new(asio_t *asio)
{
	io_watcher_t *watcher = (io_watcher_t *)malloc(sizeof(io_watcher_t));
	if(watcher != NULL) {
		ZERO_STRUCTP(watcher);
		watcher->loop = asio->loop;
		watcher->evio = (struct ev_io *) malloc (sizeof(struct ev_io));
		ZERO_STRUCTP(watcher->evio);
	}
	
	return watcher;
}

void io_watcher_free(io_watcher_t *watcher)
{
	if(watcher != NULL) {
		ZERO_STRUCTP(watcher);
		SAFE_FREE(watcher);
	}
	
}

int io_watcher_fd(io_watcher_t *watcher)
{
	int fd = -1;
	if(watcher != NULL && watcher->evio != NULL) {
		fd = watcher->evio->fd;
	}
	return fd;
}

void io_watcher_add(io_watcher_t *watcher, int fd, ae_callback cb, int revents, void *owner)
{
	struct ev_io *evio = NULL;
	
	if(watcher != NULL) {
		watcher->cb_read = cb;
		watcher->cb_write = NULL;
		watcher->cb_error = NULL;
		watcher->cb_close = NULL;
		watcher->owner = owner;
		evio = watcher->evio;
		evio->data = watcher;
		ev_io_init(evio, read_cb, fd, revents);  
		ev_io_start(watcher->loop, evio);
	}
}

void io_watcher_stop(io_watcher_t *watcher, int toClose)
{
	if(watcher != NULL && watcher->evio != NULL) {
		ev_io_stop(watcher->loop, watcher->evio);
		if(toClose) {
			close(watcher->evio->fd);
			watcher->evio->fd = -1;
		}
	}
}


