package com.tsp.learn.coroutines

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.tsp.learn.R
import com.tsp.learn.coroutines.demos.ChannelDemos
import com.tsp.learn.coroutines.demos.ConcurrencyDemos
import com.tsp.learn.coroutines.demos.DispatcherDemos
import com.tsp.learn.coroutines.demos.ExceptionDemos
import com.tsp.learn.coroutines.demos.FlowDemos
import com.tsp.learn.coroutines.demos.JobDemos
import com.tsp.learn.coroutines.demos.ScopeDemos
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Kotlin 协程全面学习 Demo
 *
 * 涵盖 7 大主题：
 * 1. ScopeDemos   — 协程构建器与作用域
 * 2. DispatcherDemos — 调度器与 withContext
 * 3. JobDemos     — Job 生命周期与取消
 * 4. FlowDemos    — Flow / StateFlow / SharedFlow
 * 5. ExceptionDemos — 异常处理
 * 6. ChannelDemos — 协程间通信
 * 7. ConcurrencyDemos — 并发控制
 *
 * 每个 demo 点击按钮运行，日志区查看执行结果。
 * 日志格式：[线程名] HH:mm:ss.SSS 消息
 */
class CoroutineDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CoroutineDemo"
    }

    private lateinit var container: LinearLayout

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_demo)
        container = findViewById(R.id.container)

        addTitle("Kotlin 协程全面学习")

        // ── 1. 协程构建器与作用域 ──
        addSection(
            title = "1. launch — 启动协程",
            principle = "launch 返回 Job，不返回结果。用于 fire-and-forget 场景。\n" +
                    "join() 等待协程完成，cancel() 取消协程。",
            demo = { log -> ScopeDemos.demoLaunch(log) }
        )

        addSection(
            title = "2. async/await — 获取结果",
            principle = "async 返回 Deferred<T>，await() 挂起等待结果。\n" +
                    "多个 async 可以并发执行，总耗时 ≈ 最慢的任务。",
            demo = { log -> ScopeDemos.demoAsync(log) }
        )

        addSection(
            title = "3. async 并发 vs 串行",
            principle = "并发：多个 async 同时启动，await() 各自等待。\n" +
                    "串行：等一个完成再启动下一个，耗时累加。\n" +
                    "看日志中的总耗时对比。",
            demo = { log ->
                ScopeDemos.demoAsyncConcurrent(log)
                ScopeDemos.demoAsyncSequential(log)
            }
        )

        addSection(
            title = "4. runBlocking — 桥接阻塞世界",
            principle = "创建一个协程并阻塞当前线程直到完成。\n" +
                    "⚠️ 只用于 main 函数和测试，Android 生产代码禁止使用！\n" +
                    "观察 runBlocking 前后的线程阻塞行为。",
            demo = { log -> ScopeDemos.demoRunBlocking(log) }
        )

        addSection(
            title = "5. coroutineScope vs supervisorScope",
            principle = "coroutineScope：子协程失败 → 整个 scope 失败。\n" +
                    "supervisorScope：子协程失败 → 不影响兄弟协程。\n" +
                    "看异常发生后兄弟协程是否继续运行。",
            demo = { log ->
                ScopeDemos.demoCoroutineScope(log)
                ScopeDemos.demoCoroutineScopeFailure(log)
                ScopeDemos.demoSupervisorScope(log)
            }
        )

        // ── 2. 调度器 ──
        addSection(
            title = "6. Dispatchers 调度器对比",
            principle = "Main → UI 线程\n" +
                    "IO → 网络/数据库/文件（线程池）\n" +
                    "Default → CPU 密集计算（线程池）\n" +
                    "Unconfined → 不限定（不推荐生产使用）\n" +
                    "看日志中的线程名来区分调度器。",
            demo = { log -> DispatcherDemos.demoDispatchers(log) }
        )

        addSection(
            title = "7. withContext — 线程切换",
            principle = "Android 最高频的协程 API！\n" +
                    "在不创建新协程的情况下切换到指定调度器执行，\n" +
                    "完成后自动切回原线程。\n" +
                    "替代回调+Handler 的终极方案。",
            demo = { log -> DispatcherDemos.demoWithContext(log) }
        )

        addSection(
            title = "8. 链式 withContext — 完整业务流程",
            principle = "模拟真实场景：\n" +
                    "UI → IO(网络请求) → Default(JSON解析) → IO(存数据库) → UI\n" +
                    "看线程如何在各调度器间切换。",
            demo = { log -> DispatcherDemos.demoWithContextChain(log) }
        )

        addSection(
            title = "9. newSingleThreadContext",
            principle = "创建专用线程，任务在该线程上串行执行。\n" +
                    "同一线程上的协程按启动顺序执行。\n" +
                    "⚠️ 用完必须 close()。",
            demo = { log -> DispatcherDemos.demoSingleThread(log) }
        )

        // ── 3. Job ──
        addSection(
            title = "10. Job 控制 — join / cancel",
            principle = "join：等待协程完成。\n" +
                    "cancel：取消协程。\n" +
                    "cancelAndJoin：取消并等待完成。",
            demo = { log -> JobDemos.demoJobControl(log) }
        )

        addSection(
            title = "11. 协作式取消 — isActive / ensureActive",
            principle = "取消是协作式的！没有挂起点（delay/withContext）的\n" +
                    "CPU 密集计算必须手动检查 isActive 或调用 ensureActive()。\n" +
                    "看错误的例子如何不响应取消。",
            demo = { log -> JobDemos.demoCooperativeCancellation(log) }
        )

        addSection(
            title = "12. 超时控制 — withTimeout / withTimeoutOrNull",
            principle = "withTimeout：超时抛 TimeoutCancellationException。\n" +
                    "withTimeoutOrNull：超时返回 null。\n" +
                    "推荐用后者，避免异常处理。",
            demo = { log -> JobDemos.demoTimeout(log) }
        )

        addSection(
            title = "13. 父子协程取消传播",
            principle = "父协程取消 → 自动级联取消所有子协程。\n" +
                    "这是结构化并发的核心机制。\n" +
                    "子协程捕获 CancellationException 后必须重新抛出！",
            demo = { log -> JobDemos.demoParentChildCancel(log) }
        )

        // ── 4. Flow ──
        addSection(
            title = "14. 冷 Flow — 每次 collect 重新执行",
            principle = "flow { } 中的代码只在被 collect 时才执行。\n" +
                    "每次 collect 都是独立的数据流。\n" +
                    "看两次 collect 各自的日志。",
            demo = { log -> FlowDemos.demoFlow(log) }
        )

        addSection(
            title = "15. Flow 操作符 — map / filter / catch",
            principle = "操作符是中间操作，不触发执行。\n" +
                    "collect / first 等 terminal 操作才启动 Flow。\n" +
                    "链式调用，数据流经每一层变换。",
            demo = { log -> FlowDemos.demoFlowOperators(log) }
        )

        addSection(
            title = "16. Flow 错误处理 — catch / retry",
            principle = "catch：捕获上游异常（必须在 collect 前）。\n" +
                    "retry：失败后重试。\n" +
                    "看异常被捕获和自动重试的行为。",
            demo = { log -> FlowDemos.demoFlowErrorHandling(log) }
        )

        addSection(
            title = "17. debounce — 防抖",
            principle = "debounce(200ms)：安静 200ms 后才发射最新值。\n" +
                    "中间的快速变化被丢弃。\n" +
                    "适用：搜索框输入防抖。",
            demo = { log -> FlowDemos.demoDebounce(log) }
        )

        addSection(
            title = "18. StateFlow — 状态容器",
            principle = "必须有初始值。新订阅者立即收到当前值（粘性）。\n" +
                    "相等值不重复发射（去重）。\n" +
                    "适用：UI 状态、登录状态等持续状态。",
            demo = { log -> FlowDemos.demoStateFlow(log) }
        )

        addSection(
            title = "19. StateFlow — 去重验证",
            principle = "StateFlow 默认去重（distinctUntilChanged）。\n" +
                    "连续设置相同值，collect 只收到一次通知。\n" +
                    "看日志中 collect 收到的次数。",
            demo = { log -> FlowDemos.demoStateFlowDistinct(log) }
        )

        addSection(
            title = "20. SharedFlow — 一次性事件",
            principle = "无初始值，replay=0 不保留历史。\n" +
                    "晚到的订阅者收不到之前的事件。\n" +
                    "适用：Toast、导航等一次性信号。",
            demo = { log -> FlowDemos.demoSharedFlow(log) }
        )

        // ── 5. 异常处理 ──
        addSection(
            title = "21. try-catch 捕获异常",
            principle = "try-catch 包住 launch 协程体捕获异常。\n" +
                    "这是最推荐的生产方式。\n" +
                    "异常不会扩散到外部协程。",
            demo = { log -> ExceptionDemos.demoTryCatch(log) }
        )

        addSection(
            title = "22. async 异常 — await() 时抛出",
            principle = "async 中的异常不会立即抛出。\n" +
                    "调用 await() 时才会抛出。\n" +
                    "用 try-catch 包住 await() 即可。",
            demo = { log -> ExceptionDemos.demoAsyncException(log) }
        )

        addSection(
            title = "23. SupervisorJob — 失败隔离",
            principle = "默认行为：子协程失败 → 兄弟被取消。\n" +
                    "SupervisorJob：子协程失败 → 只取消自己。\n" +
                    "看两个 demo 的对比行为。",
            demo = { log ->
                ExceptionDemos.demoDefaultJob(log)
                ExceptionDemos.demoSupervisorJob(log)
            }
        )

        addSection(
            title = "24. CancellationException 特殊性",
            principle = "CancellationException 是取消的正常机制！\n" +
                    "不是业务异常。try-catch 捕获后必须重新抛出！\n" +
                    "不重新抛出会导致父协程状态异常。",
            demo = { log -> ExceptionDemos.demoCancelException(log) }
        )

        // ── 6. Channel ──
        addSection(
            title = "25. Channel 基本收发",
            principle = "Channel 是协程间的 BlockingQueue。\n" +
                    "send 发数据，receive 收数据。\n" +
                    "close 关闭 Channel，consumeEach 遍历消费。",
            demo = { log -> ChannelDemos.demoChannelBasic(log) }
        )

        addSection(
            title = "26. produce — 生产者模式",
            principle = "produce 启动生产者协程，返回 ReceiveChannel。\n" +
                    "用 consumeEach 消费。\n" +
                    "生产者完成后自动 close。",
            demo = { log -> ChannelDemos.demoProduce(log) }
        )

        addSection(
            title = "27. RENDEZVOUS — 无缓冲握手",
            principle = "默认类型。send 必须等 receive 就绪。\n" +
                    "双方要同时准备好，否则挂起等待。\n" +
                    "看 send/receive 的交替行为。",
            demo = { log -> ChannelDemos.demoRendezvous(log) }
        )

        addSection(
            title = "28. CONFLATED — 合并模式",
            principle = "只保留最新值，中间值被丢弃。\n" +
                    "send 永不挂起。\n" +
                    "适用：传感器数据、位置更新等可丢弃中间值。",
            demo = { log -> ChannelDemos.demoConflated(log) }
        )

        addSection(
            title = "29. BUFFERED — 有缓冲 Channel",
            principle = "缓冲区满后 send 挂起等待消费者腾出空间。\n" +
                    "可指定大小，默认 64。\n" +
                    "适用：数据批量处理、日志批量上报、Room 批量写入。",
            demo = { log -> ChannelDemos.demoBuffered(log) }
        )

        addSection(
            title = "30. Fan-Out / Fan-In — 多消费者/生产者",
            principle = "Fan-Out：多个消费者平分 Channel 元素。\n" +
                    "Fan-In：多个生产者向同一 Channel 发数据。\n" +
                    "并行处理与多源汇合。",
            demo = { log ->
                ChannelDemos.demoFanOut(log)
                ChannelDemos.demoFanIn(log)
            }
        )

        // ── 7. 并发控制 ──
        addSection(
            title = "32. Mutex — 协程安全互斥锁",
            principle = "Mutex.withLock 确保代码块同时只有一个协程执行。\n" +
                    "对比没有 Mutex 的数据竞争。\n" +
                    "挂起等待锁时不阻塞线程。",
            demo = { log -> ConcurrencyDemos.demoMutex(log) }
        )

        addSection(
            title = "32. Semaphore — 限制并发数",
            principle = "Semaphore(3) 允许最多 3 个协程同时执行。\n" +
                    "withPermit 获取许可，超限则挂起等待。\n" +
                    "适用：限制并发网络请求数。",
            demo = { log -> ConcurrencyDemos.demoSemaphore(log) }
        )

        addSection(
            title = "33. yield — 协程谦让",
            principle = "yield() 让出执行机会给其他协程。\n" +
                    "在 CPU 密集操作中定期调用可避免独占。\n" +
                    "看任务A yield 后任务B 获得执行。",
            demo = { log -> ConcurrencyDemos.demoYield(log) }
        )

        // 底部留白
        val spacer = TextView(this)
        spacer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 120
        )
        container.addView(spacer)
    }

    /**
     * 添加一个 Demo 卡片到页面
     */
    private fun addSection(
        title: String,
        principle: String,
        demo: suspend ((String) -> Unit) -> Unit
    ) {
        // ── Card ──
        val card = MaterialCardView(this)
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 12) }
        card.setContentPadding(16, 16, 16, 16)
        card.radius = 8f
        card.cardElevation = 3f

        val inner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        card.addView(inner)

        // ── Title ──
        val tvTitle = TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(0xFF1A73E8.toInt())
            setPadding(0, 0, 0, 6)
        }
        inner.addView(tvTitle)

        // ── Principle ──
        val tvPrinciple = TextView(this).apply {
            text = principle
            textSize = 12f
            setTextColor(0xFF666666.toInt())
            setPadding(0, 0, 0, 10)
        }
        inner.addView(tvPrinciple)

        // ── Run Button ──
        val btn = Button(this).apply {
            text = "▶ 运行"
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF1A73E8.toInt())
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                240, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        inner.addView(btn)
        container.addView(card)

        // ── Click to run ──
        btn.setOnClickListener {
            val handler = CoroutineExceptionHandler { _, e ->
                Log.e(TAG, "‼️ 未捕获异常：${e.message}")
            }
            lifecycleScope.launch(handler) {
                try {
                    demo { msg -> appendLog(msg) }
                } catch (e: Exception) {
                    Log.e(TAG, "‼️ Demo 异常：${e.message}")
                }
                Log.d(TAG, "═════════════════════════════")
            }
        }
    }

    /**
     * 添加页面大标题
     */
    private fun addTitle(title: String) {
        val tv = TextView(this).apply {
            text = title
            textSize = 22f
            setTextColor(0xFF202124.toInt())
            setPadding(0, 8, 0, 16)
        }
        container.addView(tv)
    }

    /**
     * 格式化日志并输出到 Logcat
     */
    private fun appendLog(msg: String) {
        val time = timeFormat.format(Date())
        val thread = Thread.currentThread().name
        Log.d(TAG, "[$thread] $time $msg")
    }
}
