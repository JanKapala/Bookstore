package com.distributedsystems.akka.bookstore;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;
import java.math.BigDecimal;

public class Servant extends AbstractActor {
    static public Props props(ActorRef client_ref) {
        return Props.create(Servant.class, () -> new Servant(client_ref));
    }

    private LoggingAdapter log;
    private ActorRef client_ref;

    public Servant(ActorRef client_ref){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.client_ref = client_ref;
    }

    // Messages
    static public class Find implements Serializable {
        public final String title;
        public Find(String title){
            this.title = title;
        }
    }

    static public class Price implements Serializable{
        public final BigDecimal value;

        public Price(BigDecimal value){
            this.value = value;
        }
    }

    static public class Order implements Serializable {
        public final String title;
        public Order(String title){
            this.title = title;
        }
    }

    static public class OrderConfirmation implements Serializable{
        public final String content;

        public OrderConfirmation(String content){
            this.content = content;
        }
    }

    // TODO stream text messages

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Find.class, find -> {
                    log.info("Received getServant");

                    // TODO Find title in the database
                    // placeholder:
                    String title = "Iliada";
                    if(find.title.toLowerCase().equals(title.toLowerCase())) {
                        Price price = new Price(new BigDecimal(320));
                        this.client_ref.tell(price, getSelf());
                        log.info("Sent price: " + price.value);
                    }
                    else {
                        Price price = null;
                        this.client_ref.tell(price, getSelf());
                        log.info("Sent no such position flag");
                    }
                })
                .build();
    }
}
