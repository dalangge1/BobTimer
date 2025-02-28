package com.sandyz.alltimers.common.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import com.sandyz.alltimers.common.R
import kotlin.math.abs

/**
 *@author zhangzhe
 *@date 2022/2/20
 *@description
 */

fun View.setOnClickAction(action: (() -> Unit)?) {
    setOnTouchListener(object : View.OnTouchListener {
        private var x: Float = 0f
        private var y: Float = 0f
        private var lastClickTime = 0L
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            val v = view ?: return true
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.6f
                    x = event.x
                    y = event.y
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    if ((event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= height) && (abs(event.x - x) <= 20f && abs(event.y - y) <= 20f)) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                        v.alpha = 0.6f
                    } else {
                        parent?.requestDisallowInterceptTouchEvent(false)
                        v.alpha = 1f
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= height) {
                        // 防抖处理
                        if (System.currentTimeMillis() - lastClickTime > 500L) {
                            action?.invoke()
                            lastClickTime = System.currentTimeMillis()
                        }
                    }
                    v.alpha = 1f
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.alpha = 1f
                }
            }
            return true
        }
    })
}

@Deprecated("remind has some bug")
fun View.setOnClickActionWithScale(action: (() -> Unit)?) {
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.setTag(R.id.common_tag_btn_size, Pair(width, height))
                downAnim(v)
            }
            MotionEvent.ACTION_MOVE -> {
                val sizePair = v.getTag(R.id.common_tag_btn_size) as? Pair<Float, Float>
                if (sizePair != null) {
                    if (event.x >= 0 && event.x <= sizePair.first && event.y >= 0 && event.y <= sizePair.second) {
                        downAnim(v)
                    } else {
                        upAnim(v)
                    }
                } else {
                    if (event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= height) {
                        downAnim(v)
                    } else {
                        upAnim(v)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val sizePair = v.getTag(R.id.common_tag_btn_size) as? Pair<Float, Float>
                if (sizePair != null) {
                    if (event.x >= 0 && event.x <= sizePair.first && event.y >= 0 && event.y <= sizePair.second) {
                        action?.invoke()
                    }
                } else {
                    if (event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= height) {
                        action?.invoke()
                    }
                }
                upAnim(v)
            }
            MotionEvent.ACTION_CANCEL -> {
                upAnim(v)
            }
        }
        true
    }
}

private fun downAnim(v: View) {
    val lastAnim = v.getTag(R.id.common_tag_btn_anim)
    if (lastAnim != null) {
        (lastAnim as? AnimatorSet)?.cancel()
    }

    val anim1 = ObjectAnimator.ofFloat(v, "scaleX", v.scaleX, 0.8f)
    val anim2 = ObjectAnimator.ofFloat(v, "scaleY", v.scaleY, 0.8f)
    val animSet = AnimatorSet().apply {
        duration = 70L
    }
    animSet.play(anim1).with(anim2)
    v.setTag(R.id.common_tag_btn_anim, animSet)
    animSet.start()
}

private fun upAnim(v: View) {
    val lastAnim = v.getTag(R.id.common_tag_btn_anim)
    if (lastAnim != null) {
        (lastAnim as? AnimatorSet)?.cancel()
    }

    val anim1 = ObjectAnimator.ofFloat(v, "scaleX", v.scaleX, 1f)
    val anim2 = ObjectAnimator.ofFloat(v, "scaleY", v.scaleY, 1f)
    val animSet = AnimatorSet().apply {
        duration = 70L
    }
    animSet.play(anim1).with(anim2)
    v.setTag(R.id.common_tag_btn_anim, animSet)
    animSet.start()
}