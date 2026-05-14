package ru.yandex.practicum.sleeptracker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OneSleepSession {
    private Instant startSleep;
    private Instant finishSleep;
    private String quality;

     public OneSleepSession(Instant startSleep, Instant finishSleep, String quality) {
        this.startSleep = startSleep;
        this.finishSleep = finishSleep;
        this.quality = quality;
    }

    // для преобразования строки
     public static OneSleepSession fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректный формат записи о сессии: " + line);
        }

        String startStr = parts[0];
        String finishStr = parts[1];
        String quality = parts[2];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startStr, formatter);
        LocalDateTime finishDateTime = LocalDateTime.parse(finishStr, formatter);

        Instant startInstant = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Instant finishInstant = finishDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return new OneSleepSession(startInstant, finishInstant, quality);
    }

     public Instant getStartSleep() { return startSleep; }
    public Instant getFinishSleep() { return finishSleep; }
    public String getQuality() { return quality; }

    @Override
    public String toString() {
        return String.format("OneSleepSession{start=%s, finish=%s, quality='%s'}",
                startSleep, finishSleep, quality);
    }
}
