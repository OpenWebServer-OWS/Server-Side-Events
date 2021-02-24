package com.openwebserver.extensions.sse;


import com.openwebserver.core.Connection.Connection;
import com.openwebserver.core.Connection.ConnectionManager;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.WebException;
import com.openwebserver.extensions.sse.Handlers.AuthenticationHandler;
import com.openwebserver.extensions.sse.Handlers.CookieHandler;
import com.openwebserver.extensions.sse.Objects.Event;
import com.openwebserver.extensions.sse.Objects.SyncStore;

import java.io.IOException;
import java.util.function.BiConsumer;

import static com.openwebserver.core.Connection.ConnectionManager.Access.CONNECTION;


public class EventHandler extends RequestHandler implements BiConsumer<Connection, Object[]> {


    private CookieHandler cookieHandler = request -> null;

    private AuthenticationHandler authenticationHandler = request -> true;
    private boolean enabled = true;

    private final SyncStore<Event> eventSyncStore = new SyncStore<>();

    public EventHandler(String path) {
        super(new Route(path, Method.UNDEFINED),null);
        super.setContentHandler( request -> {
            try {
                Connection connection = request.access(CONNECTION);
                return connection.HandOff(EventHandler.this,request);
            } catch (IOException | ConnectionManager.ConnectionManagerException ioException) {
                return new WebException(ioException).respond();
            }
        });
    }

    public EventHandler setCookieHandler(CookieHandler handler){
        this.cookieHandler = handler;
        return this;
    }

    public EventHandler setAuthenticationHandler(AuthenticationHandler handler){
        this.authenticationHandler = handler;
        return this;
    }

    public void disable(){
        this.enabled = false;
    }

    private boolean isEnabled() {
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
                connection.writeOpen(Event.Accept());
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
                System.err.println("Client disconnected " + connection.getConnectionString());
            } catch (IOException e) {
                connection.close();
            }
        } else {
            connection.write(Response.simple(Code.Unauthorized));
        }
    }
    //endregion

}
