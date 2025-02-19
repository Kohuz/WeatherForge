package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen() {

                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Detail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )

            }
