package com.distributedsystems.akka.bookstore.Client;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.distributedsystems.akka.bookstore.Bookstore.DispatcherActor;
import com.distributedsystems.akka.bookstore.Bookstore.ServantActor;
import scala.concurrent.Future;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class ClientActor extends AbstractActor{
    static public Props props(String dispatcher_remote_path) {
        return Props.create(ClientActor.class, () -> new ClientActor(dispatcher_remote_path));
    }

    private LoggingAdapter log;
    private ActorRef servant;

    public ClientActor(String dispatcher_remote_path){
        this.log = Logging.getLogger(getContext().getSystem(), this);

        // Get servant from bookstore dispatcher
        ActorSelection dispatcher = getContext().actorSelection(dispatcher_remote_path);
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<Object> future = Patterns.ask(dispatcher, new DispatcherActor.GetServant(getSelf()), timeout);
        try {
            DispatcherActor.ServantRef result = (DispatcherActor.ServantRef) Await.result(future, timeout.duration());
            this.servant = result.ref;
            System.out.println("ServantActor obtained: " + this.servant);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ClientActor created");
    }

    // Messages Router
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ServantActor.Find.class, find ->{
                    this.servant.tell(find, getSelf());
                    System.out.println("Sent Find request with title: " + find.title);
                })
                .match(ServantActor.Price.class, price ->{
                    if(price == null){
                        log.info("No such position");
                    }
                    else{
                        log.info("The price of this position is: " + price.value);
                    }
                })
                .match(ServantActor.Order.class, order ->{
                    this.servant.tell(order, getSelf());
                    System.out.println("Sent Order request with title: " + order.title);
                })
                .match(ServantActor.OrderConfirmation.class, confirmation -> {
                    System.out.println("Received: " + confirmation.content);
                })
                .matchAny(o -> log.info("Invalid request"))
                .build();
    }
}