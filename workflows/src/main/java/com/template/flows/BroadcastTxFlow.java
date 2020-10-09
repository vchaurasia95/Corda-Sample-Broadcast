package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.SignedTransaction;

import java.net.ConnectException;

@InitiatingFlow
public class BroadcastTxFlow extends FlowLogic<Void> {

    private final Party partyToBroadcast;
    private final SignedTransaction signedTransaction;

    public BroadcastTxFlow(Party partyToBroadcast, SignedTransaction signedTransaction) {
        this.partyToBroadcast = partyToBroadcast;
        this.signedTransaction = signedTransaction;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        FlowSession session;
             session = initiateFlow(partyToBroadcast);
        subFlow(new SendTransactionFlow(session, signedTransaction));

        return null;
    }
}
@InitiatedBy(BroadcastTxFlow.class)
class BroadcastTxResponder extends FlowLogic<Void>{

    private final FlowSession session;

    public BroadcastTxResponder(FlowSession session) {
        this.session = session;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
            subFlow(new ReceiveTransactionFlow(session,true, StatesToRecord.ALL_VISIBLE));
        return null;
    }
}
