package com.openwebserver.sse.components;


import com.openwebserver.core.Connection.ConnectionWriter;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Cookie;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Objects.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Event {

    private final String id;
    private final String event;
    private String data;

    private final ArrayList<Cookie> cookies = new ArrayList<>();

    public Event(String id){
        this(id, "message");
    }

    public Event(String id, String type){
        this.id = id;
        this.event = type;
    }

    public Event data(String data) {
        if(data.contains("\n")){
            return data(data,true);
        }
        return data(data, false);
    }

    public Event data(Object data) {
        if(data.toString().contains("\n")){
            return data(data.toString(),true);
        }
        return data(data.toString(), false);
    }

    public static Event create(String type){
        return new Event(UUID.randomUUID().toString(), type);
    }

    public static Event create(){
        return new Event(UUID.randomUUID().toString());
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

    public boolean hasCookies() {
        return !cookies.isEmpty();
    }

    public ArrayList<Cookie> getCookies() {
        return cookies;
    }

    public static Headers Accept(Headers headers){
        Headers headers1 = new Headers();
        headers1.add(Header.raw("HTTP/" + Response.version + " " + Code.Ok.getCode()));
        headers1.add(new Header("Cache-Control", "no-cache,public"));
        headers1.add(new Header("Content-Type", "text/event-stream"));
        headers1.addAll(headers);
        return headers1;
    }


}
