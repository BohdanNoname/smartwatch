package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.*
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.Utils.getAllApps
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsSettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    dataBase: DataBaseSmartWatch
) : ViewModel() {

    private val dao = dataBase.daoAppData()
    private val _listAppData = MutableLiveData<List<AppDataEntity>>()
    var listAppData: LiveData<List<AppDataEntity>> = _listAppData

    private val searchList = mutableListOf<AppDataEntity>()

    init {
        getAllApplications(context)
    }

    fun getAllApplications(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val addedApps = dao.getAll()

            _listAppData.value = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter {
                    it.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }
                .filter { app ->
                    !addedApps.any { it.applicationName == app.packageName }
                }
                .map { appInfo ->
                    AppDataEntity(
                        icon = appInfo.icon,
                        applicationName = appInfo.loadLabel(pm).toString(),
                        packageName = appInfo.packageName,
                    )
                }
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
                    searchList.add(app)
            }
            _listAppData.value = searchList
        }
    }

    fun setSoundModeForAllApps(soundMode: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _listAppData.value?.forEach { app ->
                app.mute = soundMode
                dao.update(app)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchList.clear()
    }
}