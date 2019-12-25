package com.example.exampledemo.ui.camera

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exampledemo.databinding.ItemCountryBinding
import com.example.exampledemo.databinding.ItemHeaderCountryBinding
import com.example.exampledemo.model.Country

class CountryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val countries = ArrayList<Country>()
    private val alphabetList = ArrayList<String>()
    private val positions = ArrayList<Int>()


    fun getAlphabets(): ArrayList<String> {
        return alphabetList
    }

    fun getPositionOfAlphabet(alphabetIndex: Int): Int? {
        if (alphabetIndex < 0 || alphabetIndex >= alphabetList.size) return null
        if (alphabetIndex == 0) return 0
        return positions.indexOf(-alphabetIndex)
    }

    fun updateList(list: ArrayList<Country>) {
        countries.clear()
        alphabetList.clear()
        positions.clear()

        countries.addAll(list)
        var alphabet = ""

        for (i in 0 until list.size) {
            val country = list[i]
            val alpha = country.name.substring(0, 1)

            if (alpha != alphabet) {
                // new alphabet
                alphabet = alpha
                alphabetList.add(alpha)
                positions.add(1 - alphabetList.size)
                positions.add(i)
            } else {
                // increase alphabet
                positions.add(i)
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COUNTRY_VIEW_TYPE -> {
                val binding =
                    ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CountryViewHolder(binding)
            }

            else -> {
                val binding = ItemHeaderCountryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return positions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CountryViewHolder) {
            holder.bindTo(getCountry(position))
        }

        if (holder is HeaderViewHolder) {
            holder.bindTo(getAlphabet(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) HEADER_VIEW_TYPE else COUNTRY_VIEW_TYPE
    }

    private fun getCountry(position: Int): Country {
        return countries[positions[position]]
    }

    private fun getAlphabet(position: Int): String {
        return alphabetList[-positions[position]]
    }

    fun isHeader(position: Int): Boolean {
        return (position == 0 || positions[position] < 0)
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

    class HeaderViewHolder(val binding: ItemHeaderCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(name: String) {
            binding.tvHeader.text = name
        }
    }

    companion object {
        const val HEADER_VIEW_TYPE = 0
        const val COUNTRY_VIEW_TYPE = 1
    }
}