package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.OneSleepSession;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeFunction implements Function<ArrayList<OneSleepSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(ArrayList<OneSleepSession> sessions) {
        Map<LocalDate, OneSleepSession> firstSessionOfNight = sessions.stream()
                .filter(SleepSessionUtils::isNightSession)
                .collect(Collectors.groupingBy(
                        s -> s.getFinishSleep().atZone(SleepSessionUtils.getZone()).toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(OneSleepSession::getStartSleep)),
                                opt -> opt.orElseThrow()
                        )
                ));

        Map<String, Integer> counts = firstSessionOfNight.values().stream()
                .map(SleepSessionUtils::classifyChronotype)
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
