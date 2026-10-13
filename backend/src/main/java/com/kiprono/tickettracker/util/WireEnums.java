package com.kiprono.tickettracker.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Maps Java enum constants to and from the lowercase form used on the wire
 * ({@code IN_PROGRESS} &lt;-&gt; {@code in_progress}), for both JSON bodies and query params.
 */
public final class WireEnums {

    private WireEnums() {
    }

    public static String toWireValue(Enum<?> constant) {
        return constant.name().toLowerCase(Locale.ROOT);
    }

    /**
     * @throws IllegalArgumentException when the value is blank or not one of the enum's constants;
     *                                  the message lists the accepted values and is safe to return to clients
     */
    public static <E extends Enum<E>> E parse(Class<E> type, String value, String label) {
        if (value != null && !value.isBlank()) {
            String candidate = value.trim().toUpperCase(Locale.ROOT);
            for (E constant : type.getEnumConstants()) {
                if (constant.name().equals(candidate)) {
                    return constant;
                }
            }
        }
        String allowed = Arrays.stream(type.getEnumConstants())
                .map(WireEnums::toWireValue)
                .collect(Collectors.joining(", "));
        throw new IllegalArgumentException(
                "Invalid %s '%s'. Allowed values: %s".formatted(label, value, allowed));
    }
}
