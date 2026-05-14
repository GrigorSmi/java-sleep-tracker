package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepTrackerAppTest {

    private SleepAnalytics analytics;
    private ArrayList<OneSleepSession> sessions;

    private static OneSleepSession makeSession(int startDay, int startHour, int startMinute,
                                                int endDay, int endHour, int endMinute,
                                                String quality) {
        return new OneSleepSession(
                LocalDateTime.of(2025, 10, startDay, startHour, startMinute)
                        .atZone(ZoneId.systemDefault()).toInstant(),
                LocalDateTime.of(2025, 10, endDay, endHour, endMinute)
                        .atZone(ZoneId.systemDefault()).toInstant(),
                quality
        );
    }

    @BeforeEach
    void setUp() {
        analytics = new SleepAnalytics();
        sessions = new ArrayList<>();
    }

    @Test
    void minDuration_singleSession_returnsItsDuration() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.minDuration(sessions);

        assertEquals("Минимальная продолжительность сессии сна (мин)", result.getDescription());
        assertEquals(480L, result.getResult());
    }

    @Test
    void minDuration_multipleSessions_returnsSmallest() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min
        sessions.add(makeSession(5, 0, 0, 5, 6, 0, "GOOD"));       // 360 min

        SleepAnalysisResult result = analytics.minDuration(sessions);

        assertEquals(60L, result.getResult());
    }

    @Test
    void maxDuration_singleSession_returnsItsDuration() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.maxDuration(sessions);

        assertEquals("Максимальная продолжительность сессии сна (мин)", result.getDescription());
        assertEquals(480L, result.getResult());
    }

    @Test
    void maxDuration_multipleSessions_returnsLargest() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min
        sessions.add(makeSession(5, 0, 0, 5, 6, 0, "GOOD"));       // 360 min

        SleepAnalysisResult result = analytics.maxDuration(sessions);

        assertEquals(480L, result.getResult());
    }

    @Test
    void avgDuration_singleSession_returnsItsDurationAsDouble() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.avgDuration(sessions);

        assertEquals("Средняя продолжительность сессии сна (мин)", result.getDescription());
        assertEquals(480.0, (Double) result.getResult(), 1e-9);
    }

    @Test
    void avgDuration_multipleSessions_returnsCorrectAverage() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min

        SleepAnalysisResult result = analytics.avgDuration(sessions);

        assertEquals(270.0, (Double) result.getResult(), 1e-9);
    }

    @Test
    void countBadQuality_noBadSessions_returnsZero() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));

        SleepAnalysisResult result = analytics.countBadQuality(sessions);

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    void countBadQuality_mixedSessions_returnsCorrectCount() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(3, 23, 40, 4, 8, 0, "BAD"));
        sessions.add(makeSession(5, 13, 30, 5, 14, 15, "NORMAL"));
        sessions.add(makeSession(7, 23, 10, 8, 7, 0, "BAD"));

        SleepAnalysisResult result = analytics.countBadQuality(sessions);

        assertEquals(2L, result.getResult());
    }

    @Test
    void sleeplessNights_sessionInsideNight_returnsZero() {
        sessions.add(makeSession(1, 2, 0, 1, 5, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    void sleeplessNights_sessionEndingAtSix_returnsZero() {
        sessions.add(makeSession(1, 1, 0, 1, 6, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(0L, result.getResult());
    }

    @Test
    void sleeplessNights_sessionAfterNight_returnsOne() {
        sessions.add(makeSession(1, 7, 0, 1, 11, 0, "NORMAL"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(1L, result.getResult());
    }

    @Test
    void sleeplessNights_allNightsCovered_returnsZero() {
        sessions.add(makeSession(1, 2, 0, 1, 5, 0, "GOOD"));
        sessions.add(makeSession(2, 1, 0, 2, 4, 30, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(0L, result.getResult());
    }

    @Test
    void sleeplessNights_gapInDays_returnsCorrectCount() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(5, 23, 0, 6, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(4L, result.getResult());
    }

    @Test
    void sleeplessNights_emptyList_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> analytics.countSleeplessNights(sessions));
    }

    @Test
    void allFunctions_haveRussianDescriptions() {
        List<SleepAnalysisResult> results = List.of(
                analytics.minDuration(sessions),
                analytics.maxDuration(sessions),
                analytics.avgDuration(sessions),
                analytics.countBadQuality(sessions),
                analytics.countSleeplessNights(sessions)
        );

        results.forEach(r ->
                assertTrue(r.getDescription() != null && !r.getDescription().isEmpty(),
                        "Описание не должно быть пустым")
        );
    }
}
