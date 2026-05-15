package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private final SleepAnalytics analytics = new SleepAnalytics();

    private final List<Function<ArrayList<OneSleepSession>, SleepAnalysisResult>> functions = List.of(
            analytics::countAllSessions,
            analytics::minDuration,
            analytics::maxDuration,
            analytics::avgDuration,
            analytics::countBadQuality,
            analytics::countSleeplessNights,
            analytics::determineChronotype
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Не указан путь к файлу с данными");
            return;
        }

        try {
            ArrayList<OneSleepSession> sleepSessions = Files.lines(
                            Paths.get(args[0]),
                            StandardCharsets.UTF_8
                    )
                    .filter(line -> !line.trim().isEmpty())
                    .map(OneSleepSession::fromString)
                    .collect(Collectors.toCollection(ArrayList::new));

            SleepTrackerApp app = new SleepTrackerApp();
            app.functions.stream()
                    .map(f -> f.apply(sleepSessions))
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Текущий рабочий каталог: " + System.getProperty("user.dir"));
            System.err.println("Файл существует: " + Files.exists(Paths.get(args[0])));
            e.printStackTrace();
        }
    }
}