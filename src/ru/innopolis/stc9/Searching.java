package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс имплементирует интерфейс, содержащий метод поиска и сохранения результата
 * !!!
 * Вывод: с использованием пула потоков процесс поиска "зависает".
 * Без пула потоков разница между обработкой 100 и 1000 файлов составляет 2 - 3 секунды.
 */
public class Searching implements ISearching {
    final static Logger logger = Logger.getLogger(Searching.class);
    WriterReader writerReader;
    ArrayList<SingleResourceReader> treads = new ArrayList<>();

    public Searching(WriterReader writerReader) {
        this.writerReader = writerReader;
    }

    /**
     * Основной метод проекта, осуществляющий поиск вхождений всех слов из массива слов в ресурсы
     *
     * @param sources массив адресов ресурсов
     * @param words   массив искомых слов
     * @param res     файл для записи результат
     * @throws IOException
     */
    @Override
    public void getOccurences(String[] sources, String[] words, String res) throws IOException, InterruptedException {
        if (!writerReader.isNull(new Object[]{sources, words, res}, new String[]{"массив ресурсов", "массив слов", "файл"})) {
            checkAndCleaning(res);
            Timer timer = new Timer(System.currentTimeMillis());
            Counter counter = new Counter(treads);
            for (int i = 0; i < sources.length; i++) {
                boolean isBig = isBigSource(sources[i]);
                SingleResourceReader srr = new SingleResourceReader("Thread_" + i, sources[i], isBig, res, words, writerReader, counter);
                treads.add(srr);
                srr.start();
            }
            counter.start();
        }
    }

    /**
     * Метод для проверки существования и очистки файла
     *
     * @param filename - имя файла
     */
    private void checkAndCleaning(String filename) {
        File checkFile = new File(filename);
        if (checkFile.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
                bw.write("");
                bw.flush();
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            try {
                checkFile.createNewFile();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    private boolean isBigSource(String filenameSource) {
        File f = new File(filenameSource);
        long len = f.length();
        if (len > 10_000_000) {
            return true;
        } else {
            return false;
        }
    }
}