package ru.innopolis.stc9;

import org.apache.log4j.Logger;

/**
 * Класс, запускаемый в отдельный поток.
 * Читает и обрабатывает данные из одного ресурса
 */
public class SingleResourceReader extends Thread {
    final static Logger logger = Logger.getLogger(SingleResourceReader.class);
    String name;
    String fileSource;
    boolean isBigFile;
    String fileResult;
    String[] words;
    WriterReader writerReader;
    Counter counter;

    public SingleResourceReader(String name, String fileSource, boolean isBigFile, String fileResult, String[] words, WriterReader writerReader, Counter counter) {
        this.name = name;
        this.fileSource = fileSource;
        this.isBigFile = isBigFile;
        this.fileResult = fileResult;
        this.words = words;
        this.writerReader = writerReader;
        this.counter = counter;
    }

    /**
     * Метод, выполняющий: чтение текста из одного ресурса, разбивка его на предложения,
     * поиск слов, запись найденных предложений в файл.
     */
    @Override
    public void run() {
        while (counter.getCount() > 7) {
            synchronized (counter) {
                try {
                    counter.wait();
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }
        counter.incCount();
        logger.info(name + " создан новый поток");
        String[] result;
        if (!isBigFile) {
            String fromFile = writerReader.readFrom(fileSource);
            String[] temp = writerReader.writeToSentences(fromFile);
            result = writerReader.wordSearch(temp, words);
        } else {
            logger.info("Внимание! Идет обработка файла большого размера. Это может занять несколько (от 3 до 7) минут");
            result = writerReader.readFromBigFile(fileSource, words);
        }
        writerReader.writeToResult(result, fileResult);
        logger.info(name + " поток завершен");
        counter.decCount();
        synchronized (counter) {
            counter.notifyAll();
        }

    }
}