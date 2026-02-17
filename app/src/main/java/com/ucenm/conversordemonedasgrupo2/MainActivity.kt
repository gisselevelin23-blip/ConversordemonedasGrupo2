package com.ucenm.conversordemonedasgrupo2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val db = DatabaseHelper(this)

        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 1. REFERENCIAS DE ELEMENTOS (Agrega btnVerHistorial aquí)
        val etImporte = findViewById<EditText>(R.id.etImporte)
        val spOrigen = findViewById<Spinner>(R.id.spOrigen)
        val spDestino = findViewById<Spinner>(R.id.spDestino)
        val btnConvertir = findViewById<Button>(R.id.btnConvertir)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        // ESTA ES LA NUEVA REFERENCIA:
        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)

        val listaMonedas = arrayOf("USD", "HNL", "GTQ", "NIO", "CRC", "SVC")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaMonedas)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spOrigen.adapter = adaptador
        spDestino.adapter = adaptador

        // 2. LÓGICA DEL BOTÓN CONVERTIR
        btnConvertir.setOnClickListener {
            val montoTexto = etImporte.text.toString()

            if (montoTexto.isEmpty()) {
                tvResultado.text = "Por favor, ingresa un monto"
            } else {
                val monto = montoTexto.toDouble()
                val monedaOrigen = spOrigen.selectedItem.toString()
                val monedaDestino = spDestino.selectedItem.toString()

                val resultado = realizarConversion(monto, monedaOrigen, monedaDestino)

                db.guardarHistorial(monedaOrigen, monedaDestino, monto, resultado)

                tvResultado.text = "Resultado: $resultado $monedaDestino"

                val intent = Intent(this, ResultadoActivity::class.java)
                intent.putExtra("MONTO_ORIGEN", monto)
                intent.putExtra("SIMBOLO_ORIGEN", monedaOrigen)
                intent.putExtra("RESULTADO", resultado)
                intent.putExtra("SIMBOLO_DESTINO", monedaDestino)
                startActivity(intent)
            }
        }

        // 3. LÓGICA DEL NUEVO BOTÓN (Agrégalo aquí al final del onCreate)
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarConversion(monto: Double, origen: String, destino: String): Double {
        val tasas = mapOf(
            "USD" to 1.0,
            "HNL" to 26.45,
            "GTQ" to 7.73,
            "NIO" to 36.62,
            "CRC" to 510.20,
            "SVC" to 8.75
        )
        val enDolares = monto / (tasas[origen] ?: 1.0)
        val total = enDolares * (tasas[destino] ?: 1.0)
        return String.format("%.2f", total).toDouble()
    }
}