package com.nedash.com.smartwatch.notifier.app.ui.fragment.notification_themes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.NotificationTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationThemesViewModel @Inject constructor(
    dataBase: DataBaseSmartWatch
): ViewModel() {
    private val dao = dataBase.daoAppData()

    fun updateThemeForAppDataEntity(appDataEntity: AppDataEntity){
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(appDataEntity)
        }
    }

    fun updateDefaultThemeForAllAppDataEntities(themeName: String, color: Int){
        viewModelScope.launch(Dispatchers.IO) {
            for (app in dao.getAll()){
                app.themeName = themeName
                app.notificationColor = color
                dao.update(app)
            }
        }
    }
}