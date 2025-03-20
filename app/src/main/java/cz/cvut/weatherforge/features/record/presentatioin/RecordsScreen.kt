package cz.cvut.weatherforge.features.record.presentation

import ResolutionDatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.record.presentatioin.RecordsScreenViewModel
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem

import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordsScreen(
    viewModel: RecordsScreenViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

//    // Show/hide date pickers
//    if (screenState.showDatePicker) {
//        ResolutionDatePickerDialog(
//            minimumDate = screenState.selectedElement?.let { element ->
//                screenState.elementCodelist.find { it.abbreviation == element.abbreviation }?.beginDate?.date?.toJavaLocalDate()
//            },
//            resolution = "Daily", // Adjust as needed
//            onDismiss = { viewModel.showDatePicker(false) },
//            onDateSelected = { date ->
//                viewModel.setSelectedDate(date)
//            },
//            dateToShow = LocalDate.now()
//        )
//    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (screenState.loading) {
                true -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Element selection dropdown
                        ElementDropdownMenu(
                            items = screenState.elementCodelist,
                            selectedItem = screenState.selectedElement,
                            onItemSelected = { element ->
                                viewModel.selectElement(element)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Date selection button
                        OutlinedButton(
                            onClick = { true},
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = screenState.selectedDate?.toString() ?: stringResource(R.string.select_date),
                                    modifier = Modifier.padding(8.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        Spacer(modifier = Modifier.height(16.dp))


                    }
                }
            }
        }
    }
}

@Composable
fun ElementDropdownMenu(
    items: List<ElementCodelistItem>,
    selectedItem: ElementCodelistItem?,
    onItemSelected: (ElementCodelistItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Enhanced Button to toggle dropdown visibility
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedItem?.name ?: stringResource(R.string.select_element),
                    modifier = Modifier.padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    text = {
                        Text(text = item.name)
                    }
                )
            }
        }
    }
}