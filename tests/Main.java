

import com.openwebserver.core.WebServer;
import com.openwebserver.core.objects.Domain;
import com.openwebserver.core.routing.Router;


public class Main {

    public static void main(String[] args) {
//        Router.VERBOSE = true;
        new WebServer().addDomain(
                new Domain()
                        .addHandler(
                                new EventTestService("/"))
        ).start();
        Router.print();
    }

}
