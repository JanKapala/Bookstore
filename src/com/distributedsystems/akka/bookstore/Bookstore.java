package com.distributedsystems.akka.bookstore;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.AddressFromURIString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.Scanner;

public class Bookstore {
    public static void main(String[] args) {

        String basic_system_name = "bookStoreSystem";
        int port_number = 3001;
        String system_name = basic_system_name + "_" + port_number;
        String hostname = "127.0.0.1";
        String dispatcher_name = "main_dispatcher";
        String bookstore_name = "matras";

        // Config creation for remoting purpose
        Config config = ConfigFactory.parseString(String.format(
                "akka {\n" +
                        "  actor {\n" +
                        "    provider = remote\n" +
                        "    warn-about-java-serializer-usage = false\n" +
                        "  }\n" +
                        "  remote {\n" +
                        "    enabled-transports = [\"akka.remote.netty.tcp\"]\n" +
                        "    netty.tcp {\n" +
                        "      hostname = \"%s\"\n" +
                        "      port = %d\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", hostname, port_number));

        final ActorSystem system = ActorSystem.create(system_name, config);

        // Create dispatcher
        ActorRef dispatcher = system.actorOf(Dispatcher.props(bookstore_name), dispatcher_name);

        // Display dispatcher address
        Address dispatcher_address = new Address("akka.tcp", system_name, hostname, port_number);
        String dispatcher_remote_path = dispatcher_address.toString() + "/user/" + dispatcher_name;
        System.out.println("Dispatcher remote path: " + dispatcher_remote_path);


        // Exit
        System.out.println(">>> Press ENTER to exit <<<");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        system.terminate();
    }
}
