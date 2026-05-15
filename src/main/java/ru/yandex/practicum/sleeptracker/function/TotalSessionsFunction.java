package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.util.ArrayList;
import java.util.function.Function;

public class TotalSessionsFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        return new SleepAnalysisResult(SleepAnalysisResult.ALL_SESSIONS, sessions.size());
    }
}
