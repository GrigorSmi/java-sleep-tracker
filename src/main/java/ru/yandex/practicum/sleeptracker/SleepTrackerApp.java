package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    public static void main(String[] args) {
        try {
            ArrayList<OneSleepSession> sleepSessions = Files.lines(
                            Paths.get("sleep_log.txt"),
                            StandardCharsets.UTF_8
                    )
                    .filter(line -> !line.trim().isEmpty())
                    .map(OneSleepSession::fromString)     // Преобразуем каждую строку в OneSleepSession
                    .collect(Collectors.toCollection(ArrayList::new));

            // Выводим результаты(удалить после полного написания)
            sleepSessions.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Текущий рабочий каталог: " + System.getProperty("user.dir"));
            System.err.println("Файл существует: " + Files.exists(Paths.get("sleep_log.txt")));
            e.printStackTrace();        }

    }
}