package com.hjzf.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hjzf.NewsApplication
import com.hjzf.R
import com.hjzf.logic.model.News
import com.hjzf.ui.detail.DetailActivity
import com.hjzf.ui.news.NewsViewModel.Companion.ERROR_STATUS
import com.hjzf.ui.news.NewsViewModel.Companion.FINISHED_STATUS
import com.hjzf.ui.news.NewsViewModel.Companion.LOADING_STATUS

class NewsAdapter(private val viewModel: NewsViewModel) :
    ListAdapter<News, NewsAdapter.BaseViewHolder>(DiffCallback) {
    companion object {
        // 三种不同的列表项:两种新闻列表项 + 底部列表项 footer_view
        const val ONE_IMAGE_VIEW_TYPE = 1
        const val THREE_IMAGES_VIEW_TYPE = 3
        const val FOOTER_VIEW_TYPE = -1
    }

    /**
     * footer_view所处的状态。
     * onBindViewHolder函数会根据footerViewStatus的不同取值绘制不同的UI
     */
    var footerViewStatus: Int = LOADING_STATUS

    // super.getItemCount() 只是 list.size , 要加上 footer_view
    override fun getItemCount(): Int = super.getItemCount() + 1

    // 判断第position条新闻应该用哪一种列表项展示，返回 viewType
    override fun getItemViewType(position: Int): Int {
        // 最后一个列表项放 footer_view
        if (position == itemCount - 1) return FOOTER_VIEW_TYPE
        val news = currentList[position]
        if (news.thumbnail_pic_s02 == null
            || news.thumbnail_pic_s02 == ""
            || news.thumbnail_pic_s02 == "null"
            || news.thumbnail_pic_s03 == null
            || news.thumbnail_pic_s03 == ""
            || news.thumbnail_pic_s03 == "null"
        ) {
            return ONE_IMAGE_VIEW_TYPE
        }
        return THREE_IMAGES_VIEW_TYPE
    }

    // 根据不同的viewType加载不同的列表项布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            THREE_IMAGES_VIEW_TYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item_three_images, parent, false)
                return ThreeImagesViewHolder(itemView)
            }
            ONE_IMAGE_VIEW_TYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item_one_image, parent, false)
                return OneImageViewHolder(itemView)
            }
            else -> {
                // 其它情况只会是 FOOTER_VIEW_TYPE
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.footer_view, parent, false)
                return FooterViewHolder(itemView)
            }
        }
    }

    // 将数据展示在列表项中,即绘制列表项的 UI
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val context = NewsApplication.context
        if (holder is NewsViewHolder) {
            val news = currentList[position]
            // (1)显示新闻标题
            holder.title.text = news.title
            // (2)显示新闻描述:用 author_name和 date两个字段拼接出来
            holder.description.text =
                context.getString(R.string.news_desc, news.author_name, news.date)
            // (3)加载新闻图片
            when (holder) {
                is OneImageViewHolder -> {
                    Glide.with(context).load(news.thumbnail_pic_s).into(holder.image)
                }
                is ThreeImagesViewHolder -> {
                    Glide.with(context).load(news.thumbnail_pic_s).into(holder.image1)
                    Glide.with(context).load(news.thumbnail_pic_s02).into(holder.image2)
                    Glide.with(context).load(news.thumbnail_pic_s03).into(holder.image3)
                }
            }
            // (4)列表项点击事件
            holder.itemView.setOnClickListener {
                // holder.adapterPosition 是什么? 当前点击的新闻在newsList中的下标
                val currentNews = currentList[holder.absoluteAdapterPosition]
                DetailActivity.actionStart(
                    context,
                    currentNews.url,
                    currentNews.author_name
                )
            }
        } else {
            // 其它情况只会是 FooterViewHolder
            val footerViewHolder = holder as FooterViewHolder
            when (footerViewStatus) {
                LOADING_STATUS -> {
                    footerViewHolder.processBar.visibility = View.VISIBLE
                    footerViewHolder.message.text = "正在加载中..."
                }
                ERROR_STATUS -> {
                    footerViewHolder.processBar.visibility = View.GONE
                    footerViewHolder.message.text = "加载失败,点击重新加载"
                }
                FINISHED_STATUS -> {
                    footerViewHolder.processBar.visibility = View.GONE
                    footerViewHolder.message.text = "已经没有更多内容了"
                }
            }
            footerViewHolder.itemView.setOnClickListener {
                footerViewHolder.processBar.visibility = View.VISIBLE
                footerViewHolder.message.text = "正在加载中..."
                viewModel.loadCacheData()
            }
        }
    }

    // 列表项基类
    open inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 新闻列表项基类(无论是哪种类型的新闻列表项，都有标题和描述)
    open inner class NewsViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val description: TextView = itemView.findViewById(R.id.news_desc)
    }

    // footer_view也是一个列表项
    open inner class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val processBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val message: TextView = itemView.findViewById(R.id.message)
    }

    // 只有一张图片的新闻列表项
    inner class OneImageViewHolder(itemView: View) : NewsViewHolder(itemView) {
        val image: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image)
    }

    // 有三张图片的新闻列表项
    inner class ThreeImagesViewHolder(itemView: View) : NewsViewHolder(itemView) {
        val image1: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_1)
        val image2: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_2)
        val image3: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_3)
    }

    object DiffCallback : DiffUtil.ItemCallback<News>() {
        // 假设新闻标题各不重复,只要标题不同，就认定为不同的新闻
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.url == newItem.url
        }
    }
}