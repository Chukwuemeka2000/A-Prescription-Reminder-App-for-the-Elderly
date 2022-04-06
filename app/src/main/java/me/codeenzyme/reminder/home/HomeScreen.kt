package me.codeenzyme.reminder.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun HomeScreen() {

    val medicationViewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(DefaultMedicationRepository()))

    val context = LocalContext.current

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var medicationName by remember {
        mutableStateOf("")
    }

    var medicationDosage by remember {
        mutableStateOf(0)
    }

    var medicationInterval by remember {
        mutableStateOf(0)
    }

    var medicationDescription by remember {
        mutableStateOf("")
    }

    val dosageTypes by remember {
        mutableStateOf(
            listOf(
                "Pill",
                "Tablet",
                "Capsule",
                "Injection",
                "Tablespoon",
                "Teaspoon",
                "Drops"
            )
        )
    }

    var dosageTypeExpanded by remember {
        mutableStateOf(false)
    }

    var selectedDosageIndex by remember {
        mutableStateOf(-1)
    }

    var startMedicationNow by remember {
        mutableStateOf(false)
    }

    fun validate(): Boolean {
        return when {
            medicationName.isEmpty() || medicationName.isBlank() -> {
                Toast.makeText(
                    context,
                    "Please fill the medication name field",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            medicationDescription.isEmpty() || medicationDescription.isBlank() -> {
                Toast.makeText(
                    context,
                    "Please fill the medication description field",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            medicationDosage <= 0 -> {
                Toast.makeText(
                    context,
                    "Please fill in the dosage",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            selectedDosageIndex == -1 -> {
                Toast.makeText(
                    context,
                    "Please select a dosage type",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            medicationInterval <= 0 -> {
                Toast.makeText(
                    context,
                    "Please fill the medication interval field",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            else -> {true}
        }
    }

    Scaffold(modifier = Modifier.padding(bottom = 50.dp), floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add new medication")
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val data by remember {
                medicationViewModel.getMedications()
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
               data?.let {
                   items(it) { medication ->
                       MedicationItem(medication)
                   }
               }
            }



            if (showAddDialog) {
                Dialog(onDismissRequest = { showAddDialog = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = medicationName,
                                onValueChange = { medicationName = it },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                maxLines = 1,
                                label = { Text("Medication Name") })

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = medicationDescription,
                                onValueChange = { medicationDescription = it },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                label = { Text("Medication Description") })

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = if (medicationDosage == 0) "" else medicationDosage.toString(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                maxLines = 1,
                                onValueChange = {
                                    medicationDosage = if (it.isEmpty()) 0 else it.toInt()
                                },
                                label = { Text("Medication Dosage") })

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dosageTypeExpanded = true }
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(16.dp),
                                text = if (selectedDosageIndex == -1) "Select a dosage type" else dosageTypes[selectedDosageIndex],
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            DropdownMenu(
                                modifier = Modifier.fillMaxWidth(),
                                expanded = dosageTypeExpanded,
                                onDismissRequest = { dosageTypeExpanded = false }) {
                                dosageTypes.forEachIndexed { index, dosageType ->
                                    DropdownMenuItem(onClick = {
                                        selectedDosageIndex = index
                                        dosageTypeExpanded = false
                                    }) {
                                        Text(dosageType)
                                    }
                                }
                            }

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = if (medicationInterval == 0) "" else medicationInterval.toString(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                maxLines = 1,
                                onValueChange = {
                                    medicationInterval = if (it.isEmpty()) 0 else it.toInt()
                                },
                                label = { Text("Medication Interval (Hours)") })

                            Spacer(modifier = Modifier.height(16.dp))

                            Row {
                                Checkbox(checked = startMedicationNow, onCheckedChange = { startMedicationNow = it })
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(modifier = Modifier.clickable { startMedicationNow = !startMedicationNow }, text = "Start now")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(modifier = Modifier.padding(8.dp), onClick = { showAddDialog = false }) {
                                    Text(text = "Cancel")
                                }

                                TextButton(modifier = Modifier.padding(8.dp), onClick = {
                                    if (validate()) {
                                        var medication = MedicationModel(
                                            medicationName,
                                            medicationDescription,
                                            medicationDosage,
                                            dosageTypes[selectedDosageIndex],
                                            medicationInterval,
                                            startMedicationNow,
                                            null
                                        )

                                        medicationViewModel.addMedication(medication) {
                                            when (it) {
                                                MedicationRepoStatus.Failure -> Toast.makeText(
                                                    context,
                                                    "Medication was not added",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                MedicationRepoStatus.Success -> Toast.makeText(
                                                    context,
                                                    "Medication added successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        
                                        showAddDialog = false
                                        // Save to Firebase
                                        // Set Alarm
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "All fields are required",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }) {
                                    Text(text = "Ok")
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationItem(medicationModel: MedicationModel) {
    val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
    Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().padding(8.dp), backgroundColor = Color.LightGray) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(medicationModel.medicationName!!)
            Text(medicationModel.medicationDescription!!)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(medicationModel.medicationInterval.toString())
                Text("${medicationModel.medicationDosage} ${medicationModel.medicationDosageType}")
            }
            Text(dateTimeFormat.format(Date()))
        }
    }
}