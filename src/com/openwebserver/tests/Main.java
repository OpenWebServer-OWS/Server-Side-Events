package com.openwebserver.tests;

import com.openwebserver.core.Domain;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.WebServer;

import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        String domain = "http://localhost:80";
        if(args.length > 0){
            domain = "http://" + args[0];
        }
        new WebServer().addDomain(
                new Domain(domain)
                        .addHandler(
                                new EventTestService("/"))
        ).start();
        Router.print();
    }

}
