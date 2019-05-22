package com.distributedsystems.akka.bookstore.Client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.distributedsystems.akka.bookstore.Bookstore.ServantActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        String basic_system_name = "clientSystem";
        int port_number = 3002;
        String system_name = basic_system_name + "_" + port_number;
        String hostname = "127.0.0.1";

        // Create config for remoting purpose
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

        // Get dispatcher remote path
        System.out.println("Copy/paste bookstore dispatcher remote path:");
        Scanner scanner = new Scanner(System.in);
        String dispatcher_remote_path = scanner.nextLine();

        // Create ClientActor
        ActorRef client = system.actorOf(ClientActor.props(dispatcher_remote_path), "client");

        // TODO finish up interaction loop
        System.out.println("Type one of commands: [find, stream, order, exit]");
        while(true){
            String command = scanner.nextLine();

            if(command.equals("find")){
                System.out.println("Type a title of the book which price you want find:");
                String title = scanner.nextLine();
                ServantActor.Find find = new ServantActor.Find(title);
                client.tell(find, ActorRef.noSender());
            }
            else if(command.equals("stream")){
                System.out.println("Type a title of the book which text you want stream:");
                String title = scanner.nextLine();
                // TODO
                System.out.println("Sorry, functionality missing, it's TODO section");
            }
            else if(command.equals("order")){
                // TODO
                System.out.println("Type a title of the book which you want order:");
                String title = scanner.nextLine();
                ServantActor.Order order = new ServantActor.Order(title);
                client.tell(order, ActorRef.noSender());
            }
            else if(command.equals("exit")){
                system.terminate();
                System.exit(0);
            }

            else{
                System.out.println("Invalid command, try again");
            }
        }
    }
}
