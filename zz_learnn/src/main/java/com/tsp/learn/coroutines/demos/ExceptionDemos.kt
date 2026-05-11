package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

/**
 * ── 协程异常处理 ──
 *
 * 核心规则：
 * 1. launch 中的异常：直接抛出，通过 CoroutineExceptionHandler 捕获
 * 2. async 中的异常：await() 时抛出，通过 try-catch 捕获
 * 3. 默认父协程会等子协程完成，但子协程异常会取消父协程
 * 4. SupervisorJob / supervisorScope 隔离子协程异常
 * 5. CancellationException 是"正常"退出机制，不应视为异常
 */
object ExceptionDemos {

    /**
     * try-catch 在 launch 中 —— 捕获协程内的异常
     *
     * 适用场景：协程体内执行可能失败的操作时包 try-catch。
     *   例如：用户点击收藏按钮 → launch 发起网络请求 → try-catch 处理失败情况。
     *   这是 Android 生产代码中最推荐的方式——精确控制异常范围，
     *   一个失败不会影响其他协程。
     *
     * 原理：try-catch 包住协程体，异常不会扩散到外部。
     * 这是生产中最常用的方式。
     *
     * 执行顺序：
     *   ① launch 启动，协程体开始执行
     *   ② delay 100ms 后抛 IllegalArgumentException
     *   ③ try-catch 捕获异常，外部协程不受影响
     */
    suspend fun demoTryCatch(log: (String) -> Unit) = coroutineScope {
        log("① ═══ try-catch 捕获协程异常 ═══")

        launch {
            try {
                log("② 开始执行可能失败的操作")
                delay(100)
                throw IllegalArgumentException("参数错误")
            } catch (e: Exception) {
                log("③ try-catch 捕获：${e.message}")
            }
        }

        delay(200)
        log("④ 外部协程未被影响")
    }

    /**
     * async 中的异常 —— await() 时再捕获
     *
     * 适用场景：通过 async 并发请求多个 API，各自 await 时分别处理异常。
     *   例如：同时请求用户信息和推荐列表，用户信息失败不影响推荐列表，
     *   各自 await 后分别 try-catch，互不干扰。
     *   注意：async 中的异常不会立即抛出，await() 时才抛。
     *
     * 原理：async 中的异常不会立即抛出，而是在调用 await() 时抛出。
     * 这与 Future.get() 的行为类似。
     *
     * 执行顺序：
     *   ① async 启动，内部 delay 100ms 后抛 ArithmeticException
     *   ② 外部调用 await()，await 抛出该异常
     *   ③ try-catch 包住 await() 捕获异常
     */
    suspend fun demoAsyncException(log: (String) -> Unit) = coroutineScope {
        log("① ═══ async 异常：await() 时抛出 ═══")

        val deferred = async {
            delay(100)
            throw ArithmeticException("除以0啦")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            log("② await() 时捕获 async 异常：${e.message}")
        }

        log("③ 外部协程未被影响")
    }

    /**
     * CoroutineExceptionHandler —— 全局异常处理器
     *
     * 适用场景：全局兜底异常处理，统一上报 Crash。
     *   例如：在 Application 或 BaseActivity 中设置 handler，
     *   所有未被 try-catch 捕获的异常自动进入 handler，
     *   统一上报 Bugly / Firebase Crashlytics。
     *   ⚠️ 只在 launch 中生效，async 的异常需要 await() 时处理。
     *
     * 原理：未捕获的异常会沿着协程层次向上传播，
     * 直到根协程，如果设置了 handler 则由它处理。
     * ⚠️ 只在 launch 中生效，async 的异常需要 await() 时处理。
     *
     * 执行顺序：
     *   ① 创建 CoroutineExceptionHandler
     *   ② handler 在顶层协程（如 lifecycleScope.launch(handler)）上生效
     *   ③ 子协程未捕获的异常会传播到 handler
     */
    suspend fun demoExceptionHandler(log: (String) -> Unit) = coroutineScope {
        log("① ═══ CoroutineExceptionHandler ═══")
        log("② launch 的未捕获异常会进入 handler")

        val handler = CoroutineExceptionHandler { _, throwable ->
            log("Global Handler 捕获：${throwable.message}")
        }

        // handler 需要设置在顶层协程上
        // 在 coroutineScope 内无法演示 handler（因为 scope 本身就是顶层协程）
        // 这里直接使用 launch 并传入 handler
        log("③ （handler 方式适合根协程，演示见 main 函数）")
    }

    /**
     * SupervisorJob —— 子协程失败不扩散
     *
     * 适用场景：页面多个独立模块互不影响。
     *   例如一个页面中有「用户资料」「推荐列表」「广告 banner」三个模块，
     *   每个模块各自 launch 加载，推荐列表加载失败不应该影响用户资料和广告。
     *   Activity/Fragment 中 launch 默认继承父协程的 SupervisorJob，
     *   天然隔离异常扩散。
     *
     * 默认行为：一个子协程异常 → 父协程被取消 → 所有其他子协程被取消
     * SupervisorJob：子协程异常 → 只取消那个子协程 → 兄弟不受影响
     *
     * 执行顺序：
     *   ① supervisorScope 内启动两个子协程
     *   ② 子协程1 在 100ms 后抛出异常，自身捕获
     *   ③ 子协程2 不受影响，继续完成 5 次循环
     *   ④ supervisorScope 正常完成
     */
    suspend fun demoSupervisorJob(log: (String) -> Unit) = coroutineScope {
        log("① ═══ SupervisorJob：失败隔离 ═══")
        log("② 子协程1 失败，子协程2 不受影响")

        supervisorScope {
            launch {
                try {
                    delay(100)
                    throw RuntimeException("子协程1 出错啦")
                } catch (e: Exception) {
                    log("③ⓐ 子协程1 内部捕获：${e.message}")
                }
            }
            launch {
                try {
                    repeat(5) { i ->
                        delay(50)
                        log("③ⓑ 子协程2 正常工作 $i")
                    }
                } catch (e: Exception) {
                    log("子协程2 被异常影响：${e.message}")
                }
            }
        }

        log("④ supervisorScope 完成，子协程2 不受子协程1 的影响")
    }

    /**
     * 默认行为对比 —— 没有 SupervisorJob 时
     * 子协程异常 → 兄弟协程被取消
     *
     * 适用场景：与 SupervisorJob 对比理解默认行为。
     *   默认 Job 的取消传播适用于：一个操作失败则整个任务组无意义。
     *   了解默认行为才能正确选择用 Job 还是 SupervisorJob。
     *
     * 执行顺序：
     *   ① 两个子协程同时启动
     *   ② 子协程2 先输出一次（50ms）
     *   ③ 子协程1 在 100ms 抛出异常 → 父协程被取消 → 子协程2 被级联取消
     *   ④ 子协程2 捕获 CancellationException
     */
    suspend fun demoDefaultJob(log: (String) -> Unit) = coroutineScope {
        log("① ═══ 默认行为（无SupervisorJob）═══")
        log("② 子协程1 失败会导致子协程2 被取消")

        // 注意：这里使用 coroutineScope 而不是 supervisorScope
        // 子协程1 失败 → 子协程2 会被取消
        launch {
            launch {
                delay(100)
                log("③ⓐ 子协程1 抛出异常")
                throw RuntimeException("出错")
            }
            launch {
                try {
                    repeat(10) { i ->
                        delay(50)
                        log("③ⓑ 子协程2 运行 $i")
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    log("③ⓒ 子协程2 被取消（受兄弟牵连）")
                }
            }
        }.join()

        delay(300)
        log("④ 与 supervisorScope 对比可看出区别")
    }

    /**
     * CancellationException 的特殊性 —— 不是真正的异常
     *
     * 适用场景：清理资源场景——协程取消时需要关闭文件、断开连接等。
     *   例如：协程正在下载文件，被取消时需要删除未完成的临时文件。
     *   捕获 CancellationException 做清理工作后，必须重新抛出！
     *   否则父协程状态会异常，可能导致挂起永远不恢复。
     *
     * 原理：CancellationException 是协程取消的正常机制，
     * 不应该被当作业务异常处理。如果 try-catch 里捕获了它，
     * 必须重新抛出，否则父协程无法正确跟踪状态。
     *
     * 执行顺序：
     *   ① launch 启动，delay(1000) 等待中
     *   ② 100ms 后外部调用 cancelAndJoin()
     *   ③ 协程的 delay 抛出 CancellationException，被捕获
     *   ④ 必须重新抛出 throw e，确保父协程状态正确
     */
    suspend fun demoCancelException(log: (String) -> Unit) = coroutineScope {
        log("① ═══ CancellationException 特殊性 ═══")
        log("② 捕获 CancellationException 后必须重新抛出！")

        launch {
            try {
                delay(1000)
            } catch (e: kotlinx.coroutines.CancellationException) {
                log("③ 协程被取消了")
                // ★ 必须重新抛出！否则父协程无法正常跟踪状态
                throw e
            }
        }.apply {
            delay(100)
            cancelAndJoin()
            log("④ 协程已取消")
        }
    }

    /**
     * runBlocking 中的异常 —— 直接向调用方抛
     *
     * 适用场景：仅用于单元测试中验证协程异常冒泡行为。
     *   ⚠️ 不用于 Android 生产代码。
     *
     * 执行顺序：
     *   ① runBlocking 启动，内部 launch 开始执行
     *   ② 100ms 后抛 IndexOutOfBoundsException
     *   ③ 异常向外传播到 runBlocking，被外层的 try-catch 捕获
     */
    fun demoRunBlockingException(log: (String) -> Unit) {
        log("① ═══ runBlocking 异常 ═══")
        log("② runBlocking 中的异常直接向外抛")

        try {
            runBlocking {
                launch {
                    delay(100)
                    throw IndexOutOfBoundsException("越界")
                }
            }
        } catch (e: Exception) {
            log("③ runBlocking 异常被捕：${e.message}")
        }
    }
}
