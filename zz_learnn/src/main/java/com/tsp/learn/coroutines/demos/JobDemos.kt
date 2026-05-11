package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/**
 * ── Job 生命周期与协程取消 ──
 *
 * Job 的状态机：New → Active → Completing → Completed ← 正常路径
 *                        → Cancelling → Cancelled   ← 取消路径
 *
 * 关键原则：
 * 1. 取消是"协作式"的 —— 协程必须检查取消状态才能响应取消
 * 2. 挂起点（delay、withContext 等）会自动检查取消
 * 3. CPU 密集计算需要手动检查 isActive 或 ensureActive()
 * 4. 取消会向上传播到父协程（结构化并发）
 */
object JobDemos {

    /**
     * Job 的基本控制 —— join / cancel
     *
     * 适用场景：UI 生命周期中取消正在执行的协程。
     *   例如：用户打开页面发起网络请求，在请求返回前退出页面，
     *   通过 cancel 取消协程，避免 UI 更新操作在已销毁的页面上执行。
     *   ViewModel.onCleared() 中 cancel 协程是最常见的用法。
     *
     * 执行顺序：
     *   ① 等待 Job1
     *   ② Job1 内部延迟 300ms 后完成
     *   ③ join() 返回，继续执行
     *   ④ 启动 Job2（每50ms输出一次）
     *   ⑤ 120ms 后取消 Job2
     *   ⑥ Job2 被 cancel
     */
    suspend fun demoJobControl(log: (String) -> Unit) = coroutineScope {
        log("═══ Job 控制（join / cancel）═══")

        val job1: Job = launch {
            delay(300)
            log("② Job1 延迟300ms后完成")
        }
        log("① 等待 Job1...")
        job1.join()
        log("③ Job1 已 join（join 是挂起函数，会等待到结束）")

        val job2 = launch {
            repeat(100) { i ->
                delay(50)
                log("④ⓐ Job2 执行第 $i 次")
            }
        }
        delay(120)
        log("④ⓑ 延迟120ms后，取消 Job2！")
        job2.cancelAndJoin()
        log("⑤ Job2 已取消（只执行了2~3次就被终止）")
    }

    /**
     * 协作式取消 —— CPU 密集任务必须手动检查
     *
     * 适用场景：CPU 密集处理大量数据时需要响应取消。
     *   例如：在大列表中搜索匹配项、Bitmap 批量加水印、
     *   本地日志文件解析、图片滤镜处理。
     *   这些操作没有挂起点（delay/withContext），
     *   必须通过 isActive / ensureActive() 手动响应取消。
     *   否则 cancel 调用了协程也不会退出，浪费 CPU 资源。
     *
     * 原理：delay()、withContext() 等挂起函数会抛出 CancellationException，
     * 但如果协程中没有挂起点，取消信号不会被响应。
     * 需要通过 isActive 或 ensureActive() 主动检查。
     *
     * 执行顺序：
     *   ① 错误示范：纯计算无检查 → cancel 不生效
     *   ② 正确示范：isActive 检查 → cancel 后主动退出
     *   ③ ensureActive 写法 → cancel 后抛 CancellationException
     */
    suspend fun demoCooperativeCancellation(log: (String) -> Unit) = coroutineScope {
        log("═══ 协作式取消 ═══")

        log("①ⓐ 错误：纯计算没有挂起点，不响应取消")
        launch {
            var i = 0
            while (i < 100_000_000) {
                i++
                if (i % 20_000_000 == 0) log("①ⓑ 还在跑... i=$i")
            }
            log("①❌ 竟然完成了! cancel 没起作用")
        }.apply {
            delay(10)
            cancelAndJoin()
            log("①❌ 取消已发送，但协程没有检查取消状态，不会退出")
        }

        delay(50)

        log("②ⓐ 正确：每次循环检查 isActive")
        launch {
            var i = 0
            while (isActive) {
                i++
                if (i % 20_000_000 == 0) log("②ⓑ isActive 检查中... i=$i")
            }
            log("②ⓒ 检测到取消，主动退出。i=$i")
        }.apply {
            delay(10)
            cancelAndJoin()
            log("②ⓓ 已正确取消（isActive 返回 false 后循环退出）")
        }

        delay(50)

        log("③ⓐ ensureActive() 简化写法（自动抛 CancellationException）")
        launch {
            try {
                var i = 0
                while (i < 100_000_000) {
                    i++
                    ensureActive()
                    if (i % 20_000_000 == 0) log("③ⓑ ensureActive 检查中... i=$i")
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                log("③ⓒ 捕获到 CancellationException，协程已取消")
            }
        }.apply {
            delay(10)
            cancelAndJoin()
            log("③ⓓ ensureActive 方式取消成功")
        }
    }

    /**
     * withTimeout / withTimeoutOrNull —— 超时控制
     *
     * 适用场景：网络请求超时保护。
     *   例如：调用第三方 API 设置 5s 超时，超时后自动取消请求；
     *   启动页最长等待 3s，超时自动跳转到主页面；
     *   遮罩层/Toast 自动消失。优先用 withTimeoutOrNull 避免异常处理。
     *
     * 执行顺序：
     *   ① withTimeout(300ms) → 300ms 到，抛 TimeoutCancellationException
     *   ② withTimeoutOrNull(300ms) → 300ms 到，返回 null
     */
    suspend fun demoTimeout(log: (String) -> Unit) = coroutineScope {
        log("═══ 超时控制 ═══")

        log("①ⓐ withTimeout(300ms) 超时将抛异常 ---")
        try {
            withTimeout(300) {
                repeat(100) { i ->
                    delay(100)
                    log("①ⓑ withTimeout 执行中... $i")
                }
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            log("①ⓒ 捕获超时异常：${e.message}（300ms 到，协程被取消）")
        }

        log("②ⓐ withTimeoutOrNull(300ms) 超时返回 null ---")
        val result = withTimeoutOrNull(300) {
            delay(500)
            "不会到达这里"
        }
        log("②ⓑ 结果是 null：${result == null}（超时不抛异常，返回 null）")
    }

    /**
     * 父协程与子协程 —— 结构化并发的取消传播
     *
     * 适用场景：父页面销毁时取消所有子协程，避免内存泄漏和 UI 空指针。
     *   例如 ViewModel.onCleared() 取消 viewModelScope，
     *   → 内部所有 launch 被自动取消
     *   → 子 launch 内的网络请求也被级联取消。
     *   这就是结构化并发的威力——不需要手动管理每个协程的取消。
     *
     * 执行顺序：
     *   ① 启动父协程（内部含 子协程1、子协程2）
     *   ② 子协程1、2 并发运行（每100ms输出一次）
     *   ③ 350ms 后父协程完成（内部子协程继续运行）
     *   ④ 600ms 后取消父协程
     *   ⑤ 子协程1、2 被级联取消
     *
     * 核心规则：
     * - 父协程被取消 → 自动取消所有子协程
     * - 取消方向：从上到下
     */
    suspend fun demoParentChildCancel(log: (String) -> Unit) = coroutineScope {
        log("═══ 父子协程取消传播 ═══")

        val parentJob = launch {
            val child1 = launch {
                try {
                    repeat(10) { i ->
                        delay(100)
                        log("①ⓐ 子协程1 运行第 $i 次")
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    log("①ⓑ 子协程1 被父协程取消（catch 后必须 rethrow!）")
                    // ★★★ 关键：CancellationException 必须重新抛出！★★★
                    // 否则协程框架会认为子协程"正常完成"，导致父协程状态跟踪异常。
                    // 不 rethrow → 父协程可能永远卡在 Cancelling 状态，无法进入 Cancelled。
                    throw e
                }
            }

            val child2 = launch {
                repeat(10) { i ->
                    delay(100)
                    log("①ⓒ 子协程2 运行第 $i 次")
                }
            }

            delay(350)
            log("①ⓓ 父协程内部逻辑完成（子协程作为后台任务仍在运行）")
        }

        delay(600)
        log("② 取消整个父协程 → 级联取消所有子协程")
        parentJob.cancelAndJoin()
        log("③ 父协程已取消，全部子协程也被级联取消")
    }
}
