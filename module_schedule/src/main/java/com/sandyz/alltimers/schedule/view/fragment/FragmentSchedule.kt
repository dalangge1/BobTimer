package com.sandyz.alltimers.schedule.view.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sandyz.alltimers.common.base.BaseFragment
import com.sandyz.alltimers.common.config.SCHEDULE_DETAILS
import com.sandyz.alltimers.common.config.SCHEDULE_EDIT
import com.sandyz.alltimers.common.config.SCHEDULE_ENTRY
import com.sandyz.alltimers.common.extensions.dp2px
import com.sandyz.alltimers.api_schedule.ScheduleData
import com.sandyz.alltimers.schedule.databinding.ScheduleFragmentScheduleBinding
import com.sandyz.alltimers.schedule.model.ScheduleReader
import com.sandyz.alltimers.schedule.view.adapter.ScheduleMainAdapter
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

@Route(path = SCHEDULE_ENTRY)
class FragmentSchedule : BaseFragment() {

    private var list = mutableListOf<ScheduleData>()
    private lateinit var binding: ScheduleFragmentScheduleBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ScheduleFragmentScheduleBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    private val adapter = ScheduleMainAdapter(list,
        {
            ARouter.getInstance().build(SCHEDULE_DETAILS).withInt("schedule_id", it.id).navigation()
        }, {
            it.topping = !it.topping
            ScheduleReader.db?.scheduleDao()?.insert(it)
            refresh()
        }, {
            ARouter.getInstance().build(SCHEDULE_EDIT).withInt("schedule_id", it.id).navigation()
        },{
            ScheduleReader.db?.scheduleDao()?.delete(it)
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = binding.scheduleRvList
        rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv.adapter = adapter
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                when (parent.getChildAdapterPosition(view)) {
                    list.size - 1 -> {
                        outRect.set(0, view.context.dp2px(8), 0, view.context.dp2px(80))
                    }
                    else -> {
                        outRect.set(0, view.context.dp2px(8), 0, view.context.dp2px(8))
                    }
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    private fun refresh() {
        adapter.notifyItemRangeRemoved(0, list.size)
        list.clear()
        list.addAll((ScheduleReader.db?.scheduleDao()?.all?.toMutableList() ?: mutableListOf()).apply { sortBy { !it.topping } })
        adapter.notifyItemRangeInserted(0, list.size)
    }


}