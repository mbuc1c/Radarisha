package com.bucic.radarisha.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat

object VectorDrawableUtils {

//    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int, text: String): Bitmap {
//        val drawable = ContextCompat.getDrawable(context, drawableId)
//        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        drawable.setBounds(0, 0, canvas.width, canvas.height)
//        drawable.draw(canvas)
//
//        // Draw the text on top of the bitmap
//        val paint = Paint()
//        paint.color = android.graphics.Color.BLACK
//        paint.textSize = 40f
//        paint.textAlign = Paint.Align.CENTER
//
//        // Calculate position for text (centered)
//        val xPos = canvas.width / 2
//        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
//        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
//
//        return bitmap
//    }

//    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
//        val drawable = ContextCompat.getDrawable(context, drawableId)
//        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        drawable.setBounds(0, 0, canvas.width, canvas.height)
//        drawable.draw(canvas)
//        return bitmap
//    }

    fun getBitmapFromVectorDrawable(
        context: Context,
        drawableId: Int,
        backgroundColorResId: Int,
        text: String
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val backgroundColor = ContextCompat.getColor(context, backgroundColorResId)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Define the radius for rounded corners
        val cornerRadius = 30f

        // Create a rounded rectangle background
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL

        val rectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        // Draw the vector drawable on top
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // Draw the text on top of the bitmap
        val textPaint = Paint()
        textPaint.color = android.graphics.Color.BLACK
        textPaint.textSize = 40f
        textPaint.textAlign = Paint.Align.CENTER

        // Calculate position for text (centered)
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), textPaint)

        return bitmap
    }

    fun getBitmapFromVectorDrawable(
        context: Context,
        drawableId: Int,
        backgroundColorResId: Int
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val backgroundColor = ContextCompat.getColor(context, backgroundColorResId)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Define the radius for rounded corners
        val cornerRadius = 30f

        // Create a rounded rectangle background
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL

        val rectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        // Draw the vector drawable on top
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}