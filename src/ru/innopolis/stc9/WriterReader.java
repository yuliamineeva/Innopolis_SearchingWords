package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Чтение текста из файла, преобразование текста в массив предложений.
 * Поиск искомых слов в предложениях. Запись массива предложений в файл
 */
public class WriterReader {
    final static Logger logger = Logger.getLogger(WriterReader.class);
    final String REGEX_WORDS = "[,;:.!?'_\\-\"\\s]+";
    final String REGEX_SENTENCES = "(\r\n)|(\n)|([.!?]+\\s*)";
    final String NPE = "NPE! Передана пустая ссылка на ";

    public WriterReader() {
    }

    /**
     * Проверка на null одного аргумента
     *
     * @param obj         проверяемый объект
     * @param description описание
     * @return boolean
     */
    public boolean isNull(Object obj, String description) {
        if (obj == null) {
            logger.error(NPE + description);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Проверка на null нескольких аргументов
     *
     * @param objects      массив проверяемых объектов
     * @param descriptions массив описаний к ним (для вывода сообщений)
     * @return boolean
     */
    public boolean isNull(Object[] objects, String[] descriptions) {
        boolean isNull = false;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                logger.error(NPE + descriptions[i]);
                isNull = true;
            }
        }
        return isNull;
    }

    /**
     * Чтение предложений из большого файла, поиск предложений по словам, вывод готовых предложений
     *
     * @param fileName    откуда читаем
     * @param searchWords массив искомых слов
     * @return массив готовых предложений
     */
    public String[] readFromBigFile(String fileName, String[] searchWords) {
        String[] readFromFileSentences;
        String[] tempReady;
        String[] readySentences = new String[0];
        if (!isNull(fileName, "файл")) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                ArrayList<String> tempArray = new ArrayList<>();
                ArrayList<String> tempReadySentences = new ArrayList<>();
                StringBuffer sb = new StringBuffer();
                int c;
                while ((c = br.read()) != -1) { // чтение посимвольно
                    if (tempArray.size() < 10_000) { //для того чтобы ArrayList не переполнялся
                        char ch = (char) c;
                        if (ch != '.' && ch != '!' && ch != '?') {
                            sb.append(ch);
                        } else {
                            tempArray.add(sb.toString());
                            sb = new StringBuffer();
                        }
                    } else {
                        readFromFileSentences = tempArray.toArray(new String[tempArray.size()]);
                        tempReady = wordSearch(readFromFileSentences, searchWords);
                        for (String s : tempReady) {
                            tempReadySentences.add(s);
                        }
                        tempArray = new ArrayList<>();
                    }
                }
                readFromFileSentences = tempArray.toArray(new String[tempArray.size()]);
                tempReady = wordSearch(readFromFileSentences, searchWords);
                for (String s : tempReady) {
                    tempReadySentences.add(s);
                }
                readySentences = tempReadySentences.toArray(new String[tempReadySentences.size()]);
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return readySentences;
    }

    /**
     * Чтение текста из файла в StringBuffer
     * Выражение // try (BufferedReader br = new BufferedReader(
     * new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
     * (то есть использование new InputStreamReader(new FileInputStream) вместо FileReader)
     * не помогает прочитать текст из документа .docx
     *
     * @param fileName откуда читаем
     * @return String прочитанное из файла целиком
     */

    public String readFrom(String fileName) {
        String readFromFile = "";
        if (!isNull(fileName, "файл")) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                StringBuffer sb = new StringBuffer();
                int c;
                while ((c = br.read()) != -1) { // чтение посимвольно
                    sb.append((char) c);
                }
                readFromFile = sb.toString();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return readFromFile;
    }


    /**
     * Разбивка текста на предложения по регулярным выражениям.
     * Загрузка предложений без знаков конца строки (.?!) в массив строк
     */
    public String[] writeToSentences(String input) {
        String[] sentences;
        if (isNull(input, "текст")) {
            sentences = new String[0];
        } else {
            Pattern pattern = Pattern.compile(REGEX_SENTENCES);
            sentences = pattern.split(input);
        }
        return sentences;
    }

    /**
     * Разбивка предложения на слова по регулярным выражениям.
     * Загрузка слов в массив слов
     */
    private String[] splitIntoWords(String inputSentence) {
        String[] words;
        if (isNull(inputSentence, "предложение")) {
            words = new String[0];
        } else {
            Pattern pattern = Pattern.compile(REGEX_WORDS);
            words = pattern.split(inputSentence);
        }
        return words;
    }

    /**
     * Загрузка предложений в файл результат
     */
    public synchronized void writeToResult(String[] resultSentences, String result) {
        Object[] args = {resultSentences, result};
        String[] descriptions = {"массив готовых предложений", "файл"};
        if (!isNull(args, descriptions)) {
            String[] sentences = resultSentences;
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(result, true))) {
                for (String sentence : sentences) {
                    bw.write(sentence);
                    bw.append("\r\n");
                }
                bw.flush();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    /**
     * Поиск искомых слов во всех предложениях текста
     *
     * @param allSentences все предложения из текста
     * @param searchWords  искомые слова
     * @return массив предложений, в которых есть хотя бы одно искомое слово
     */
    public String[] wordSearch(String[] allSentences, String[] searchWords) {
        String[] resultSentences;
        Object[] args = {allSentences, searchWords};
        String[] descriptions = {"массив исходных предложений", "массив искомых слов"};
        if (!isNull(args, descriptions)) {
            ArrayList<String> tempArray = new ArrayList<>();
            for (int i = 0; i < allSentences.length; i++) {
                if (searchInOneSentence(allSentences[i], searchWords)) {
                    tempArray.add(allSentences[i]);
                }
            }
            resultSentences = tempArray.toArray(new String[tempArray.size()]);
        } else {
            resultSentences = new String[0];
        }
        return resultSentences;
    }

    /**
     * Поиск искомых слов в одном предложении
     *
     * @param oneSentence предложение, в котором ищем
     * @param searchWords искомые слова
     * @return boolean
     */
    private boolean searchInOneSentence(String oneSentence, String[] searchWords) {
        String[] words = splitIntoWords(oneSentence);
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < searchWords.length; j++) {
                if ((words[i].toLowerCase()).equals(searchWords[j].toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

}
