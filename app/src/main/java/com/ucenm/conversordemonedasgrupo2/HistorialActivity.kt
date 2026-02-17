package com.ucenm.conversordemonedasgrupo2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val rvHistorial = findViewById<RecyclerView>(R.id.rvHistorial)
        val db = DatabaseHelper(this)

        // Obtenemos los datos y los ponemos en la lista
        val datos = db.obtenerTodoElHistorial()
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = HistorialAdapter(datos)
    }
}