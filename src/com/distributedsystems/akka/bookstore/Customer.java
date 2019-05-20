package com.distributedsystems.akka.bookstore;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Customer {
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

        // Create Client
        ActorRef client = system.actorOf(Client.props(dispatcher_remote_path), "client");

        // TODO finish up interaction loop
        System.out.println("Type one of commands: [find, exit]");
        while(true){
            String command = scanner.nextLine();
            if(command.equals("find")){
                System.out.println("Type a title of the book which price you want find:");
                String title = scanner.nextLine();
                Servant.Find find = new Servant.Find(title);
                client.tell(find, ActorRef.noSender());
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
