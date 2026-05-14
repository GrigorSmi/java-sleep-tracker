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

    // Тест: одна сессия — минимальная длительность равна её длительности (480 мин)
    @Test
    void minDuration_singleSession_returnsItsDuration() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.minDuration(sessions);

        assertEquals(SleepAnalysisResult.MIN_DURATION, result.getDescription());
        assertEquals(480, result.getResult());
    }

    // Тест: несколько сессий разной длины — выбирается минимальная (60 мин)
    @Test
    void minDuration_multipleSessions_returnsSmallest() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min
        sessions.add(makeSession(5, 0, 0, 5, 6, 0, "GOOD"));       // 360 min

        SleepAnalysisResult result = analytics.minDuration(sessions);

        assertEquals(60, result.getResult());
    }

    // Тест: одна сессия — максимальная длительность равна её длительности (480 мин)
    @Test
    void maxDuration_singleSession_returnsItsDuration() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.maxDuration(sessions);

        assertEquals(SleepAnalysisResult.MAX_DURATION, result.getDescription());
        assertEquals(480, result.getResult());
    }

    // Тест: несколько сессий разной длины — выбирается максимальная (480 мин)
    @Test
    void maxDuration_multipleSessions_returnsLargest() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min
        sessions.add(makeSession(5, 0, 0, 5, 6, 0, "GOOD"));       // 360 min

        SleepAnalysisResult result = analytics.maxDuration(sessions);

        assertEquals(480, result.getResult());
    }

    // Тест: одна сессия — средняя длительность равна её длительности (480.0 мин)
    @Test
    void avgDuration_singleSession_returnsItsDurationAsDouble() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.avgDuration(sessions);

        assertEquals(SleepAnalysisResult.AVG_DURATION, result.getDescription());
        assertEquals(480.0, (Double) result.getResult(), 1e-9);
    }

    // Тест: две сессии 480 и 60 мин — среднее = 270.0 мин
    @Test
    void avgDuration_multipleSessions_returnsCorrectAverage() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));     // 480 min
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));   // 60 min

        SleepAnalysisResult result = analytics.avgDuration(sessions);

        assertEquals(270.0, (Double) result.getResult(), 1e-9);
    }

    // Тест: нет сессий с качеством BAD — результат 0
    @Test
    void countBadQuality_noBadSessions_returnsZero() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(3, 14, 0, 3, 15, 0, "NORMAL"));

        SleepAnalysisResult result = analytics.countBadQuality(sessions);

        assertEquals(SleepAnalysisResult.BAD_QUALITY, result.getDescription());
        assertEquals(0, result.getResult());
    }

    // Тест: 2 BAD из 4 сессий — результат 2
    @Test
    void countBadQuality_mixedSessions_returnsCorrectCount() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(3, 23, 40, 4, 8, 0, "BAD"));
        sessions.add(makeSession(5, 13, 30, 5, 14, 15, "NORMAL"));
        sessions.add(makeSession(7, 23, 10, 8, 7, 0, "BAD"));

        SleepAnalysisResult result = analytics.countBadQuality(sessions);

        assertEquals(2, result.getResult());
    }

    // Тест: сессия целиком внутри ночного интервала 02:00-05:00 — ночь покрыта, результат 0
    @Test
    void sleeplessNights_sessionInsideNight_returnsZero() {
        sessions.add(makeSession(1, 2, 0, 1, 5, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(SleepAnalysisResult.SLEEPLESS_NIGHTS, result.getDescription());
        assertEquals(0, result.getResult());
    }

    // Тест: сессия 01:00-06:00 — заканчивается ровно в 06:00 — ночь покрыта, результат 0
    @Test
    void sleeplessNights_sessionEndingAtSix_returnsZero() {
        sessions.add(makeSession(1, 1, 0, 1, 6, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(0, result.getResult());
    }

    // Тест: сессия 07:00-11:00 — не пересекает [00:00, 06:00), ночь бессонная, результат 1
    @Test
    void sleeplessNights_sessionAfterNight_returnsOne() {
        sessions.add(makeSession(1, 7, 0, 1, 11, 0, "NORMAL"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(1, result.getResult());
    }

    // Тест: две сессии в разные даты, каждая покрывает свою ночь — 0 бессонных
    @Test
    void sleeplessNights_allNightsCovered_returnsZero() {
        sessions.add(makeSession(1, 2, 0, 1, 5, 0, "GOOD"));
        sessions.add(makeSession(2, 1, 0, 2, 4, 30, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(0, result.getResult());
    }

    // Тест: две сессии с разрывом в 4 дня — бессонными считаются ночи без покрытия, результат 4
    @Test
    void sleeplessNights_gapInDays_returnsCorrectCount() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));
        sessions.add(makeSession(5, 23, 0, 6, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.countSleeplessNights(sessions);

        assertEquals(4, result.getResult());
    }

    // Тест: пустой список сессий — исключение
    @Test
    void sleeplessNights_emptyList_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> analytics.countSleeplessNights(sessions));
    }

    // Тест: ночная сессия 23:30-09:30 — сова (засыпание после 23:00, пробуждение после 09:00)
    @Test
    void determineChronotype_owl_returns() {
        sessions.add(makeSession(1, 23, 30, 2, 9, 30, "GOOD"));

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals(SleepAnalysisResult.CHRONOTYPE, result.getDescription());
        assertEquals("Сова", result.getResult());
    }

    // Тест: ночная сессия 21:00-06:30 — жаворонок (засыпание до 22:00, пробуждение до 07:00)
    @Test
    void determineChronotype_lark_returns() {
        sessions.add(makeSession(1, 21, 0, 2, 6, 30, "GOOD"));

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals("Жаворонок", result.getResult());
    }

    // Тест: ночная сессия 23:00-07:00 — не подходит ни под сову, ни под жаворонка, голубь
    @Test
    void determineChronotype_pigeon_returns() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals("Голубь", result.getResult());
    }

    // Тест: только дневная сессия (14:00-15:00) — недостаточно данных
    @Test
    void determineChronotype_daySessionIgnored_returnsNoData() {
        sessions.add(makeSession(1, 14, 0, 1, 15, 0, "NORMAL"));

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals(SleepAnalysisResult.NO_DATA, result.getResult());
    }

    // Тест: одна сова и один жаворонок — ничья, результат "Голубь"
    @Test
    void determineChronotype_tie_returns() {
        sessions.add(makeSession(1, 23, 30, 2, 9, 30, "GOOD"));   // сова
        sessions.add(makeSession(3, 21, 0, 4, 6, 30, "GOOD"));     // жаворонок

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals("Голубь", result.getResult());
    }

    // Тест: две совы и один жаворонок — большинство за совой
    @Test
    void determineChronotype_owlMajority_returns() {
        sessions.add(makeSession(1, 23, 30, 2, 9, 30, "GOOD"));   // сова
        sessions.add(makeSession(3, 23, 45, 4, 9, 15, "GOOD"));   // сова
        sessions.add(makeSession(5, 21, 0, 6, 6, 30, "GOOD"));    // жаворонок

        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals("Сова", result.getResult());
    }

    // Тест: пустой список — недостаточно данных
    @Test
    void determineChronotype_emptyList_returnsNoData() {
        SleepAnalysisResult result = analytics.determineChronotype(sessions);

        assertEquals(SleepAnalysisResult.NO_DATA, result.getResult());
    }

    // Тест: все функции имеют непустое русскоязычное описание
    @Test
    void allFunctions_haveRussianDescriptions() {
        sessions.add(makeSession(1, 23, 0, 2, 7, 0, "GOOD"));

        List<SleepAnalysisResult> results = List.of(
                analytics.minDuration(sessions),
                analytics.maxDuration(sessions),
                analytics.avgDuration(sessions),
                analytics.countBadQuality(sessions),
                analytics.countSleeplessNights(sessions),
                analytics.determineChronotype(sessions)
        );

        results.forEach(r ->
                assertTrue(r.getDescription() != null && !r.getDescription().isEmpty(),
                        "Описание не должно быть пустым")
        );
    }
}
