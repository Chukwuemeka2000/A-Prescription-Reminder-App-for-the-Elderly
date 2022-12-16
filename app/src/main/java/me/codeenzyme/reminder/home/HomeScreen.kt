package me.codeenzyme.reminder.home

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import me.codeenzyme.reminder.MedicationRepoStatus
import me.codeenzyme.reminder.R
import me.codeenzyme.reminder.Util
import me.codeenzyme.reminder.ui.theme.ColorWhite
import java.lang.StrictMath.abs
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val medicationViewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(DefaultMedicationRepository(), context))

    fun deleteMedication(medicationModel: MedicationModel) {
        medicationViewModel.deleteMedication(medicationModel, null)
    }

    var medicationStartTime by remember {
        mutableStateOf("Select time")
    }

    val date = Date()
    var time = 0L
    val timeDialog = TimePickerDialog(context,
        { p0, p1, p2 ->
            val calendar = Calendar.getInstance().let {
                it.set(Calendar.HOUR_OF_DAY, p1)
                it.set(Calendar.MINUTE, p2)
                it
            }
            time = calendar.timeInMillis
            Toast.makeText(context, "$p1 : $p2", Toast.LENGTH_SHORT).show()
            medicationStartTime = "$p1 : $p2"
        }, 0, 0, false)

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

    Scaffold(modifier = Modifier.padding(bottom = 50.dp), backgroundColor = Color(0xFFDDDDDD), floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true },) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add new medication",)
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val data by remember {
                medicationViewModel.getMedications()
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .background(
                    MaterialTheme.colors.primaryVariant,
                    RoundedCornerShape(bottomEnd = 48.dp, bottomStart = 48.dp)
                ), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Number of Medicines", color = ColorWhite, fontSize = 26.sp)
                Text(text = "${data?.size ?: 0}", fontSize = 24.sp, color = ColorWhite)
                Spacer(modifier = Modifier.height(16.dp))
            }

            data?.let {
                if (it.isEmpty()) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("No medication available.")
                    }
                } else {
                    it.forEachIndexed { index, model ->
                        Log.d("ELEZUA", model.toString())
                        medicationViewModel.setAlarm(model.medicationName!!, model.medicationDescription!!, model.medicationDosage!!, model.medicationDosageType!!, model.alarmId!!, (model.startTime!!.toDate().time), model.medicationInterval!!.toLong())
                        Log.e("time stamp", model.startTime!!.toDate().toString())
                    }
                    LazyVerticalGrid(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                        columns = GridCells.Fixed(2)
                    ) {
                        items(it) { medication ->
                            MedicationItem(medication, ::deleteMedication)
                        }
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
                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(modifier = Modifier.size(48.dp), painter = painterResource(id = Util.medicIconMap[dosageType] ?: R.drawable.ic_pill), contentDescription = dosageType)
                                            Spacer(modifier = Modifier.size(16.dp))
                                            Text(dosageType)
                                        }
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

                            ReadonlyTextField(
                                value = TextFieldValue(medicationStartTime),
                                onValueChange = {},
                                onClick = { timeDialog.show() }) {

                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(modifier = Modifier.padding(8.dp), onClick = { showAddDialog = false }) {
                                    Text(text = "Cancel")
                                }

                                TextButton(modifier = Modifier.padding(8.dp), onClick = {
                                    if (validate()) {
                                        val medication = MedicationModel(
                                            UUID.randomUUID().toString(),
                                            medicationName,
                                            medicationDescription,
                                            medicationDosage,
                                            dosageTypes[selectedDosageIndex],
                                            medicationInterval,
                                            Timestamp(Date(time)),
                                            Timestamp.now(),
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) abs(Random(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).nextInt()) else (0..30000).random(),
                                            Timestamp.now()
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
fun MedicationItem(medicationModel: MedicationModel, delete: (medicationModel: MedicationModel) -> Unit) {
    val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
    Card(modifier = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth()
        .padding(8.dp), backgroundColor = Color(0xFFFFFFFF)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { delete(medicationModel) }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,) {
                when (medicationModel.medicationDosageType?.toUpperCase()) {
                    "PILL" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_pill), contentDescription = "pill", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "TABLET" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_pill), contentDescription = "tablet", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "CAPSULE" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_capsule), contentDescription = "capsule", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "INJECTION" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_syringe), contentDescription = "injection", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "TABLESPOON" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_syrup), contentDescription = "tablespoon", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "TEASPOON" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_syrup), contentDescription = "teaspoon", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    "DROPS" -> {
                        Icon(painter = painterResource(id = R.drawable.ic_syrup), contentDescription = "syrup", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                    else -> {
                        Icon(painter = painterResource(id = R.drawable.ic_pill), contentDescription = "pill", tint = MaterialTheme.colors.primaryVariant, modifier = Modifier.size(48.dp))
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(medicationModel.medicationName!!, overflow = TextOverflow.Ellipsis, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.size(16.dp))
                Text(medicationModel.medicationDescription!!, overflow = TextOverflow.Ellipsis, maxLines = 2)
                Spacer(modifier = Modifier.height(16.dp))
                Text("${medicationModel.medicationDosage} ${medicationModel.medicationDosageType}")
                Text("Every ${medicationModel.medicationInterval.toString()} hours")
                Spacer(modifier = Modifier.height(16.dp))
                Text(dateTimeFormat.format(Date()))
            }
        }
    }
}