/* Licensed to the MyMMSC Software Foundation (MSF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MSF licenses this file to You under the MyMMSC License, Version 6.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.mymmsc.org/licenses/LICENSE-6.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>

#include "abts.h"
#include "testutil.h"
//////////////////////////////////////////////////////////////////////////////////////////

//#include "aio_object.h"

//aio_pool_t *p;

void aio_assert_success(abts_case* tc, const char* context, api_status_t rv, 
                        int lineno) 
{
    if (rv == API_ENOTIMPL) {
        abts_not_impl(tc, context, lineno);
    } else if (rv != API_SUCCESS) {
        char buf[STRING_MAX], ebuf[128];
        sprintf(buf, "%s (%d): %s\n", context, rv,
                api_strerror(rv, ebuf, sizeof ebuf));
        abts_fail(tc, buf, lineno);
    }
}

void initialize(void) {
#ifdef API_WINDOWS
    StartDebug(TRUE);
#endif
    //aio_initialize();
    //atexit(aio_terminate);
    
    //aio_pool_create(&p, NULL);
}
