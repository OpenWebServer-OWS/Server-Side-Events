package com.openwebserver.extensions.sse.Handlers;


import com.openwebserver.core.Objects.Request;

public interface AuthenticationHandler {

    boolean OnAuthentication(Request request);

}
