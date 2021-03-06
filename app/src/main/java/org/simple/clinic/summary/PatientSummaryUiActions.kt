package org.simple.clinic.summary

import java.util.UUID

interface PatientSummaryUiActions {
  fun showScheduleAppointmentSheet(patientUuid: UUID, sheetOpenedFrom: AppointmentSheetOpenedFrom)
  fun showEditPatientScreen(patientSummaryProfile: PatientSummaryProfile)
  fun goToPreviousScreen()
  fun goToHomeScreen()
}
