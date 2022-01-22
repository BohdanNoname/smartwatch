package com.nedash.com.smartwatch.notifier.app.ui.fragment.notification_themes

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.Utils.getAllApps
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationThemesViewModel @Inject constructor(
    private val dataBase: DataBaseSmartWatch
): ViewModel() {
    private val dao = dataBase.daoAppData()
    fun updateThemeForAppDataEntity(appDataEntity: AppDataEntity){
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(appDataEntity)
        }
    }

    fun updateDefaultThemeForAllAppDataEntities(context: Context, themeName: String, color: Int){
        viewModelScope.launch(Dispatchers.IO) {
            for (app in getAllApps(context, dataBase)){
                app.themeName = themeName
                app.notificationColor = color
                dao.insert(app)
            }
        }
    }
}