package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.distributedsystems.akka.bookstore.database.DatabaseAdapter;
import com.distributedsystems.akka.bookstore.database.Order;

public class WriterActor extends AbstractActor{
    static public Props props(String database_root_path) {
        return Props.create(WriterActor.class, () -> new WriterActor(database_root_path));
    }

    private LoggingAdapter log;
    private DatabaseAdapter db_adapter;

    public WriterActor(String database_root_path){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.db_adapter = new DatabaseAdapter(database_root_path);
    }

    // Messages Router
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ServantActor.Order.class, order ->{
                    Order db_order = db_adapter.order(order.title);
                    String order_confirmation = "Confirmation: " + db_order.toString();
                    getSender().tell(new ServantActor.OrderConfirmation(order_confirmation), getSelf());
                })
                .matchAny(o -> log.info("Invalid request"))
                .build();
    }
}

