package com.netsoftware.wallpool.ui.adapters

import android.view.View
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.ui.adapters.ProgressItem.StatusEnum.*
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.Payload
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class ProgressItem : AbstractFlexibleItem<ProgressItem.ProgressViewHolder>() {
    private var status = MORE_TO_LOAD

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
        ProgressViewHolder(view, adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>, holder: ProgressViewHolder,
        position: Int, payloads: List<*>
    ) {
        if (!adapter.isEndlessScrollEnabled) {
            setStatus(DISABLE_ENDLESS)
        } else if (payloads.contains(Payload.NO_MORE_LOAD)) {
            setStatus(NO_MORE_LOAD)
        }

        if (status == NO_MORE_LOAD || status == ON_CANCEL || status == ON_ERROR) setStatus(MORE_TO_LOAD)
    }

    class ProgressViewHolder(view: View, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(view, adapter)

    enum class StatusEnum {
        MORE_TO_LOAD, //Default = should have an empty Payload
        DISABLE_ENDLESS, //Endless is disabled because user has set limits
        NO_MORE_LOAD, //Non-empty Payload = Payload.NO_MORE_LOAD
        ON_CANCEL,
        ON_ERROR
    }
}
