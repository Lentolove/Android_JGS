package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

/**
 * ── 调度器（Dispatchers）与 上下文切换（withContext） ──
 *
 * 核心概念：
 * - Dispatchers.Main:      Android 主线程，用于 UI 操作
 * - Dispatchers.IO:        用于 IO 密集型任务（网络、数据库、文件读写）
 * - Dispatchers.Default:   用于 CPU 密集型任务（排序、解析）
 * - Dispatchers.Unconfined: 初始在当前线程，挂起恢复后可能在任意线程
 * - withContext:           在不改变协程本身的前提下切换到指定调度器
 * - newSingleThreadContext: 创建一个专用线程
 */
object DispatcherDemos {

    /**
     * 不同调度器的对比 —— 通过日志查看线程名来理解调度行为
     *
     * 适用场景：根据任务类型选择合适的调度器。
     *   Main → UI 操作、LiveData/StateFlow 更新（主线程）。
     *   IO → 网络请求、数据库读写、文件 IO（线程池可增长）。
     *   Default → JSON 解析、Bitmap 压缩、列表排序等 CPU 密集型（线程池大小=CPU核数）。
     *   Unconfined → 几乎不用，仅在协程启动时不需要切换线程的场景。
     *
     * 执行顺序：四个 launch 并发执行（②ⓐ~②ⓓ 顺序无法保证），
     * 哪个先执行取决于调度器分配。日志中看线程名来区分：
     *   Main  → main
     *   IO    → DefaultDispatcher-worker-*
     *   Default → DefaultDispatcher-worker-*
     *   Unconfined → 初始同 caller，恢复后可能变
     */
    suspend fun demoDispatchers(log: (String) -> Unit) = coroutineScope {
        log("═══ 调度器对比 ═══")
        log("① 四个 launch 同时启动，各自在不同的调度器上运行")
        log("（②ⓐ~②ⓓ 并发执行，输出顺序不可预测）")

        // Main：Android 主线程
        launch(Dispatchers.Main) {
            log("②ⓐ~ Dispatchers.Main  -> 线程：${Thread.currentThread().name}（主线程，UI 操作）")
        }

        // Default：CPU 密集型任务
        launch(Dispatchers.Default) {
            log("②ⓑ~ Dispatchers.Default -> 线程：${Thread.currentThread().name}（CPU 密集型：排序、解析）")
        }

        // IO：IO 密集型任务
        launch(Dispatchers.IO) {
            log("②ⓒ~ Dispatchers.IO     -> 线程：${Thread.currentThread().name}（IO 密集型：网络、数据库）")
        }

        // Unconfined：不限定调度器
        launch(Dispatchers.Unconfined) {
            log("②ⓓ~ Dispatchers.Unconfined -> 线程：${Thread.currentThread().name}（初始线程同调用方）")
            delay(10)
            log("    Unconfined 恢复后 -> 线程：${Thread.currentThread().name}（⚠️ 线程可能已切换）")
        }

        delay(200) // 等待所有启动完成
        log("③ 全部调度器演示完成")
    }

    /**
     * withContext —— 在不创建新协程的情况下切换线程
     * 这是 Android 开发中最高频的协程 API 之一：
     * 在 IO 线程执行耗时操作，然后切回 Main 更新 UI。
     *
     * 适用场景：单次线程切换，Android 中最高频的协程操作。
     *   例如：lifecycleScope.launch 在 Main 线程启动，
     *   内部 withContext(IO) 执行网络请求，
     *   完成后自动回到 Main 线程更新 UI。
     *   完全替代旧式的 callback + Handler 模式。
     *
     * 执行顺序：
     *   ① 当前线程（Main）
     *   ② withContext(IO) 切换到 IO 线程执行
     *   ③ 自动回到原线程（Main）
     */
    suspend fun demoWithContext(log: (String) -> Unit) = coroutineScope {
        log("═══ withContext：线程切换 ═══")
        log("① 当前线程：${Thread.currentThread().name}")

        // withContext(Dispatchers.IO) 切换到 IO 线程执行
        val result = withContext(Dispatchers.IO) {
            log("② withContext(IO) 内 -> 线程：${Thread.currentThread().name}")
            delay(200) // 模拟网络请求
            "网络数据"
        }

        // 自动切回原来的调度器（Main）
        log("③ withContext 恢复后 -> 线程：${Thread.currentThread().name}，结果：$result")
    }

    /**
     * 多层 withContext —— 在 IO 中执行数据库操作后再切回 UI
     * 典型的生产模式：UI → IO(网络) → Default(解析) → IO(存储) → UI
     *
     * 适用场景：完整的端到端业务流程。
     *   例如：点击刷新按钮 →
     *   IO(网络请求 JSON) → Default(解析 JSON 为 Model) →
     *   IO(缓存 Model 到 Room) → Main(更新 RecyclerView)。
     *   一个协程内完成全流程，清晰且安全。
     *
     * 执行顺序：① IO(网络) → ② Default(解析) → ③ IO(存储) → ④ UI(更新)
     */
    suspend fun demoWithContextChain(log: (String) -> Unit) = coroutineScope {
        log("═══ 链式 withContext ═══")
        log("模拟：UI → IO(网络) → Default(解析) → IO(存储) → UI")

        val rawData = withContext(Dispatchers.IO) {
            log("① 网络请求 -> 线程：${Thread.currentThread().name}")
            delay(300)
            "{name: '协程', type: 'demo'}"
        }

        val parsed = withContext(Dispatchers.Default) {
            log("② 数据解析 -> 线程：${Thread.currentThread().name}")
            delay(100)
            "解析完成: $rawData"
        }

        withContext(Dispatchers.IO) {
            log("③ 存入数据库 -> 线程：${Thread.currentThread().name}")
            delay(200)
        }

        log("④ 回到主线程 -> 线程：${Thread.currentThread().name}，完整流程完成：$parsed")
    }

    /**
     * newSingleThreadContext —— 创建专用线程
     * 原理：为特定任务创建自己的线程池，任务在该线程上串行执行。
     *
     * 适用场景：第三方 SDK 要求串行访问的线程模型。
     *   例如蓝牙 SDK 要求所有指令在同一个线程发送、
     *   串口通信需要按序写入、特定硬件驱动的操作队列。
     *   ⚠️ 用完需要 close()，否则线程泄漏。
     *   大多数场景下用 Dispatchers.IO 就足够了，不必自定义。
     *
     * 执行顺序：三个协程在同一个线程 MyWorker 上串行执行
     *   ① 任务1 先执行 → delay → 任务1 完成
     *   ② 任务2 然后执行 → delay → 任务2 完成
     *   ③ 任务3 最后执行（delay 0）→ 立刻完成
     *
     * ⚠️ 用完需要 close()，否则会泄漏线程。
     */
    suspend fun demoSingleThread(log: (String) -> Unit) = coroutineScope {
        log("═══ newSingleThreadContext ═══")
        log("① 所有协程在同一个线程上串行执行")

        val threadContext = newSingleThreadContext("MyWorker")

        launch(threadContext) {
            log("②ⓐ 任务1 -> 线程：${Thread.currentThread().name}（开始）")
            delay(200)
            log("②ⓕ 任务1 完成")
        }
        launch(threadContext) {
            log("②ⓑ 任务2 -> 线程：${Thread.currentThread().name}（开始）")
            delay(100)
            log("②ⓔ 任务2 完成（比任务1快，但排在后）")
        }
        launch(threadContext) {
            log("②ⓒ 任务3 -> 线程：${Thread.currentThread().name}（开始，无delay）")
            log("②ⓓ 任务3 完成（最先执行完）")
        }

        delay(500)
        threadContext.close()
        log("③ 专用线程已关闭")
    }
}
