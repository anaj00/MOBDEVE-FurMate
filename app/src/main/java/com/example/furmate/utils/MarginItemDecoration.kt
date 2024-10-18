package com.example.furmate.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            // Add top margin only for items other than the first item
            if (parent.getChildAdapterPosition(view) != 0) {
                top = spaceHeight
            }
        }
    }
}