package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;

public class SleepAnalytics {

    // Находит минимальную продолжительность сессии сна в минутах
    public SleepAnalysisResult minDuration(ArrayList<OneSleepSession> sessions) {
        long min = sessions.stream()
                .mapToLong(s -> Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .min()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult("Минимальная продолжительность сессии сна (мин)", min);
    }

    // Находит максимальную продолжительность сессии сна в минутах
    public SleepAnalysisResult maxDuration(ArrayList<OneSleepSession> sessions) {
        long max = sessions.stream()
                .mapToLong(s -> Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult("Максимальная продолжительность сессии сна (мин)", max);
    }

    // Вычисляет среднюю продолжительность сессии сна в минутах
    public SleepAnalysisResult avgDuration(ArrayList<OneSleepSession> sessions) {
        double avg = sessions.stream()
                .mapToLong(s -> Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult("Средняя продолжительность сессии сна (мин)", avg);
    }

    // Подсчитывает количество сессий с плохим качеством сна (BAD)
    public SleepAnalysisResult countBadQuality(ArrayList<OneSleepSession> sessions) {
        long count = sessions.stream()
                .filter(s -> "BAD".equals(s.getQuality()))
                .count();
        return new SleepAnalysisResult("Количество сессий с плохим качеством сна", count);
    }

    // Подсчитывает количество бессонных ночей.
    // Бессонной считается ночь (00:00-06:00) конкретной даты,
    // которую не пересекает ни одна сессия сна.
    public SleepAnalysisResult countSleeplessNights(ArrayList<OneSleepSession> sessions) {
        ZoneId zone = ZoneId.systemDefault();

        LocalDate minDate = sessions.stream()
                .map(s -> s.getStartSleep().atZone(zone).toLocalDate())
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));

        LocalDate maxDate = sessions.stream()
                .map(s -> s.getFinishSleep().atZone(zone).toLocalDate())
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));

        long sleeplessNights = minDate.datesUntil(maxDate.plusDays(1))
                .filter(date -> {
                    Instant nightStart = date.atStartOfDay(zone).toInstant();
                    Instant nightEnd = date.atTime(6, 0).atZone(zone).toInstant();
                    return sessions.stream().noneMatch(s ->
                            s.getStartSleep().isBefore(nightEnd) &&
                            s.getFinishSleep().isAfter(nightStart)
                    );
                })
                .count();

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }
}
