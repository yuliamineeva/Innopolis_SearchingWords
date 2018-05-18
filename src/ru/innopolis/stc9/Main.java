package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Главный класс проекта
 * <p>
 * Функциональные требования:
 * метод void getOccurences(String[] sources, String[] words, String res) throws...
 * получает на вход массив адресов ресурсов (файлы, FTP, web-ссылки) и слов.
 * Необходимо в многопоточном режиме найти вхождения всех слов второго массива в ресурсы.
 * Если слово входит в предложение, это предложение добавляется в файл по адресу res.
 * При начале исполнения метода файл очищается (чтобы исключить наличие старой информации).
 * Все ресурсы текстовые. Необходимо определить оптимальную производительность.
 * Возможны ситуации как с большим числом ресурсов (>2000 текстовых ресурсов каждый <2кб),
 * так и с очень большими ресурсами (ресурс>1ГБ).
 */
public class Main {
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        SourcesArray sourcesArray = new SourcesArray();
        String[] sources = sourcesArray.getSources();
        String[] words = sourcesArray.getWords();
        String resultFileName = sourcesArray.getResultFile();
        WriterReader writerReader = new WriterReader();
        Searching searching = new Searching(writerReader);
        try {
            searching.getOccurences(sources, words, resultFileName);
        } catch (InterruptedException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }

    }
}
