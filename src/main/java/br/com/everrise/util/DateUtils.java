package br.com.everrise.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateUtils {

    private DateUtils() {
    }

    public static LocalDateTime nowBrazil() {
        return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}

