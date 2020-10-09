package com.template.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class IOUContract implements Contract {
    public static final String ID = "com.template.contracts.IOUContract";
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }
    public interface Commands extends CommandData {
        class Action implements Commands {}
    }
}
