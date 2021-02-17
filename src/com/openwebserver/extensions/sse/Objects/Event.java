package com.openwebserver.extensions.sse.Objects;


import com.openwebserver.core.Connection.ConnectionWriter;
import com.openwebserver.core.Objects.Cookie;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Event{

    private final String id;
    private final String event;
    private String data;

    private final Headers headers;

    private final ArrayList<Cookie> cookies = new ArrayList<>();

    public Event(String id){
        this(id, "message");
    }

    public Event(String id, String type){
        this.id = id;
        this.event = type;
        this.headers = Event.Accept();
    }

    public Event data(String data) {
        if(data.contains("\n")){
            return data(data,true);
        }
        return data(data, false);
    }

    public Event data(String data, boolean encode) {
        if(encode){
            data = data.replaceAll("\n", "%0A");
        }
        if(data.contains("\n")){
            data = data.replaceAll("\n", "");
        }
        this.data = data;
        return this;
    }

    public Event send(ConnectionWriter out) throws IOException {
        out.write(("event: " + event + "\n").getBytes());
        out.write(("id: " + id + "\n").getBytes());
        out.write(("data: " + data + "\n\n").getBytes());
        out.flush();
        return this;
    }

    public Event addCookie(Cookie c) {
        cookies.add(c);
        return this;
    }

    public Headers getHeaders() {
        return headers;
    }

    public static Headers Accept(Header ... header){
        Headers headers = new Headers();
        headers.add(Header.raw("HTTP/1.1 /events 200 OK"));
        headers.add(new Header("Cache-Control", "no-cache,public"));
        headers.add(new Header("Content-Type", "text/event-stream"));
        headers.add(new Header("Charset", "UTF-8"));
        headers.add(new Header("Access-Control-Allow-Origin", "*"));
        Collections.addAll(headers, header);
        return headers;
    }

    public boolean hasCookies() {
        return !cookies.isEmpty();
    }

    public ArrayList<Cookie> getCookies() {
        return cookies;
    }

}
