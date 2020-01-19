package org.simple.clinic

import android.app.Application
import androidx.work.WorkManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.simple.clinic.appupdate.AppUpdateConfig
import org.simple.clinic.facility.change.FacilityChangeModule
import org.simple.clinic.login.LoginOtpSmsListener
import org.simple.clinic.newentry.country.InputFields
import org.simple.clinic.settings.Language
import org.simple.clinic.settings.SettingsRepository
import org.simple.clinic.sync.IDataSyncOnApproval
import org.simple.clinic.sync.indicator.SyncIndicatorModule
import org.simple.clinic.user.clearpatientdata.ClearPatientDataModule
import javax.inject.Named

@Module(includes = [
  FacilityChangeModule::class,
  ClearPatientDataModule::class,
  SyncIndicatorModule::class
])
class TempModule {

  @Provides
  @Named("number_of_patients_registered")
  fun numberOfPatientsRegistered(rxSharedPreferences: RxSharedPreferences, moshi: Moshi): Preference<Int> {
    return rxSharedPreferences.getInteger("number_of_patients_registered")
  }

  @Provides
  @Named("training_video_youtube_id")
  fun provideSimpleVideoUrlBasedOnLocale(): String {
    return "YO3D1paAuqU"
  }

  @Provides
  fun provideInputFields(): InputFields {
    return InputFields(emptyList())
  }

  @Provides
  fun provideSettingsRepository(): SettingsRepository {
    return object : SettingsRepository {
      override fun getCurrentLanguage(): Single<Language> {
        throw UnsupportedOperationException("NOT IMPLEMENTED")
      }

      override fun getSupportedLanguages(): Single<List<Language>> {
        throw UnsupportedOperationException("NOT IMPLEMENTED")
      }

      override fun setCurrentLanguage(newLanguage: Language): Completable {
        throw UnsupportedOperationException("NOT IMPLEMENTED")
      }
    }
  }

  @Provides
  fun provideSyncOnApproval(): IDataSyncOnApproval {
    return object : IDataSyncOnApproval {
      override fun sync(): Disposable {
        return Completable.complete().subscribe()
      }
    }
  }

  @Provides
  fun provideWorkManager(application: Application): WorkManager {
    return WorkManager.getInstance(application)
  }

  @Provides
  fun provideAppUpdateConfig(): Observable<AppUpdateConfig> {
    return Observable.empty()
  }

  @Provides
  fun provideLoginOtpSmsListener(): LoginOtpSmsListener {
    return object : LoginOtpSmsListener {
      override fun listenForLoginOtp(): Completable {
        return Completable.complete()
      }
    }
  }
}
