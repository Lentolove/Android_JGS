@file:OptIn(ExperimentalCoroutinesApi::class)
package com.tsp.learn.coroutines.demos

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch

/**
 * ── Flow 系列 —— 响应式数据流 ──
 *
 * 核心概念：
 * - Flow:       冷流，每次 collect 都重新执行生产者代码
 * - StateFlow:  热流，保存最新状态，新订阅者立即拿到当前值
 * - SharedFlow: 热流，可配置 replay，无初始值，适合一次性事件
 *
 * 对比：
 * ┌───────────┬──────────┬────────────┬──────────┐
 * │           │  Flow    │ StateFlow  │SharedFlow│
 * ├───────────┼──────────┼────────────┤──────────┤
 * │ 冷/热      │ 冷       │ 热         │ 热        │
 * │ 初始值     │ 无       │ 必须有      │ 无        │
 * │ replay    │ 0        │ 1          │ 可配置    │
 * │ 粘性       │ 否       │ 是         │ 默认否    │
 * └───────────┴──────────┴────────────┴──────────┘
 */
object FlowDemos {

    /**
     * 冷 Flow —— 每次 collect 都会重新执行
     *
     * 适用场景：惰性计算、按需生成数据流。
     *   例如：每次 collect 时从 Room 查询最新数据、
     *   DataStore 配置监听、系统设置变化监听。
     *   冷 Flow 的特点是"没有 collect 就不执行代码"，
     *   每次 collect 都是独立的数据流，互不影响。
     *   Room DAO 返回 Flow 就是典型用法——每次有新数据 DB 都会推送。
     *
     * 原理：flow { } 构建器中的代码只在被 collect 时才执行，
     * 每次 collect 都是独立的数据流，互不影响。
     *
     * 执行顺序：
     *   ① 输出标题
     *   ② 说明 Flow 特性
     *   ③ Flow 未 collect 不执行
     *   ④ 第一次 collect（④ⓐ→④ⓑ→④ⓒ 为 collect 内的步骤）
     *   ⑤ 第二次 collect，生产者重新执行
     */
    suspend fun demoFlow(log: (String) -> Unit) = coroutineScope {
        log("① ═══ 冷 Flow ═══")
        log("② 每次 collect 都重新执行生产者代码")

        val coldFlow = flow {
            log("  ⓐ Flow 生产者开始执行（每次 collect 都会执行）")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
            log("  ⓒ Flow 生产者执行完毕")
        }

        log("③ Flow 还没被 collect，生产者不会执行")

        // 第一次 collect
        log("④ --- 第一次 collect ---")
        coldFlow.collect { value ->
            log("  ⓑ collector1 收到：$value")
        }

        // 第二次 collect —— 生产者的代码会重新执行
        log("⑤ --- 第二次 collect（重新执行）---")
        coldFlow.collect { value ->
            log("  ⓑ collector2 收到：$value")
        }
    }

    /**
     * Flow 操作符 —— map / filter / onEach
     *
     * 适用场景：数据加工链，类似 Kotlin 集合操作符。
     *   例如：API 返回用户列表 JSON → map(解析) → filter(过滤VIP) → map(转换UI Model)。
     *   Flow 的操作符是中间操作（不触发执行），collect 才是 terminal 操作。
     *   Android 中常用 map 做数据转换、filter 做条件过滤、catch 做异常兜底。
     *
     * 原理：操作符是中间操作，不会触发执行，
     * 只有 terminal 操作（collect、first、toList 等）才会启动 Flow。
     *
     * 执行顺序：
     *   ① 输出标题
     *   ② Flow 发射 1~5，filter 过滤出偶数 2、4
     *   ③ 每个偶数经过 map 转换 → onEach 输出 → collect 最终接收
     */
    suspend fun demoFlowOperators(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Flow 操作符 ═══")

        flow {
            for (i in 1..5) {
                delay(80)
                emit(i)
            }
        }
            .filter { it % 2 == 0 }
            .map { "数字: $it" }
            .onEach { log("② onEach: $it") }
            .collect { log("③ collect 最终收到: $it") }

        log("④ 操作符是链式的，结果经过每一层变换")
    }

    /**
     * Flow 错误处理 —— catch / retry
     *
     * 适用场景：网络请求失败自动重试、REST API 调用链异常兜底。
     *   例如：网络不稳定时 retry(3) 自动重试，
     *   catch 捕获异常后 emit 兜底数据（如缓存），
     *   避免因网络异常导致 UI 展示空白。
     *   ⚠️ catch 必须在 collect 前面，否则收不到上游异常。
     *
     * catch:      捕获上游异常（必须在 collect 之前调用）
     * retry:      失败后重试（retry(3) 最多重试3次）
     *
     * 执行顺序：
     *   ① catch 捕获上游异常，collect 正常接收前两个值后异常被捕获
     *   ② retry 失败后自动重试，直到第3次成功
     */
    suspend fun demoFlowErrorHandling(log: (String) -> Unit) = coroutineScope {
        log("① ═══ Flow 错误处理 ═══")

        // ── catch ──
        log("② --- catch 捕获异常 ---")
        flow {
            emit(1)
            emit(2)
            throw RuntimeException("模拟异常")
        }
            .catch { e -> log("④ 捕获异常：${e.message}") }
            .collect { log("③ 收到：$it") }

        // ── retry ──
        log("⑤ --- retry 重试 ---")
        var attempt = 0
        flow {
            attempt++
            if (attempt < 3) throw RuntimeException("第 $attempt 次失败")
            emit("第 $attempt 次终于成功了")
        }
            .retry(2) { e ->
                log("⑥ 重试，原因：${e.message}")
                true
            }
            .collect { log("⑦ 最终结果：$it") }
    }

    /**
     * debounce —— 防抖，用于搜索框等场景
     *
     * 适用场景：搜索框实时搜索、按钮双击防抖、文本输入自动保存。
     *   例如：用户输入关键词时每敲一个字符就请求 API 太浪费，
     *   debounce(300ms) 让用户在停顿时才发射最终输入。
     *   搜索框 + debounce + flatMapLatest 是 Android 中的经典组合。
     *
     * 执行顺序：
     *   ① 快速连续发射 "a"→"ab"→"abc"→"abcd"→"abcde"
     *   ② debounce(200ms) 只保留安静超过 200ms 后的最新值
     *   ③ "abc" 发射后安静 300ms → 输出
     *   ④ "abcde" 发射后安静 250ms → 输出
     */
    suspend fun demoDebounce(log: (String) -> Unit) = coroutineScope {
        log("① ═══ debounce 防抖 ═══")
        log("② debounce(200ms)：200ms 内没有新数据才发射")

        val searchFlow = flow {
            emit("a")    // 被丢弃
            delay(50)
            emit("ab")   // 被丢弃
            delay(50)
            emit("abc")  // 被丢弃
            delay(300)   // 安静 300ms > 200ms，发射 "abc"
            emit("abcd") // 被丢弃
            delay(100)
            emit("abcde") // 安静 250ms 后发射
            delay(250)
        }

        searchFlow
            .debounce(200)
            .collect { log("③ 搜索：$it") }

        log("④ 只有静默超过 200ms 的最终输入才会发射")
    }

    /**
     * StateFlow —— 状态容器
     *
     * 特点：
     * 1. 必须有初始值
     * 2. 新订阅者立即收到当前值（粘性）
     * 3. 值相等时不重复发射（distinctUntilChanged 默认行为）
     * 4. 热流 —— 不依赖 collect 者存在
     *
     * 适用：UI 状态、登录状态、加载状态等持续状态
     *
     * 执行顺序：
     *   ① 输出标题，查看初始值
     *   ② 依次更新三次值
     *   ③ 延迟订阅者在 200ms 后开始 collect
     *   ④ 延迟订阅者直接收到最新值"第三次更新"（粘性）
     */
    suspend fun demoStateFlow(log: (String) -> Unit) = coroutineScope {
        log("① ═══ StateFlow：状态容器 ═══")
        log("② 新订阅者立即收到当前值（粘性）")

        val stateFlow = MutableStateFlow("初始值")
        val readOnly = stateFlow.asStateFlow()

        log("③ 当前值：${readOnly.value}")

        // 延迟订阅者
        launch {
            delay(200)
            log("④ 延迟订阅者（200ms后）")
            readOnly.collect { v ->
                log("⑤ 延迟订阅者收到：$v（收到了最新值）")
            }
        }

        // 发射几个值
        delay(50)
        stateFlow.value = "第一次更新"
        delay(50)
        stateFlow.value = "第二次更新"
        delay(50)
        stateFlow.value = "第三次更新"
        delay(50)
        // ★★★ StateFlow 去重：值与当前相同，不会通知订阅者 ★★★
        // 底层用 equals() 比较，相等则跳过 emit，collect 收不到这次更新
        stateFlow.value = "第三次更新"

        delay(300)
        log("⑥ StateFlow 演示结束")
    }

    /**
     * StateFlow 去重特性 —— 相同值不发射
     *
     * 适用场景：监听同一个数据源多次更新时，只有值真的变了才需要响应。
     *   例如：ViewModel 中的加载状态（Loading/Success/Error），
     *   连续设置同一个值时 UI 不需要刷新。
     *   StateFlow 默认 distinctUntilChanged，减少不必要的 UI 刷新和计算。
     *
     * 执行顺序：
     *   ① 通过 MutableStateFlow(1) 创建，collect 立即收到初始值 1
     *   ② 设置 value=2 → 收到 2
     *   ③ 连续两次设置 value=2 → 相同值，不发射
     *   ④ 设置 value=3 → 收到 3
     *   ⑤ 总共 5 次写入，实际只通知了 3 次
     */
    suspend fun demoStateFlowDistinct(log: (String) -> Unit) = coroutineScope {
        log("① ═══ StateFlow 去重 ═══")

        val flow = MutableStateFlow(1)
        var count = 0

        launch {
            flow.collect {
                count++
                log("② collect 第${count}次收到：$it")
            }
        }

        delay(50)
        flow.value = 2
        delay(50)
        flow.value = 2 // 相同值，不会发射！
        delay(50)
        flow.value = 2 // 相同值，不会发射！
        delay(50)
        flow.value = 3
        delay(100)

        log("③ 虽然设置了4次值，但实际通知了${count}次（去重）")
    }

    /**
     * SharedFlow —— 一次性事件 / 广播
     *
     * 特点：
     * 1. 无初始值
     * 2. replay=0 时新订阅者不收到历史事件（适合 Toast、导航等一次性事件）
     * 3. 热流
     *
     * 适用：导航事件、Toast 消息、SnackBar 等"一次性"信号
     *
     * 执行顺序：
     *   ① 先发射事件1（无订阅者，已丢失）
     *   ② 订阅者1 开始 collect
     *   ③ 发射事件2 → 订阅者1 收到
     *   ④ 订阅者2 晚到后开始 collect
     *   ⑤ 发射事件3 → 订阅者1 和 订阅者2 都收到
     *   ⑥ 订阅者2 收不到历史事件（事件1、事件2）
     */
    suspend fun demoSharedFlow(log: (String) -> Unit) = coroutineScope {
        log("① ═══ SharedFlow：一次性事件 ═══")
        log("② replay=0，新订阅者不会收到历史事件")

        val sharedFlow = MutableSharedFlow<String>(replay = 0)
        val readOnly = sharedFlow.asSharedFlow()

        // 先发射一个事件
        log("③ 发射事件1（还没有订阅者）")
        sharedFlow.emit("事件1: 已消失")

        // 订阅者1
        launch {
            readOnly.collect { v ->
                log("⑤ 订阅者1 收到：$v")
            }
        }

        delay(50)
        log("④ 发射事件2")
        sharedFlow.emit("事件2: 广播给了订阅者1")

        // 订阅者2（晚到）
        delay(100)
        launch {
            log("⑥ --- 订阅者2 晚到了 ---")
            readOnly.collect { v ->
                log("⑨ 订阅者2 收到：$v（收不到历史事件）")
            }
        }

        delay(50)
        log("⑦ 发射事件3")
        sharedFlow.emit("事件3: 两个订阅者都收到")

        delay(100)
        log("⑩ SharedFlow 演示结束")
    }
}
