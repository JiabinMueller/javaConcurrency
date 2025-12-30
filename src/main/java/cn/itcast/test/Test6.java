package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test6")
public class Test6 {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000); // t1 Thread sleep
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        t1.start();
        log.debug("{}", t1.getState());

        try {
            Thread.sleep(500); // main Thread sleep
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug("{}", t1.getState());
    }
}
