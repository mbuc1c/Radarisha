package com.bucic.radarisha.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat

object VectorDrawableUtils {

    /**
     * Converts a vector drawable to a bitmap and draws text on top of it.
     *
     * @param context The context to use for retrieving the drawable.
     * @param drawableId The resource ID of the vector drawable.
     * @param text The text to draw on the bitmap.
     * @return A bitmap with the vector drawable and the text drawn on it.
     */
    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int, text: String): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // Draw the text on top of the bitmap
        val paint = Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER

        // Calculate position for text (centered)
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)

        return bitmap
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}