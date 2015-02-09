/*
 * Copyright (c) 2013, Vitja Makarov <vitja.makarov@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of Redis nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
//#include <hiredis/hiredis.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "hiredis_locker.h"
#include "lua_scripts.h"

#define DEFAULT_TIMEOUT 3153600000 /* ~100 years */

#define SHA1_HEXDIGEST_LEN 40

typedef struct {
    char hexdigest[SHA1_HEXDIGEST_LEN + 1];
} redisScript;


static redisReply *redis_reply_error(const char *fmt, ...)
{
    redisReply *reply = calloc(1, sizeof(redisReply));
    char buf[1024];
    int len;

    if (reply == NULL)
        return NULL;

    va_list ap;

    va_start(ap, fmt);
    len = vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);

    reply->type = REDIS_REPLY_ERROR;

    if (len >= 0) {
        if (NULL != (reply->str = strdup(buf)))
            reply->len = len;
    }

    return reply;
}

static
int redis_load_script(redisContext *c, redisScript *script,
                      const char *source, redisReply **reply_error)
{
    const char *argv[] = {"SCRIPT", "LOAD", source};
    size_t lens[] = {6, 4, strlen(source)};
    redisReply *reply;

    reply = redisCommandArgv(c, 3, argv, lens);

    if (!reply) {
        if (reply_error)
            *reply_error = NULL;
        return -1;
    }

    if (reply->type != REDIS_REPLY_STRING || reply->len != SHA1_HEXDIGEST_LEN) {
        if (reply_error)
            *reply_error = reply;
        else
            freeReplyObject(reply);
        return -1;
    }

    strcpy(script->hexdigest, reply->str);
    freeReplyObject(reply);
    return 0;
}


int redis_lock_init_context(redisContext *c, redisReply **reply_error)
{
    redisScript script;

    if (reply_error)
        *reply_error = NULL;

    if (redis_load_script(c, &script, script_lua_lock, reply_error))
        return -1;

    if (memcmp(script.hexdigest, script_lua_lock_sha1, SHA1_HEXDIGEST_LEN)) {
        if (reply_error)
            *reply_error = redis_reply_error(
                "SHA1 doesn't match for lock script: %s != %s\n",
                script.hexdigest, script_lua_lock_sha1);
        return -1;
    }

    if (redis_load_script(c, &script, script_lua_unlock, reply_error))
        return -1;

    if (memcmp(script.hexdigest, script_lua_unlock_sha1, SHA1_HEXDIGEST_LEN)) {
        if (reply_error)
            *reply_error = redis_reply_error(
                "SHA1 doesn't match for unlock script: %s != %s\n",
                script.hexdigest, script_lua_lock_sha1);
        return -1;
    }

    return 0;
}

static
redisReply *redis_lock_script(
    redisContext *c,
    const char *lock_key, size_t lock_key_len,
    const char *timestamp, size_t timestamp_len,
    const char *expire_at, size_t expire_at_len)
{
    const char *argv[] = {
        "EVALSHA", script_lua_lock_sha1,
        "1", lock_key,
        timestamp, expire_at,
    };
    const size_t lens[] = {
        7, SHA1_HEXDIGEST_LEN,
        1, lock_key_len,
        timestamp_len, expire_at_len
    };
    return redisCommandArgv(c, 6, argv, lens);
}

static
redisReply *redis_lock_script_data(
    redisContext *c,
    const char *lock_key, size_t lock_key_len,
    const char *data_key, size_t data_key_len,
    const char *timestamp, size_t timestamp_len,
    const char *expire_at, size_t expire_at_len)
{
    const char *argv[] = {
        "EVALSHA", script_lua_lock_sha1,
        "2", lock_key, data_key,
        timestamp, expire_at,
    };
    const size_t lens[] = {
        7, SHA1_HEXDIGEST_LEN,
        1, lock_key_len, data_key_len,
        timestamp_len, expire_at_len
    };
    return redisCommandArgv(c, 7, argv, lens);
}

static
redisReply *redis_unlock_script(
    redisContext *c,
    const char *lock_key, size_t lock_key_len,
    const char *expire_at, size_t expire_at_len)
{
    const char *argv[] = {
        "EVALSHA", script_lua_unlock_sha1,
        "1", lock_key,
        expire_at,
    };
    const size_t lens[] = {
        7, SHA1_HEXDIGEST_LEN,
        1, lock_key_len,
        expire_at_len
    };
    return redisCommandArgv(c, 5, argv, lens);
}

static
redisReply *redis_unlock_script_data_delete(
    redisContext *c,
    const char *lock_key, size_t lock_key_len,
    const char *data_key, size_t data_key_len,
    const char *expire_at, size_t expire_at_len)
{
    const char *argv[] = {
        "EVALSHA", script_lua_unlock_sha1,
        "2", lock_key, data_key,
        expire_at,
    };
    const size_t lens[] = {
        7, SHA1_HEXDIGEST_LEN,
        1, lock_key_len, data_key_len,
        expire_at_len
    };
    return redisCommandArgv(c, 6, argv, lens);
}

static
redisReply *redis_unlock_script_data(
    redisContext *c,
    const char *lock_key, size_t lock_key_len,
    const char *data_key, size_t data_key_len,
    const char *expire_at, size_t expire_at_len,
    const char *data, size_t data_len)
{
    const char *argv[] = {
        "EVALSHA", script_lua_unlock_sha1,
        "2", lock_key, data_key,
        expire_at, data,
    };
    const size_t lens[] = {
        7, SHA1_HEXDIGEST_LEN,
        1, lock_key_len, data_key_len,
        expire_at_len, data_len
    };
    return redisCommandArgv(c, 7, argv, lens);
}

int redis_lock_init(redisLock *lock,
                    const char *lock_key, ssize_t lock_key_len)
{
    if (lock_key_len < 0)
        lock_key_len = strlen(lock_key);

    if ((size_t) lock_key_len > (sizeof(lock->lock_key) - 1))
        return -1;

    memset(lock, 0, sizeof(redisLock));

    strncpy(lock->lock_key, lock_key, lock_key_len);
    lock->lock_key_len = lock_key_len;

    return 0;
}

int redis_lock_set_data(redisLock *lock,
                        const char *data_key, ssize_t data_key_len)
{
    if (data_key_len < 0)
        data_key_len = strlen(data_key);

    if ((size_t) data_key_len > (sizeof(lock->data_key) - 1))
        return -1;

    strncpy(lock->data_key, data_key, data_key_len);
    lock->data_key_len = data_key_len;
    return 0;
}

void redis_lock_destroy(redisLock *lock)
{
    if (lock->error_reply) {
        freeReplyObject(lock->error_reply);
        lock->error_reply = NULL;
    }
}

int redis_lock_set_time(redisLock *lock, double timestamp, double timeout)
{
    double expire_at;

    if (timeout <= 0)
        timeout = DEFAULT_TIMEOUT;

    expire_at = timestamp + timeout;

    lock->timestamp_len =
        snprintf(lock->timestamp, sizeof(lock->timestamp), "%.6lf", timestamp);
    lock->expire_at_len =
        snprintf(lock->expire_at, sizeof(lock->expire_at), "%.6lf", expire_at);
    return 0;
}


/* set default timestamp and expire_at if no one is specified */
static void redis_lock_check_timestamp(redisLock *lock)
{
    if (lock->timestamp_len > 0)
        return;

    strcpy(lock->timestamp, "0");
    strcpy(lock->expire_at, "1");

    lock->timestamp_len = 1;
    lock->expire_at_len = 1;
}

static void redis_lock_set_error_reply(redisLock *lock,
                                       redisReply *error_reply)
{
    if (lock->error_reply) {
        freeReplyObject(lock->error_reply);
        lock->error_reply = NULL;
    }

    lock->error_reply = error_reply;
}

static int redis_lock_fetch_status(redisLock *lock, redisReply *reply)
{
    int status;

    if (!reply)
        goto bad_reply;

    if (reply->type != REDIS_REPLY_ARRAY || reply->elements != 2)
        goto bad_reply;

    if (reply->element[0]->type != REDIS_REPLY_INTEGER)
        goto bad_reply;

    status = reply->element[0]->integer;
    if (status < 0)
        goto bad_reply;

    if (status > 0)
        lock->is_locked = 1;
    else
        lock->is_locked = 0;
    lock->state = status;
    return status;
bad_reply:
    redis_lock_set_error_reply(lock, reply);
    return -1;
}

int redis_lock_acquire(redisLock *lock, redisContext *context)
{
    return redis_lock_acquire_data(lock, context, NULL);
}

int redis_lock_acquire_data(redisLock *lock, redisContext *context,
                            redisReply **data_reply)
{
    redisReply *reply;
    int status;

    redis_lock_check_timestamp(lock);

    if (data_reply) {
        reply = redis_lock_script_data(
            context,
            lock->lock_key, lock->lock_key_len,
            lock->data_key, lock->data_key_len,
            lock->timestamp, lock->timestamp_len,
            lock->expire_at, lock->expire_at_len);
    } else {
        reply = redis_lock_script(
            context,
            lock->lock_key, lock->lock_key_len,
            lock->timestamp, lock->timestamp_len,
            lock->expire_at, lock->expire_at_len);
    }

    if ((status = redis_lock_fetch_status(lock, reply)) < 0)
        return -1;

    if (lock->is_locked && data_reply) {
        *data_reply = reply->element[1];
        reply->element[1] = NULL; /* steal reply reference */
    }

    freeReplyObject(reply);
    return status;
}


int redis_lock_release(redisLock *lock, redisContext *context)
{
    redisReply *reply;
    int status;

    if (!lock->is_locked)
        return -1;

    redis_lock_check_timestamp(lock);

    reply = redis_unlock_script(
        context,
        lock->lock_key, lock->lock_key_len,
        lock->expire_at, lock->expire_at_len);

    if (!reply) {
        redis_lock_set_error_reply(lock, NULL);
        return -1;
    }

    if (reply->type != REDIS_REPLY_INTEGER || reply->integer < 0) {
        redis_lock_set_error_reply(lock, reply);
        return -1;
    }

    lock->is_locked = 0;

    status = reply->integer;
    freeReplyObject(reply);

    return status;
}


int redis_lock_release_data(redisLock *lock, redisContext *context,
                            const char *data, size_t data_len)
{
    redisReply *reply;
    int status;

    if (!lock->is_locked)
        return -1;

    redis_lock_check_timestamp(lock);


    if (data) {
        reply = redis_unlock_script_data(
            context,
            lock->lock_key, lock->lock_key_len,
            lock->data_key, lock->data_key_len,
            lock->expire_at, lock->expire_at_len,
            data, data_len);
    } else {
        reply = redis_unlock_script_data_delete(
            context,
            lock->lock_key, lock->lock_key_len,
            lock->data_key, lock->data_key_len,
            lock->expire_at, lock->expire_at_len);
    }

    if (!reply) {
        redis_lock_set_error_reply(lock, NULL);
        return -1;
    }

    if (reply->type != REDIS_REPLY_INTEGER || reply->integer < 0) {
        redis_lock_set_error_reply(lock, reply);
        return -1;
    }

    lock->is_locked = 0;

    status = reply->integer;
    freeReplyObject(reply);

    return status;
}


int redis_lock_release_data_delete(redisLock *lock, redisContext *context)
{
    return redis_lock_release_data(lock, context, NULL, 0);
}
