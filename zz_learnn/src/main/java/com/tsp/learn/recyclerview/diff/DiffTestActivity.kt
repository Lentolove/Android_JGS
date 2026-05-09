package com.tsp.learn.recyclerview.diff

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsp.learn.R
import com.tsp.learn.databinding.DiffTestActivityBinding

/**
 * DiffUtil.ItemCallback + AsyncListDiffer 学习案例
 *
 * 通过不同场景的按钮操作，观察 Logcat 输出，
 * 理解 DiffUtil 的 diff 机制和 RecyclerView 的复用行为。
 *
 * 过滤标签: DiffUtil
 */
class DiffTestActivity : AppCompatActivity() {

    private lateinit var mBinding: DiffTestActivityBinding
    private lateinit var mAdapter: StudentAdapter

    /** 数据源（每次 submitList 都会基于它创建一个新副本） */
    private var mDataList = mutableListOf<StudentBean>()

    private var nextId = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DiffTestActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initRecyclerView()
        initButtons()

        // 初始加载
        resetData()
    }

    private fun initRecyclerView() {
        mAdapter = StudentAdapter()
        mBinding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@DiffTestActivity)
            setHasFixedSize(true)
        }
    }

    private fun initButtons() {
        mBinding.btnReset.setOnClickListener { resetData() }
        mBinding.btnAdd.setOnClickListener { addItem() }
        mBinding.btnRemove.setOnClickListener { removeLastItem() }
        mBinding.btnChange.setOnClickListener { changeFirstItemContent() }
        mBinding.btnShuffle.setOnClickListener { shuffleList() }
        mBinding.btnInsert.setOnClickListener { insertInMiddle() }
        mBinding.btnReplace.setOnClickListener { replaceAll() }
        mBinding.btnSameContent.setOnClickListener { submitSameContent() }
    }

    // ==================== 数据操作场景 ====================

    /** 加载初始数据 */
    private fun resetData() {
        Toast.makeText(this, "重置数据", Toast.LENGTH_SHORT).show()
        mDataList = mutableListOf(
            StudentBean(1, "张三", 18, StudentBean.GENDER_BOY),
            StudentBean(2, "李四", 19, StudentBean.GENDER_BOY),
            StudentBean(3, "小红", 18, StudentBean.GENDER_GIRL),
            StudentBean(4, "王五", 20, StudentBean.GENDER_BOY),
            StudentBean(5, "小丽", 19, StudentBean.GENDER_GIRL),
        )
        nextId = 100
        submitAndLog("重置")
    }

    /** 在末尾添加一个学生 */
    private fun addItem() {
        val newStudent = StudentBean(
            id = nextId++,
            name = "新生${nextId - 100}",
            age = 18 + (0..5).random(),
            gender = if ((0..1).random() == 0) StudentBean.GENDER_BOY else StudentBean.GENDER_GIRL
        )
        mDataList = (mDataList + newStudent).toMutableList()
        submitAndLog("末尾添加(id=${newStudent.id})")
    }

    /** 删除最后一个学生 */
    private fun removeLastItem() {
        if (mDataList.isEmpty()) {
            Toast.makeText(this, "列表已空", Toast.LENGTH_SHORT).show()
            return
        }
        val removed = mDataList.last()
        mDataList = mDataList.dropLast(1).toMutableList()
        submitAndLog("删除末尾(id=${removed.id})")
    }

    /** 修改第一个学生的内容（ID不变，内容变化） */
    private fun changeFirstItemContent() {
        if (mDataList.isEmpty()) return
        val old = mDataList.first()
        val newName = "${old.name}~" // 在名字后加 ~
        mDataList = mDataList.toMutableList().apply {
            set(0, old.copy(name = newName, age = old.age + 1))
        }
        submitAndLog("修改第一条: ${old.name} → $newName")
    }

    /** 打乱列表顺序 */
    private fun shuffleList() {
        Toast.makeText(this, "打乱顺序", Toast.LENGTH_SHORT).show()
        mDataList = mDataList.shuffled().toMutableList()
        submitAndLog("打乱顺序")
    }

    /** 在中间位置插入 */
    private fun insertInMiddle() {
        val insertPos = (mDataList.size / 2).coerceAtLeast(1)
        val newStudent = StudentBean(
            id = nextId++,
            name = "插入${nextId - 100}",
            age = 22,
            gender = StudentBean.GENDER_GIRL
        )
        mDataList = (mDataList.take(insertPos) + newStudent + mDataList.drop(insertPos)).toMutableList()
        submitAndLog("中间插入(id=${newStudent.id}) at pos=$insertPos")
    }

    /** 全量替换为另一组新数据 */
    private fun replaceAll() {
        mDataList = mutableListOf(
            StudentBean(10, "赵六", 21, StudentBean.GENDER_BOY),
            StudentBean(20, "孙七", 22, StudentBean.GENDER_BOY),
            StudentBean(30, "周八", 20, StudentBean.GENDER_GIRL),
        )
        submitAndLog("全量替换为全新数据")
    }

    /**
     * 模拟「服务器返回全新 List，但内容完全一样」
     *
     * 每次下拉刷新，服务端返回的都是同一个 ArrayList 的新实例，
     * 里面每个 StudentBean 也是新对象，但 name/age/gender 和之前一模一样。
     * 按业务直觉，UI 不应该闪烁或重绘。
     */
    private fun submitSameContent() {
        // 完全重建：新 List + 新 Bean 对象，但数据和当前完全一致
        val newList = mDataList.map { it.copy() }
        // 注意：直接 submitList(mDataList) 传的是同一个引用，
        //       而 data class 的 copy() 创建了全新的对象
        Log.d("DiffUtil", "[submitSameContent] 新 List 引用 ≠ 旧 List: ${newList !== mDataList}")
        Log.d("DiffUtil", "[submitSameContent] 新 Bean 也是全新对象，但 name/age/gender 全部相同")
        mAdapter.submitList(newList)
    }

    // ==================== 提交并打印日志 ====================

    private fun submitAndLog(action: String) {
        Log.d("DiffUtil", "========================================")
        Log.d("DiffUtil", "[操作] $action")
        Log.d("DiffUtil", "[提交前列表] size=${mDataList.size}")
        mAdapter.submitList(mDataList)
    }
}