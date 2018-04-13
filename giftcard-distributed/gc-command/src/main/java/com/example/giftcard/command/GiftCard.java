package com.example.giftcard.command;

import com.example.giftcard.command.api.IssueCmd;
import com.example.giftcard.command.api.IssuedEvt;
import com.example.giftcard.command.api.RedeemCmd;
import com.example.giftcard.command.api.RedeemedEvt;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class GiftCard {

    private final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @AggregateIdentifier
    private String id;
    private int remainingValue;

    public GiftCard() {
        log.debug("empty constructor invoked");
    }

    @CommandHandler
    public GiftCard(IssueCmd cmd) {
        log.debug("handling {}", cmd);
        if(cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        apply(new IssuedEvt(cmd.getId(), cmd.getAmount()));
    }

    @CommandHandler
    public void handle(RedeemCmd cmd) {
        log.debug("handling {}", cmd);
        if(cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        if(cmd.getAmount() > remainingValue) throw new IllegalStateException("amount > remaining value");
        apply(new RedeemedEvt(id, cmd.getAmount()));
    }

    @EventSourcingHandler
    public void on(IssuedEvt evt) {
        log.debug("applying {}", evt);
        id = evt.getId();
        remainingValue = evt.getAmount();
        log.debug("new remaining value: {}", remainingValue);
    }

    @EventSourcingHandler
    public void on(RedeemedEvt evt) {
        log.debug("applying {}", evt);
        remainingValue -= evt.getAmount();
        log.debug("new remaining value: {}", remainingValue);
    }

}
