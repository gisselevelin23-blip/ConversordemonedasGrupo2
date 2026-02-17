package com.ucenm.conversordemonedasgrupo2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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

        // 1. REFERENCIAS DE ELEMENTOS
        val etImporte = findViewById<EditText>(R.id.etImporte)
        val spOrigen = findViewById<Spinner>(R.id.spOrigen)
        val spDestino = findViewById<Spinner>(R.id.spDestino)
        val btnConvertir = findViewById<Button>(R.id.btnConvertir)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)

        // BOTÓN DEL RETO ADICIONAL
        val btnConfigurar = findViewById<Button>(R.id.btnConfigurarTasas)

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

                // Guardamos en la base de datos
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

        // 3. LÓGICA DEL BOTÓN HISTORIAL
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        // 4. LÓGICA DEL RETO ADICIONAL (Configurar Tasas)
        btnConfigurar.setOnClickListener {
            mostrarDialogoConfiguracion()
        }
    }

    private fun realizarConversion(monto: Double, origen: String, destino: String): Double {
        val db = DatabaseHelper(this)
        if (origen == destino) return monto

        // BUSCAMOS LA TASA EN LA TABLA 'rates'
        val tasa = db.obtenerTasaDeCambio(origen, destino)

        val total = monto * tasa
        return String.format("%.2f", total).toDouble()
    }

    // FUNCIÓN PARA EL RETO ADICIONAL: TASAS PERSONALIZADAS
    private fun mostrarDialogoConfiguracion() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Configurar Tasa Personalizada")

        val monedaOrigen = findViewById<Spinner>(R.id.spOrigen).selectedItem.toString()
        val monedaDestino = findViewById<Spinner>(R.id.spDestino).selectedItem.toString()

        builder.setMessage("Ingrese la nueva tasa para $monedaOrigen a $monedaDestino:")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Ejemplo: 24.65"
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevaTasaTexto = input.text.toString()
            val nuevaTasa = nuevaTasaTexto.toDoubleOrNull()
            if (nuevaTasa != null) {
                val db = DatabaseHelper(this)
                // Actualizamos en SQLite
                db.actualizarTasa(monedaOrigen, monedaDestino, nuevaTasa)
                Toast.makeText(this, "Tasa actualizada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Valor no válido", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}