package im.vector.app.ext.prescription

import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment

abstract class PrescriptionBaseFragment<VB : ViewBinding> : VectorBaseFragment<VB>() {

    protected val viewModel: PrescriptionViewModel by activityViewModel()

    open var toolbarTitleRes: Int? = null

    override fun onResume() {
        super.onResume()

        if (toolbarTitleRes != null) {
            (activity as PrescriptionActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
        }
    }

    final override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    open fun updateWithState(state: PrescriptionViewState) {
        // No op by default
    }
}
