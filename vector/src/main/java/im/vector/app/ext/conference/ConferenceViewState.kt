/*
 * Copyright (c) 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.ext.conference

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.LabTestOrderAttachment
import im.vector.app.ext.data.model.MedicalExamination
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.model.ServiceEvaluationCardByEncounter
import org.matrix.android.sdk.api.session.widgets.model.Widget

data class ConferenceViewState(
        val roomId: String = "",
        val widgetId: String = "",
        val enableVideo: Boolean = false,
        val widget: Async<Widget> = Uninitialized,
        var asyncPatientInfo: Async<PatientInfo> = Uninitialized,
        var asyncMedicalExamination: Async<MedicalExamination> = Uninitialized,
        var asyncServiceRating: Async<ServiceEvaluationCardByEncounter> = Uninitialized,
        var asyncEditingServiceRating: Async<ServiceEvaluationCardByEncounter> = Uninitialized,
) : MvRxState
