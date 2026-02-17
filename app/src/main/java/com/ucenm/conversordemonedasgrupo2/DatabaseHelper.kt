package com.ucenm.conversordemonedasgrupo2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Monedas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla de tasas
        db?.execSQL("CREATE TABLE rates (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, rate REAL)")

        // 1. TABLA MODIFICADA: Se agregó la columna is_favorite
        db?.execSQL("CREATE TABLE conversions (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, amount REAL, result REAL, date TEXT, is_favorite INTEGER DEFAULT 0)")

        // Tasas iniciales
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
        // Guardamos por defecto como NO favorito (0)
        values.put("is_favorite", 0)
        db.insert("conversions", null, values)
    }

    // 2. FUNCIÓN MODIFICADA: Ahora lee la estrella si es favorito
    fun obtenerTodoElHistorial(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM conversions ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val de = cursor.getString(1)
                val a = cursor.getString(2)
                val monto = cursor.getDouble(3)
                val res = cursor.getDouble(4)

                // Leemos la columna 6 que es is_favorite
                val esFav = cursor.getInt(6)
                val estrella = if (esFav == 1) "⭐ " else ""

                lista.add("$estrella Cambio: $monto $de a $res $a")
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

    // Función extra por si quieres marcar favoritos después
    fun marcarComoFavorito(id: Int, favorito: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("is_favorite", if (favorito) 1 else 0)
        db.update("conversions", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }
}