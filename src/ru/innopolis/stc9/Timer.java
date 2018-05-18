package ru.innopolis.stc9;

import org.apache.log4j.Logger;

/**
 * Класс, следящий за временем выполнения программы
 */
public class Timer {
    final static Logger logger = Logger.getLogger(Timer.class);
    long startTime;

    public Timer(long startTime) {
        this.startTime = startTime;
    }

    public void calculateTime() {
        long finishTime = System.currentTimeMillis();
        logger.info("Время выполнения поиска: " + (finishTime - startTime));
    }
}
