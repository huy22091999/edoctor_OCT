package im.vector.app.ext.prescription

import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.repository.PrescriptionRepository
import im.vector.app.ext.registration.RegistrationViewEvents
import im.vector.app.ext.registration.RegistrationViewState

class PrescriptionViewModel @AssistedInject constructor(
    @Assisted initialState: PrescriptionViewState,
    val repository: PrescriptionRepository
) : VectorViewModel<PrescriptionViewState, PrescriptionAction, PrescriptionViewEvents>(initialState) {

    private val defaultFilter = QueryFilter(pageIndex = 1, pageSize = 10)

    @AssistedFactory
    interface Factory {
        fun create(initialState: PrescriptionViewState): PrescriptionViewModel
    }

    companion object : MvRxViewModelFactory<PrescriptionViewModel, PrescriptionViewState> {

        override fun initialState(viewModelContext: ViewModelContext): PrescriptionViewState {
            return PrescriptionViewState(
                asyncEditPrescription = Uninitialized,
                asyncPrescription = Uninitialized,
                asyncPagePrescriptions = Uninitialized,
            )
        }
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: PrescriptionViewState): PrescriptionViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: PrescriptionAction) {
        when(action) {
            is PrescriptionAction.QueryPrescription -> handleQueryPrescription()
            is PrescriptionAction.QueryOneById -> handleQueryOneById(action.id)
            is PrescriptionAction.LaunchDetailFragment -> {
                _viewEvents.post(PrescriptionViewEvents.LaunchDetailFragment)
            }
            is PrescriptionAction.LaunchUpdateMethodReceive -> {
                _viewEvents.post(PrescriptionViewEvents.LaunchUpdateMethodReceive)
            }
            is PrescriptionAction.UpdatePrescription -> handleUpdatePrescription(action.prescription)
            is PrescriptionAction.UpdateStatusReceived -> handleUpdateStatusReceived(action.prescription)
        }
    }

    private fun handleQueryPrescription() {
        setState {
            copy(asyncPagePrescriptions = Loading())
        }
        repository.searchByPage(defaultFilter).execute {
            copy(asyncPagePrescriptions = it)
        }
    }

    private fun handleQueryOneById(id: String) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.getOneById(id).execute {
            copy(asyncPrescription = it)
        }
    }

    private fun handleUpdatePrescription(prescription: Prescription) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.updateReceive(prescription, prescription.id!!).execute {
            asyncPrescription.invoke().let {
                _viewEvents.post(PrescriptionViewEvents.UpdatePrescriptionComplete)
            }
            copy(asyncPrescription = it)
        }
    }

    private fun handleUpdateStatusReceived(prescription: Prescription) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.updateStatusReceived(prescription, prescription.id!!).execute {
            asyncPrescription.invoke().let {
                _viewEvents.post(PrescriptionViewEvents.UpdateStatusReceivedComplete)
            }
            copy(asyncPrescription = it)
        }
    }

}
