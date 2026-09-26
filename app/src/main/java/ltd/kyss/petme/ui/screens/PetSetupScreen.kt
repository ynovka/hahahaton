package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.model.ColorPattern
import ltd.kyss.petme.core.model.PetSpecies

@Composable
fun PetSetupScreen(onStartGame: (PetSpecies, ColorPattern, String) -> Unit) {
    var selectedSpecies by remember { mutableStateOf(PetSpecies.CAT) }
    var selectedPattern by remember { mutableStateOf(ColorPattern.CLASSIC) }
    var petName by remember { mutableStateOf("Финни") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFE8EAF6))))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PET ME!", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFF5E35B1))
        Text(
            "Выбери питомца, с которым будешь учиться заботе и разумным покупкам",
            textAlign = TextAlign.Center,
            color = Color(0xFF4E342E)
        )
        Spacer(Modifier.height(18.dp))

        PetSpecies.entries.chunked(2).forEach { rowSpecies ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowSpecies.forEach { species ->
                    val selected = species == selectedSpecies
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSpecies = species },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFFD1C4E9) else Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(species.emoji, fontSize = 36.sp)
                            Text(species.title, fontWeight = FontWeight.Bold)
                            Text(species.description, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
                if (rowSpecies.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }

        Text("Окрас", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ColorPattern.entries.forEach { pattern ->
                OutlinedButton(
                    onClick = { selectedPattern = pattern },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("${if (selectedPattern == pattern) "✓ " else ""}${pattern.title}")
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = petName,
            onValueChange = { petName = it.take(16) },
            label = { Text("Кличка питомца") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = { onStartGame(selectedSpecies, selectedPattern, petName) },
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            Text("Начать приключение с ${petName.trim().ifBlank { "Финни" }}")
        }
    }
}
