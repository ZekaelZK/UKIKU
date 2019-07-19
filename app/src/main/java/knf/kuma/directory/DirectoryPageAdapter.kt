package knf.kuma.directory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import knf.kuma.R
import knf.kuma.animeinfo.ActivityAnime
import knf.kuma.commons.PatternUtil
import knf.kuma.commons.PrefsUtil
import knf.kuma.commons.bind
import knf.kuma.commons.load

class DirectoryPageAdapter internal constructor(private val fragment: Fragment) : PagedListAdapter<DirObject, DirectoryPageAdapter.ItemHolder>(DIFF_CALLBACK), FastScrollRecyclerView.SectionedAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context).inflate(getLayType(), parent, false))
    }

    @LayoutRes
    private fun getLayType(): Int {
        return if (PrefsUtil.layType == "0") {
            R.layout.item_dir
        } else {
            R.layout.item_dir_grid
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (fragment.context == null) return
        val animeObject = getItem(position)
        if (animeObject?.aid != null) {
            holder.imageView.load(PatternUtil.getCover(animeObject.aid))
            holder.progressView.visibility = View.GONE
            holder.textView.text = animeObject.name
            holder.cardView.setOnClickListener { ActivityAnime.open(fragment, animeObject, holder.imageView, persist = false) }
        } else {
            holder.progressView.visibility = View.VISIBLE
            holder.textView.text = null
        }
    }

    override fun getSectionName(position: Int): String {
        return when (PrefsUtil.dirOrder) {
            1 -> "\u2605${getItem(position)?.rate_stars ?: "?.?"}"
            2 -> getItem(position)?.aid ?: ""
            3 -> getItem(position)?.aid ?: ""
            else -> getItem(position)?.name?.first()?.toUpperCase()?.toString() ?: ""
        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView by itemView.bind(R.id.card)
        val imageView: ImageView by itemView.bind(R.id.img)
        val progressView: ProgressBar by itemView.bind(R.id.progress)
        val textView: TextView by itemView.bind(R.id.title)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DirObject>() {
            override fun areItemsTheSame(oldItem: DirObject, newItem: DirObject): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: DirObject, newItem: DirObject): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}
