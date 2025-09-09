package com.donation.carebridge.payments.payment.application;

import com.donation.carebridge.payments.payment.annotation.IdempotencyCheck;
import com.donation.carebridge.payments.payment.model.IdempotencyKeyed;
import com.donation.carebridge.payments.payment.out.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final String PROCESS = "PROCESS";
    private static final String COMPLETE = "COMPLETE";

    private final IdempotencyRepository idempotencyRepository;

    @Around("@annotation(idempotencyCheck)")
    public Object handleIdempotencyRequest(ProceedingJoinPoint joinPoint, IdempotencyCheck idempotencyCheck)
            throws Throwable {
        IdempotencyKeyed idempotencyKeyed = findIdempotencyKey(joinPoint.getArgs());

        if (idempotencyKeyed == null) {
            log.debug("The object does not implement the IdempotencyKeyed interface");
            return joinPoint.proceed();
        }

        String idempotencyKey = idempotencyKeyed.idempotencyKey();

        if (!StringUtils.hasText(idempotencyKey)) {
            log.debug("The idempotency key is empty");
            return joinPoint.proceed();
        }

        String prefix = idempotencyCheck.prefix();
        if (idempotencyRepository.reserveIdempotencyKey(prefix, idempotencyKey)) {
            throw new IllegalStateException("The request has been executed already.");
        }

        try {
            Object result = joinPoint.proceed();
            idempotencyRepository.completeIdempotencyKey(prefix, idempotencyKey);
            return result;
        } catch (Exception e) {
            idempotencyRepository.cancelIdempotencyKey(prefix, idempotencyKey);
            throw e;
        }
    }

    private IdempotencyKeyed findIdempotencyKey(Object[] args) {
        return Stream.of(args)
                .filter(arg -> arg instanceof IdempotencyKeyed)
                .map(arg -> (IdempotencyKeyed) arg)
                .findFirst()
                .orElse(null);
    }
}
