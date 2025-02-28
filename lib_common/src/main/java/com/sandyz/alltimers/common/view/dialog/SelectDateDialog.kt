package com.sandyz.alltimers.common.view.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.google.android.material.tabs.TabLayout
import com.sandyz.alltimers.common.R
import com.sandyz.alltimers.common.extensions.setOnClickAction

/**
 *@author zhangzhe
 *@date 2021/6/7
 *@description
 */

class SelectDateDialog(
    private val context: Context, private val scale: String, private val onOptionSelected: ((Int, Int, Int) -> Unit)
) {
    private var pvTime: TimePickerView? = null

    fun show() {
        pvTime = TimePickerBuilder(context) { date, _ ->
            onOptionSelected.invoke(date.year + 1900, date.month + 1, date.date)
        }.apply {
            setLayoutRes(R.layout.common_dialog_bottom_sheet_time) {
                it.findViewById<TabLayout>(R.id.common_tl_year_type).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        val t = tab?.text.toString()
                        pvTime?.isLunarCalendar = when (t) {
                            "公历" -> false
                            "农历" -> true
                            else -> false
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
                it.findViewById<View>(R.id.common_tv_cancel).setOnClickAction {
                    pvTime?.dismiss()
                }
                it.findViewById<View>(R.id.common_iv_done).setOnClickAction {
                    pvTime?.returnData()
                    pvTime?.dismiss()
                }
            }
            setBgColor(Color.TRANSPARENT)
            setLunarCalendar(false)
            setTextColorCenter(Color.parseColor("#995B3A"))
            when (scale) {
                "year" -> setType(booleanArrayOf(true, false, false, false, false, false))
                "month" -> setType(booleanArrayOf(true, true, false, false, false, false))
                "day" -> setType(booleanArrayOf(true, true, true, false, false, false))
                else -> setType(booleanArrayOf(true, true, true, false, false, false))
            }
            isDialog(false)
        }.build()
        pvTime?.show()

    }
}