package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import androidx.lifecycle.*
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsSettingsViewModel @Inject constructor(
    dataBase: DataBaseSmartWatch
) : ViewModel() {

    private val dao = dataBase.daoAppData()

    private val _listAppData = MutableLiveData<List<AppDataEntity>>()
    var listAppData: LiveData<List<AppDataEntity>> = _listAppData
    private val list = mutableListOf<AppDataEntity>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _listAppData.value = dao.getAll()
        }
    }

    fun setSoundMode(appDataEntity: AppDataEntity){
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(appDataEntity)
        }
    }

    fun getAppsByName(name: String){
        viewModelScope.launch(Dispatchers.Main) {
            _listAppData.value?.forEach { app ->
                if (app.packageName.contains(name))
                    list.add(app)
            }
            _listAppData.value = list
        }
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }
}