package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.trigeredgedigitalcurrencyproject.R

class ShopBuyer : Fragment() {

    private lateinit var orders: CardView
    private lateinit var groceries: LinearLayout
    private lateinit var fashion: LinearLayout
    private lateinit var electronics: LinearLayout
    private lateinit var appliances: LinearLayout
    private lateinit var sports: LinearLayout
    private lateinit var furniture: LinearLayout
    private lateinit var books: LinearLayout
    private lateinit var personalcare: LinearLayout
    private lateinit var medicines: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_buyer, container, false)

        orders = view.findViewById(R.id.orders_buyer_shop)
        groceries = view.findViewById(R.id.groceries)
        fashion = view.findViewById(R.id.fashion)
        electronics = view.findViewById(R.id.electronics)
        appliances = view.findViewById(R.id.appliances)
        sports = view.findViewById(R.id.sports)
        furniture = view.findViewById(R.id.furniture)
        books = view.findViewById(R.id.books)
        personalcare = view.findViewById(R.id.personal_care)
        medicines = view.findViewById(R.id.medicines)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

//        addProduct.setOnClickListener {
//            Navigation.findNavController(view)
//                .navigate(R.id.nav_add_product, null, navBuilder.build())
//        }

        orders.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_orders, null, navBuilder.build())
        }

        groceries.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Groceries")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        fashion.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Fashion")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        electronics.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Electronics")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        appliances.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Appliances")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        sports.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Sports")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        furniture.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Furniture")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        books.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Books")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        personalcare.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Personal Care")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        medicines.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Medicines")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        return view
    }

}