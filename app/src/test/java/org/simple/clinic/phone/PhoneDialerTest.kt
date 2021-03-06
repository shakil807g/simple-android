package org.simple.clinic.phone

import androidx.appcompat.app.AppCompatActivity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.simple.clinic.util.RxErrorsRule

class PhoneDialerTest {

  @get:Rule
  val rxErrorsRule = RxErrorsRule()

  private lateinit var phoneCaller: PhoneCaller
  private val config: PhoneNumberMaskerConfig = PhoneNumberMaskerConfig(proxyPhoneNumber = "987", phoneMaskingFeatureEnabled = false)
  private val dialer: Dialer = mock()
  private val activity: AppCompatActivity = mock()

  @Before
  fun setUp() {
    phoneCaller = PhoneCaller(config, activity)
  }

  @Test
  fun `when a normal call is made, the phone call should be made to the given number`() {
    val plainNumber = "123"

    phoneCaller.normalCall(plainNumber, dialer = dialer).blockingAwait()

    verify(dialer).call(activity, plainNumber)
  }

  @Test
  fun `when a secure call is made, the phone call should be made to the masked number`() {
    val plainNumber = "123"

    phoneCaller.secureCall(plainNumber, dialer = dialer).blockingAwait()

    verify(dialer).call(activity, "987,123#")
  }
}
