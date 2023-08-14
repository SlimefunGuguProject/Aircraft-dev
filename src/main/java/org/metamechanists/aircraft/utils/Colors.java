package org.metamechanists.aircraft.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum Colors {
    ;

    @Getter
    private final String formattedColor;

    Colors(@NotNull final String rawHex) {
        // "#ffffff" -> "&x&f&f&f&f&f&f
        // "#123456" -> "&x&1&2&3&4&5&6
        final StringBuilder colorStringBuilder = new StringBuilder("&x");
        for (final char character : rawHex.toCharArray()) {
            colorStringBuilder.append('&').append(character);
        }

        formattedColor = colorStringBuilder.toString();
    }
}
