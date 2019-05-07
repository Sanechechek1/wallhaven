package com.netsoftware.wallhaven.ui.adapters

import android.animation.Animator
import android.view.View
import android.widget.ProgressBar
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.databinding.RvItemLoaderBinding
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.Payload
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class ProgressItem : AbstractFlexibleItem<ProgressItem.ProgressViewHolder>() {
    private var status = StatusEnum.MORE_TO_LOAD

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return 42
    }

    fun getStatus(): StatusEnum {
        return status
    }

    private fun setStatus(status: StatusEnum) {
        this.status = status
    }

    override fun getLayoutRes(): Int {
        return R.layout.rv_item_loader
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ProgressViewHolder =
        ProgressViewHolder(RvItemLoaderBinding.bind(view), adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>, holder: ProgressViewHolder,
        position: Int, payloads: List<*>
    ) {
        if (!adapter.isEndlessScrollEnabled) {
            setStatus(StatusEnum.DISABLE_ENDLESS)
        } else if (payloads.contains(Payload.NO_MORE_LOAD)) {
            setStatus(StatusEnum.NO_MORE_LOAD)
        }

        when (this.status) {
            StatusEnum.NO_MORE_LOAD, StatusEnum.ON_CANCEL, StatusEnum.ON_ERROR -> {
                setStatus(StatusEnum.MORE_TO_LOAD)
                holder.progressBar.visibility = View.GONE
            }
            StatusEnum.DISABLE_ENDLESS -> {
                holder.progressBar.visibility = View.GONE
            }
            StatusEnum.MORE_TO_LOAD -> holder.progressBar.visibility = View.VISIBLE
        }
    }

    class ProgressViewHolder(binding: RvItemLoaderBinding, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(binding.root, adapter) {
        var progressBar: ProgressBar = binding.progressBar

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            AnimatorHelper.scaleAnimator(animators, itemView, 0f)
        }
    }

    enum class StatusEnum {
        MORE_TO_LOAD, //Default = should have an empty Payload
        DISABLE_ENDLESS, //Endless is disabled because user has set limits
        NO_MORE_LOAD, //Non-empty Payload = Payload.NO_MORE_LOAD
        ON_CANCEL,
        ON_ERROR
    }
}
