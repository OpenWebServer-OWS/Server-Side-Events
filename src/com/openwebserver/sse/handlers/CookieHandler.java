package com.openwebserver.sse.handlers;


import com.openwebserver.core.http.Cookie;
import com.openwebserver.core.objects.Request;

public interface CookieHandler {

    Cookie onRequest(Request request);

}
