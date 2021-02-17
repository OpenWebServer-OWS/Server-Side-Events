package com.openwebserver.extensions.sse.Handlers;


import com.openwebserver.core.Objects.Cookie;
import com.openwebserver.core.Objects.Request;

public interface CookieHandler {

    Cookie onRequest(Request request);

}
