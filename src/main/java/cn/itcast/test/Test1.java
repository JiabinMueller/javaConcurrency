package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test1")
public class Test1 {

    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * 这样的写法更为推荐，原因：
     * 1. 用 Runnable 更容易与线程池等高级 API 配合，不推荐直接修改 Thread 对象，而是对 Runnable 这个任务对象进行修改
     * 2. 用 Runnable 让任务类脱离了 Thread 的继承体系，代码更加灵活。（Java 中 组合优于继承）
     */
    public static void test2() {
        Runnable runnable = () -> log.debug("running"); // Runnable 只有一个 abstract 方法，所以有@FunktionalInterface 标签，所以可以使用 lambda来写

        Thread t = new Thread(runnable, "t2"); // 任务和线程分开定义，更为推荐
        t.start();
    }

    public static void test1() {
        Thread t = new Thread() {
            @Override
            public void run() {
                log.debug("running");
            }
        };
        t.setName("t1");

        log.debug("running");
    }
}
