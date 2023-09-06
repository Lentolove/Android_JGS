package com.tsp.learn

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsp.learn.databinding.LayoutDialogInputTextBinding
import kotlin.math.abs
import kotlin.math.log

class InputTextDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutDialogInputTextBinding

    private var isEmojiShow = false

    private var isKeyBoardShow = false

    private var keyBoardHeight = 0

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutDialogInputTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var confirmCallback: Function1<String, Unit>? = null
    private var cancelCallback: Function0<Unit>? = null
    private var inputText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBottomWindow()
        initCallback()
        initView()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setWindowInsetsAnimationCallback(
                binding.root,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

                    private var startHeight = 0
                    private var lastDiffH = 0

                    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                        if (startHeight == 0) {
                            startHeight = binding.root.height
                        }
                        Log.d("tsp---->", "onPrepare: $startHeight")
                    }

                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: MutableList<WindowInsetsAnimationCompat>
                    ): WindowInsetsCompat {

                        val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                        val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                        val diff = Insets.subtract(typesInset, otherInset).let {
                            Insets.max(it, Insets.NONE)
                        }

                        val diffH = abs(diff.top - diff.bottom)
                        Log.d("tsp---->", "onProgress diffH = : $diffH  lastDiff = $lastDiffH")

                        if (keyBoardHeight == 0 && diffH != 0){
                            keyBoardHeight = diffH
                            onKeyBoardShow()
                        }

                        if (diffH < lastDiffH) {
                            onKeyBordHide()
                        }

                        lastDiffH = diffH

                        return insets
                    }
                }
            )
        } else {
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                var lastBottom = 0
                override fun onGlobalLayout() {
                    ViewCompat.getRootWindowInsets(binding.root)?.let { insets ->
                        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                        if (lastBottom != 0 && bottom == 0) {
                            onKeyBordHide()
                        }
                        if (lastBottom == 0 && bottom != 0){
                            onKeyBoardShow()
                        }
                        Log.d(
                            "tsp---->",
                            "onGlobalLayout = : $lastBottom  bottom = $bottom"
                        )

                        if (keyBoardHeight == 0 && bottom != 0){
                            onKeyBoardHeightChange(bottom)
                        }
                        lastBottom = bottom
                    }
                }
            })
        }
        inputText?.let { binding.etInput.setText(it) }
        binding.etInput.requestFocus()
    }

    private fun onKeyBoardHeightChange(height : Int){
        this.keyBoardHeight = height
        binding.coverLay.layoutParams.height = keyBoardHeight - dip2px(40f).toInt()
//        binding.titleLay.updateLayoutParams<ConstraintLayout.LayoutParams> {
//            bottomMargin = height
//        }
    }

    private fun onKeyBordHide() {
        Log.d("tsp---->",
            "onKeyBordHide = ===="
        )

    }
    var density = 0f


    fun dip2px( dpValue: Float): Float {

        if (density > 0) return (dpValue * density + 0.5f)
        density = resources.displayMetrics.density
        return (dpValue * density + 0.5f)
    }

    private fun onKeyBoardShow(){
        Log.d("tsp---->",
            "onKeyBoardShow = ===="
        )
    }


    private fun setConfirmCallback(block: (String) -> Unit) {
        confirmCallback = block
    }

    private fun setCancelCallback(block: () -> Unit) {
        cancelCallback = block
    }

    private fun setInputText(text: String) {
        if (text.isNotBlank()) {
            inputText = text
        }
    }

    private fun initCallback() {
        binding.apply {
            tvConfirm.setOnClickListener {
//                confirmCallback?.invoke(etInput.text.toString())
//                this@InputTextDialogFragment.dismiss()



            }
            tvCancel.setOnClickListener {
                cancelCallback?.invoke()
                this@InputTextDialogFragment.dismiss()
            }
            switchTv.setOnClickListener {
                if (!isEmojiShow){
                    isEmojiShow = true
//                    coverLay.visibility = View.VISIBLE
                    IMMHelper.hideSoftInput(context,root.windowToken)
                }else{
//                    coverLay.visibility = View.INVISIBLE
                    isEmojiShow = false
                    etInput.requestFocus()
                    IMMHelper.showSoftInput(etInput)
                }
            }
        }
    }


    private var bottomSheetBehavior: BottomSheetBehavior<out View>? = null

    private fun initBottomWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
//                } else {
//                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
//                }
            )
            attributes?.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            bottomSheetBehavior = this
            state = BottomSheetBehavior.STATE_EXPANDED
            addBottomSheetCallback(bottomSheetCallback)
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                //判断为向下拖动行为时，则强制设定状态为展开
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private var screenWidth = 0


    fun getScreenWidth(): Int {
        val activity = activity
        if (activity != null) {
            val metrics = activity.resources.displayMetrics
            screenWidth = metrics.widthPixels
        }
        return 0
    }

    fun getScreenHeight(): Int {
        val metrics = binding.root.resources.displayMetrics
        screenWidth = metrics.heightPixels
        return screenWidth
    }

    class Builder {
        private val dialog = InputTextDialogFragment()

        fun showNow(fragmentManager: FragmentManager, tag: String = "input") {
            dialog.showNow(fragmentManager, tag)
        }

        fun setConfirmCallback(block: (String) -> Unit): Builder {
            dialog.setConfirmCallback(block)
            return this
        }

        fun setCancelCallback(block: () -> Unit): Builder {
            dialog.setCancelCallback(block)
            return this
        }

        fun setInputText(text: String): Builder {
            dialog.setInputText(text)
            return this
        }
    }
}