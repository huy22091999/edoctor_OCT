package im.vector.app.ext.prescription.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import im.vector.app.R
import im.vector.app.ext.data.model.DrugOrder
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.encounter.EncounterViewModel
import im.vector.app.ext.prescription.PrescriptionAction
import im.vector.app.ext.prescription.PrescriptionViewModel
import im.vector.app.ext.utils.clickWithThrottle
import io.realm.Realm.getApplicationContext
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class PrescriptionAdapter(private val list: ArrayList<Prescription>, private val viewModel: PrescriptionViewModel) :
    RecyclerView.Adapter<PrescriptionAdapter.VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_prescription_list_item, parent, false)
        return VH(itemView)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = list[position]

        holder.encounterIndex.text = String.format(holder.encounterIndex.text.toString() + currentItem.encounter?.indexNumber)

        val formatter = SimpleDateFormat("dd/MM/yyyy")
        holder.encounterDate.text = formatter.format(currentItem.encounter?.encounterDate)
        holder.practitioner.text = currentItem.practitioner.toString()
        holder.status.text = AppConst.prescriptionStatusDrug(currentItem.status, holder.status.context)
        holder.orderDate.text = formatter.format(currentItem.orderDate)

        holder.btnDetail.clickWithThrottle {
            if (currentItem.id != null) {
                viewModel.handle(PrescriptionAction.QueryOneById(currentItem.id))
            }
            viewModel.handle(PrescriptionAction.LaunchDetailFragment)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val encounterIndex : TextView = itemView.findViewById(R.id.encounter_index)
        val encounterDate : TextView = itemView.findViewById(R.id.encounter_date)
        val practitioner : TextView = itemView.findViewById(R.id.practitioner)
        val status : TextView = itemView.findViewById(R.id.status)
        val orderDate : TextView = itemView.findViewById(R.id.order_date)
        val btnDetail : MaterialButton = itemView.findViewById(R.id.btn_detail)
    }

}
