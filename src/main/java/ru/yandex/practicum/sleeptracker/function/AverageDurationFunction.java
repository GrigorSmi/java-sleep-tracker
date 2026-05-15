package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;

public class AverageDurationFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        double avg = sessions.stream()
                .mapToDouble(s -> (double) Duration.between(s.getStartSleep(), s.getFinishSleep()).toMinutes())
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Список сессий пуст"));
        return new SleepAnalysisResult(SleepAnalysisResult.AVG_DURATION, avg);
    }
}
