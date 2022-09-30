package im.vector.app.ext.prescription

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.R
import im.vector.app.databinding.GlobitsFragmentPrescriptionBinding
import im.vector.app.databinding.GlobitsPrescriptionDetailBinding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.prescription.list.DrugOrderAdapter
import im.vector.app.ext.prescription.list.PrescriptionAdapter
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.list.LabTestResultAdapter
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.snackbar
import java.text.SimpleDateFormat
import javax.inject.Inject

class PrescriptionDetailFragment @Inject constructor() : PrescriptionBaseFragment<GlobitsPrescriptionDetailBinding>() {

    override var toolbarTitleRes: Int? = R.string.detail_prescriptions

    companion object {
        const val TAG = "_PRESCRIPTION_DETAIL_FRAGMENT"
    }
    private lateinit var prescription: Prescription

    //phiếu đồng thuận
    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private fun createDialog() {
        if (this::prescription.isInitialized) {
            dialogBuilder = AlertDialog.Builder(requireActivity())
            val eligiblePopup: View = layoutInflater.inflate(R.layout._globits_prescription_feedback_popup, null)

            val btnAgree = eligiblePopup.findViewById<Button>(R.id.btn_agree)
            val btnClose = eligiblePopup.findViewById<Button>(R.id.btn_close)
            val txtFeedBack = eligiblePopup.findViewById<TextInputEditText>(R.id.feed_back)
            txtFeedBack.setText(prescription.feedback)
            dialogBuilder.setView(eligiblePopup)
            dialog = dialogBuilder.create()
            dialog.show()

            btnAgree.clickWithThrottle {
                with(prescription) {
                    this.feedback = txtFeedBack.text.toString()
                }
                viewModel.handle(PrescriptionAction.UpdatePrescription(prescription))
                dialog.dismiss()
            }

            btnClose.clickWithThrottle {
                dialog.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(PrescriptionAction.QueryPrescription)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsPrescriptionDetailBinding {
        return GlobitsPrescriptionDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    private fun handleViewEvents(event: PrescriptionViewEvents) {
        when (event) {
            is PrescriptionViewEvents.UpdateStatusReceivedComplete -> {
                withState(viewModel) {
                    when (it.asyncPrescription) {
                        is Success -> {
                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
                        }
                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                        else -> Unit
                    }
                }
            }
            is PrescriptionViewEvents.UpdatePrescriptionComplete -> {
                withState(viewModel) {
                    when (it.asyncPrescription) {
                        is Success -> {
                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
                        }
                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    override fun updateWithState(state: PrescriptionViewState) {
        if (state.asyncPrescription is Success) {
            state.asyncPrescription.invoke().let {
                prescription = it
                populateData()
            }
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun populateData() {
        if (!this::prescription.isInitialized) {
            return
        }

        with(prescription) {

            views.btnUpdate.setOnContextClickListener(null)
            views.btnUpdate.visibility = View.GONE

            if (this.encounter != null) {
                if (this.encounter.treatmentPeriod != null) {
                    views.treatmentPeriodIndex.text = this.encounter.treatmentPeriod.indexNumber.toString()
                }
                views.encounterDate.text = SimpleDateFormat("dd/MM/yyyy").format(this.encounter.encounterDate)
                views.status.text = AppConst.prescriptionStatusDrug(this.status, views.status.context)
                if (this.encounter.templateCorrespondingTo != null) {
                    views.encounterIndex.text = this.encounter.templateCorrespondingTo
                }
            }
            //drug by regimen
            views.recyclerViewDrugs.layoutManager = LinearLayoutManager(context)
            views.recyclerViewDrugs.setHasFixedSize(true)

            val drugs = mutableListOf<DrugOrder>()
            if (this.drugsByRegimen != null && this.drugsByRegimen.isNotEmpty()) {
                drugs.addAll(this.drugsByRegimen)
            }
            if (this.drugsOther != null && this.drugsOther.isNotEmpty()) {
                drugs.addAll(this.drugsOther)
            }
            views.recyclerViewDrugs.adapter = DrugOrderAdapter(drugs as ArrayList<DrugOrder>)

            //nhận thuốc
            views.receivingUnitName.text = this.receivingUnitName ?: ""
            views.receiveType.text = if (this.receiveType != null)
                AppConst.receiveType(this.receiveType!!, views.receiveType.context)
            else ""

            //nếu nhận tại nhà thì hiện thêm thông tin
            if (this.receiveType!= null && this.receiveType == 1) {
                views.layoutReceiveTypeHome.visibility = View.VISIBLE
                views.receiveName.text = this.receiveName
                views.receiveAddress.text = this.receiveAddress
                views.receivePhoneNumber.text = this.receivePhoneNumber
            }
            if (this.receivingUnit==null)
            {
                views.layoutState.visibility=View.GONE
                views.layoutReceive.visibility=View.GONE
                views.layoutMothod.visibility=View.GONE
                views.layoutMothodBuy.visibility=View.VISIBLE
                views.receiveTypeBuy.text=getString(R.string.buy_sell)
            }

            if (this.status != null) {
                //setup nút bấm
                //nếu trạng thái là mới tiếp nhận -> hiện nút thay đổi hình thức nhận
                if (this.status == AppConst.PRESCRIPTION_STATUS_NEW) {
                    views.btnUpdate.visibility = View.VISIBLE
                    views.btnUpdate.clickWithThrottle {
                        viewModel.handle(PrescriptionAction.LaunchUpdateMethodReceive)
                    }
                }
                //trạng thài = đã gửi đi -> hiện nút đã nhận
                if (this.status == AppConst.PRESCRIPTION_STATUS_SEND) {
                    views.btnUpdate.visibility = View.VISIBLE
                    views.btnUpdate.text = requireActivity().getString(R.string.received)
                    views.btnUpdate.clickWithThrottle {
                        viewModel.handle(PrescriptionAction.UpdateStatusReceived(prescription))
                    }
                }
                //trạng thái = đã nhận -> hiện nút phản hồi
                if (this.status == AppConst.PRESCRIPTION_STATUS_RECEIVED) {
                    views.btnUpdate.visibility = View.VISIBLE
                    views.btnUpdate.text = requireActivity().getString(R.string.feed_back)
                    views.btnUpdate.clickWithThrottle {
                        createDialog()
                    }
                }
            }
        }
    }
}
