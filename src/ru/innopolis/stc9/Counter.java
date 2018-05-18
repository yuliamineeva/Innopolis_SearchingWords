package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Отслеживание всех запущенных потоков
 */
public class Counter extends Thread {
    final static Logger logger = Logger.getLogger(Counter.class);
    final static Logger logSRR = Logger.getLogger(SingleResourceReader.class);
    ArrayList<SingleResourceReader> treads = new ArrayList<>();
    Timer timer;
    private int count;


    public Counter(ArrayList<SingleResourceReader> treads) {
        this.treads = treads;
        count = 0;
        timer = new Timer(System.currentTimeMillis());
        logger.info("Создан поток-счетчик Counter");
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void incCount() {
        count++;
        logSRR.info(count + " потоков выполняется");
    }

    public synchronized void decCount() {
        if (count > 0) count--;
        logSRR.info(count + " потоков выполняется");
    }

    @Override
    public void run() {
        for (int i = 0; i < treads.size(); i++) {
            try {
                treads.get(i).join();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        timer.calculateTime();
        logger.info("Counter завершен");
    }
}
