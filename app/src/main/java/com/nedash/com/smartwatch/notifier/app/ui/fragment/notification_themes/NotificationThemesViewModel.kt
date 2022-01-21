package com.nedash.com.smartwatch.notifier.app.ui.fragment.notification_themes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.DispatcherProvider
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.NotificationTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationThemesViewModel @Inject constructor(
    private val dataBase: DataBaseSmartWatch,
    private val dispatcherProvider: DispatcherProvider
): ViewModel() {
    private val dao = dataBase.daoAppData()

    fun updateThemeForAppDataEntity(appDataEntity: AppDataEntity){
        viewModelScope.launch(dispatcherProvider.io()) {
            dao.update(appDataEntity)
        }
    }

    fun updateDefaultThemeForAllAppDataEntities(theme: NotificationTheme){
        viewModelScope.launch(dispatcherProvider.io()) {
            for (app in dao.getAll()){

                dao.update(app)
            }
        }
    }
}