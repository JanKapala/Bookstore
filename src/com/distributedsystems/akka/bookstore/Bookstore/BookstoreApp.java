package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class BookstoreApp {
    public static void main(String[] args) {

        // Config
        String basic_system_name = "bookStoreSystem";
        int port_number = 3001;
        String system_name = basic_system_name + "_" + port_number;
        String hostname = "127.0.0.1";

        // Dispatcher
        String dispatcher_name = "main_dispatcher";

        // Databases
        String databases_root_path = System.getProperty("user.dir") + "\\databases";
        String db1_root_path = databases_root_path + "\\database_1";
        String db2_root_path = databases_root_path + "\\database_2";

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
        ActorRef dispatcher = system.actorOf(DispatcherActor.props(db1_root_path, db2_root_path), dispatcher_name);

        // Display dispatcher address
        Address dispatcher_address = new Address("akka.tcp", system_name, hostname, port_number);
        String dispatcher_remote_path = dispatcher_address.toString() + "/user/" + dispatcher_name;
        System.out.println("DispatcherActor remote path: " + dispatcher_remote_path);


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
