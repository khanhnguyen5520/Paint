package com.lutech.paintV3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class WidgetListAdapter(private val context: Context, private val widgetNames: Array<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return widgetNames.size
    }

    override fun getItem(position: Int): Any {
        return widgetNames[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = when (position) {
            0 -> inflater.inflate(R.layout.widget_1, parent, false)
            1 -> inflater.inflate(R.layout.widget_2, parent, false)
            else -> inflater.inflate(R.layout.widget_3, parent, false)
        }



        // Change background based on position
//        when (position) {
//            0 -> R.layout.widget_note.widget_container.setBackgroundColor(context.getColor(R.color.yellow))
//            1 -> widgetNote.setBackgroundColor(context.getColor(R.color.green))
//            else -> widgetNote.setBackgroundColor(context.getColor(R.color.blue))
//        }

        val title: TextView = view.findViewById(R.id.widget_title)
        val content: TextView = view.findViewById(R.id.widget_content)

        title.text = widgetNames[position]
        content.text = "Content for ${widgetNames[position]}"

        return view
    }
}
