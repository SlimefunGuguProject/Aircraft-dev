package org.metamechanists.aircraft.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum Colors {
    ;

    private final String formattedColor;

    Colors(@NotNull String rawHex) {
        // "#ffffff" -> "&x&f&f&f&f&f&f
        // "#123456" -> "&x&1&2&3&4&5&6
        StringBuilder colorStringBuilder = new StringBuilder("&x");
        for (char character : rawHex.toCharArray()) {
            colorStringBuilder.append('&').append(character);
        }

        formattedColor = colorStringBuilder.toString();
    }
}
