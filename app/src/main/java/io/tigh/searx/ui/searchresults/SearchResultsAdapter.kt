package io.tigh.searx.ui.searchresults

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.tigh.searx.R
import io.tigh.searx.api.SearxngResultItem

class SearchResultsAdapter(private val mList: List<SearxngResultItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnTouchListener {
        fun onTouch(uri: Uri): Boolean
    }

    private lateinit var mOnTouchListener: OnTouchListener

    fun setOnTouchListener(listener: OnTouchListener) {
        this.mOnTouchListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            0 -> {
                val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_search_result_item, parent, false)
                return ViewHolder(v)
            }
            1 -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_search_result_item_padding, parent, false)
                return Separator(v)
            }

        }
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_search_result_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size * 2 - 1
    }

    override fun getItemViewType(position: Int): Int {
        if(position % 2 == 0) {
            return 0
        }
        return 1
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        if (h.itemViewType == 1) {
            return
        }
        val holder = h as ViewHolder

        val pos = position / 2
        val item = mList[pos]

        holder.title.text = item.title
        holder.content.text = item.content

        val host = item.parsedURL[0] + "://" + item.parsedURL[1]
        val breadcrumbParts = arrayListOf<String>()
        with(breadcrumbParts) {
            add(host)
            addAll(item.parsedURL[2].split("/").filter { s -> s != "" })
        }

        holder.breadcrumb.text = breadcrumbParts.joinToString(
            separator = " > ",
        )
        holder.engines.text = item.engines.joinToString(
            separator = "  ",
        )

        holder.cached.setOnTouchListener { v, event ->
            val uri = Uri.parse("https://web.archive.org/web/" + item.url)
            mOnTouchListener.onTouch(uri)
        }

        holder.touchZone.setOnTouchListener { v, event ->
            val uri = Uri.parse(item.url)
            mOnTouchListener.onTouch(uri)
        }
    }


    class ViewHolder(iv: View): RecyclerView.ViewHolder(iv) {
        val touchZone: ConstraintLayout = iv.findViewById(R.id.clv_search_result_touch_area)
        val title: TextView = iv.findViewById(R.id.tv_result_title)
        val breadcrumb: TextView = iv.findViewById(R.id.tv_result_url_breadcrumb)
        val content: TextView = iv.findViewById(R.id.tv_content)
        val cached: TextView = iv.findViewById(R.id.tv_result_cache)
        val engines: TextView = iv.findViewById(R.id.tv_engines)
    }

    class Separator(iv: View): RecyclerView.ViewHolder(iv) {
    }
}