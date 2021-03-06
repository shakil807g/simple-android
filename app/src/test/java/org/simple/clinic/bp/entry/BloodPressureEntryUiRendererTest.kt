package org.simple.clinic.bp.entry

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.junit.Test
import org.simple.clinic.bp.entry.OpenAs.New
import org.simple.clinic.util.TestUserClock
import org.threeten.bp.LocalDate
import java.util.UUID

class BloodPressureEntryUiRendererTest {
  private val ui = mock<BloodPressureEntryUi>()
  private val uiRenderer = BloodPressureEntryUiRenderer(ui)
  private val patientUuid = UUID.fromString("f5387bb5-0dbf-4d91-a487-bbe219adcd60")

  @Test
  fun `when the sheet is show for a new entry, then hide remove BP button and show enter new BP title`() {
    // given
    val newBloodPressureEntryModel = BloodPressureEntryModel
        .create(New(patientUuid), LocalDate.now(TestUserClock()).year)

    // when
    uiRenderer.render(newBloodPressureEntryModel)

    // then
    verify(ui).hideRemoveBpButton()
    verify(ui).showEnterNewBloodPressureTitle()
    verifyNoMoreInteractions(ui)
  }
}
