package im.vector.app.ext.prescription

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.databinding.GlobitsFragmentPrescriptionBinding
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.model.TreatmentPeriod
import im.vector.app.ext.prescription.list.PrescriptionAdapter
import javax.inject.Inject

class PrescriptionFragment @Inject constructor() : PrescriptionBaseFragment<GlobitsFragmentPrescriptionBinding>() {

    override var toolbarTitleRes: Int? = R.string.drug_prescriptions

    private lateinit var prescriptionAdapter: ArrayAdapter<TreatmentPeriod>
    private lateinit var prescriptionPage: Page<Prescription>

    override fun onResume() {
        super.onResume()
        viewModel.handle(PrescriptionAction.QueryPrescription)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentPrescriptionBinding {
        return GlobitsFragmentPrescriptionBinding.inflate(inflater, container, false)
    }

    override fun updateWithState(state: PrescriptionViewState) {
        if (state.asyncPagePrescriptions is Success) {
            state.asyncPagePrescriptions.invoke().let {
                prescriptionPage = it
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        if (!this::prescriptionPage.isInitialized) {
            return
        }

        with(prescriptionPage) {
            if (this.content != null && this.content.isNotEmpty()) {
                views.noContent.visibility = View.GONE
                views.recyclerView.layoutManager = LinearLayoutManager(context)
                views.recyclerView.setHasFixedSize(true)
                views.recyclerView.adapter =
                    PrescriptionAdapter(prescriptionPage.content as ArrayList<Prescription>, viewModel)
            }

        }
    }
}
