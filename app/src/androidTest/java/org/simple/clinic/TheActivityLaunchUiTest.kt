package org.simple.clinic

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.f2prateek.rx.preferences2.Preference
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.simple.clinic.appconfig.Country
import org.simple.clinic.main.TheActivity
import org.simple.clinic.rules.LocalAuthenticationRule
import org.simple.clinic.util.Just
import org.simple.clinic.util.Optional
import java.net.URI
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheActivityLaunchUiTest {

  private val activityRule: ActivityTestRule<TheActivity> = ActivityTestRule<TheActivity>(TheActivity::class.java, false, false)

  @get:Rule
  val ruleChain: TestRule = RuleChain
      .outerRule(activityRule)
      .around(LocalAuthenticationRule())

  @Inject
  lateinit var storedCountry: Preference<Optional<Country>>

  @Before
  fun setUp() {
    TestClinicApp.appComponent().inject(this)
    storedCountry.set(Just(Country("IN", URI.create(BuildConfig.FALLBACK_ENDPOINT), "India", "91")))
  }

  @Test
  fun the_activity_must_launch() {
    activityRule.launchActivity(null)
  }
}
