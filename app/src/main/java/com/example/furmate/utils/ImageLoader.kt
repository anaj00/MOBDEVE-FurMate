package com.example.furmate.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.firestore.Blob

class ImageLoader {
    companion object {
        fun fromBlobScaled(blob: Blob, width: Int, height: Int): Bitmap {
            val imageBytes = blob.toBytes()
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return Bitmap.createScaledBitmap(bitmap, width, height, false)
        }
    }
}