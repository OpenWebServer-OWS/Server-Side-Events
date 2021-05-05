import ByteReader.ByteReader;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Content.Content;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.sse.EventStream;
import com.openwebserver.sse.components.Event;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;


public class EventTestService extends Service {

    private final EventStream stream = new EventStream("/events");

    public EventTestService(String path) {
        super(path);
        add(stream);
    }

    @Route(path = "/broadcast", method = Method.GET)
    public Response broadcast(Request request){
        stream.broadcast(new Event(UUID.randomUUID().toString(),request.GET().keySet().toArray(String[]::new)[0]).data(request.GET().values().toArray(String[]::new)[0]));
        return Response.simple(Code.Ok, new JSONObject().put("done", true));
    }
    
    public void broadcast(Event e){
        stream.broadcast(e);
    }

    @Route(path = "/page", method = Method.GET)
    public Response page(Request request){
        try {
            String page = new ByteReader(getClass().getResourceAsStream("/html/index.html")).readAll().toString();
            page = page.replaceAll("\\[PATH]", "\"" + this.getPath() + "\"");
            return Response.simple(Code.Ok, page , Content.Type.Text.edit("html"));
        } catch (IOException | ByteReader.ByteReaderException.PrematureStreamException e) {
            return Response.simple(Code.Service_Unavailable, "Can't read resource file");
        }
    }
}
