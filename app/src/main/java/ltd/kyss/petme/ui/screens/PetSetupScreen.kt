package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.model.ColorPattern
import ltd.kyss.petme.core.model.PetSpecies
import ltd.kyss.petme.ui.components.GameArt

@Composable
fun PetSetupScreen(onStartGame: (PetSpecies, ColorPattern, String) -> Unit) {
    var selectedSpecies by remember { mutableStateOf(PetSpecies.CAT) }
    var selectedPattern by remember { mutableStateOf(ColorPattern.CLASSIC) }
    var petName by remember { mutableStateOf("Финни") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF4DF), Color(0xFFF4EAF7), Color(0xFFE5EAF8))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "PET ME!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF5636A6),
                letterSpacing = 1.sp
            )
            Text(
                "Выбери друга и начинай заботиться о нём",
                fontSize = 11.sp,
                color = Color(0xFF5D4A46)
            )

            Spacer(Modifier.height(9.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(112.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .background(Color(0xFFFFE8A8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        GameArt(
                            assetName = "pet_${selectedSpecies.id}_portrait",
                            fallbackEmoji = selectedSpecies.emoji,
                            modifier = Modifier.size(70.dp),
                            fallbackSize = 50.sp
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            selectedSpecies.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF312A45)
                        )
                        Text(
                            selectedSpecies.description,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            color = Color(0xFF655E70),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Text(
                "ВЫБЕРИ ПИТОМЦА",
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6F6680),
                letterSpacing = 1.sp
            )

            PetSpecies.entries.chunked(4).forEach { speciesRow ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(66.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    speciesRow.forEach { species ->
                        PetChoice(
                            species = species,
                            selected = species == selectedSpecies,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedSpecies = species }
                        )
                    }
                    repeat(4 - speciesRow.size) { Spacer(Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(5.dp))
            }

            Text(
                "ОКРАС",
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6F6680),
                letterSpacing = 1.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorPattern.entries.forEach { pattern ->
                    val selected = selectedPattern == pattern
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { selectedPattern = pattern },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) Color(0xFF6750A4) else Color.White.copy(alpha = 0.8f),
                        border = if (selected) null else BorderStroke(1.dp, Color(0xFFB8AFC5))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                if (selected) "✓ ${pattern.title}" else pattern.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else Color(0xFF514A5D),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it.take(16) },
                label = { Text("Кличка питомца", fontSize = 11.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
            )

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onStartGame(selectedSpecies, selectedPattern, petName) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
            ) {
                Text(
                    "Начать с ${petName.trim().ifBlank { "Финни" }}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PetChoice(
    species: PetSpecies,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFD8C9FF) else Color.White.copy(alpha = 0.72f),
        border = if (selected) BorderStroke(2.dp, Color(0xFF6750A4)) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameArt(
                assetName = "pet_${species.id}_portrait",
                fallbackEmoji = species.emoji,
                modifier = Modifier.size(38.dp),
                fallbackSize = 27.sp
            )
            Text(
                species.title,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B3445),
                maxLines = 1
            )
        }
    }
}
