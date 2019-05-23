package com.distributedsystems.akka.bookstore.Bookstore;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.distributedsystems.akka.bookstore.database.Book;
import com.distributedsystems.akka.bookstore.database.DatabaseAdapter;

public class FinderActor extends AbstractActor{
    static public Props props(String database_root_path) {
        return Props.create(FinderActor.class, () -> new FinderActor(database_root_path));
    }

    private LoggingAdapter log;
    private DatabaseAdapter db_adapter;

    public FinderActor(String database_root_path){
        this.log = Logging.getLogger(getContext().getSystem(), this);
        this.db_adapter = new DatabaseAdapter(database_root_path);
    }

    // Messages Router
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ServantActor.Find.class, find ->{
                    Book book = db_adapter.findBook(find.title);
                    ServantActor.Price price = new ServantActor.Price(book.getPrice(), find.hashCode);
                    getSender().tell(price, getSelf());
                })
                .matchAny(o -> log.info("Invalid request"))
                .build();
    }
}
