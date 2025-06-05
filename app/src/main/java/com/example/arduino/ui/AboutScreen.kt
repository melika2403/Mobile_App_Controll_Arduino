package com.example.arduino.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "My Smart Home",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = """
                Dobrodošli u budućnost!

                Ova aplikacija omogućava daljinsko upravljanje vašim pametnim uređajima – uključujući svetla, TV, klima uređaj i zvučnike – sa bilo kog mesta.

                Automatska kontrola na osnovu temperature, pregled istorije korišćenja i više.

                Verzija aplikacije: 1.0.0
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
