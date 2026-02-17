package com.ucenm.conversordemonedasgrupo2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class HistorialAdapter(private val context: Context, private val lista: List<Conversion>) : BaseAdapter() {

    private val db = DatabaseHelper(context)

    override fun getCount(): Int = lista.size
    override fun getItem(position: Int): Any = lista[position]
    override fun getItemId(position: Int): Long = lista[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_conversion, parent, false)

        val item = lista[position]
        val tvInfo = view.findViewById<TextView>(R.id.tvInfo)
        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val imgFav = view.findViewById<ImageView>(R.id.imgFav)

        // Llenar los datos
        tvInfo.text = "Cambio: ${item.amount} ${item.from} a ${item.result} ${item.to}"
        tvFecha.text = "Fecha: ${item.date}"

        // Mostrar estrella prendida o apagada
        if (item.isFavorite == 1) {
            imgFav.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            imgFav.setImageResource(android.R.drawable.btn_star_big_off)
        }

        // Al tocar la estrella
        imgFav.setOnClickListener {
            val nuevoEstado = if (item.isFavorite == 1) 0 else 1
            db.marcarComoFavorito(item.id, nuevoEstado == 1)
            item.isFavorite = nuevoEstado
            notifyDataSetChanged() // Actualiza la lista visualmente
        }

        return view
    }
}