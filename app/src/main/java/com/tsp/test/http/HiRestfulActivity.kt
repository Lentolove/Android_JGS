package com.tsp.test.http

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tsp.android.common.http.ApiFactory
import com.tsp.android.hilibrary.restful.HiCallback
import com.tsp.android.hilibrary.restful.HiResponse
import com.tsp.android.jgs.databinding.ActivityHiRestfulBinding
import com.tsp.test.http.flutter.MallApi
import com.tsp.test.http.flutter.MallFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HiRestfulActivity : AppCompatActivity() {

    companion object {
        const val TAG = "HiRestfulActivity"
    }

    lateinit var mBinding: ActivityHiRestfulBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHiRestfulBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.btnGet.setOnClickListener {
            getRequest()
        }

        mBinding.btnPost.setOnClickListener {
            postRequest()
        }

        mBinding.btnMall.setOnClickListener {
            getMallRequest()
        }
    }

    private fun getMallRequest(){
        MallFactory.retrofit.create(MallApi::class.java).getTopList().enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e(TAG, " getMallRequest response = $response ")
                mBinding.request.text = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, " getMallRequest failed = ${t.message} ")
                mBinding.request.text = t.message
            }

        })
    }

    private fun postRequest() {
        ApiFactory.create(ArticleService::class.java).search("安卓", 1)
            .enqueue(object : HiCallback<Pagination> {
                override fun onFailed(throwable: Throwable) {
                    Log.e(TAG, " postRequest failed = ${throwable.message} ")
                    mBinding.request.text = throwable.message
                }

                override fun onSuccess(response: HiResponse<Pagination>) {
                    Log.e(TAG, " postRequest response = ${response.data} ")
                    mBinding.request.text = response.data.toString()
                }
            })
    }

    private fun getRequest() {
        ApiFactory.create(ArticleService::class.java).getProjectList(1)
            .enqueue(object : HiCallback<Pagination> {
                override fun onFailed(throwable: Throwable) {
                    Log.e(TAG, " getRequest failed = ${throwable.message} ")
                    mBinding.request.text = throwable.message
                }

                override fun onSuccess(response: HiResponse<Pagination>) {
                    Log.e(TAG, " onSuccess response = ${response.data} ")
                    mBinding.request.text = response.data.toString()
                }
            })
    }
}