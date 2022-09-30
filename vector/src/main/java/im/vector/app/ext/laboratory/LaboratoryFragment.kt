package im.vector.app.ext.laboratory

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsFragmentLaboratoryBinding
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.laboratory.list.LabTestOrderAdapter
import javax.inject.Inject

class LaboratoryFragment @Inject constructor(
//    val laboratoryViewModelFactory: LaboratoryViewModel.Factory
) :
    VectorBaseFragment<GlobitsFragmentLaboratoryBinding>() {

    private val viewModel: LaboratoryViewModel by activityViewModel()

    private lateinit var labTestOrders: Page<LabTestOrder>

    private var toolbarTitleRes: Int? = R.string.lab_tests

    override fun onResume() {
        super.onResume()
        viewModel.handle(LaboratoryAction.InitAction)

        if (toolbarTitleRes != null) {
            (activity as LaboratoryActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentLaboratoryBinding {
        return GlobitsFragmentLaboratoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun invalidate() = withState(viewModel) {
        updateWithState(it)
    }

    fun updateWithState(state: LaboratoryViewState) {
        when (state.asyncPageLabTests) {
            is Success -> {
                state.asyncPageLabTests.invoke().let {
                    labTestOrders = it
                    setupListLabTestOrder()
                }
            }
            else -> Unit
        }
    }

    private fun setupListLabTestOrder() {
        if (labTestOrders.content?.size!! > 0) {
            views.upcomingAppsBox.visibility = GONE
            views.upcomingAppsBoxView.visibility= GONE
            views.recyclerViewLabtestOrder.layoutManager = LinearLayoutManager(context)
            views.recyclerViewLabtestOrder.setHasFixedSize(true)

            views.recyclerViewLabtestOrder.adapter =
                LabTestOrderAdapter(labTestOrders.content as ArrayList<LabTestOrder>, viewModel)
        }
    }
}
