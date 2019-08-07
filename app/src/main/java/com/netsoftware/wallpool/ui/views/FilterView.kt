package com.netsoftware.wallpool.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.data.dataSources.local.SharedPrefs
import com.netsoftware.wallpool.data.models.SearchConfig
import com.netsoftware.wallpool.data.models.User.Companion.CATEGORY_ANIME
import com.netsoftware.wallpool.data.models.User.Companion.CATEGORY_GENERAL
import com.netsoftware.wallpool.data.models.User.Companion.CATEGORY_PEOPLE
import com.netsoftware.wallpool.data.models.User.Companion.PURITY_SFW
import com.netsoftware.wallpool.data.models.User.Companion.PURITY_SKETCHY
import com.netsoftware.wallpool.databinding.PartFilterBinding


class FilterView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: PartFilterBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.part_filter, this, true)
    var searchConfig = SearchConfig()
        set(value) {
            field = SearchConfig(value)
            setUpViews(field)
        }
    var defaultConfig = SearchConfig()
        set(value) {
            field = SearchConfig(value)
            setUpViews(field)
        }

    init {
        context?.resources?.getStringArray(R.array.ratio_values).let {
            it?.indexOf(SharedPrefs.getSharedPrefs().screenRatio)
        }
        binding.categorySwitch.setOnSwitchListener { _, _ ->
            searchConfig.categories = SearchConfig.getCategoriesCode(
                binding.categorySwitch.selectedTabs.contains(0),
                binding.categorySwitch.selectedTabs.contains(1),
                binding.categorySwitch.selectedTabs.contains(2)
            )
        }
        binding.puritySwitch.setOnSwitchListener { _, _ ->
            searchConfig.purity = SearchConfig.getPurityCode(
                binding.puritySwitch.selectedTabs.contains(0),
                binding.puritySwitch.selectedTabs.contains(1)
            )
        }

        binding.resolutionValueBtn.setOnClickListener { showResolutionPicker() }
        binding.resolutionSwitch.setOnSwitchListener { position, _ ->
            if (position == 0 && searchConfig.resolutions.isNotEmpty()) {
                searchConfig.resolution_at_least = searchConfig.resolutions.split(",").first()
                searchConfig.resolutions = ""
                binding.resolutionValueBtn.text = searchConfig.resolution_at_least
            } else if (position == 1 && searchConfig.resolution_at_least.isNotEmpty()) {
                searchConfig.resolutions = searchConfig.resolution_at_least
                searchConfig.resolution_at_least = ""
            }
        }

        binding.ratioChipClick = CompoundButton.OnCheckedChangeListener { view, b ->
            var chipText = ((view as Chip).chipDrawable as ChipDrawable).text.toString()
            SharedPrefs.getSharedPrefs().screenRatio.let {
                if (chipText.contains(it))
                    chipText = it
            }
            searchConfig.ratios =
                searchConfig.getRatioList().let {
                    if (b && !searchConfig.getRatioList().contains(chipText)) {
                        it.add(chipText)
                    } else if (!b && searchConfig.getRatioList().contains(chipText)) {
                        it.remove(chipText)
                    }
                    SearchConfig.listToProperty(it)
                }
        }

        binding.colorChipgroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                group.findViewById<Chip>(checkedId).tag.toString().let {
                    searchConfig.colors = it.removePrefix("#")
                }
            }
        }

        binding.defaultButton.setOnClickListener {
            defaultClick()
        }
        binding.executePendingBindings()
    }

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private fun showResolutionPicker() {
        context?.let {
            val resolutionPicker = ResolutionPicker(
                it,
                searchConfig.getResolutionList(),
                binding.resolutionSwitch.getState(0)
            )
            resolutionPicker.setOnPositiveButtonClick {
                binding.resolutionValueBtn.text =
                    SearchConfig.getReadableResolution(resolutionPicker.getPickedResolution()).ifEmpty {
                        context.getString(R.string.select)
                    }
                val pickedRes = SearchConfig.listToProperty(resolutionPicker.getPickedResolution())
                if (resolutionPicker.pickResolutionAtLeast) searchConfig.resolution_at_least = pickedRes
                else searchConfig.resolutions = pickedRes
                resolutionPicker.dismiss()
            }
            resolutionPicker.setOnNegativeButtonClick {
                resolutionPicker.dismiss()
            }
            resolutionPicker.show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun lockResolutionPicker() {
        binding.resolutionValueBtn.isEnabled = false
        binding.resolutionSwitch.setOnTouchListener { _, _ -> true }
        binding.resolutionSwitch.alpha = 0.7F
        binding.resolutionValueBtn.alpha = 0.6F
        if (SharedPrefs.getSharedPrefs().suitableRatioOn) {
            binding.ratioChipgroup.alpha = 0.7F
            for (chip in binding.ratioChipgroup.children) {
                chip.setOnTouchListener { _, _ -> true }
            }
        }
    }

    private fun defaultClick() {
        searchConfig = SearchConfig(defaultConfig)
        setUpViews(defaultConfig)
    }

    private fun setUpViews(searchConfig: SearchConfig) {
        setCategories(searchConfig.getCategoriesMap())
        setPurities(searchConfig.getPurityMap())
        setResolution(searchConfig)
        setRatio(searchConfig.getRatioList())
        setColor(searchConfig.colors)
    }

    private fun setCategories(categories: Map<String, Boolean>) {
        categories.onEach {
            when (it.key) {
                CATEGORY_GENERAL ->
                    if (it.value) {
                        binding.categorySwitch.setSelectedTab(0)
                    } else {
                        binding.categorySwitch.unselectTab(0)
                    }
                CATEGORY_ANIME ->
                    if (it.value) {
                        binding.categorySwitch.setSelectedTab(1)
                    } else {
                        binding.categorySwitch.unselectTab(1)
                    }
                CATEGORY_PEOPLE ->
                    if (it.value) {
                        binding.categorySwitch.setSelectedTab(2)
                    } else {
                        binding.categorySwitch.unselectTab(2)
                    }
            }
        }
    }

    private fun setPurities(purities: Map<String, Boolean>) {
        purities.onEach {
            when (it.key) {
                PURITY_SFW ->
                    if (it.value) {
                        binding.puritySwitch.setSelectedTab(0)
                    } else {
                        binding.puritySwitch.unselectTab(0)
                    }
                PURITY_SKETCHY ->
                    if (it.value) {
                        binding.puritySwitch.setSelectedTab(1)
                    } else {
                        binding.puritySwitch.unselectTab(1)
                    }
//                PURITY_NSFW ->
//                    if (it.value) {
//                        binding.puritySwitch.setSelectedTab(2)
//                    } else {
//                        binding.puritySwitch.unselectTab(2)
//                    }
            }
        }
    }

    private fun setResolution(config: SearchConfig) {
        when {
            config.resolution_at_least.isNotEmpty() -> binding.resolutionSwitch.setSelectedTab(0)
            config.resolutions.isNotEmpty() -> binding.resolutionSwitch.setSelectedTab(1)
            else -> binding.resolutionSwitch.selectedTabs.forEach { binding.resolutionSwitch.unselectTab(it) }
        }
        binding.resolutionValueBtn.text =
            SearchConfig.getReadableResolution(config.getResolutionList()).ifEmpty {
                context.getString(R.string.select)
            }
    }

    private fun setRatio(ratios: List<String>) {
        binding.ratioChipgroup.clearCheck()
        context.resources.getStringArray(R.array.ratio_values).let { ratioArray ->
            ratios.forEach { ratio ->
                ratioArray.indexOf(ratio).let { index ->
                    if (index >= 0) (binding.ratioChipgroup[index] as Chip).isChecked = true
                }
            }
        }
    }

    private fun setColor(color: String) {
        context.resources.getStringArray(R.array.color_values).indexOf("#$color").let {
            if (it >= 0) (binding.colorChipgroup[it] as Chip).isChecked = true
            else binding.colorChipgroup.clearCheck()
        }
    }

    fun setOnPositiveButtonClick(l: (View) -> Unit) {
        binding.applyButton.setOnClickListener { l(binding.applyButton) }
    }

    fun setOnNegativeButtonClick(l: (View) -> Unit) {
        binding.cancelButton.setOnClickListener { l(binding.cancelButton) }
    }

    fun onPositiveButtonClick() {
        binding.applyButton.callOnClick()
    }

    fun onNegativeButtonClick() {
        binding.cancelButton.callOnClick()
    }

    fun hasChanges(): Boolean = defaultConfig.copy(q="") != searchConfig.copy(q="")
}