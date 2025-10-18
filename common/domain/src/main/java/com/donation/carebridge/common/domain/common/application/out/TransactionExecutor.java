package com.donation.carebridge.common.domain.common.application.out;

import org.springframework.transaction.TransactionDefinition;

import java.util.function.Supplier;

public interface TransactionExecutor {

    <T> T executeInTransaction(Supplier<T> operation);
    void executeInTransaction(Runnable operation);
    <T> T executeInTransaction(Supplier<T> operation, TransactionDefinition definition);
}
