package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;

public class DispatcherActor extends AbstractActor {
    static public Props props(String db1_root_path, String db2_root_path) {
        return Props.create(DispatcherActor.class, () -> new DispatcherActor(db1_root_path, db2_root_path));
    }

    private LoggingAdapter log;
    private String db1_root_path;
    private String db2_root_path;
    private ActorRef writer;


    public DispatcherActor(String db1_root_path, String db2_root_path){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.db1_root_path = db1_root_path;
        this.db2_root_path = db2_root_path;
        this.writer = getContext().actorOf(WriterActor.props(this.db1_root_path));
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
                    ActorRef servant = getContext().actorOf(ServantActor.props(getServant.client_ref,
                                                                               this.writer,
                                                                               this.db1_root_path,
                                                                               this.db2_root_path), "servant");
                    System.out.println("ServantActor path: " + servant.path());

                    // Send servant reference to the client
                    getSender().tell(new ServantRef(servant), getSelf());

                    log.info("Sent ServantRef to: " + getSender());
                })
                .build();
    }
}
