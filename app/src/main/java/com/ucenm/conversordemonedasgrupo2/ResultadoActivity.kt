package com.ucenm.conversordemonedasgrupo2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        // 1. Recibir los datos con las LLAVES CORRECTAS (las que enviamos desde MainActivity)
        val monto = intent.getDoubleExtra("MONTO_ORIGEN", 0.0)
        val origen = intent.getStringExtra("SIMBOLO_ORIGEN") ?: ""
        val resultado = intent.getDoubleExtra("RESULTADO", 0.0)
        val destino = intent.getStringExtra("SIMBOLO_DESTINO") ?: ""

        // 2. Enlazar con los TextViews (Asegúrate que estos IDs existan en tu XML)
        val tvResumen = findViewById<TextView>(R.id.tvMontoEnviado) // Cambié el ID al que te pasé antes
        val tvResultadoGrande = findViewById<TextView>(R.id.tvResultadoFinal)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // 3. Mostrar la información
        tvResumen.text = "Has convertido $monto $origen a:"
        tvResultadoGrande.text = String.format("%.2f %s", resultado, destino)

        btnVolver.setOnClickListener { finish() }
    }
}