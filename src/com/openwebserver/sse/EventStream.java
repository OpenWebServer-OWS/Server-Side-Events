package com.openwebserver.sse;


import com.openwebserver.core.WebException;
import com.openwebserver.core.connection.ConnectionManager;
import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.http.content.Code;
import com.openwebserver.core.objects.Request;
import com.openwebserver.core.objects.Response;
import com.openwebserver.core.routing.Route;
import com.openwebserver.sse.components.Event;
import com.openwebserver.sse.components.SyncStore;
import com.openwebserver.sse.handlers.AuthenticationHandler;
import com.openwebserver.sse.handlers.CookieHandler;

import java.io.IOException;
import java.util.function.BiConsumer;


public class EventStream extends RequestHandler implements BiConsumer<Connection, Object[]> {


    private CookieHandler cookieHandler = request -> null;
    private AuthenticationHandler authenticationHandler = request -> true;
    private boolean enabled = true;

    private final SyncStore<Event> eventSyncStore = new SyncStore<>();

    public EventStream(String path) {
        super(new Route(path, Method.UNDEFINED), null);
        super.setContentHandler(request -> {
            try {
                Connection connection = request.access(ConnectionManager.Access.CONNECTION);
                return connection.HandOff(EventStream.this, request);
            } catch (IOException | ConnectionManager.ConnectionManagerException ioException) {
                return new WebException(ioException).respond();
            }
        });
    }

    public EventStream setCookieHandler(CookieHandler handler) {
        this.cookieHandler = handler;
        return this;
    }

    public EventStream setAuthenticationHandler(AuthenticationHandler handler) {
        this.authenticationHandler = handler;
        return this;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void broadcast(Event message) {
        eventSyncStore.set(message);
    }

    @Override
    public void accept(Connection connection, Object[] args) {
        Request request = (Request) args[0];
        if (authenticationHandler.OnAuthentication(request)) {
            try {
                connection.writeOpen(Event.Accept(getHeaders()));
                Event oldEvent = null;
                while (enabled && connection.isConnected()) {
                    Event event = eventSyncStore.get();
                    try {
                        if (event != null && (oldEvent != event)) {
                            oldEvent = event.send(connection);
                        } else {
                            Thread.yield();
                        }
                    } catch (NullPointerException e) {
                        connection.write(Response.simple(Code.Internal_Server_Error, e.getMessage()));
                    }
                }
                System.err.println("Client disconnected " + connection);
            } catch (IOException e) {
                connection.close();
            }
        } else {
            try {
                connection.write(Response.simple(Code.Unauthorized));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

}
