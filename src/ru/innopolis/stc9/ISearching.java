package ru.innopolis.stc9;

import java.io.IOException;

/**
 * Интерфейс, содержащий метод поиска и сохранения результата
 */
public interface ISearching {
    void getOccurences(String[] sources, String[] words, String res) throws IOException, InterruptedException;
}
