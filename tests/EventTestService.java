import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Content.Content;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.extensions.sse.EventHandler;
import com.openwebserver.extensions.sse.Objects.Event;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;


public class EventTestService extends Service {

    private final EventHandler eventHandler = new EventHandler("/events");

    public EventTestService(String path) {
        super(path);
        add(eventHandler);
    }

    @Route(path = "/broadcast", method = Request.Method.GET)
    public Response broadcast(Request request){
        eventHandler.broadcast(new Event(UUID.randomUUID().toString()).data(request.GET("message")));
        return Response.simple(Code.Ok, new JSONObject().put("done", true));
    }
    
    public void broadcast(Event e){
        eventHandler.broadcast(e);
    }

    @Route(path = "/page", method = Request.Method.GET)
    public Response page(Request request){
        try {
            String page = Files.readString(Path.of("./html/index.html"));
            page = page.replaceAll("\\[PATH]", "\"" + this.getPath() + "\"");
            return Response.simple(Code.Ok, page , Content.Type.Text.edit("html"));
        } catch (IOException e) {
            return Response.simple(Code.Service_Unavailable, "Can't read resource file");
        }
    }
}
