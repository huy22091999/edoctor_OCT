package im.vector.app.ext.encounter

import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.viewModel
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityBinding
import im.vector.app.ext.utils.makeBarsTransparent
import javax.inject.Inject

class EncounterActivity : VectorBaseActivity<GlobitsActivityBinding>(), EncounterViewModel.Factory {

    private val viewModel: EncounterViewModel by viewModel()

    @Inject
    lateinit var encounterViewModelFactory: EncounterViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    private val commonOption: (FragmentTransaction) -> Unit = { ft ->
        val enterAnim = R.anim.enter_fade_in
        val exitAnim = R.anim.exit_fade_out

        val popEnterAnim = R.anim.no_anim
        val popExitAnim = R.anim.exit_fade_out

        ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        if (isFirstCreation()) {
            addFragment(R.id.container, EncounterFragment::class.java)
        }

        setupToolbar()

        viewModel.subscribe(this) {
            updateWithState(it)
        }

        viewModel.observeViewEvents {
            handleRegistrationViewEvents(it)
        }

    }

    private fun updateWithState(state: EncounterViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else {
            hideWaitingView()
        }
    }

    private fun handleRegistrationViewEvents(viewEvent: EncounterViewEvents) {
        when (viewEvent) {
            is EncounterViewEvents.LaunchDetailFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    EncounterDetailFragment::class.java,
                    tag = EncounterDetailFragment.TAG,
                    option = commonOption
                )
        }
    }

    override fun create(initialState: EncounterViewState): EncounterViewModel {
        return encounterViewModelFactory.create(initialState)
    }

    override fun showWaitingView(text: String?) {
        hideKeyboard()
        super.showWaitingView(text)

        views.waitingView.waitingHorizontalProgress.isIndeterminate = true
        views.waitingView.waitingHorizontalProgress.isVisible = true
        views.waitingView.waitingStatusText.isGone = views.waitingView.waitingStatusText.text.isNullOrBlank()
    }

    override fun hideWaitingView() {
        views.waitingView.waitingStatusText.text = null
        views.waitingView.waitingStatusText.isGone = true
        views.waitingView.waitingHorizontalProgress.progress = 0
        views.waitingView.waitingHorizontalProgress.isVisible = false
        super.hideWaitingView()
    }

    ////////////////////////////// PRIVATE //////////////////////////

    private fun setupToolbar() {
        views.toolbar.also {
            title = getString(R.string.list_of_visit)
            setSupportActionBar(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        if (waitingView!!.isVisible) {
            // ignore
            return
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
    ////////////// PUBLIC ///////////////////
    fun updateToolbarTitle(title: String): Unit? {
        return supportActionBar?.setTitle(title)
    }

    override fun getBinding(): GlobitsActivityBinding {
        return GlobitsActivityBinding.inflate(layoutInflater)
    }

}
