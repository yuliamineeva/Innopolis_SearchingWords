package ru.innopolis.stc9;

import java.io.File;

/**
 * Вспомогательный класс для создания массивов ресурсов и слов,
 * а также имени файла, куда сохраняется результат поиска
 */
public class SourcesArray {
    String[] words;
    String[] sources;
    String resultFile;

    public SourcesArray() {
        addWords();
        addSources();
        addResultFile();
    }

    public String getResultFile() {
        return resultFile;
    }

    public void addResultFile() {
        this.resultFile = "C:\\ForJava\\TestForLesson7\\Result.txt";
    }

    public String[] getWords() {
        return words;
    }

    public void addWords() {
        this.words = new String[]{"starter", "ffdf", "wfrrf", "cdcd", "dc"};
    }

    public String[] getSources() {
        return sources;
    }

    public void addSources() {
        this.sources = new String[188];
        String txtPath = "C:\\ForJava\\TestForLesson7\\medium\\";
        File directory = new File(txtPath);
        for (int i = 0; i < directory.listFiles().length; i++) {
            String filename = txtPath + directory.listFiles()[i].getName();
            sources[i] = filename;
        }
    }

}