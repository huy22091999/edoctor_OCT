package im.vector.app.ext.prescription

import im.vector.app.core.platform.VectorViewEvents

sealed class PrescriptionViewEvents : VectorViewEvents {

    object LaunchDetailFragment : PrescriptionViewEvents()
    object LaunchUpdateMethodReceive : PrescriptionViewEvents()
    object UpdatePrescriptionComplete : PrescriptionViewEvents()
    object UpdateStatusReceivedComplete : PrescriptionViewEvents()

}
