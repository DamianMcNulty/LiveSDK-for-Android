//------------------------------------------------------------------------------
// Copyright 2014 Microsoft Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//------------------------------------------------------------------------------

package com.microsoft.live.test.util;

import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import com.microsoft.live.LiveConnectClient;

public abstract class AsyncRunnableWithBody<OperationType, ListenerType>
        extends AsyncRunnable<OperationType, ListenerType> {

    private enum BodyType {
        JSON {
            @Override
            public <OperationType> OperationType calledWithoutUserState(AsyncRunnableWithBody<OperationType, ?> a) {
                return a.calledWithoutUserState(a.jsonBody);
            }

            @Override
            public <OperationType> OperationType calledWithUserState(AsyncRunnableWithBody<OperationType, ?> a,
                                                                     Object userState) {
                return a.calledWithUserState(a.jsonBody, userState);
            }
        },
        STRING {
            @Override
            public <OperationType> OperationType calledWithoutUserState(AsyncRunnableWithBody<OperationType, ?> a) {
                return a.calledWithoutUserState(a.stringBody);
            }

            @Override
            public <OperationType> OperationType calledWithUserState(AsyncRunnableWithBody<OperationType, ?> a,
                                                                     Object userState) {
                return a.calledWithUserState(a.stringBody, userState);
            }
        };

        public abstract <OperationType> OperationType calledWithoutUserState(AsyncRunnableWithBody<OperationType, ?> a);
        public abstract <OperationType> OperationType calledWithUserState(AsyncRunnableWithBody<OperationType, ?> a,
                                                                          Object userState);
    }

    private final BodyType bodyType;
    private final JSONObject jsonBody;
    private final String stringBody;

    public AsyncRunnableWithBody(BlockingQueue<OperationType> queue,
                                 LiveConnectClient connectClient,
                                 String path,
                                 JSONObject body,
                                 ListenerType listener) {
        super(queue, connectClient, path, listener);
        this.jsonBody = body;
        this.stringBody = null;
        this.bodyType = BodyType.JSON;
    }

    public AsyncRunnableWithBody(BlockingQueue<OperationType> queue,
                                 LiveConnectClient connectClient,
                                 String path,
                                 JSONObject body,
                                 ListenerType listener,
                                 Object userState) {
        super(queue, connectClient, path, listener, userState);
        this.jsonBody = body;
        this.stringBody = null;
        this.bodyType = BodyType.JSON;
    }

    public AsyncRunnableWithBody(BlockingQueue<OperationType> queue,
                                 LiveConnectClient connectClient,
                                 String path,
                                 String body,
                                 ListenerType listener) {
        super(queue, connectClient, path, listener);
        this.jsonBody = null;
        this.stringBody = body;
        this.bodyType = BodyType.STRING;
    }

    public AsyncRunnableWithBody(BlockingQueue<OperationType> queue,
                                 LiveConnectClient connectClient,
                                 String path,
                                 String body,
                                 ListenerType listener,
                                 Object userState) {
        super(queue, connectClient, path, listener, userState);
        this.jsonBody = null;
        this.stringBody = body;
        this.bodyType = BodyType.STRING;
    }

    protected abstract OperationType calledWithoutUserState(JSONObject body);
    protected abstract OperationType calledWithoutUserState(String body);
    protected abstract OperationType calledWithUserState(JSONObject body, Object userState);
    protected abstract OperationType calledWithUserState(String body, Object userState);

    @Override
    protected OperationType calledWithoutUserState() {
        return this.bodyType.calledWithoutUserState(this);
    }

    @Override
    protected OperationType calledWithUserState(Object userState) {
        return this.bodyType.calledWithUserState(this, userState);
    }

}
