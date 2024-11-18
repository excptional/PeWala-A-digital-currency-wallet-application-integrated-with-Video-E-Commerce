package com.te.celer.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.dialog.SetQuantityDialog
import com.te.celer.main_files.models.CartItems

class CartAdapter(
    val context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val cartItems: ArrayList<CartItems>
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    val dbViewModel = ViewModelProvider(viewModelStoreOwner)[DBViewModel::class.java]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        val newQuantity = currentItem.quantity.toString()
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.price.text = "₹${Integer.parseInt(currentItem.price!!) * Integer.parseInt(newQuantity)}"
        holder.quantity.text = "Quantity : ${currentItem.quantity}"
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)

        holder.checkBox.isClickable = false

        if(currentItem.isChecked!!) {
            holder.checkBox.isChecked = true
            holder.checkText.text = "Selected"
        } else {
            holder.checkBox.isChecked = false
            holder.checkText.text = "Select"
        }

        val setQuantityDialog = SetQuantityDialog(context, dbViewModel, newQuantity)

        holder.setQuantityBox.setOnClickListener {
            setQuantityDialog.showQuantityDialog(currentItem.productId!!, currentItem.buyerUID!!)
            println(setQuantityDialog.newQuantity)
            holder.quantity.text = setQuantityDialog.newQuantity
        }

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        holder.removeBtn.setOnClickListener {
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

        holder.selectBtn.setOnClickListener {
            if(currentItem.isChecked!!) {
                holder.checkBox.isChecked = false
                holder.checkText.text = "Select"
            } else {
                holder.checkBox.isChecked = true
                holder.checkText.text = "Selected"
            }
            dbViewModel.updateSelectOptionCart(currentItem.productId!!, currentItem.buyerUID!!)
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

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_cart_items)
        val brandName: TextView = itemView.findViewById(R.id.brandName_cart_items)
        val quantity: TextView = itemView.findViewById(R.id.quantity_cart_items)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_cart_items)
        val item: LinearLayout = itemView.findViewById(R.id.itemLayout_cart_items)
        val setQuantityBox: LinearLayout = itemView.findViewById(R.id.set_quantity_box_cart_items)
        val price: TextView = itemView.findViewById(R.id.price_cart_items)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox_cart_items)
        val checkText: TextView = itemView.findViewById(R.id.checkText_cart_items)
        val removeBtn: LinearLayout = itemView.findViewById(R.id.remove_layout_cart_items)
        val selectBtn: LinearLayout = itemView.findViewById(R.id.select_layout_cart_items)
    }
}