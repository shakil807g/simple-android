package org.simple.clinic.summary

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType
import org.simple.clinic.ReplayUntilScreenIsDestroyed
import org.simple.clinic.analytics.Analytics
import org.simple.clinic.summary.OpenIntention.LinkIdWithPatient
import org.simple.clinic.widgets.ScreenCreated
import org.simple.clinic.widgets.UiEvent
import java.util.UUID

typealias Ui = PatientSummaryScreenUi
typealias UiChange = (Ui) -> Unit

class PatientSummaryScreenController @AssistedInject constructor(
    @Assisted private val patientUuid: UUID,
    @Assisted private val openIntention: OpenIntention
) : ObservableTransformer<UiEvent, UiChange> {

  @AssistedInject.Factory
  interface Factory {
    fun create(
        patientUuid: UUID,
        openIntention: OpenIntention
    ): PatientSummaryScreenController
  }

  override fun apply(events: Observable<UiEvent>): ObservableSource<UiChange> {
    val replayedEvents = ReplayUntilScreenIsDestroyed(events).replay()

    return Observable.mergeArray(
        reportViewedPatientEvent(replayedEvents),
//        openLinkIdWithPatientSheet(replayedEvents),
        hideLinkIdWithPatientSheet(replayedEvents)
    )
  }

  private fun reportViewedPatientEvent(events: Observable<UiEvent>): Observable<UiChange> {
    return events.ofType<ScreenCreated>()
        .take(1L)
        .doOnNext { Analytics.reportViewedPatient(patientUuid, openIntention.analyticsName()) }
        .flatMap { Observable.empty<UiChange>() }
  }

  private fun openLinkIdWithPatientSheet(events: Observable<UiEvent>): Observable<UiChange> {
    return events
        .ofType<ScreenCreated>()
        .filter { openIntention is LinkIdWithPatient }
        .map {
          val linkIdWithPatient = openIntention as LinkIdWithPatient
          { ui: Ui -> ui.showLinkIdWithPatientView(patientUuid, linkIdWithPatient.identifier) }
        }
  }

  private fun hideLinkIdWithPatientSheet(events: Observable<UiEvent>): Observable<UiChange> {
    return events
        .ofType<PatientSummaryLinkIdCompleted>()
        .map { Ui::hideLinkIdWithPatientView }
  }
}
