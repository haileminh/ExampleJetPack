package com.example.exampledemo.ui.demo.popupwindow

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exampledemo.databinding.ItemCountryBinding
import com.example.exampledemo.model.Country

class PopupWindowAdapter(private val countries: ArrayList<Country>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CountryViewHolder) {
            holder.bindTo(countries[position])
        }
    }

    class CountryViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(country: Country) {
            binding.tvCountryName.text = country.name
            binding.tvCountryName.setOnClickListener {
                Toast.makeText(it.context, country.name, Toast.LENGTH_SHORT).show()
            }
        }
    }
}