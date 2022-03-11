import com.openwebserver.core.http.content.Code;
import com.openwebserver.core.http.content.Content;
import com.openwebserver.core.objects.Request;
import com.openwebserver.core.objects.Response;


import com.openwebserver.services.annotations.Route;
import com.openwebserver.services.objects.Service;
import com.openwebserver.sse.EventStream;
import com.openwebserver.sse.components.Event;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;



public class EventTestService extends Service {

    private final EventStream stream = new EventStream("/events");

    public EventTestService(String path) {
        super(path);
        add(stream);
    }

    @Route(path = "/broadcast", method = Method.GET)
    public Response broadcast(Request request){
        stream.broadcast(new Event(UUID.randomUUID().toString(),request.GET.keySet().toArray(String[]::new)[0]).data(request.GET.values().toArray(String[]::new)[0]));
        return Response.simple(com.openwebserver.core.http.content.Code.Ok, new JSONObject().put("done", true).put("info", "Only message=<VALUE> will be displayed on test page add for each url arg a event listener on the eventsource"));
    }
    
    public void broadcast(Event e){
        stream.broadcast(e);
    }

    @Route(path = "/page", method = Method.GET)
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
