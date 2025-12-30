package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.Test2")
public class Test2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            log.debug("running...");
            Thread.sleep(2000);
            return 100;
        });

        Thread t1 = new Thread(futureTask, "t1");
        t1.start();

        log.debug("{}", futureTask.get()); // main 和 t1 线程是并行执行的，但是 main 线程执行到这里会等待 2s, 再打印
    }
}
