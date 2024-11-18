package com.te.celer.main_files.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.te.celer.R
import com.te.celer.main_files.adapters.CommunityAdapter

class AddCommunityDialog(private val context: Context) {

    fun showDialog(items: List<String>) {
        val dialog = Dialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.dialog_add_community)
        dialog.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.anim.slide_up

        val recyclerView: RecyclerView = dialog.findViewById(R.id.recyclerview_add_community_dialog)

        val adapter = CommunityAdapter(items, 2)
        recyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(context, 5) // Default to 2 columns
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Adjust span size based on item length
                val itemLength = items[position].length
                return when {
                    itemLength <= 10 -> 1 // Small items take 1 span
                    itemLength <= 20 -> 2 // Medium items take 2 spans
                    else -> 3 // Large items take 2 spans (full width of the grid)
                }
            }
        }

        recyclerView.layoutManager = layoutManager

        dialog.show()

    }

}