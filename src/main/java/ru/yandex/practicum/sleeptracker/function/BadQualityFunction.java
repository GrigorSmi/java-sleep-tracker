package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.util.ArrayList;
import java.util.function.Function;

public class BadQualityFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        int count = (int) sessions.stream()
                .filter(s -> "BAD".equals(s.getQuality()))
                .count();
        return new SleepAnalysisResult(SleepAnalysisResult.BAD_QUALITY, count);
    }
}
