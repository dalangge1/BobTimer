package com.sandyz.alltimers.common.widgets

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.sandyz.alltimers.common.R
import com.sandyz.alltimers.common.extensions.setOnClickAction

/**
 * created by zhangzhe
 * 2020/12/4
 */

class OptionalPopWindow private constructor(val context: Context?) : PopupWindow(context) {

    companion object {
        private val TAG = OptionalPopWindow::class.java.name
    }

    enum class AlignMode {
        LEFT,  // 左对齐
        RIGHT, // 右对齐
        MIDDLE,// 居中
        CENTER // 在目标视图的正中间
    }

    init {
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    class Builder {
        var optionalPopWindow: OptionalPopWindow? = null
        var context: Context? = null
        var mainView: LinearLayout? = null
        var parentView: View? = null
        var childCount = 0
        fun with(context: Context): Builder {
            optionalPopWindow = OptionalPopWindow(context)
            this.context = context
            mainView = LayoutInflater.from(context).inflate(R.layout.common_popwindow_options, null, false).also {
                parentView = it
            }.findViewById(R.id.popwindow_mainview)
            return this
        }

        fun addOptionAndCallback(optionText: String, onClickCallback: () -> Unit): Builder {
            if (context == null || optionalPopWindow == null) {
                throw IllegalStateException("$TAG Can't add option without context!")
            }
            val view = LayoutInflater.from(context).inflate(R.layout.common_popwindow_option_normal, null, false)
            view.findViewById<TextView>(R.id.popwindow_tv_option).text = optionText
            view.findViewById<TextView>(R.id.popwindow_tv_option).setOnClickAction {
                optionalPopWindow!!.dismiss()
                onClickCallback()
            }
            childCount++
            mainView?.addView(view)
            mainView?.invalidate()
            return this
        }

        /**
         * 显示弹窗
         * @param view 要展示在哪个view的下面？
         * @param alignMode 对齐方式
         * @param offsetY 要在view的下面多少的位置？
         */
        fun show(view: View, alignMode: AlignMode = AlignMode.CENTER, offsetY: Int = 0) {
            if (optionalPopWindow == null || parentView == null || context == null || childCount == 0) {
                throw IllegalStateException("$TAG IllegalState!")
            }
            optionalPopWindow!!.contentView = parentView

            // 先测量mainView，以免取到measuredWidth为0
            mainView!!.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            // 这样就可以根据布局的大小去计算是居中还是左右对齐了
            val offsetXt = when (alignMode) {
                AlignMode.MIDDLE -> -(View.MeasureSpec.getSize(mainView!!.measuredWidth) - view.width) / 2
                AlignMode.LEFT -> 0
                AlignMode.RIGHT -> -(View.MeasureSpec.getSize(mainView!!.measuredWidth) - view.width)
                AlignMode.CENTER -> -(View.MeasureSpec.getSize(mainView!!.measuredWidth) - view.width) / 2
            }

            // 如果是正中间，则修改Y坐标为正中间
            var offsetYt = offsetY
            if (alignMode == AlignMode.CENTER) {
                offsetYt = -(View.MeasureSpec.getSize(parentView!!.measuredHeight) + view.height) / 2
            }

            // 隐藏最后一个选项的分割线
            mainView?.let {
                it.getChildAt(it.childCount - 1).findViewById<View>(R.id.qa_divide_line).visibility = View.GONE
            }

            // 显示弹窗
            optionalPopWindow!!.showAsDropDown(view, offsetXt, offsetYt, Gravity.START)
            optionalPopWindow!!.showBackgroundAnimator()
            optionalPopWindow!!.setOnDismissListener {
                optionalPopWindow!!.hideBackgroundAnimator()
            }

        }
    }


    private fun setWindowBackgroundAlpha(alpha: Float) {
        if (context is Activity) {
            val window: Window = context.window
            val layoutParams: WindowManager.LayoutParams = window.attributes
            layoutParams.alpha = alpha
            window.attributes = layoutParams
        }
    }

    /**
     * 窗口显示，窗口背景透明度渐变动画
     */
    private fun showBackgroundAnimator() {
        val animator = ValueAnimator.ofFloat(1.0f, 0.5f)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }

    private fun hideBackgroundAnimator() {
        val animator = ValueAnimator.ofFloat(0.5f, 1.0f)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }

}