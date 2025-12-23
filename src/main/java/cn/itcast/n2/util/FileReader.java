package cn.itcast.n2.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.FileReader")
public class FileReader {

    public static void read(String filePath) {
        log.debug("read start ... {}", filePath);

        try {
            Thread.sleep(3000); // 模拟 3 秒读文件
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("read end ...");
    }
}

