package com.distributedsystems.akka.bookstore;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;

public class Dispatcher extends AbstractActor {
    static public Props props(String bookstore_name) {
        return Props.create(Dispatcher.class, () -> new Dispatcher(bookstore_name));
    }

    private String bookstore_name = "";
    private LoggingAdapter log;

    public Dispatcher(String bookstore_name){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.bookstore_name = bookstore_name;
    }

    // Messages
    static public class GetServant implements Serializable{
        public final ActorRef client_ref;
        public GetServant(ActorRef client_ref) {
            this.client_ref = client_ref;
        }
    }

    static public class ServantRef implements Serializable {
        public final ActorRef ref;

        public ServantRef(ActorRef ref){
            this.ref = ref;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetServant.class, getServant -> {
                    log.info("Received getServant from: " + getSender());

                    // Create individual servant for the client
                    ActorRef servant = getContext().actorOf(Servant.props(getServant.client_ref), "servant");
                    System.out.println("Servant path: " + servant.path());

                    // Send servant reference to the client
                    getSender().tell(new ServantRef(servant), getSelf());

                    log.info("Sent ServantRef to: " + getSender());
                })
                .build();
    }
}
