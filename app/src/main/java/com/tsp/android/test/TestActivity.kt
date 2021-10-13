package com.tsp.android.test

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.tsp.android.detail.DetailActivity
import com.tsp.android.hilibrary.executor.HiExecutor
import com.tsp.android.hilibrary.utils.MainHandler
import com.tsp.android.hiui.cityselector.*
import com.tsp.android.hiui.cityselector.entity.ProvinceEntity
import com.tsp.android.jgs.databinding.ActivityTestBinding
import com.tsp.android.test.refresh.RefreshDemoActivity
import com.tsp.android.test.tap.TabTopDemoActivity
import com.tsp.android.test.tap.TestMainActivity
import com.tsp.test.http.HiRestfulActivity
import com.tsp.test.slider.SliderTestActivity
import com.tsp.test.view.ViewShowActivity
import com.tsp.test.viewgroup.MViewGroupActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class TestActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        mBinding.btnBottomTest.setOnClickListener {
            val intent = Intent(this, TestMainActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnTopTest.setOnClickListener {
            val intent = Intent(this, TabTopDemoActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnChildCount.setOnClickListener {
            val intent = Intent(this, RefreshDemoActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnHiSlider.setOnClickListener {
            startActivity(Intent(this, SliderTestActivity::class.java))
        }
        mBinding.btnHttp.setOnClickListener {
            startActivity(Intent(this, HiRestfulActivity::class.java))
        }
        mBinding.btnView.setOnClickListener {
            startActivity(Intent(this, ViewShowActivity::class.java))

        }
        mBinding.btnDetail.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        mBinding.btnHome.setOnClickListener {
            ARouter.getInstance().build("/activity/main").navigation()
        }

        mBinding.btnSearch.setOnClickListener {
            ARouter.getInstance().build("/mall/search").navigation()
        }
        mBinding.btnTest.setOnClickListener {
            startActivity(Intent(this, MViewGroupActivity::class.java))
        }

        mBinding.btnCity.setOnClickListener {
            loadAssetsData {
                val citySelector = CitySelectorDialog.newInstance(null, it)
                citySelector.setCitySelectListener(object :
                    CitySelectorDialog.IOnCitySelectListener {
                    override fun onCitySelect(province: Province) {
                        Toast.makeText(
                            this@TestActivity,
                            "province = ${province.districtName}, city = ${province.selectCity?.districtName}, area = ${province.selectDistrict?.districtName}" ,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                citySelector.show(supportFragmentManager, "city_selector")
            }
        }
    }

    private fun loadAssetsData(callback: (List<Province>) -> Unit) {
        HiExecutor.execute(runnable = {
            val text = loadFromAssets()
            if (TextUtils.isEmpty(text)) return@execute
            val parser = AddressJsonParser()
            val parseData = parser.parseData(text)
            groupByProvince(parseData, callback)
        })
    }

    /**
     * 加载 Assets 下文件
     * @return String?
     */
    private fun loadFromAssets(): String {
        try {
            val inputStream = this.assets.open("china_address.json")
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            val stringBuilder = StringBuilder()
            do {
                line = br.readLine()
                if (line != null) stringBuilder.append(line) else break
            } while (true)
            inputStream.close()
            br.close()
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 对数据进行处理
     * @param list List<ProvinceEntity>
     * @param callback Function1<List<Province>?, Unit>
     */
    private fun groupByProvince(
        ProvinceEntityList: List<ProvinceEntity>,
        callback: (List<Province>) -> Unit
    ) {
        val provinceList = ArrayList<Province>()

        for (provinceEntity in ProvinceEntityList) {
            val province = Province().apply {
                type = TYPE_PROVINCE
                id = provinceEntity.code
                districtName = provinceEntity.name
            }
            //城市集合
            provinceEntity.cityList.forEach { cityEntity ->
                val city = City().apply {
                    type = TYPE_CITY
                    id = cityEntity.code
                    districtName = cityEntity.name
                    pid = provinceEntity.code
                }
                //市里的县
                //县集合
                val areaList = ArrayList<District>()
                cityEntity.countyList.forEach { area ->
                    val district = District().apply {
                        districtName = area.name
                        id = area.code
                        type = TYPE_DISTRICT
                        pid = cityEntity.code
                    }
                    areaList.add(district)
                }
                city.districts.addAll(areaList)
                province.cities.add(city)
            }
            provinceList.add(province)
        }
        MainHandler.post {
            callback(provinceList)
        }
    }
}