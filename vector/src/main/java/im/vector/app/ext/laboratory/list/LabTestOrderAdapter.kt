package im.vector.app.ext.laboratory.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.R
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.laboratory.LaboratoryAction
import im.vector.app.ext.laboratory.LaboratoryViewModel
import im.vector.app.ext.utils.clickWithThrottle
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LabTestOrderAdapter(private val list: ArrayList<LabTestOrder>, private val viewModel: LaboratoryViewModel) :
    RecyclerView.Adapter<LabTestOrderAdapter.LabTestOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabTestOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_labtest_order_list_item, parent, false)
        return LabTestOrderViewHolder(itemView)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: LabTestOrderViewHolder, position: Int) {
        val currentItem = list[position]
        holder.templateName.text = currentItem.template?.name
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (currentItem.orderDate != null) {
            val requestDate : Date = currentItem.orderDate
            val requestDateString : String = formatter.format(requestDate)
            holder.requestDate.text = requestDateString
        }
        if (currentItem.expectedDate != null) {
            holder.expectedDate.text = formatter.format(currentItem.expectedDate)
        }
        if (currentItem.testStatus != null) {
            holder.testStatus.text = AppConst.labTestOrderStatus(currentItem.testStatus, holder.testStatus.context)
        }

        holder.btnDetail.clickWithThrottle {
            if (currentItem.id != null) {
                viewModel.handle(LaboratoryAction.GetOneById(currentItem.id))
            }
            viewModel.handle(LaboratoryAction.ViewTemplateDetail)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class LabTestOrderViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val templateName: TextView = itemView.findViewById(R.id.labtest_template_name)
        val requestDate: TextView = itemView.findViewById(R.id.labtest_order_request_date)
        val expectedDate: TextView = itemView.findViewById(R.id.labtest_order_expected_date)
        val testStatus: TextView = itemView.findViewById(R.id.labtest_order_test_status)
        val btnDetail: TextView = itemView.findViewById(R.id.btn_detail)

    }
}
