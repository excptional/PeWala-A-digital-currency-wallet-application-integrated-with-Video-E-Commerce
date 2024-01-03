package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.items.CartItems

class CartAdapter(
    val context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner,
    private val cartItems: ArrayList<CartItems>
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    val authViewModel = ViewModelProvider(viewModelStoreOwner)[AuthViewModel::class.java]
    val dbViewModel = ViewModelProvider(viewModelStoreOwner)[DBViewModel::class.java]
    private lateinit var newQuantity: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_items, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        newQuantity = currentItem.quantity.toString()
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.price.text = "₹${currentItem.price}"
        holder.quantity.text = "Quantity : ${currentItem.quantity}"
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)

        holder.setQuantityBox.setOnClickListener {
            authViewModel.userdata.observe(lifecycleOwner) { user ->
                if(user != null) {
                    showQuantityDialog(holder.quantity, currentItem.productId!!, user.uid)
                }
            }
        }

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        holder.close.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productName", currentItem.productName)
            bundle.putString("brandName", currentItem.brandName)
            bundle.putString("quantity", newQuantity)
            bundle.putString("productPrice", currentItem.price)
            bundle.putString("productImg", currentItem.productImageUrl)
            bundle.putString("description", currentItem.description)
            bundle.putString("productId", currentItem.productId)

            Navigation.findNavController(it).navigate(R.id.nav_remove_cart_items, bundle)
        }

        holder.item.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productId", currentItem.productId)
            bundle.putString("category", currentItem.category)
            Navigation.findNavController(it).navigate(R.id.nav_order_place, bundle, navBuilder.build())
        }

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCart(updateCartItems: ArrayList<CartItems>) {
        cartItems.clear()
        cartItems.addAll(updateCartItems)
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun showQuantityDialog(textView: TextView, productId: String, uid: String) {
        val dialog = Dialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.set_quantity_dialog)
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
            textView.text = "Quantity : " + lastText.text.toString()
            newQuantity = lastText.text.toString()
            dbViewModel.updateQuantityOfCart(productId, uid, lastText.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun onClick(
        currentView: RelativeLayout,
        pastView: RelativeLayout,
        currentText: TextView,
        pastText: TextView
    ) {
        pastView.setBackgroundResource(R.drawable.circle_background)
        pastText.setTextColor(R.color.material_black)

        currentView.setBackgroundResource(R.drawable.circle_background_material_black)
        currentText.setTextColor(Color.WHITE)
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_cart_items)
        val brandName: TextView = itemView.findViewById(R.id.brandName_cart_items)
        val quantity: TextView = itemView.findViewById(R.id.quantity_cart_items)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_cart_items)
        val item: CardView = itemView.findViewById(R.id.itemLayout_cart_items)
        val setQuantityBox: CardView = itemView.findViewById(R.id.set_quantity_box_cart_items)
        val price: TextView = itemView.findViewById(R.id.price_cart_items)
        val close: ImageButton = itemView.findViewById(R.id.close_btn_cart_items)
    }
}