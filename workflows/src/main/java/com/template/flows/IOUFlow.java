package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.IOUContract;
import com.template.states.IOUState;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

@StartableByRPC
@InitiatingFlow
public class IOUFlow extends FlowLogic<SignedTransaction> {
    private final Integer iouValue;
    private final Party otherParty;
    private final Party partyToSend;

    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public IOUFlow(Integer iouValue, Party otherParty, Party partyToSend) {
        this.iouValue = iouValue;
        this.otherParty = otherParty;
        this.partyToSend = partyToSend;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We retrieve the notary identity from the network map.
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // We create the transaction components.
        IOUState outputState = new IOUState(iouValue, getOurIdentity(), otherParty);
        Command command = new Command<>(new IOUContract.Commands.Action(), getOurIdentity().getOwningKey());

        // We create a transaction builder and add the components.
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, IOUContract.ID)
                .addCommand(command);

        // Signing the transaction.
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Creating a session with the other party.
        FlowSession otherPartySession = initiateFlow(otherParty);

        // We finalise the transaction and then send it to the counterparty.
        SignedTransaction tx = subFlow(new FinalityFlow(signedTx, otherPartySession));

        subFlow(new BroadcastTxFlow(partyToSend,tx));

        return tx;
    }
}

@InitiatedBy(IOUFlow.class)
class IOUFlowResponder extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public IOUFlowResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        subFlow(new ReceiveFinalityFlow(otherPartySession));

        return null;
    }
}

