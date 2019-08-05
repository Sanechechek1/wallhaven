package com.netsoftware.wallpool.ui.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.EditText
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.data.dataSources.local.SharedPrefs
import com.netsoftware.wallpool.databinding.PickerResolutionBinding
import com.netsoftware.wallpool.utility.extensions.dpToPx
import com.netsoftware.wallpool.utility.managers.MyDisplayManager

class ResolutionPicker(
    context: Context,
    private val checkedResolutions: MutableList<String> = mutableListOf(),
    val pickResolutionAtLeast: Boolean
) : Dialog(context) {
    private val pickerBinding: PickerResolutionBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.picker_resolution, null, false)
    private var oldCustomResolution = ""

    init {
        setContentView(pickerBinding.root)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            MyDisplayManager.getCurrentDisplaySize(context).y - 50.dpToPx
        )
        pickerBinding.chipsSingleSelection = pickResolutionAtLeast
        pickerBinding.widthInput.editText?.setOnFocusChangeListener { _, b ->
            if (b && pickResolutionAtLeast) clearSelection()
        }
        pickerBinding.heightInput.editText?.setOnFocusChangeListener { _, b ->
            if (b && pickResolutionAtLeast) clearSelection()
        }
        pickerBinding.heightInput.editText?.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                editText.clearFocus()
            }
            false
        }

        pickerBinding.chipClick = CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val chipText = ((view as Chip).chipDrawable as ChipDrawable).text.toString()
            if (pickResolutionAtLeast && checkedResolutions.isNotEmpty()) {
                clearSelection()
                view.isChecked = isChecked
                if (!checkedResolutions.contains(chipText) && isChecked) checkedResolutions.add(chipText)
                clearCustomResolution()
            } else {
                if (isChecked) {
                    if (!checkedResolutions.contains(chipText)) checkedResolutions.add(chipText)
                    if (pickResolutionAtLeast) clearCustomResolution()
                } else {
                    checkedResolutions.remove(chipText)
                }
            }
        }

        pickerBinding.executePendingBindings()
        setPredefinedResolution(checkedResolutions)
    }

    private fun clearCustomResolution() {
        WallpoolApp.hideKeyboard(this.pickerBinding.root)
        pickerBinding.heightInput.editText?.let {
            clearEditText(it)
        }
        pickerBinding.widthInput.editText?.let {
            clearEditText(it)
        }
        checkedResolutions.remove(getCustomResolution())
    }

    private fun clearEditText(editText: EditText) {
        editText.text.clear()
        editText.clearFocus()
    }

    private fun getCustomResolution(): String {
        val width = pickerBinding.widthInput.editText?.text.toString()
        val height = pickerBinding.heightInput.editText?.text.toString()
        return if (width.isNotEmpty() && height.isNotEmpty())
            pickerBinding.widthInput.editText?.text.toString() + MyDisplayManager.delimiter +
                    pickerBinding.heightInput.editText?.text.toString()
        else ""
    }

    private fun setCustomResolution(resolution: String) {
        val resSplitted = resolution.split(MyDisplayManager.delimiter)
        pickerBinding.widthInput.editText?.setText(resSplitted[0])
        pickerBinding.heightInput.editText?.setText(resSplitted[1])
        oldCustomResolution = resolution
    }

    private fun setPredefinedResolution(checkedResolutions: MutableList<String>) {
        for (resolution in checkedResolutions) {
            var thisChecked = false
            val ultrawideIndex =
                context.resources.getStringArray(R.array.resolution_ultrawide_values).indexOf(resolution)
            val res16x9Index = context.resources.getStringArray(R.array.resolution_16x9_values).indexOf(resolution)
            val res16x10Index = context.resources.getStringArray(R.array.resolution_16x10_values).indexOf(resolution)
            if (ultrawideIndex >= 0) {
                (pickerBinding.resUltrawideChipgroup[ultrawideIndex] as Chip).isChecked = true
                thisChecked = true
            }
            if (res16x9Index >= 0) {
                (pickerBinding.res16x9Chipgroup[ultrawideIndex] as Chip).isChecked = true
                thisChecked = true
            }
            if (res16x10Index >= 0) {
                (pickerBinding.res16x10Chipgroup[ultrawideIndex] as Chip).isChecked = true
                thisChecked = true
            }
            if (SharedPrefs.getSharedPrefs().screenResolution == resolution) {
                pickerBinding.resMyChip.isChecked = true
                thisChecked = true
            }
            if (!thisChecked) setCustomResolution(resolution)
        }
    }

    private fun clearSelection() {
        checkedResolutions.clear()
        pickerBinding.resUltrawideChipgroup.clearCheck()
        pickerBinding.res16x10Chipgroup.clearCheck()
        pickerBinding.res16x9Chipgroup.clearCheck()
        pickerBinding.resMyChip.isChecked = false
    }

    fun setOnPositiveButtonClick(l: (View) -> Unit) {
        pickerBinding.applyButton.setOnClickListener { l(pickerBinding.applyButton) }
    }

    fun setOnNegativeButtonClick(l: (View) -> Unit) {
        pickerBinding.cancelButton.setOnClickListener { l(pickerBinding.cancelButton) }
    }

    fun getPickedResolution(): List<String> {
        if (getCustomResolution().isNotEmpty() && !checkedResolutions.contains(getCustomResolution())) {
            checkedResolutions.remove(oldCustomResolution)
            oldCustomResolution = ""
            checkedResolutions.add(getCustomResolution())
        }
        return checkedResolutions.sorted()
    }
}
