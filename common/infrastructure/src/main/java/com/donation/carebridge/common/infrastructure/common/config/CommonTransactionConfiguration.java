package com.donation.carebridge.common.infrastructure.common.config;


import com.donation.carebridge.common.domain.common.application.out.TransactionExecutor;
import com.donation.carebridge.common.infrastructure.common.executor.JpaTransactionExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration
@ConditionalOnClass(PlatformTransactionManager.class)
@RequiredArgsConstructor
public class CommonTransactionConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionExecutor.class)
    public TransactionExecutor transactionExecutor(PlatformTransactionManager transactionManager) {
        return new JpaTransactionExecutor(transactionManager);
    }

}
