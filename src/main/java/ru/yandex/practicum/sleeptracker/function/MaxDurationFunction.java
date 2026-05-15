package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;

public class MaxDurationFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        int max = sessions.stream()
                .mapToInt(s -> (int) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.MAX_DURATION, max);
    }
}
