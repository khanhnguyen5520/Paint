package com.lutech.paintV3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ColorGridAdapter(private val colors: List<Int>, private val onColorClick: (Int) -> Unit) :
    BaseAdapter() {

    override fun getCount(): Int = colors.size

    override fun getItem(position: Int): Any = colors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.color_grid_item, parent, false)
        val color = colors[position]
        view.findViewById<View>(R.id.color_view).setBackgroundColor(color)
        view.setOnClickListener { onColorClick(color) }
        return view
    }
}
