package me.codeenzyme.reminder.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.codeenzyme.reminder.*
import me.codeenzyme.reminder.R
import me.codeenzyme.reminder.home.MedicationModel
import java.text.SimpleDateFormat
import java.util.*

@Preview
@Composable
fun ProfileScreen() {
    val coroutineScope = rememberCoroutineScope()
    val medicationHistoryViewModel: AlarmViewModel = viewModel(
        factory = MedicationHistoryViewModelFactory(
            MedicationHistoryRepositoryImpl()
        )
    )

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "User profile picture"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Victor Elezua", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "09076282648")

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Medication History",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val data by remember {
                medicationHistoryViewModel.getMedicationHistory(coroutineScope)
            }
            data?.let {
                if (it.isEmpty()) {
                    Text(
                        "No medication available",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    /*it.forEachIndexed { index, model ->

                    }*/
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(it) { medication ->
                            MedicationHistoryItem(medication)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationHistoryItem(medicationHistory: MedicationHistory) {
    val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
    Card(modifier = Modifier
        .padding(horizontal = 0.dp)
        .fillMaxWidth()
        .padding(bottom = 8.dp), backgroundColor = if (medicationHistory.complete == true) Color.Green else Color.Red) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("Medication: ${medicationHistory.medicationName!!}")
                Text("Description: ${medicationHistory.medicationDescription!!}")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Interval: ${medicationHistory.medicationInterval} hour(s)")
                    Text("${medicationHistory.medicationDosage} ${medicationHistory.medicationDosageType}")
                }
                Text("Date: ${dateTimeFormat.format(Date())}")
            }
        }
    }
}