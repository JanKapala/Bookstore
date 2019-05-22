package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;

public class DispatcherActor extends AbstractActor {
    static public Props props(String bookstore_name) {
        return Props.create(DispatcherActor.class, () -> new DispatcherActor(bookstore_name));
    }

    private String bookstore_name = "";
    private LoggingAdapter log;

    public DispatcherActor(String bookstore_name){
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
                    ActorRef servant = getContext().actorOf(ServantActor.props(getServant.client_ref), "servant");
                    System.out.println("ServantActor path: " + servant.path());

                    // Send servant reference to the client
                    getSender().tell(new ServantRef(servant), getSelf());

                    log.info("Sent ServantRef to: " + getSender());
                })
                .build();
    }
}
