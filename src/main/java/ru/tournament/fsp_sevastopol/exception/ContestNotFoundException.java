package ru.tournament.fsp_sevastopol.exception;

public class ContestNotFoundException extends RuntimeException {

    public ContestNotFoundException(Long id) {
        super("Соревнование с id " + id + " не найдено");
    }
}