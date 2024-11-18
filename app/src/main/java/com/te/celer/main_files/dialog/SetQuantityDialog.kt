package com.te.celer.main_files.dialog

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.te.celer.R
import com.te.celer.db.DBViewModel

class SetQuantityDialog(val context: Context, val dbViewModel: DBViewModel?, val quantity: String){

    var newQuantity: String = "Quantity : $quantity"

    fun showQuantityDialog(productId: String, uid: String): Int {
        val dialog = Dialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.dialog_set_quantity)
        dialog.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.anim.slide_up

        val closeBtn: ImageView = dialog.findViewById(R.id.close_btn_quantity_dialog)
        val doneBtn: RelativeLayout = dialog.findViewById(R.id.done_btn_quantity_dialog)
        val view1: RelativeLayout = dialog.findViewById(R.id.view_1)
        val view2: RelativeLayout = dialog.findViewById(R.id.view_2)
        val view3: RelativeLayout = dialog.findViewById(R.id.view_3)
        val view4: RelativeLayout = dialog.findViewById(R.id.view_4)
        val view5: RelativeLayout = dialog.findViewById(R.id.view_5)
        val view6: RelativeLayout = dialog.findViewById(R.id.view_6)
        val view7: RelativeLayout = dialog.findViewById(R.id.view_7)
        val view8: RelativeLayout = dialog.findViewById(R.id.view_8)
        val view9: RelativeLayout = dialog.findViewById(R.id.view_9)
        val view10: RelativeLayout = dialog.findViewById(R.id.view_10)
        val text1: TextView = dialog.findViewById(R.id.text_1)
        val text2: TextView = dialog.findViewById(R.id.text_2)
        val text3: TextView = dialog.findViewById(R.id.text_3)
        val text4: TextView = dialog.findViewById(R.id.text_4)
        val text5: TextView = dialog.findViewById(R.id.text_5)
        val text6: TextView = dialog.findViewById(R.id.text_6)
        val text7: TextView = dialog.findViewById(R.id.text_7)
        val text8: TextView = dialog.findViewById(R.id.text_8)
        val text9: TextView = dialog.findViewById(R.id.text_9)
        val text10: TextView = dialog.findViewById(R.id.text_10)

        var lastView: RelativeLayout = view1
        var lastText: TextView = text1

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        view1.setOnClickListener {
            onClick(view1, lastView, text1, lastText)
            lastView = view1
            lastText = text1
        }

        view2.setOnClickListener {
            onClick(view2, lastView, text2, lastText)
            lastView = view2
            lastText = text2
        }

        view3.setOnClickListener {
            onClick(view3, lastView, text3, lastText)
            lastView = view3
            lastText = text3
        }

        view4.setOnClickListener {
            onClick(view4, lastView, text4, lastText)
            lastView = view4
            lastText = text4
        }

        view5.setOnClickListener {
            onClick(view5, lastView, text5, lastText)
            lastView = view5
            lastText = text5
        }

        view6.setOnClickListener {
            onClick(view6, lastView, text6, lastText)
            lastView = view6
            lastText = text6
        }

        view7.setOnClickListener {
            onClick(view7, lastView, text7, lastText)
            lastView = view7
            lastText = text7
        }

        view8.setOnClickListener {
            onClick(view8, lastView, text8, lastText)
            lastView = view8
            lastText = text8
        }

        view9.setOnClickListener {
            onClick(view9, lastView, text9, lastText)
            lastView = view9
            lastText = text9
        }

        view10.setOnClickListener {
            onClick(view10, lastView, text10, lastText)
            lastView = view10
            lastText = text10
        }

        doneBtn.setOnClickListener {
            if(dbViewModel != null)
                dbViewModel.updateQuantityOfCart(productId, uid, lastText.text.toString())
            dialog.dismiss()
            newQuantity = lastText.text.toString()
        }
        dialog.show()
        return newQuantity.substring(11).toInt()
    }

    @SuppressLint("ResourceAsColor")
    private fun onClick(
        currentView: RelativeLayout,
        pastView: RelativeLayout,
        currentText: TextView,
        pastText: TextView
    ) {
        pastView.setBackgroundResource(R.drawable.circle_background)
        pastText.setTextColor(R.color.black)

        currentView.setBackgroundResource(R.drawable.circle_background_material_black)
        currentText.setTextColor(Color.WHITE)
    }

}