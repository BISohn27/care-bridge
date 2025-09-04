package com.donation.carebridge.payments.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Reason {

    private static final String WHITE_SPACE = " ";
    private static final String DELIMITER = "_";
    private static final String UNKNOWN_MESSAGE = "_UNKNOWN";

    @Column(name = "reason_code", length = 50)
    private String code;

    @Column(name = "reason_message", length = 250)
    private String message;

    private Reason(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Reason pg(String rawCode, String rawMessage) {
        return new Reason(
            normalize(Origin.PG, rawCode),
            safe(rawMessage)
        );
    }

    public static Reason system(String rawCode, String rawMessage) {
        return new Reason(
            normalize(Origin.SYSTEM, rawCode),
            safe(rawMessage)
        );
    }

    public static Reason user(String rawCode, String rawMessage) {
        return new Reason(
            normalize(Origin.USER, rawCode),
            safe(rawMessage)
        );
    }

    public static Reason none() {
        return new Reason(null, null);
    }

    private static String normalize(Origin origin, String raw) {
        if (!StringUtils.hasText(raw)) {
            return origin.getCode() + UNKNOWN_MESSAGE;
        }
        String code = raw.trim().toUpperCase().replace(WHITE_SPACE, DELIMITER);
        return hasNotPrefix(origin, code) ? origin.getCode() + DELIMITER + code : code;
    }

    private static boolean hasNotPrefix(Origin origin, String code) {
        return !code.startsWith(origin.getCode() + DELIMITER);
    }

    private static String safe(String msg) {
        return !StringUtils.hasText(msg) ? null : msg;
    }
}