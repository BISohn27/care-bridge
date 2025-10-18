package com.donation.carebridge.common.infrastructure.common.executor;

import com.donation.carebridge.common.domain.common.application.out.TransactionExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseTransactionExecutor implements TransactionExecutor {

    private final PlatformTransactionManager transactionManager;

    public <T> T executeInTransaction(Supplier<T> operation) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        return execute(operation, definition);
    }

    private <T> T execute(Supplier<T> operation, TransactionDefinition definition) {
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            T result = operation.get();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }

    public void executeInTransaction(Runnable operation) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            operation.run();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }

    public <T> T executeInTransaction(Supplier<T> operation, TransactionDefinition definition) {
        return execute(operation, definition);
    }
}
