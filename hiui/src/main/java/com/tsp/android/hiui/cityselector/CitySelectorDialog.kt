package com.tsp.android.hiui.cityselector

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.widget.CheckedTextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.R
import com.tsp.android.hiui.item.HiViewHolder
import com.tsp.android.hiui.tab.top.HiTabTopInfo
import com.tsp.android.hiui.tab.top.HiTabTopLayout
import java.lang.IllegalStateException

/**
 *     author : shengping.tian
 *     time   : 2021/10/12
 *     desc   : 城市选择器底部弹窗
 *     version: 1.0
 */
class CitySelectorDialog : AppCompatDialogFragment() {

    /**
     * 城市选择接口回调
     */
    private var citySelectListener: IOnCitySelectListener? = null

    /**
     * 记录当前选择的省市区，或者是已经选择过，再次拉起弹窗又重新传入的 Provice
     */
    private lateinit var province: Province

    /**
     * 城市选择器的所有数据，省，市，区
     */
    private var provinceList: List<Province>? = null

    /**
     * 未选中时候的颜色
     */
    private val defaultColor = HiRes.getColor(R.color.color_333)

    /**
     * 默认选中时候的颜色
     */
    private val selectColor = HiRes.getColor(R.color.color_dd2)

    /**
     * 弹窗标题 title 提示语：请选择
     */
    private val pleasePickStr = HiRes.getString(R.string.city_selector_tab_hint)

    /**
     * TabLayout 选中的项
     */
    private var topTabSelectIndex = 0

    private lateinit var tabTopLayout: HiTabTopLayout

    private lateinit var viewPager: ViewPager

    private lateinit var close: View

    companion object {
        /**
         * 传入的 provinceList 集合 key
         */
        private const val KEY_PARAMS_PROVINCE_LIST = "key_province_list"

        /**
         * 传入的 province  key
         */
        private const val KEY_PARAMS_PROVINCE_SELECT = "key_province_select"

        private const val TAB_PROVINCE = 0
        private const val TAB_CITY = 1
        private const val TAB_DISTRICT = 2

        /**
         * 创建城市选择器弹窗
         * @param province Province? 可能拉起弹窗选择过，有传进来
         * @param list List<Province> 城市集合数据
         * @return CitySelectorDialog
         */
        fun newInstance(province: Province?, list: List<Province>): CitySelectorDialog {
            val args = Bundle()
            args.putParcelable(KEY_PARAMS_PROVINCE_SELECT, province)
            args.putParcelableArrayList(KEY_PARAMS_PROVINCE_LIST, ArrayList(list))
            val fragment = CitySelectorDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //1.加载window作为根布局
        val window = dialog?.window
        //2.加载要显示的布局
        val contentView = inflater.inflate(
            R.layout.dialog_city_selector,
            window?.findViewById(android.R.id.content),
            false
        )
        window?.apply {
            //3.设置背景为透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //4.设置宽高，高度占屏幕的 60%
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                (HiDisplayUtil.getDisplayHeightInPx(context) * 0.6f).toInt()
            )
            //5.底部显示
            setGravity(Gravity.BOTTOM)
        }
        return contentView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //获取数据
        this.province = arguments?.getParcelable(KEY_PARAMS_PROVINCE_SELECT) ?: Province()
        this.provinceList = arguments?.getParcelableArrayList(KEY_PARAMS_PROVINCE_LIST)
        tabTopLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        close = view.findViewById(R.id.close)
        close.setOnClickListener {
            //关闭弹窗
            dismiss()
        }
        requireNotNull(provinceList) { "params provinceList cannot be null" }
        refreshTabLayoutCount()

        //刷新顶部 tabTopLayout 视图状态显示，是显示 请选择，还是显示省市区
        tabTopLayout.addTabSelectedChangeListener { index, preInfo, nextInfo ->
            //tabLayout 选中的第2个，viewpager 可能还处于第一个1.则同步viewpager的页选中项
            if (viewPager.currentItem != index) {
                viewPager.setCurrentItem(index, false)
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position != topTabSelectIndex) {
                    //通知 tabTopLayout 进行标签的切换
                    tabTopLayout.defaultSelected(tabTabs[position])
                    topTabSelectIndex = position
                }
            }
        })

        viewPager.setPageTransformer(false,ZoomOutPageTransformer())

        //tabIndex 代表就是哪一个列表 发生了点击事件
        //selectDistrict 是代表该页面选中的数据对象(省市区)
        viewPager.adapter = CityPagerAdapter { tabIndex, selectDistrict ->
            when (selectDistrict.type) {
                TYPE_PROVINCE -> {
                    province = selectDistrict as Province
                }
                TYPE_CITY -> {
                    province.selectCity = selectDistrict as City
                }
                TYPE_DISTRICT -> {
                    province.selectDistrict = selectDistrict
                }
            }
            //如果说本次选中的数据对象的type ,不是区的类型,既不是第三季
            if (!TextUtils.equals(selectDistrict.type, TYPE_DISTRICT)) {
                refreshTabLayoutCount()
            } else {
                //三级列表选择完成，提示
                citySelectListener?.onCitySelect(province)
                dismiss()
            }
        }

    }

    /**
     * 存储顶部 tabLayout 标签
     */
    private val tabTabs = mutableListOf<HiTabTopInfo<Int>>()

    /**
     *  更新顶部视图状态显示
     *  例如：
     *      湖北省  黄冈市  蕲春县
     *      湖北省  黄冈市  请选择
     *      请选择
     *  根据 province  更新tabLayout的标签的数据
     *  province--->拉起选择器的时候 传递过来的。
     *  province--->本次拉起选择器的每一次选择，都会记录到province
     *  每一次选择都会调用该方法 更新tabLayout的个数
     */
    private fun refreshTabLayoutCount() {
        tabTabs.clear()
        //1.是否需要添加 请选择 条目
        var addPleasePickTab = true

        //2.是否需要创建 省 itemTab 条目
        if (!TextUtils.isEmpty(province.id)) {
            tabTabs.add(newTabInfo(province.districtName))
        }

        //3.构建 市条目
        if (province.selectCity != null) {
            tabTabs.add(newTabInfo(province.selectCity!!.districtName))
        }
        //4.构建区 tab 条目
        if (province.selectDistrict != null) {
            tabTabs.add(newTabInfo(province.selectDistrict!!.districtName))
            //此时要去掉请选择条目，因为 省市区 全都选择了
            addPleasePickTab = false
        }
        if (addPleasePickTab) {
            tabTabs.add(newTabInfo(pleasePickStr))
        }
        //通知ViewPager刷新数据
        viewPager.adapter?.notifyDataSetChanged()

        /**
         * tabLayout 更新视图数据
         * notifyDataSetChanged它是异步,inflateInfo，defaultSelected是同步，就会触发addTabSelectedChangeListener，进而触发viewpager.setCurrenItem,如果viewpager 还没刷新完成， 还没有从1页变成2页，此时肯定会报错
         */
        tabTopLayout.post {
            tabTopLayout.inflateInfo(tabTabs as List<HiTabTopInfo<*>>)
            //场景 addPleasePickTab = true ,省市区还没选择完，此时需要选择最后一个tab
            //addPleasePickTab=false.,省市区都已经选择完了，就发生在第二次进入
            tabTopLayout.defaultSelected(tabTabs[if (addPleasePickTab) tabTabs.size - 1 else 0])
        }
    }

    /**
     * 创建 HiTabTopInfo 条目
     * @param name String
     * @return HiTabTopInfo<Int>
     */
    private fun newTabInfo(name: String?): HiTabTopInfo<Int> {
        return HiTabTopInfo(name, defaultColor, selectColor)
    }

    /**
     * ViewPager适配器
     */
    inner class CityPagerAdapter
        (private val itemClickCallback: (Int, District) -> Unit) : PagerAdapter() {
        //三级选择器列表视图
        private val views = SparseArray<CityListView>(3)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            //1. 需要完成对应页面的view 的创建
            val view = views.get(position) ?: CityListView(container.context)
            views.put(position, view)
            //2.给 view 设置数据 position = 0 为省，1 为 市,2 为区

            val select: District?
            val list = when (position) {
                TAB_PROVINCE -> {
                    select = province  //如果还未选择过，自然是没有，如果选择过了province 就不为空，就是当前页选中项了
                    provinceList
                }
                TAB_CITY -> {
                    select = province.selectCity
                    province.cities
                }
                TAB_DISTRICT -> {
                    select = province.selectDistrict
                    province.selectCity!!.districts
                }
                else -> throw IllegalStateException("pageCount must be less than ${views.size()}")
            }
            //设置数据
            view.setData(select, list) {
                if (viewPager.currentItem != position) return@setData
                itemClickCallback(position, it)
            }
            if (view.parent == null) container.addView(view)
            return view
        }

        override fun getItemPosition(`object`: Any): Int {
            //需要根据object ,其实就是 instantiateItem 返回的值，也就是cityListView 来判断它是第几个页面
            return if (views.indexOfValue(`object` as CityListView?) > 0) POSITION_NONE else POSITION_UNCHANGED
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(views[position])
        }

        override fun getCount(): Int {
            return tabTabs.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }

    /**
     * 城市列表视图，每一列用一个 RecyclerView 来承载
     */
    inner class CityListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : RecyclerView(context, attrs, defStyleAttr) {
        //选择器回调接口
        private lateinit var onItemClick: (District) -> Unit

        //选中的条目
        private var selectDistrict: District? = null

        //当前要显示的列表数据
        private var districtList = ArrayList<District>()

        private var lastSelectIndex = -1

        //当前选择的位置
        private var currentSelectIndex = -1

        fun setData(select: District?, list: List<District>?, onItemClick: (District) -> Unit) {
            if (list.isNullOrEmpty()) return
            lastSelectIndex = -1
            currentSelectIndex = -1
            this.onItemClick = onItemClick
            selectDistrict = select
            districtList.clear()
            districtList.addAll(list)
            //Cannot call this method while RecyclerView is computing a layout or scrolling
            //recyclerview 在布局阶段 或者滑动阶段 不可以调用notifyDataSetChanged
            //这不是必现的，经过测试，是在viewpager刷新的时候，新增了页面，新创建了recyclerview 。由于recyclerview可能还没有布局完成
            post { adapter?.notifyDataSetChanged() }
        }

        init {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = object : RecyclerView.Adapter<HiViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiViewHolder {
                    return HiViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.dialog_city_selector_list_item, parent, false)
                    )
                }

                override fun onBindViewHolder(holder: HiViewHolder, position: Int) {
                    val checkedTextView = holder.findViewById<CheckedTextView>(R.id.title)
                    val district = districtList[position]
                    checkedTextView?.text = district.districtName
                    holder.itemView.setOnClickListener {
                        selectDistrict = district
                        currentSelectIndex = position
                        notifyItemChanged(lastSelectIndex)
                        notifyItemChanged(position)
                    }

                    //点击之后触发刷新，说明当前item 是本次选中项
                    if (currentSelectIndex == position && currentSelectIndex != lastSelectIndex) {
                        onItemClick(district)
                    }

                    //首次进入或者点击之后的刷新Item 状态的正确性
                    if (selectDistrict?.id == district.id) {
                        currentSelectIndex = position
                        lastSelectIndex = position
                    }

                    //改变item的状态了
                    checkedTextView?.isChecked = currentSelectIndex == position
                }

                override fun getItemCount(): Int {
                    return districtList.size
                }

            }

        }
    }

    fun setCitySelectListener(listener: IOnCitySelectListener) {
        this.citySelectListener = listener
    }

    interface IOnCitySelectListener {
        fun onCitySelect(province: Province)
    }
}