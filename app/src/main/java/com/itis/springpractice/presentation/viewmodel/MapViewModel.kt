package com.itis.springpractice.presentation.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.MyApplication
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.itis.springpractice.domain.usecase.user.DeleteNicknameUseCase
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import kotlinx.coroutines.launch
import kotlin.math.min

class MapViewModel(
) : ViewModel(), SuggestSession.SuggestListener {

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

    companion object {
        private const val RESULT_NUMBER_LIMIT = 15
        private val searchOptions = SuggestOptions().setSuggestTypes(
            SuggestType.BIZ.value
        )
    }

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun isPermissionsAllowed(): Boolean {
        var flag = false
        viewModelScope.launch {
            try {
                MyApplication.appContext.let {
                    flag = (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED)
                }
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
        return flag
    }

    private val _suggestResult: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val suggestResult: LiveData<ArrayList<String>> = _suggestResult

    fun requestSuggest(place: String, point: VisibleRegion) {
        val boundingBox = BoundingBox(point.bottomLeft, point.topRight)
        try {
            val suggestSession = searchManager.createSuggestSession()
            suggestSession.suggest(place, boundingBox, searchOptions, this)
        } catch (ex: Exception) {
            _error.value = ex
        }
    }

    override fun onResponse(@NonNull list: List<SuggestItem>) {
        val emptyList = ArrayList<String>()
        for (i in 0 until min(RESULT_NUMBER_LIMIT, list.size)) {
            list[i].displayText?.let {
                if (!emptyList.contains(it)) {
                    emptyList.add(it)
                }
            }
        }
        _suggestResult.value = emptyList
    }

    override fun onError(error: Error) {
        _error.value = Throwable(error.toString())
    }
}
