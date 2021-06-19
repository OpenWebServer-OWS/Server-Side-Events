package com.openwebserver.sse.handlers;


import com.openwebserver.core.objects.Request;

public interface AuthenticationHandler {

    boolean OnAuthentication(Request request);

}
