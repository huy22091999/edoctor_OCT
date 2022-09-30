package im.vector.app.ext.encounter.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import im.vector.app.R
import im.vector.app.ext.encounter.EncounterViewModel
import im.vector.app.ext.data.model.Encounter
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.encounter.EncounterActions
import im.vector.app.ext.utils.clickWithThrottle
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class EncounterAdapter(private val list: ArrayList<Encounter>, private val viewModel: EncounterViewModel) :
    RecyclerView.Adapter<EncounterAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_encounter_list_item, parent, false)
        return VH(itemView)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = list[position]
        holder.appointmentDate.text = SimpleDateFormat("dd/MM/yyyy").format(currentItem.encounterDate)
        holder.encounterType.text = if (currentItem.encounterType == 1) "Tái khám" else  "Khám lần đầu"
        holder.encounterStatus.text = AppConst.encounterStatus(currentItem.status, holder.encounterStatus.context)
        holder.encounterIndex.text = String.format("Lần khám: %s", currentItem.indexNumber.toString())

//        holder.templateName.text = currentItem.template?.name
//        val formatter = SimpleDateFormat("dd/MM/yyyy")
//        val requestDate : Date = currentItem.orderDate!!
//
//        val requestDateString : String = formatter.format(requestDate)
//
//        holder.requestDate.text = requestDateString
//        holder.expectedDate.text = formatter.format(currentItem.expectedDate!!)
//        holder.testStatus.text = AppConst.labTestOrderStatus(currentItem.testStatus!!, holder.testStatus.context)

        holder.btnDetail.clickWithThrottle {
            if (currentItem.id != null) {
                viewModel.handle(EncounterActions.QueryMedicalExaminationById(currentItem.id!!))
            }
            viewModel.handle(EncounterActions.LaunchEncounterDetailFragment)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val encounterIndex: TextView = itemView.findViewById(R.id.encounter_index)
        val appointmentDate: TextView = itemView.findViewById(R.id.appointment_date)
        val encounterType: TextView = itemView.findViewById(R.id.encounter_type)
        val encounterStatus: TextView = itemView.findViewById(R.id.encounter_status)
        val btnDetail: MaterialButton = itemView.findViewById(R.id.btn_detail)

    }
}
