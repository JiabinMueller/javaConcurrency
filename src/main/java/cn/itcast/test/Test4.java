package cn.itcast.test;

import cn.itcast.n2.util.Constants;
import cn.itcast.n2.util.FileReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test4")
public class Test4 {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running");
                FileReader.read(Constants.MP4_FULL_PATH);
            }
        };

        //t1.run(); // 直接调用 run 方法，并没有启动t1线程，而是在 main 线程中执行了run 方法的内容
        t1.start(); // 启动了 t1 线程，然后线程调用其中 run 方法，执行其内容
        log.debug("Do other things...");
    }
}
