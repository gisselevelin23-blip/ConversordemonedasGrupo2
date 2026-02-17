package com.ucenm.conversordemonedasgrupo2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase modelo para manejar los datos individualmente
data class Conversion(
    val id: Int,
    val from: String,
    val to: String,
    val amount: Double,
    val result: Double,
    val date: String,
    var isFavorite: Int
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Monedas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE rates (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, rate REAL)")
        db?.execSQL("CREATE TABLE conversions (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, amount REAL, result REAL, date TEXT, is_favorite INTEGER DEFAULT 0)")

        db?.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('HNL', 'USD', 0.038)")
        db?.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('USD', 'HNL', 26.45)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS rates")
        db?.execSQL("DROP TABLE IF EXISTS conversions")
        onCreate(db)
    }

    fun guardarHistorial(from: String, to: String, amount: Double, result: Double) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("from_code", from)
        values.put("to_code", to)
        values.put("amount", amount)
        values.put("result", result)
        values.put("date", java.text.DateFormat.getDateTimeInstance().format(java.util.Date()))
        values.put("is_favorite", 0)
        db.insert("conversions", null, values)
    }

    // MODIFICADA: Ahora devuelve una lista de objetos 'Conversion' para el HistorialAdapter
    fun obtenerHistorialObjetos(): List<Conversion> {
        val lista = mutableListOf<Conversion>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM conversions ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(Conversion(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getString(5),
                    cursor.getInt(6)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerTasaDeCambio(from: String, to: String): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT rate FROM rates WHERE from_code = ? AND to_code = ?", arrayOf(from, to))
        var tasa = 1.0
        if (cursor.moveToFirst()) {
            tasa = cursor.getDouble(0)
        }
        cursor.close()
        return tasa
    }

    fun marcarComoFavorito(id: Int, favorito: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("is_favorite", if (favorito) 1 else 0)
        db.update("conversions", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }
    fun actualizarTasa(from: String, to: String, nuevaTasa: Double) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("rate", nuevaTasa)

        // Actualiza la tasa en la tabla 'rates'
        db.update("rates", values, "from_code = ? AND to_code = ?", arrayOf(from, to))
        db.close()
    }
}