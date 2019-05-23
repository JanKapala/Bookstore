package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServantActor extends AbstractActor {
    static public Props props(ActorRef client_ref, ActorRef writer, String db1_root_path, String db2_root_path) {
        return Props.create(ServantActor.class, () -> new ServantActor(client_ref, writer, db1_root_path, db2_root_path));
    }

    private LoggingAdapter log;
    private ActorRef client_ref;
    private String db1_root_path;
    private String db2_root_path;
    private ActorRef finder1;
    private ActorRef finder2;
    private ActorRef writer;
    private Set<Integer> find_requests_hashes;

    public ServantActor(ActorRef client_ref, ActorRef writer, String db1_root_path, String db2_root_path){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.client_ref = client_ref;
        this.db1_root_path = db1_root_path;
        this.db2_root_path = db2_root_path;
        this.writer = writer;
        this.find_requests_hashes = new HashSet<>();
    }

    // Messages
    static public class Find implements Serializable {
        public final String title;
        public Integer hashCode;

        public Find(String title){
            this.title = title;
            this.hashCode = null;
        }
    }

    static public class Price implements Serializable{
        public final BigDecimal value;
        public Integer hashcode;

        public Price(BigDecimal value, Integer hashcode){
            this.hashcode = hashcode;
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

    // Messages router

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Find.class, find -> {
                    this.finder1 = getContext().actorOf(FinderActor.props(this.db1_root_path), "finder1");
                    this.finder2 = getContext().actorOf(FinderActor.props(this.db2_root_path), "finder2");

                    Date date = new Date();
                    Integer hashCode = date.hashCode();
                    this.find_requests_hashes.add(hashCode);
                    find.hashCode = hashCode;

                    finder1.tell(find, getSelf());
                    finder2.tell(find, getSelf());
                })
                .match(Price.class, price->{
                    getContext().stop(finder1);
                    getContext().stop(finder2);

                    if(find_requests_hashes.contains(price.hashcode)){
                        find_requests_hashes.remove(price.hashcode);
                        this.client_ref.tell(price, getSelf());
                    }
                })
                .match(Order.class, order -> {
                    this.writer.tell(order, getSelf());
                })
                .match(OrderConfirmation.class, confirmation -> {
                    this.client_ref.tell(confirmation, getSelf());
                })
                .build();
    }
}
