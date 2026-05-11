package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.system.measureTimeMillis

/**
 * ── 协程构建器与作用域 ──
 *
 * 核心概念：
 * - launch:      fire-and-forget，不返回结果
 * - async:       有返回值的协程，通过 await() 获取结果
 * - runBlocking: 阻塞当前线程等待协程完成（仅用于main/测试，非生产）
 * - coroutineScope: 结构化并发，子协程全部完成后才返回
 * - supervisorScope: 子协程失败不影响兄弟协程
 */
object ScopeDemos {

    /**
     * launch —— 启动一个"发射后不管"的协程
     * 原理：launch 返回 Job，不返回结果。协程体中的代码会在调度器上异步执行。
     *
     * 适用场景：网络请求、数据库写入、文件下载等"发后不管"操作。
     *   例如上传日志、发送统计事件、页面曝光等不需要回调的场景。
     *   UI 层最常用的协程启动方式（lifecycleScope.launch）。
     *
     * 执行顺序日志：
     * ═══ launch：启动协程 ═══
     *   ① launch 用于 fire-and-forget 场景，不返回结果
     *   ② launch 调用后立即返回，不阻塞主流程
     *   ③ⓐ launch 内部：开始执行（异步调度后）
     *   ③ⓑ launch 内部：执行结束
     *   ④ join 完成，launch 协程已结束
     */
    suspend fun demoLaunch(log: (String) -> Unit) = coroutineScope {
        log("═══ launch：启动协程 ═══")
        log("① launch 用于 fire-and-forget 场景，不返回结果")

        val job = launch {
            log("③ⓐ launch 内部：开始执行（异步调度后）")
            delay(300)
            log("③ⓑ launch 内部：执行结束")
        }

        log("② launch 调用后立即返回，不阻塞主流程")
        job.join() // 等待协程完成
        log("④ join 完成，launch 协程已结束")
    }

    /**
     * async/await —— 获取协程执行结果
     * 原理：async 返回 Deferred<T>（是 Job 的子接口），
     * 调用 await() 会挂起直到结果可用，类似 Future.get() 但不阻塞线程。
     *
     * 适用场景：两个并行任务需要各自的结果时。
     *   例如同时请求「用户信息」和「应用配置」，等两个都拿到再渲染 UI。
     *   async 比 launch + 共享变量更安全，通过返回值传递结果。
     *
     * 执行顺序日志：
     * ═══ async/await：获取协程结果 ═══
     *   ① async 返回 Deferred<T>，await() 挂起等待结果
     *   ② async 不阻塞，已返回 Deferred，继续执行
     *   ③ⓐ async 内部：开始计算...
     *   ③ⓑ async 内部：计算完成
     *   ④ await() 拿到结果：计算结果：43
     */
    suspend fun demoAsync(log: (String) -> Unit) = coroutineScope {
        log("═══ async/await：获取协程结果 ═══")
        log("① async 返回 Deferred<T>，await() 挂起等待结果")

        val deferred: Deferred<String> = async {
            log("③ⓐ async 内部：开始计算...")
            delay(500)
            log("③ⓑ async 内部：计算完成")
            "计算结果：${42 + 1}" // 最后一个表达式是返回值
        }

        log("② async 不阻塞，已返回 Deferred，继续执行")
        val result = deferred.await() // 挂起，不阻塞线程
        log("④ await() 拿到结果：$result")
    }

    /**
     * async 并发 —— 同时执行多个任务
     * 原理：多个 async 同时启动，await() 各自等待结果，
     * 总耗时 ≈ 最慢的那个任务，而不是串行之和。
     *
     * 适用场景：首页各模块数据并行加载。
     *   例如首页需要同时请求「商品列表」「分类菜单」「购物车数量」，
     *   三个 API 并发请求，总耗时 ≈ 最慢的接口，大幅缩短加载时间。
     *
     * 执行顺序日志：
     * ═══ async 并发执行 ═══
     *   ① 三个 async 同时启动，互不等待
     *   ② 结果1：结果A（等 one 400ms）
     *   ③ 结果2：结果B（等 two 600ms）
     *   ④ 结果3：结果C（three 早就完成）
     *   ⑤ 并发总耗时：~600ms（≈ 最慢的 600ms）
     */
    suspend fun demoAsyncConcurrent(log: (String) -> Unit) = coroutineScope {
        log("═══ async 并发执行 ═══")
        log("① 三个 async 同时启动，互不等待")

        val time = measureTimeMillis {
            val one = async {
                delay(400)
                "结果A"
            }
            val two = async {
                delay(600)
                "结果B"
            }
            val three = async {
                delay(200)
                "结果C"
            }
            // 三个 async 同时执行，总耗时 ≈ 600ms
            log("② 结果1：${one.await()}")  // 200ms时C已完成，但这里等one(400ms)
            log("③ 结果2：${two.await()}")  // 等two(600ms)
            log("④ 结果3：${three.await()}") // three(200ms)早就完成了
        }
        log("⑤ 并发总耗时：${time}ms（≈ 最慢的 600ms，非 200+400+600）")
    }

    /**
     * async 串行 —— 对比：等一个完成再启下一个
     *
     * 适用场景：任务之间有依赖关系时必须串行。
     *   例如：先请求「用户 Token」→ 再用 Token 请求「用户资料」→ 最后加载「好友列表」。
     *   并发 vs 串行的选择取决于数据依赖关系，而非性能偏好。
     *
     * 执行顺序日志：
     * ═══ async 串行执行（对比） ═══
     *   ① one: 结果A（等 400ms）
     *   ② two: 结果B（再等 600ms，累计 ~1000ms）
     *   ③ three: 结果C（再等 200ms，累计 ~1200ms）
     *   ④ 串行总耗时：~1200ms（逐个等待）
     */
    suspend fun demoAsyncSequential(log: (String) -> Unit) = coroutineScope {
        log("═══ async 串行执行（对比） ═══")

        val time = measureTimeMillis {
            val one = async {
                delay(400)
                "结果A"
            }
            log("① one: ${one.await()}") // 等 one 完成（400ms）

            val two = async {
                delay(600)
                "结果B"
            }
            log("② two: ${two.await()}") // 等 two 完成（600ms）

            val three = async {
                delay(200)
                "结果C"
            }
            log("③ three: ${three.await()}") // 等 three 完成（200ms）
        }
        log("④ 串行总耗时：${time}ms（合计 ≈ 1200ms = 400+600+200，逐个等待）")
    }

    /**
     * runBlocking —— 桥接阻塞与非阻塞世界
     * ⚠️ 仅用于 main 函数和测试，不要在 Android 生产代码中使用！
     * 原理：创建一个协程并阻塞当前线程直到内部的协程全部完成。
     * ⚠️ Android 生产代码禁止使用！会阻塞主线程导致 ANR。
     * 适用场景：仅用于单元测试验证协程行为、main 函数入口。
     *
     * 执行顺序日志：
     * ═══ runBlocking：阻塞线程等待协程 ═══
     *   ① runBlocking 将阻塞当前线程！
     *   ② runBlocking 内部 - 开始执行
     *   ③ runBlocking 内部 - 执行结束
     *   ④ runBlocking 阻塞了线程 ~300ms，现在线程恢复
     */
    fun demoRunBlocking(log: (String) -> Unit) {
        log("═══ runBlocking：阻塞线程等待协程 ═══")
        log("① runBlocking 将阻塞当前线程！")

        val time = measureTimeMillis {
            runBlocking {
                log("② runBlocking 内部 - 开始执行")
                delay(300)
                log("③ runBlocking 内部 - 执行结束")
            }
        }
        log("④ runBlocking 阻塞了线程 ${time}ms，现在线程恢复")
    }

    /**
     * coroutineScope —— 结构化并发
     * 原理：创建一个新的协程作用域，所有子协程完成后才返回。
     * 如果任一子协程失败，整个 scope 失败（快速失败）。
     *
     * 适用场景：需要等所有子任务完成后才继续的场景。
     *   例如批量数据加载完成后统一刷新 UI，全部成功才更新，任一失败整个回滚。
     *   coroutineScope 确保所有子协程执行完毕后 scope 才返回。
     *
     * 执行顺序日志：
     *   ① ═══ coroutineScope：结构化并发 ═══
     *   ② 启动两个子协程（异步），coroutineScope 会等全部完成
     *   ③ⓐ 子协程2 先完成（200ms）
     *   ③ⓑ 子协程1 完成（400ms）
     *   ④ 全部子协程已完成，scope 继续执行
     */
    suspend fun demoCoroutineScope(log: (String) -> Unit) {
        log("① ═══ coroutineScope：结构化并发 ═══")
        log("② 启动两个子协程（异步），coroutineScope 会等全部完成")

        coroutineScope {
            launch {
                delay(400)
                log("③ⓑ 子协程1 完成（400ms）")
            }
            launch {
                delay(200)
                log("③ⓐ 子协程2 先完成（200ms）")
            }
        }

        log("④ 全部子协程已完成，scope 继续执行")
    }

    /**
     * coroutineScope —— 子协程失败会级联取消兄弟协程
     *
     * 原理：coroutineScope 中任一子协程失败，整个 scope 失败，
     * 所有其他子协程被级联取消。与 supervisorScope 形成对比。
     *
     * 适用场景：任务组中任一失败则整体操作无意义时。
     *   例如：依赖链式加载——「用户信息」「权限列表」「功能配置」三个数据缺一不可，
     *   任何一个加载失败都应该取消其他请求，避免浪费。
     *
     * 执行顺序日志：
     *   ① ═══ coroutineScope：失败级联取消 ═══
     *   ② 子协程2 抛异常 → 子协程1 被级联取消（对比 supervisorScope）
     *   ③ⓐ 子协程1 正常运行
     *   ③ⓑ 子协程2 即将抛出异常
     *   ③ⓒ 子协程1 被级联取消！（因子协程2 异常）
     *   ③ⓓ 捕获 coroutineScope 异常：子协程2 出错了
     *   ④ 整个 coroutineScope 因子协程失败而取消
     *   → 对比 supervisorScope：子协程1 不受影响继续运行
     */
    suspend fun demoCoroutineScopeFailure(log: (String) -> Unit) {
        log("① ═══ coroutineScope：失败级联取消 ═══")
        log("② 子协程2 抛异常 → 子协程1 被级联取消（对比 supervisorScope）")

        try {
            coroutineScope {
                launch {
                    try {
                        delay(100)
                        log("③ⓐ 子协程1 正常运行")
                        delay(200)
                        log("子协程1 继续运行")
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        log("③ⓒ 子协程1 被级联取消！（因子协程2 异常）")
                        throw e
                    }
                }
                launch {
                    delay(150)
                    log("③ⓑ 子协程2 即将抛出异常")
                    throw RuntimeException("子协程2 出错了")
                }
            }
        } catch (e: Exception) {
            log("③ⓓ 捕获 coroutineScope 异常：${e.message}")
        }

        log("④ 整个 coroutineScope 因子协程失败而取消")
        log("→ 对比 supervisorScope：子协程1 不受影响继续运行")
    }

    /**
     * supervisorScope —— 子协程失败不扩散
     * 原理：与 coroutineScope 类似，但一个子协程的失败不会取消其他子协程。
     * 适合：一个子任务失败不应该影响其他子任务。
     *
     * 适用场景：子任务间独立，一个失败不应影响其他。
     *   例如首页各卡片模块数据加载——推荐列表、公告、广告各自独立加载，
     *   广告加载失败不影响推荐列表正常展示。互不干扰。
     *
     * 执行顺序日志：
     * ═══ supervisorScope：失败隔离 ═══
     *   ① 子协程2 会故意抛异常，但不影响子协程1
     *   ②ⓐ 子协程1 正常运行
     *   ②ⓑ 子协程2 即将抛出异常
     *   ②ⓒ 子协程1 仍在运行（不受子协程2影响）
     *   ③ supervisorScope 完成了（子协程1 不受影响）
     *   → 对比：如果是 coroutineScope，子协程1 也会被级联取消
     */
    suspend fun demoSupervisorScope(log: (String) -> Unit) = coroutineScope {
        log("═══ supervisorScope：失败隔离 ═══")
        log("① 子协程2 会故意抛异常，但不影响子协程1")

        supervisorScope {
            launch {
                try {
                    delay(100)
                    log("②ⓐ 子协程1 正常运行")
                    delay(200)
                    log("②ⓒ 子协程1 仍在运行（不受子协程2影响）")
                } catch (e: Exception) {
                    log("❌ 子协程1 被取消：${e}")
                }
            }
            launch {
                delay(150)
                log("②ⓑ 子协程2 即将抛出异常")
                throw RuntimeException("子协程2 出错了")
            }
        }

        log("③ supervisorScope 完成了（子协程1 不受影响）")
        log("→ 对比：如果是 coroutineScope，子协程1 也会被级联取消")
    }
}
