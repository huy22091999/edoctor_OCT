package im.vector.app.ext.encounter

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.databinding.GlobitsFragmentAppointmentBinding
import im.vector.app.ext.encounter.list.EncounterAdapter
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.Encounter
import im.vector.app.ext.data.model.TreatmentPeriod
import javax.inject.Inject

class EncounterFragment @Inject constructor() : EncounterBaseFragment<GlobitsFragmentAppointmentBinding>() {

    override var toolbarTitleRes: Int? = R.string.list_of_visit

    private lateinit var patientId: String

    private lateinit var treatmentPeriodAdapter: ArrayAdapter<TreatmentPeriod>
    private val treatmentPeriodList = mutableListOf<TreatmentPeriod>()

    private val encounterList = mutableListOf<Encounter>()


    private val ExposedDropdownMenu.commonDropdownOptions: () -> Unit
        get() = {
            inputType = InputType.TYPE_NULL
            imeOptions = EditorInfo.IME_ACTION_DONE
            showSoftInputOnFocus = false
            onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                hideKeyboard(requireActivity())
            }
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                hideKeyboard(requireActivity())
            }
        }

    override fun onResume() {
        super.onResume()
        viewModel.handle(EncounterActions.QueryPatientInfoData)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentAppointmentBinding {
        return GlobitsFragmentAppointmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        treatmentPeriodAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, treatmentPeriodList)
        views.listTreatmentPeriod.apply {
            commonDropdownOptions
            setAdapter(treatmentPeriodAdapter)
        }

        views.listTreatmentPeriod.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val treatmentPeriod = treatmentPeriodList.getOrNull(position)
            if (treatmentPeriod != null) {
                setupEncounterList(treatmentPeriod)
            }
        }

    }

    private fun setupEncounterList(treatmentPeriod: TreatmentPeriod) {
        encounterList.clear()
        treatmentPeriod.encounters?.let { encounterList.addAll(it) }
        views.recyclerView.layoutManager = LinearLayoutManager(context)
        views.recyclerView.setHasFixedSize(true)
        views.recyclerView.adapter =
            EncounterAdapter(encounterList as ArrayList<Encounter>, viewModel)
    }

    override fun updateWithState(state: EncounterViewState) {
        if (state.asyncPatientInfo is Success) {
            if (!this::patientId.isInitialized) {
                state.asyncPatientInfo.invoke().let {
                    patientId = it.patientId!!
                    viewModel.handle(EncounterActions.QueryTreatmentPeriodByPatientId(patientId))
                }
            }
        }
        if (state.asyncListTreatmentPeriod is Success) {
            state.asyncListTreatmentPeriod.invoke().let {
                if (it.size > 0) {

                    if (it[0].encounters.isNullOrEmpty()) {
                        views.encounterNull.visibility = View.VISIBLE
                        views.txtTreatment.visibility = View.INVISIBLE
                        views.listTreatmentPeriod.visibility = View.INVISIBLE

                    } else {
                        views.txtTreatment.visibility = View.VISIBLE
                        views.encounterNull.visibility = View.GONE
                        views.listTreatmentPeriod.visibility = View.VISIBLE
                    }

                    for (i in it.indices) {
                        it[i].name = "Đợt ${it.size - i}"
                    }
                    views.listTreatmentPeriod.setText(it[0].toString(), false)
                    setupEncounterList(it[0])
                    treatmentPeriodList.clear()
                    treatmentPeriodList.addAll(it)

                }
                else {
                    views.encounterNull.visibility = View.VISIBLE
                    views.txtTreatment.visibility = View.INVISIBLE
                    views.listTreatmentPeriod.visibility = View.INVISIBLE
                }
            }
        }

    }

    override fun resetViewModel() {

    }

    override fun updateData() {

    }
}
