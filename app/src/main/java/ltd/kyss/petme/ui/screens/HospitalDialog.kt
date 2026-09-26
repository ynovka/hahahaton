package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.PetHealth
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun HospitalDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    val checkedThisPeriod = state.pet.lastCheckupPeriod == state.period
    val needsTreatment = state.pet.health == PetHealth.NEEDS_TREATMENT

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🏥 Лечебница Доктора Совы") },
        text = {
            Column {
                Text("Период ${state.period}: ${state.pet.health.emoji} ${state.pet.health.title}")
                Text(
                    if (checkedThisPeriod) "Плановый осмотр уже пройден."
                    else "Плановый осмотр бесплатный и доступен один раз за период."
                )
                if (needsTreatment) {
                    Text("Лечение стоит ${GameEngine.TREATMENT_COST} монет. В кошельке: ${state.wallet.coins}.")
                }
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = viewModel::runHospitalCheckup,
                    enabled = !checkedThisPeriod,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (checkedThisPeriod) "Осмотр пройден" else "Пройти осмотр бесплатно")
                }
                OutlinedButton(
                    onClick = viewModel::treatPet,
                    enabled = needsTreatment && state.wallet.coins >= GameEngine.TREATMENT_COST,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Оплатить лечение — ${GameEngine.TREATMENT_COST} м.")
                }
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Закрыть") } }
    )
}
