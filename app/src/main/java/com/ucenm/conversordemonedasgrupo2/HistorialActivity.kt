package com.ucenm.conversordemonedasgrupo2

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // Ahora este ID sí existirá en el XML
        val lvHistorial = findViewById<ListView>(R.id.lvHistorial)
        val db = DatabaseHelper(this)

        // Llamamos a la función que ya revisamos que está bien en tu DatabaseHelper
        val datos = db.obtenerHistorialObjetos()

        // Configuramos el adaptador
        val adaptador = HistorialAdapter(this, datos)
        lvHistorial.adapter = adaptador
    }
}