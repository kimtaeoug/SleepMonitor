package ted.gun0912.sleep.ui.base.adapter

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<D, VH : BaseViewHolder<out ViewDataBinding, out D>> :
    RecyclerView.Adapter<VH>() {

    private var _items: MutableList<D> = mutableListOf()
    val items: List<D> get() = _items
    lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun getItem(index: Int): D = items[index]

    open fun resetAll(items: List<D>) {
        this._items.clear()
        this._items.addAll(items)
        notifyDataSetChanged()
    }

    protected fun resetAll(items: List<D>, diffCallback: DiffUtil.Callback) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager == null) {
            resetAll(items)
            return
        }
        val savedInstanceState = layoutManager.onSaveInstanceState()
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this._items.clear()
        this._items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
        layoutManager.onRestoreInstanceState(savedInstanceState)
    }

    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = getViewHolder(parent, viewType)
        return holder
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindView(holder, getItem(position), position)
    }

    protected open fun onBindView(holder: VH, item: D, position: Int) {
        (holder as? BaseViewHolder<*, D>)?.setData(item)
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.recycled()
    }

    override fun getItemCount(): Int = items.size

}
