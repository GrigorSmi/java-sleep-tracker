package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;

public class MinDurationFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        int min = sessions.stream()
                .mapToInt(s -> (int) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .min()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.MIN_DURATION, min);
    }
}
