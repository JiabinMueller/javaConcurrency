# Java 并发编程笔记

## 2.1 进程与线程
### 进程 Process
- 进程，是一个程序的实例。
- 程序，由指令和数据组成（比如.exe文件就是一个程序）
- 当一个程序被运行，就是开启了一个进程。因为进程就是用来加载指令，管理内存，管理IO, 实现运行程序的过程。

### 线程 Thread
- 一个进程可以分为一到多个线程
- 一个线程，就是一个指令流，将指令流中的一条条指令以一定的顺序交给CPU执行
- Java 中，线程作为最小调度单位，进程作为资源分配的最小单位

## 2.2 并行与并发
- 并发（concurrent）是同一时间应对（dealing with）多件事情的能力。
  1. 单核 cpu 在任务调度器的协调下，使多个线程轮流使用 cpu 的做法
- 并行（parallel）是同一时间动手做（doing）多件事情的能力。
  1. 多核 cpu 同时执行不同的线程
- 但是计算机 cpu 的 core 的个数在大部分情况下是少于线程的个数，所以最常见的情况是，并行和并发同时发生。

## 2.3 应用
- 同步：需要等待结果返回
- 异步：不需要等待结果返回
- 多线程可以让方法执行变为异步的（即不要巴巴干等着），比如说读取磁盘文件是，假设读取操花费了5秒钟，如果没有线程调度机制，这5秒，调用者什么
做不了，其代码都得暂停...

## 结论
- 单核 CPU 下，多线程不能实际提高程序运行效率
- 多核 CPU 可以并行跑多个线程，但能否提高程序的运行效率还是要分情况的
- IO 操作不占用 CPU，只是我们一般拷贝文件使用的是 阻塞IO，这时相当于线程虽不用 CPU, 但需要一致等待 IO 结束，没能充分利用线程。所以才有
后面的 非阻塞IO 和 异步IO 优化。

## 3.1 Java 线程
-  当有多个线程时，会 交替执行，且 谁先谁后，不由我们控制。
- Windows 可以在任务管理器中查看进程和线程数，或者在 cmd 中，用 tasklist 查看进程， taskkill 杀死进程。

## 3.4 线程运行的原理
### 3.4.1 栈与栈帧
- Java Virtual Machine Stacks (Java 虚拟机栈)
- JVM 中由 堆，栈，方法区组成，栈内存就是给线程使用的。每个线程启动后，虚拟机就会为其分配一块栈内存。
  - 每个栈由多个栈帧（Frame）组成，每次放一个方法被调用，就会创建一个栈帧
  - 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

### 3.4.2 线程上下文切换 （Thread Context Switch）
因为一下原因会导致 cpu 不再执行当前的线程，转而执行另外一个线程的代码：
- 线程的 cpu 时间片用完
- 垃圾回收
- 有更高优先级的线程需要执行
- 线程自己调用了 sleep, yield, wait, join, park, synchronized, lock 等方法
当 Context Switch 发生时，需要由操作系统保存当前线程的状态，并恢复另外一个线程的状态。
Java 中对应的概念就是 程序计数器 （Program Counter Register），它的作用就是记住下一条 jvm 指令的执行地址，是线程私有的。
- 状态包括 程序计数器，虚拟机栈中每个栈帧的信息，如 局部变量，操作数栈，返回地址 等。
- Context Switch 频繁发生会影响性能 （那么一个 cpu 可运行多少线程，才能保证性能呢？后面的课程会解释） // Question?

### 3.4.3 线程方法

#### API

Thread 类 API：

| 方法                                          | 说明                                                                                                                   | 注意                                                                                                                                                              |
|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| public void start()                         | 启动一个新线程，在新的线程运行 run 方法中的代码                                                                               | start 方法只是让线程进入就绪，里面的代码不一定立刻运行（cpu 的时间片还没有分给它）。<br/>每个线程对象的start方法只能调用一次，如果调用了多次会出现 IllegalThreadStateException     | 
| public void run()                           | 新线程启动后调用该方法                                                                                                     | 如果在构造Thread 对象时传递了 Runnable 参数，则线程启动后会调用Runnable 中的run 方法。否则默认不执行任何操作。但可以创建Thread 的子类对象，来覆盖默认行为。                       |
| public final void join()                    | 等待线程运行结束                                                                                                          | 比如 mian 线程需要等待另外一个线程的运行结果                                                                                                                           |
| public final void join(long millis)         | 等待线程运行结束，最多等待 n 毫秒                                                                                           |                                                                                                                                                                  |
| public void setName(String name)            | 给当前线程取名字                                                                                                          |                                                                                                                                                                  |
| public void getName()                       | 获取当前线程的名字<br />线程存在默认名称：子线程是 Thread-索引，主线程是 main                                                   |                                                                                                                                                                  |
| public final int getPriority()              | 返回此线程的优先级                                                                                                        |                                                                                                                                                                  |
| public final void setPriority(int priority) | 更改此线程的优先级                                                                                                        | java 中规定线程优先级时1-10的整数，较大的优先级能提高该线程被 CPU 调度的概率。但是java中自定义的优先级并不一定很有效，因为最终还是由操作系统来决定。                                |
| public final State getState()               | 获取线程状态                                                                                                             | java 中线程状态使用6个 enum 表示，分别为：NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED                                                                  |
| public boolean isInterrupted()              | 判断当前线程是否被打断                                                                                                    | 打断以后，会有‘打断标记’。在此基础上，决定是否真的要结束我（此线程）自己                                                                                                    |
| public final native boolean isAlive()       | 线程是否存活（还没有运行完毕）                                                                                              |                                                                                                                                                                  |
| public void interrupt()                     | 中断这个线程，异常处理机制                                                                                                 | 如果被打断的线程正在 sleep，wait, join 会导致被打断的线程抛出 InterrupedException, 并清除打断标记; park 的线程被打断，也会被设置打断标记                                      |
| public static boolean interrupted()         | 判断当前线程是否被打断                                                                                                     | 清除打断标记                                                                                                                                                       |
| public static Thread currentThread()        | 获取当前正在执行的线程                                                                                                     |                                                                                                                                                                  |
| public static void sleep(long time)         | 让当前线程休眠多少毫秒再继续执行，休眠时让出 cpu 的时间片给其他线程<br />**Thread.sleep(0)** : 让操作系统立刻重新进行一次 CPU 竞争   |                                                                                                                                                                  |
| public static native void yield()           | 提示线程调度器让出当前线程对 CPU 的使用                                                                                      | 主要时为了测试和调试                                                                                                                                                |
| public final void setDaemon(boolean on)     | 将此线程标记为守护线程或用户线程                                                                                             |                                                                                                                                                                  |

***

#### sleep 与 yield

sleep
1. 调用 sleep 会让当前线程的 state 由 *Running* 进入 *Timed Waiting* （有时间限的等待）的状态 （阻塞）
2. 其他线程可以使用 interrupt 方法打断正在水面的线程，这是 sleep 方法会抛出 InterruptedException
3. 睡眠结束后的线程未必会立刻得到执行
4. 建议用 TimeUnit 的 sleep 代替 Thread 的 sleep 来获得更好的可读性

yield
1. 调用 yield 会让当前线程从 *Running* 进入 *Runnable* 就绪状态，然后调度执行其他线程
2. 具体的实现依赖于操作系统的任务调度器 （会发生想让但没让出去的情况，比如 没有其他线程存在，那么让了之后，cpu还是会把时间片分给你）

区别：
1. cpu 可以把时间片分给 *Runnable* 状态的线程，但不会分给 *Timed Waiting* 状态的线程
2. sleep 有等待时间（参数）， yield 没有
   
线程优先级
1. 线程优先级是1-10的数字，数字越大，优先级越高，默认优先级是5
2. 线程优先级会提示（hint）调度优先调度该线程，但它仅仅只是一个提示，调度器可以忽略它
3. 如果cpu比较忙，那么优先级高的线程会获得更多的时间片，但cpu闲时，优先级几乎没有作用

*不管是 yield 还是 优先级，都不能真正地控制线程的调度，操作系统的任务调度器是最终决定调用哪个线程，以及分配为多少时间片。*

#### Join
- main 线程要等待 t1 的运行结果，即等 t1 运行结束后才继续执行，那么 t1.join() 即 t1 调用 join 方法。
- 在有时间限制的等待时，join 会有一个等待时间，但是如果线程在等待时间以内结束，那么join也会结束，并不会一直等到限制时间过去。

#### interrupt
1. 打断阻塞状态的线程，即 sleep, wait, join 的线程
2. 打断正常运行的线程
3. interrupt 可以比较优雅地停止线程，而非强制停止。线程在被interrupted 之后，可以自己决定停或者不停。


-----------已学 203 分钟，还有 29 小时 14 分钟










































