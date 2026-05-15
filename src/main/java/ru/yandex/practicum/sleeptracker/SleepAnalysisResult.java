package ru.yandex.practicum.sleeptracker;

public class SleepAnalysisResult {

    public static final String MIN_DURATION = "Минимальная продолжительность сессии сна (мин)";
    public static final String MAX_DURATION = "Максимальная продолжительность сессии сна (мин)";
    public static final String AVG_DURATION = "Средняя продолжительность сессии сна (мин)";
    public static final String ALL_SESSIONS = "Общее количество сессий сна";
    public static final String BAD_QUALITY = "Количество сессий с плохим качеством сна";
    public static final String SLEEPLESS_NIGHTS = "Количество бессонных ночей";
    public static final String CHRONOTYPE = "Хронотип";
    public static final String NO_DATA = "недостаточно данных для определения хронотипа";

    private final String description;
    private final Object result;

    public SleepAnalysisResult(String description, Object result) {
        this.description = description;
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return description + ": " + result;
    }
}