package com.donation.carebridge.common.domain.idempotency.application;

import com.donation.carebridge.common.domain.idempotency.annotation.IdempotencyCheck;
import com.donation.carebridge.common.domain.idempotency.application.out.IdempotencyRepository;
import com.donation.carebridge.common.domain.idempotency.exception.IdempotencyException;
import com.donation.carebridge.common.domain.idempotency.model.DuplicateCheckKeyed;
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

    private final IdempotencyRepository idempotencyRepository;

    @Around("@annotation(idempotencyCheck)")
    public Object handleIdempotencyRequest(ProceedingJoinPoint joinPoint, IdempotencyCheck idempotencyCheck)
            throws Throwable {
        DuplicateCheckKeyed duplicateCheckKeyed = findIdempotencyKey(joinPoint.getArgs());

        if (duplicateCheckKeyed == null) {
            log.debug("The object does not implement the IdempotencyKeyed interface");
            return joinPoint.proceed();
        }

        String idempotencyKey = duplicateCheckKeyed.duplicateCheckKey();

        if (!StringUtils.hasText(idempotencyKey)) {
            log.debug("The idempotency key is empty");
            return joinPoint.proceed();
        }

        String prefix = idempotencyCheck.prefix();
        if (!idempotencyRepository.reserveIdempotencyKey(prefix, idempotencyKey)) {
            throw new IdempotencyException("DUPLICATE_REQUEST",
                    "The request has been executed already: " + idempotencyKey);
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

    private DuplicateCheckKeyed findIdempotencyKey(Object[] args) {
        return Stream.of(args)
                .filter(arg -> arg instanceof DuplicateCheckKeyed)
                .map(arg -> (DuplicateCheckKeyed) arg)
                .findFirst()
                .orElse(null);
    }
}
