package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SleepAnalytics {

    // Находит минимальную продолжительность сессии сна в минутах
    public SleepAnalysisResult minDuration(ArrayList<OneSleepSession> sessions) {
        int min = sessions.stream()
                .mapToInt(s -> (int) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .min()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.MIN_DURATION, min);
    }

    // Находит максимальную продолжительность сессии сна в минутах
    public SleepAnalysisResult maxDuration(ArrayList<OneSleepSession> sessions) {
        int max = sessions.stream()
                .mapToInt(s -> (int) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.MAX_DURATION, max);
    }

    // Вычисляет среднюю продолжительность сессии сна в минутах
    public SleepAnalysisResult avgDuration(ArrayList<OneSleepSession> sessions) {
        double avg = sessions.stream()
                .mapToDouble(s -> (double) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.AVG_DURATION, avg);
    }

    // Подсчитывает общее количество сессий сна
    public SleepAnalysisResult countAllSessions(ArrayList<OneSleepSession> sessions) {
        return new SleepAnalysisResult(SleepAnalysisResult.ALL_SESSIONS, sessions.size());
    }

    // Подсчитывает количество сессий с плохим качеством сна
    public SleepAnalysisResult countBadQuality(ArrayList<OneSleepSession> sessions) {
        int count = (int) sessions.stream()
                .filter(s -> "BAD".equals(s.getQuality()))
                .count();
        return new SleepAnalysisResult(SleepAnalysisResult.BAD_QUALITY, count);
    }

    // Подсчитывает количество бессонных ночей.
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

        int sleeplessNights = (int) minDate.datesUntil(maxDate.plusDays(1))
                .filter(date -> sessions.stream().noneMatch(s ->
                        s.getFinishSleep().atZone(zone).toLocalDate().equals(date) && isNightSession(s, zone)
                ))
                .count();

        return new SleepAnalysisResult(SleepAnalysisResult.SLEEPLESS_NIGHTS, sleeplessNights);
    }

    // Проверяет, является ли сессия ночной (00:00-06:00) для хронотипа и кол-ва бессоных
    private boolean isNightSession(OneSleepSession session, ZoneId zone) {
        LocalDate endDate = session.getFinishSleep().atZone(zone).toLocalDate();
        Instant nightStart = endDate.atStartOfDay(zone).toInstant();
        Instant nightEnd = endDate.atTime(6, 0).atZone(zone).toInstant();
        return session.getStartSleep().isBefore(nightEnd) &&
                session.getFinishSleep().isAfter(nightStart);
    }

    // Определяет хронотип одной ночной сессии
    private String classifyChronotype(OneSleepSession session, ZoneId zone) {
        LocalTime startTime = session.getStartSleep().atZone(zone).toLocalTime();
        LocalTime endTime = session.getFinishSleep().atZone(zone).toLocalTime();

        boolean isOwl = startTime.isAfter(LocalTime.of(23, 0)) &&
                endTime.isAfter(LocalTime.of(9, 0));
        boolean isLark = startTime.isBefore(LocalTime.of(22, 0)) &&
                endTime.isBefore(LocalTime.of(7, 0));

        if (isOwl) return "Сова";
        if (isLark) return "Жаворонок";
        return "Голубь";
    }

    // Определяет хронотип пользователя всех сессий
    public SleepAnalysisResult determineChronotype(ArrayList<OneSleepSession> sessions) {
        ZoneId zone = ZoneId.systemDefault();

        Map<String, Integer> counts = sessions.stream()
                .filter(s -> isNightSession(s, zone))
                .map(s -> classifyChronotype(s, zone))
                .collect(Collectors.groupingBy(s -> s, Collectors.summingInt(s -> 1)));

        if (counts.isEmpty()) {
            return new SleepAnalysisResult(SleepAnalysisResult.CHRONOTYPE, SleepAnalysisResult.NO_DATA);
        }

        int maxCount = counts.values().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);

        List<String> topTypes = counts.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();

        String chronotype = topTypes.size() > 1 ? "Голубь" : topTypes.get(0);
        return new SleepAnalysisResult(SleepAnalysisResult.CHRONOTYPE, chronotype);
    }
}
