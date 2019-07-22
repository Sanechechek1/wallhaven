package com.netsoftware.wallhaven.ui.views

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.databinding.SetWallDialogBinding
import com.netsoftware.wallhaven.utility.managers.WallsUtil

class SetWallDialog(context: Context, private val wallUri: Uri) : Dialog(context, R.style.MyDialog) {
    private val binding: SetWallDialogBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.set_wall_dialog, null, false)

    init {
        setContentView(binding.root)
//        binding.bothBtn.setOnClickListener {
//            try {
//                wallUtil.setBothWall(bitmap)
//                Snackbar.make(
//                    binding.root, context.getString(R.string.both_wall_changed),
//                    Snackbar.LENGTH_SHORT
//                ).changeColors().show()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            dismiss()
//        }
    }

    fun setBtnsClicks(call: (wallUri: Uri, type: WallsUtil.SetWallType) -> Unit): SetWallDialog {
        binding.homeBtn.setOnClickListener { call(wallUri, WallsUtil.SetWallType.HOME); dismiss() }
        binding.lockBtn.setOnClickListener { call(wallUri, WallsUtil.SetWallType.LOCK); dismiss() }
        binding.bothBtn.setOnClickListener { call(wallUri, WallsUtil.SetWallType.BOTH); dismiss() }
        return this
    }
}