package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * ── 并发控制 —— Mutex / Semaphore ──
 *
 * 协程是并发执行的，当多个协程访问共享资源时，需要同步机制。
 * Kotlin 协程提供协程安全的 Mutex 和 Semaphore：
 *
 * - Mutex:     互斥锁，同一时刻只允许一个协程访问资源
 * - Semaphore: 信号量，允许最多 N 个协程同时访问
 *
 * 对比线程锁的好处：协程挂起时不阻塞线程（suspend 而非 block）
 */
object ConcurrencyDemos {

    /**
     * Mutex —— 互斥访问共享资源
     *
     * 适用场景：多个协程同时写入同一个数据源时需要互斥。
     *   例如：本地缓存更新——多个协程同时写入 Map 或 SharedPreferences；
     *   文件追加写入——日志写入、下载文件合并；
     *   计数器累计——埋点事件计数。
     *   Mutex 比 synchronized 好在协程挂起时不阻塞线程。
     *
     * 原理：Mutex.withLock { } 确保花括号内的代码
     * 同一时刻只有一个协程在执行。
     * 其他协程在锁外排队等待。
     *
     * 对比 synchronized：协程挂起时不释放线程，
     * Mutex 挂起等待锁时不阻塞线程。
     *
     * ⚠️ 数据竞争需要多线程才会真正呈现！
     *   如果所有协程都在 Dispatchers.Main（单线程）上运行，
     *   yield() 反而让所有协程读到同一个值 → 结果永远固定（如 100）。
     *   所以下面用 Dispatchers.Default（多线程）展示真实的数据竞争。
     *
     * 执行顺序：
     *   ① 没有 Mutex：10 个协程在 Default 上并发修改 counter，数据竞争导致结果 < 1000
     *   ② 有 Mutex：10 个协程串行化修改 counter，结果一定是 1000
     */
    suspend fun demoMutex(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Mutex：协程安全的互斥锁 ═══")
        log("② 多个协程同时修改同一个变量，没有 Mutex 会导致数据竞争")

        val mutex = Mutex()

        // ── 没有 Mutex——数据竞争 ──
        log("③ --- 没有 Mutex（数据竞争，Dispatchers.Default 多线程） ---")
        var counter1 = 0
        val jobs1 = List(10) {
            launch(Dispatchers.Default) {
                repeat(100) {
                    // 多线程并发：多个协程可能同时读到同一个 counter1，
                    // 都在各自线程里 +1 再写回，导致计数丢失。
                    // 每次运行结果都不确定（如 347、512、689...）
                    val current = counter1
                    counter1 = current + 1
                }
            }
        }
        jobs1.forEach { it.join() }
        log("④ 期望值：1000，实际值：$counter1（每次运行不一样！数据竞争导致计数丢失）")

        // ── 有 Mutex——线程安全 ──
        log("⑤ --- 有 Mutex（安全访问，Dispatchers.Default 多线程） ---")
        var counter2 = 0
        val jobs2 = List(10) {
            launch(Dispatchers.Default) {
                repeat(100) {
                    mutex.withLock {
                        // Mutex 保证同一时间只有一个协程能进入这里
                        // 即使多线程并发，读写操作被串行化，结果永远正确
                        val current = counter2
                        counter2 = current + 1
                    }
                }
            }
        }
        jobs2.forEach { it.join() }
        log("⑥ 期望值：1000，实际值：$counter2（一定是 1000，Mutex 保证了数据一致）")
    }

    /**
     * Semaphore —— 限制并发数
     *
     * 原理：Semaphore(N) 允许最多 N 个协程同时访问资源。
     * withPermit 获取许可，超过 N 个时挂起等待。
     *
     * 适用：限制并发网络请求数量、限制数据库连接数等。
     *
     * 执行顺序：
     *   ① 10 个协程同时启动
     *   ② Semaphore(3) 限制最多 3 个协程同时执行
     *   ③ 每个协程 delay(200)，观察并发数 ≤ 3
     *   ④ 所有协程完成后，打印最大并发数
     */
    suspend fun demoSemaphore(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Semaphore：限制并发数 ═══")
        log("② Semaphore(3) 允许最多 3 个协程同时执行")

        val semaphore = Semaphore(3)
        var concurrentCount = 0
        var maxConcurrent = 0

        val jobs = List(10) { id ->
            launch {
                semaphore.withPermit {
                    concurrentCount++
                    if (concurrentCount > maxConcurrent) {
                        maxConcurrent = concurrentCount
                    }
                    log("③ⓐ 协程${id} 开始执行（当前并发：$concurrentCount）")
                    delay(200)
                    concurrentCount--
                    log("③ⓑ 协程${id} 执行完毕（当前并发：$concurrentCount）")
                }
            }
        }
        jobs.forEach { it.join() }

        log("④ 最大并发数：$maxConcurrent（由 Semaphore 控制，≤3）")
    }

    /**
     * yield —— 协作式调度
     *
     * 原理：yield() 是协程的"谦让"操作，让出执行机会，
     * 让调度器可以运行其他等待中的协程。
     *
     * 适用：在 CPU 密集操作中定期调用，避免独占线程。
     *
     * 执行顺序：
     *   ① 任务A 和 任务B 同时启动
     *   ② 任务A 每次迭代后 yield()，让调度器可以运行任务B
     *   ③ 任务A 和 任务B 交替执行
     */
    suspend fun demoYield(log: (String) -> Unit) = coroutineScope {
        log("① ═══ yield：协程谦让 ═══")
        log("② yield() 让出执行机会，调度器可以运行其他协程")

        launch {
            log("③ⓐ 任务A 开始")
            for (i in 1..5) {
                delay(50)
                log("③ⓑ 任务A 执行 $i")
                yield()
            }
            log("③ⓒ 任务A 完成")
        }

        launch {
            log("④ⓐ 任务B 开始")
            for (i in 1..3) {
                delay(50)
                log("④ⓑ 任务B 执行 $i")
            }
            log("④ⓒ 任务B 完成")
        }.join()
    }

    /**
     * 协程安全的延迟初始化 —— lazy 的协程版本
     *
     * 适用场景：协程安全的懒加载单例，多个协程竞争初始化时只执行一次。
     *   例如：耗时的数据库连接初始化、SDK 初始化、全局配置加载。
     *   多个协程同时触发初始化时，Mutex 保证只有一个初始化，
     *   其他协程等待锁释放后直接复用结果，避免重复初始化。
     *
     * 执行顺序：
     *   ① 5 个协程同时尝试获取资源
     *   ② Mutex 保证只有一个协程创建资源
     *   ③ 其他协程等待锁释放后直接复用已创建的资源
     *   ④ 最终资源只被创建一次
     */
    suspend fun demoCooperativeLazy(log: (String) -> Unit) = coroutineScope {
        log("① ═══ 协程安全的延迟初始化 ═══")

        val mutex = Mutex()
        var heavyResource: String? = null

        val jobs = List(5) { id ->
            launch {
                val resource = mutex.withLock {
                    if (heavyResource == null) {
                        log("②ⓐ 协程${id} 正在创建资源...")
                        delay(100)
                        "重型资源".also { heavyResource = it }
                    } else {
                        log("②ⓑ 协程${id} 资源已存在，直接复用")
                        heavyResource
                    }
                }
                log("③ 协程${id} 拿到资源：$resource")
            }
        }
        jobs.forEach { it.join() }
        log("④ 最终资源值：$heavyResource")
    }
}
