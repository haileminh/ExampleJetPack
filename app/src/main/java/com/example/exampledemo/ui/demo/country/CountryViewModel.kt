package com.example.exampledemo.ui.demo.country

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseViewModel
import com.example.exampledemo.extensions.readRawFile
import com.example.exampledemo.model.Country
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray

class CountryViewModel : BaseViewModel() {
    val countriesLiveData = MutableLiveData<ArrayList<Country>>()

    @SuppressLint("CheckResult")
    fun loadData(ctx: Context) {
        val singleCountries = Single.create<ArrayList<Country>> {
            val list = ArrayList<Country>()
            val data = ctx.readRawFile(R.raw.countries)

            val jsonCountry = JSONArray(data)
            for (i in 0 until jsonCountry.length()) {
                val jCountry = jsonCountry.getJSONObject(i)
                jCountry.getJSONObject("translations")
                val name = jCountry.getString("name")
                val code = jCountry.getString("alpha3Code")

                val country = Country(name, code)
                list.add(country)
            }

            list.sortBy { country -> country.name }

            it.onSuccess(list)
        }

        val disposable =
            singleCountries.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        countriesLiveData.value = list

                        Log.d("List", "=======> $list")

                    },
                    { error ->
                        error.printStackTrace()
                    }

                )
        addDisposable(disposable)

    }
}