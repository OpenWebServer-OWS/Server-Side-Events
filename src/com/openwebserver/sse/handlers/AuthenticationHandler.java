package com.openwebserver.sse.handlers;


import com.openwebserver.core.Objects.Request;

public interface AuthenticationHandler {

    boolean OnAuthentication(Request request);

}
