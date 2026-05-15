package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class SleepSessionUtils {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static ZoneId getZone() {
        return ZONE;
    }

    public static boolean isNightSession(OneSleepSession session) {
        LocalDate endDate = session.getFinishSleep().atZone(ZONE).toLocalDate();
        Instant nightStart = endDate.atStartOfDay(ZONE).toInstant();
        Instant nightEnd = endDate.atTime(6, 0).atZone(ZONE).toInstant();
        return session.getStartSleep().isBefore(nightEnd) &&
                session.getFinishSleep().isAfter(nightStart);
    }

    public static String classifyChronotype(OneSleepSession session) {
        LocalTime startTime = session.getStartSleep().atZone(ZONE).toLocalTime();
        LocalTime endTime = session.getFinishSleep().atZone(ZONE).toLocalTime();

        boolean isOwl = startTime.isAfter(LocalTime.of(23, 0)) &&
                endTime.isAfter(LocalTime.of(9, 0));
        boolean isLark = startTime.isBefore(LocalTime.of(22, 0)) &&
                endTime.isBefore(LocalTime.of(7, 0));

        if (isOwl) return "Сова";
        if (isLark) return "Жаворонок";
        return "Голубь";
    }
}
