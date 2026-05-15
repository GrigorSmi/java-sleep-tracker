package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

public class SleeplessNightsFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult(SleepAnalysisResult.SLEEPLESS_NIGHTS, 0);
        }

        OneSleepSession firstSession = sessions.stream()
                .min(Comparator.comparing(OneSleepSession::getStartSleep))
                .orElseThrow();

        LocalTime firstStartTime = firstSession.getStartSleep().atZone(ZONE).toLocalTime();
        LocalDate firstDate = firstSession.getStartSleep().atZone(ZONE).toLocalDate();
        LocalDate firstNightDate = firstStartTime.isBefore(LocalTime.of(12, 0))
                ? firstDate
                : firstDate.plusDays(1);

        LocalDate maxDate = sessions.stream()
                .map(s -> s.getFinishSleep().atZone(ZONE).toLocalDate())
                .max(Comparator.naturalOrder())
                .orElseThrow();

        int sleeplessNights = (int) firstNightDate.datesUntil(maxDate.plusDays(1))
                .filter(date -> sessions.stream().noneMatch(s ->
                        s.getFinishSleep().atZone(ZONE).toLocalDate().equals(date)
                                && SleepSessionUtils.isNightSession(s)
                ))
                .count();

        return new SleepAnalysisResult(SleepAnalysisResult.SLEEPLESS_NIGHTS, sleeplessNights);
    }
}
