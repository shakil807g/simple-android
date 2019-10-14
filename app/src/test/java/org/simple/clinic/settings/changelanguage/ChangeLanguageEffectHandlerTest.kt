package org.simple.clinic.settings.changelanguage

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Test
import org.simple.clinic.mobius.EffectHandlerTestCase
import org.simple.clinic.settings.ProvidedLanguage
import org.simple.clinic.settings.SettingsRepository
import org.simple.clinic.settings.SystemDefaultLanguage
import org.simple.clinic.util.scheduler.TrampolineSchedulersProvider

class ChangeLanguageEffectHandlerTest {

  private val settingsRepository = mock<SettingsRepository>()
  private val uiActions = mock<UiActions>()

  private val testCase = EffectHandlerTestCase(ChangeLanguageEffectHandler.create(
      schedulersProvider = TrampolineSchedulersProvider(),
      settingsRepository = settingsRepository,
      uiActions = uiActions
  ))

  @After
  fun tearDown() {
    testCase.dispose()
  }

  @Test
  fun `the current selected language must be fetched when the load current selected effect is received`() {
    // given
    val selectedLanguage = SystemDefaultLanguage
    whenever(settingsRepository.getCurrentLanguage()).thenReturn(Single.just(selectedLanguage))

    // when
    testCase.dispatch(LoadCurrentLanguageEffect)

    // then
    testCase.assertOutgoingEvents(CurrentLanguageLoadedEvent(selectedLanguage))
  }

  @Test
  fun `the list of supported languages must be fetched when the load supported languages effect is received`() {
    // given
    val supportedLanguages = listOf(
        SystemDefaultLanguage,
        ProvidedLanguage(displayName = "English", languageCode = "en_IN"),
        ProvidedLanguage(displayName = "हिंदी", languageCode = "hi_IN")
    )
    whenever(settingsRepository.getSupportedLanguages()).thenReturn(Single.just(supportedLanguages))

    // when
    testCase.dispatch(LoadSupportedLanguagesEffect)

    // then
    testCase.assertOutgoingEvents(SupportedLanguagesLoadedEvent(supportedLanguages))
  }

  @Test
  fun `when the update current language effect is received, the current language must be changed`() {
    // given
    val changeToLanguage = ProvidedLanguage(displayName = "हिंदी", languageCode = "hi_IN")
    whenever(settingsRepository.setCurrentLanguage(changeToLanguage)).thenReturn(Completable.complete())

    // when
    testCase.dispatch(UpdateCurrentLanguageEffect(changeToLanguage))

    // then
    testCase.assertOutgoingEvents(CurrentLanguageChangedEvent)
  }

  @Test
  fun `when the go back to previous screen effect is received, the go back ui action must be invoked`() {
    // when
    testCase.dispatch(GoBack)

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).goBackToPreviousScreen()
    verifyNoMoreInteractions(uiActions)
  }
}