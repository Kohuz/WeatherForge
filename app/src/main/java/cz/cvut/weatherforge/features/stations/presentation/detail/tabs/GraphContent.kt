package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel

@Composable
fun GraphContent(station: Station, viewModel: DetailScreenViewModel) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

    val selectedResolution = screenState.selectedResolutionIndex
    val resolutions = listOf("Denně", "Měsíčně", "Ročně")

//    // Show/hide date pickers
//    if (screenState.showFromDatePicker) {
//        DatePickerDialog(
//            resolution = resolutions[selectedResolution],
//            onDismiss = { viewModel.showFromDatePicker(false) },
//            onDateSelected = { date -> viewModel.setFromDate(date) }
//        )
//    }
//
//    if (screenState.showToDatePicker) {
//        DatePickerDialog(
//            resolution = resolutions[selectedResolution],
//            onDismiss = { viewModel.showToDatePicker(false) },
//            onDateSelected = { date -> viewModel.setToDate(date) }
//        )
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Dropdown for selecting station elements
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Button to toggle dropdown visibility
            OutlinedButton(
                onClick = { viewModel.toggleDropdown(!screenState.expanded) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = screenState.selectedElement?.name ?: "Select Station Element",
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Dropdown menu
            DropdownMenu(
                expanded = screenState.expanded,
                onDismissRequest = { viewModel.toggleDropdown(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                screenState.elementCodelist.forEach { element ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.selectElement(element)
                            viewModel.toggleDropdown(false)
                        },
                        text = {
                            Text(text = element.name)
                        }
                    )
                }
            }
        }

        // Date Selectors for fromDate and toDate
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // From Date Selector
//            OutlinedButton(
//                onClick = { viewModel.showFromDatePicker(true) },
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 8.dp)
//            ) {
//                Text(text = screenState.fromDate ?: "Select From Date")
//            }
//
//            // To Date Selector
//            OutlinedButton(
//                onClick = { viewModel.showToDatePicker(true) },
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 8.dp)
//            ) {
//                Text(text = screenState.toDate ?: "Select To Date")
//            }
        }

        // Radio buttons for selecting resolution
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            resolutions.forEachIndexed { index, resolution ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (index == selectedResolution),
                            onClick = { viewModel.selectResolution(index) },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedResolution),
                        onClick = { viewModel.selectResolution(index) }
                    )
                    Text(
                        text = resolution,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}