@file:OptIn(ExperimentalCoroutinesApi::class)
package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ── Channel —— 协程间通信 ──
 *
 * 核心：Channel 是"协程版的 BlockingQueue"，一个协程 send，另一个 receive。
 *
 * 四种类型：
 * ┌──────────┬──────────────────────────────────────────────┐
 * │ Channel  │ 行为                                         │
 * ├──────────┼──────────────────────────────────────────────┤
 * │ RENDEZVOUS │ 默认。send 挂起直到 receiver 就绪（无缓冲）     │
 * │ BUFFERED   │ send 到缓冲区满才挂起，默认 64 个元素           │
 * │ UNLIMITED  │ send 永不挂起，无限增长 ⚠️ 可能 OOM            │
 * │ CONFLATED  │ 保留最新值，旧值被覆盖，send 永不挂起            │
 * └──────────┴──────────────────────────────────────────────┘
 */
object ChannelDemos {

    /**
     * Channel 基本用法 —— receive vs consumeEach 核心区别
     *
     * ┌───────────────┬──────────────────────────────────────────────────┐
     * │ receive()     │ 单次取一个元素，可多次调用。channel.close() 后     │
     * │               │ receive() 会抛 ClosedReceiveChannelException。    │
     * ├───────────────┼──────────────────────────────────────────────────┤
     * │ consumeEach() │ 循环取完所有剩余元素。**完成后自动 cancel channel**，│
     * │               │ 所以 consumeEach 之后绝不能再调用 receive()！     │
     * │               │ 已废弃 ⚠️ 推荐用 for (x in channel) 替代。         │
     * ├───────────────┼──────────────────────────────────────────────────┤
     * │ for (x in ch) │ consumeEach 的替代方案，不会自动 cancel channel。  │
     * └───────────────┴──────────────────────────────────────────────────┘
     *
     * 关键总结：
     *   - consumeEach() 内部调用 channel.cancel() 释放资源 ← 很隐蔽！
     *   - 所以 "consumeEach 完成后还能 receive 吗？" → **绝对不能**，会抛异常
     *   - 但先 receive() 几次、再 consumeEach() 是可以的（channel 还没被 cancel）
     *   - 推荐用 `for (x in channel)` 代替 consumeEach，语义更清晰
     *
     * 适用场景：两个协程之间传递数据，如后台任务向 UI 汇报进度。
     *   在 Android 中可以用 Channel 替代 LiveData 或回调，
     *   不过如今更推荐 SharedFlow（Channel 的底层实现）。
     *
     * 执行顺序：
     *   ① send 1,2,3（channel 已 close）
     *   ② receive() 取走 1
     *   ③ receive() 取走 2
     *   ④ consumeEach() 取走剩余的 3，然后自动 cancel channel
     *   ⑤ ⭐ 验证：在 consumeEach 之后尝试 receive() → 抛异常！
     */
    suspend fun demoChannelBasic(log: (String) -> Unit) = coroutineScope {
        log("① ═══ receive vs consumeEach 核心区别 ═══")

        val channel = Channel<Int>(Channel.UNLIMITED)

        // ── 生产者：发送 3 个元素后关闭 ──
        launch {
            for (i in 1..3) {
                log("② send：$i")
                channel.send(i)
            }
            channel.close() // 发送完毕，关闭 channel
        }

        // ── receive() 单次取元素（可多次调用）──
        log("③ receive() 取第 1 个：${channel.receive()}")
        log("④ receive() 取第 2 个：${channel.receive()}")

        // ── consumeEach() 取完剩余元素 ──
        launch {
            log("⑤ⓐ consumeEach 开始消费...")
            channel.consumeEach { value ->
                log("⑤ⓑ consumeEach 收到：$value")
            }
            // consumeEach 执行完毕后，channel 已被 cancel！
            // 此时 channel 的状态：isClosedForReceive = true, isClosedForSend = true
            log("⑤ⓒ consumeEach 结束（channel 已被自动 cancel）")
        }.join()

        // ── ⭐ 验证：consumeEach 之后还能 receive 吗？ ──
        launch {
            try {
                // 这行会抛 ClosedReceiveChannelException！
                // 因为 consumeEach 内部调用了 channel.cancel() 关闭了 channel
                log("⑥ ⚠️ 尝试 consumeEach 后继续 receive()...")
                val value = channel.receive()
                log("⑥ 竟然收到了：$value（这行不会执行到）")
            } catch (e: Exception) {
                log("⑥ ❌ receive() 失败：${e::class.simpleName} — ${e.message}")
                log("⑥ ★ 结论：consumeEach 后 channel 已被 cancel，不能再用 receive()")
            }
        }.join()
    }

    /**
     * produce —— 生产者协程
     *
     * produce 是 coroutineScope 的扩展，启动一个协程来生产数据，
     * 返回 ReceiveChannel，用 consumeEach 消费。
     *
     * 适用场景：数据分页加载。例如 Room/Paging 的 Channel 返回、
     *   数据库查询结果分批发送给 ViewModel，ViewModel 再转 StateFlow 供 UI 订阅。
     *   produce 自动管理协程生命周期，不需要手动 close。
     *
     * 执行顺序：
     *   ① produce 启动生产者，每 100ms 发送一个元素
     *   ② consumeEach 接收元素，按序打印
     *   ③ produce 完成后自动关闭 channel
     */
    suspend fun demoProduce(log: (String) -> Unit) = coroutineScope {
        log("① ═══ produce：生产者 ═══")

        val channel = produce {
            for (i in 1..5) {
                delay(100)
                log("② produce 发送：$i")
                send(i)
            }
            log("③ produce 完成，关闭 channel")
        }

        delay(50)
        channel.consumeEach { value ->
            log("④ consume 收到：$value")
        }
        log("⑤ 所有元素消费完成")
    }

    /**
     * RENDEZVOUS —— 无缓冲 Channel
     *
     * send 和 receive 必须同时准备就绪，否则 send 会挂起等待。
     *
     * 适用场景：要求生产者和消费者严格同步的场景，
     *   例如：任务分配——worker 处理完一个任务后再领取下一个。
     *   ⚠️ 易导致死锁，Android 开发中很少直接使用。
     *
     * 执行顺序：
     *   ① 生产者准备发送 → send 挂起等待消费者
     *   ② 消费者就绪后接收 → 握手成功
     *   ③ 消费者 delay(200) 模拟慢处理
     *   ④ 生产者等待下一个 send，反复握手
     */
    suspend fun demoRendezvous(log: (String) -> Unit) = coroutineScope {
        log("① ═══ RENDEZVOUS（默认）：无缓冲 ═══")
        log("② send 必须等 receive 就绪，反之亦然（握手模式）")

        val channel = Channel<String>(Channel.RENDEZVOUS)

        launch {
            for (i in 1..3) {
                log("③ⓐ 准备发送：消息$i")
                channel.send("消息$i")
                log("③ⓑ 发送完成：消息$i")
            }
            channel.close()
        }

        launch {
            delay(50)
            channel.consumeEach { v ->
                log("④ >>> 收到：$v")
                delay(200)
            }
        }.join()
    }

    /**
     * CONFLATED —— 合并模式
     *
     * send 永不挂起，如果 receiver 还没处理完，旧数据被新数据覆盖。
     * 只保留最新值，丢弃中间值。
     *
     * 适用场景：传感器数据（GPS 定位、加速度计）、位置更新、
     *   蓝牙 RSSI 值变化——中间值过时无意义，只需要最新值。
     *   类似 StateFlow 的去重语义，但 CONFLATED 无初始值。
     *
     * 执行顺序：
     *   ① 生产者快速连续发送 5 条消息
     *   ② 消费者慢速处理（delay 200ms），只能消费到最新值
     *   ③ 中间值被丢弃，只保留最后一条
     */
    suspend fun demoConflated(log: (String) -> Unit) = coroutineScope {
        log("① ═══ CONFLATED：合并模式 ═══")
        log("② 只保留最新值，中间值被丢弃")

        val channel = Channel<String>(Channel.CONFLATED)

        launch {
            repeat(5) { i ->
                log("③ 快速发送：消息$i")
                channel.send("消息$i")
                delay(50)
            }
            channel.close()
        }

        delay(80)

        launch {
            channel.consumeEach { v ->
                delay(200)
                log("④ >>> 消费到：$v（中间值已丢失）")
            }
        }.join()
    }

    /**
     * BUFFERED —— 有缓冲
     *
     * 有默认 64 个元素的缓冲区。缓冲区满后 send 挂起。
     * 可以用 Channel(10) 指定缓冲区大小。
     *
     * 适用场景：数据批量处理、队列消费。
     *   例如 Room 批量写入——多条 DB 操作先缓存到 Channel，
     *   消费者攒一批后一次性 commit，减少 IO 次数。
     *   日志批量上报——日志先入缓冲 channel，攒够数量或定时上报。
     *   ⚠️ 缓冲区太大会占用内存，需根据数据量合理设置。
     *
     * 执行顺序：
     *   ① 缓冲区容量 3，前3个 send 立即成功
     *   ② 第4个 send 等待消费者腾出空间后成功
     *   ③ 消费者 delay(300) 后开始消费
     */
    suspend fun demoBuffered(log: (String) -> Unit) = coroutineScope {
        log("① ═══ BUFFERED：有缓冲 ═══")
        log("② 缓冲区 3 个元素，满了 send 才挂起")

        val channel = Channel<Int>(3)

        launch {
            for (i in 1..6) {
                log("③ send：$i")
                channel.send(i)
            }
            channel.close()
        }

        launch {
            delay(300)
            channel.consumeEach { v ->
                log("④ >>> 消费：$v")
                delay(100)
            }
        }.join()
    }

    /**
     * fan-out —— 多个消费者
     * 多个协程从同一个 Channel 消费，每个元素只被消费一次。
     *
     * 适用场景：多个 worker 并行处理任务队列，每个任务只被一个 worker 消费。
     *   例如下载队列——多个协程同时下载不同文件，各取各的任务。
     *   图片处理队列——Bitmap 压缩、滤镜处理，多个 worker 协程并行处理。
     *   Android 中常见的"分发-处理"模式，天然适合 Fan-Out。
     *   ⚠️ 每个元素仅被消费一次，不会重复处理。
     *
     * 执行顺序：
     *   ① produce 发送 6 个元素
     *   ② 3 个消费者并行消费，每个元素只被一个消费者处理
     *   ③ 元素在消费者间均匀分配
     */
    suspend fun demoFanOut(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Fan-Out：多消费者 ═══")
        log("② 每个元素只被其中一个消费者处理")

        val channel = produce {
            for (i in 1..6) {
                delay(80)
                send(i)
            }
        }

        repeat(3) { id ->
            launch {
                channel.consumeEach { value ->
                    log("③ 消费者${id} 处理：$value")
                    delay(100)
                }
            }
        }.also { delay(1000) }

        log("④ Fan-out：3个消费者平分6个元素")
    }

    /**
     * fan-in —— 多个生产者
     * 多个协程同时向同一个 Channel 发送。
     *
     * 适用场景：多个数据源汇入同一个管道。
     *   例如多个传感器数据汇聚（加速度计、陀螺仪、磁力计）统一处理后上报。
     *   多源日志合并——不同模块的日志通过 Fan-In 汇入同一个收集器。
     *   Android 中多 Repository 向同一个 ViewModel 发送事件的场景。
     *   ⚠️ 多生产者并发写时顺序不确定，依赖协程调度。
     *
     * 执行顺序：
     *   ① 生产者A 和 生产者B 同时向 Channel 发数据
     *   ② 消费者接收数据（顺序取决于调度）
     *   ③ 500ms 后关闭 Channel
     */
    suspend fun demoFanIn(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Fan-In：多生产者 ═══")
        log("② 多个生产者同时向同一个 Channel 发数据")

        val channel = Channel<String>(Channel.BUFFERED)

        launch {
            for (i in 1..3) {
                delay(60)
                log("③ⓐ 生产者A 发送：A - $i")
                channel.send("生产者A - $i")
            }
        }
        launch {
            for (i in 1..3) {
                delay(80)
                log("③ⓑ 生产者B 发送：B - $i")
                channel.send("生产者B - $i")
            }
        }

        launch {
            delay(500)
            channel.close()
        }

        channel.consumeEach { v ->
            log("④ >>> 收到：$v")
        }
        log("⑤ 所有生产者完成")
    }
}
