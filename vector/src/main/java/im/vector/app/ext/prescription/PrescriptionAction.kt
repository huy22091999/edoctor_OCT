package im.vector.app.ext.prescription

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.network.PrescriptionAPI

sealed class PrescriptionAction : VectorViewModelAction {
    data class QueryOneById(val id: String): PrescriptionAction()
    data class UpdatePrescription(val prescription: Prescription) : PrescriptionAction()
    data class UpdateStatusReceived(val prescription: Prescription) : PrescriptionAction()

    object QueryPrescription : PrescriptionAction()
    object LaunchDetailFragment : PrescriptionAction()
    object LaunchUpdateMethodReceive : PrescriptionAction()
}
