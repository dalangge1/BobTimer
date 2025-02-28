package com.sandyz.module_community.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sandyz.alltimers.common.config.Config
import com.sandyz.alltimers.common.utils.TimeUtil
import com.sandyz.api_community.bean.CommentItem
import com.sandyz.api_community.bean.findEquals
import com.sandyz.module_community.R
import com.sandyz.module_community.view.costom.LikeViewSlim


class TalkCommentRecyclerViewAdapter(
    private var mContext: Context,
    private val onItemClick: (CommentItem, View) -> Unit,
    private val onInnerItemClick: (CommentItem, View) -> Unit,
    private val onItemLongClick: (CommentItem, View) -> Unit,
    private val onInnerItemLongClick: (CommentItem, View) -> Unit

) :
    RecyclerView.Adapter<TalkCommentRecyclerViewAdapter.ViewHolder>() {

    private var mLayoutInflater = LayoutInflater.from(mContext)
    private var mList: ArrayList<CommentItem> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        return if (position == mList.size) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ViewHolder {

        when (viewType) {
            0 -> return ViewHolder(
                mLayoutInflater.inflate(
                    R.layout.item_talk_small,
                    container,
                    false
                )
            )
        }
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_talk_small, container, false))
    }

    override fun getItemCount(): Int {

        return mList.size

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == mList.size) {
            return
        }

        val isPraise = mList[position].praise.findEquals { it.userId == Config.userId }
        holder.imageViewLikeView?.registerLikeView(
            mList[position].id,
            1,
            isPraise,
            mList[position].praise.size
        )


        holder.textViewUsrName?.text = mList[position].nickname
        holder.textViewContent?.text = mList[position].text



        holder.textViewTime?.text = TimeUtil.getChatTimeStr(mList[position].submitTime)
        // TODO 图片显示





        holder.imageViewUsrPic?.setOnClickListener {
//            val navController = Navigation.findNavController(holder.itemView)
//            val bundle = Bundle()
//            bundle.putBoolean("isMine", false)
//            bundle.putString("userId", mList[position].userId)
//            navController.navigate(R.id.action_global_fragmentIndividual, bundle)
        }

        holder.itemView.setOnClickListener {
            onItemClick.invoke(mList[position], it)

        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick.invoke(mList[position], it)
            true
        }

        if (holder.rvCommentInner?.layoutManager == null) {
            holder.rvCommentInner?.layoutManager = LinearLayoutManager(mContext)
        }
        if (holder.rvCommentInner?.adapter == null) {
            holder.rvCommentInner?.adapter = TalkReplyRecyclerViewAdapter(mContext, onInnerItemClick, onInnerItemLongClick)
        }
        (holder.rvCommentInner?.adapter as TalkReplyRecyclerViewAdapter).setList(mList[position].replyList)




    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUsrName: TextView? = itemView.findViewById(R.id.textViewUsrName)//
        val textViewTime: TextView? = itemView.findViewById(R.id.textViewTime)//
        val textViewContent: TextView? = itemView.findViewById(R.id.textViewContent)//
        val imageViewUsrPic: ImageView? = itemView.findViewById(R.id.imageViewUsrPic)//
        val imageViewLikeView: LikeViewSlim? =
            itemView.findViewById(R.id.imageViewLike)
        val rvCommentInner: RecyclerView? = itemView.findViewById(R.id.rv_comment_inner)

    }

    fun setList(list: List<CommentItem>) {
        mList.clear()
        mList.addAll(list)
//        notifyItemRangeChanged(0, mList.size)
        notifyDataSetChanged()
    }


}