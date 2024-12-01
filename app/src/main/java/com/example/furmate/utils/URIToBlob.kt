package com.example.furmate.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.furmate.R
import com.google.firebase.firestore.Blob
import java.io.ByteArrayOutputStream

class URIToBlob {
    companion object {
        fun uriToBlob(uri: Uri, context: Context): Blob? {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                return Blob.fromBytes(byteArray)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun getDefaultImageBlob(context: Context): Blob? {
            return try {
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.home)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Blob.fromBytes(byteArray)
            } catch (e: Exception) {
                Log.e("FormAddPetFragment", "Failed to load default image", e)
                null
            }
        }
    }
}