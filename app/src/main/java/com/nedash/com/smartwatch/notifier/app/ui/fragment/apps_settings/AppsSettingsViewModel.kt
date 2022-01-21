package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import androidx.lifecycle.*
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsSettingsViewModel @Inject constructor(
    private val dataBase: DataBaseSmartWatch,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val dao = dataBase.daoAppData()

    private val _listAppData = MutableLiveData<List<AppDataEntity>>()
    var listAppData: LiveData<List<AppDataEntity>> = _listAppData
    private val list = mutableListOf<AppDataEntity>()

    init {
        viewModelScope.launch(dispatcher.io()) {
            _listAppData.value = dao.getAll()
        }
    }

    fun setSoundMode(appDataEntity: AppDataEntity){
        viewModelScope.launch(dispatcher.io()) {
            dao.update(appDataEntity)
        }
    }

    fun getAppsByName(name: String){
        viewModelScope.launch(dispatcher.main()) {
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