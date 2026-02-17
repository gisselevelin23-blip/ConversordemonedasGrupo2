package com.ucenm.conversordemonedasgrupo2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Monedas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla de tasas
        db?.execSQL("CREATE TABLE rates (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, rate REAL)")

        // Crear tabla de historial
        db?.execSQL("CREATE TABLE conversions (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, amount REAL, result REAL, date TEXT)")

        // Insertar tasas iniciales para que la app no empiece vacía
        db?.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('HNL', 'USD', 0.038)")
        db?.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('USD', 'HNL', 26.45)")
        // Puedes agregar más aquí...
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS rates")
        db?.execSQL("DROP TABLE IF EXISTS conversions")
        onCreate(db)
    }

    // Función para guardar una conversión en el historial
    fun guardarHistorial(from: String, to: String, amount: Double, result: Double) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("from_code", from)
        values.put("to_code", to)
        values.put("amount", amount)
        values.put("result", result)
        values.put("date", java.text.DateFormat.getDateTimeInstance().format(java.util.Date()))
        db.insert("conversions", null, values)
    }
    // NUEVA FUNCIÓN: Sirve para leer los datos que guardaste
    fun obtenerTodoElHistorial(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase

        // Consultamos la tabla "conversions" (que es como la llamaste en tu onCreate)
        val cursor = db.rawQuery("SELECT * FROM conversions ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                // Sacamos los datos de las columnas
                val de = cursor.getString(1)   // from_code
                val a = cursor.getString(2)    // to_code
                val monto = cursor.getDouble(3) // amount
                val res = cursor.getDouble(4)   // result

                // Formateamos el texto que se verá en la lista
                lista.add("Cambio: $monto $de a $res $a")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}