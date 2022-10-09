package com.moataz.photoweather.utils

import android.graphics.*
//noinspection ExifInterface
import android.media.ExifInterface
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import com.moataz.photoweather.databinding.CompPhotoDataBinding
import com.moataz.photoweather.utils.Constants.ALIGN_CENTER
import com.moataz.photoweather.utils.Constants.ALIGN_LEFT
import com.moataz.photoweather.utils.Constants.ALIGN_RIGHT

object ViewUtils {
    fun setPhotoDataView(
        view: CompPhotoDataBinding,
        title: String,
        index:Int,
        inputType: Int,
        onChange: (Int,String,Boolean,Int) -> Unit) {
        var text = ""
        var color = false
        var align = ALIGN_LEFT
        view.photoTextInputLayout.hint = title
        view.photoInputEditText.inputType = inputType
        view.photoInputEditText.maxLines = 1

        if(index==0)
            view.photoInputEditText.filters = arrayOf(InputFilter.LengthFilter(2))
        else
            view.photoInputEditText.filters = arrayOf(InputFilter.LengthFilter(20))

        view.photoInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
                if(index==0)
                    text+="°C"
                onChange.invoke(index,text,color,align)
            }
        })
        view.blackSwitch.setOnCheckedChangeListener { _, isChecked ->
            color = isChecked
            onChange.invoke(index,text,color,align)
        }
        view.alignLeftImageView.setOnClickListener {
            align = ALIGN_LEFT
            onChange.invoke(index,text,color,align)
        }
        view.alignCenterImageView.setOnClickListener {
            align = ALIGN_CENTER
            onChange.invoke(index,text,color,align)
        }
        view.alignRightImageView.setOnClickListener {
            align = ALIGN_RIGHT
            onChange.invoke(index,text,color,align)
        }
    }

    fun setImageOrientation(bitmap: Bitmap,fileName:String):Bitmap?{
        var newBitmap:Bitmap? = null
        try {
            val exif = ExifInterface(fileName)
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                8 -> matrix.postRotate(270f)
            }
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Exception) { }
        return newBitmap
    }

    fun drawText(bitmap:Bitmap, photoDataList:ArrayList<PhotoData>): Bitmap{
        val tmpBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)
        val canvas = Canvas(tmpBitmap)
        var y = canvas.height - 200f

        for(data in photoDataList) {
            Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                this.textSize = 150f
                this.color =if(data.color)
                    Color.BLACK
                else
                    Color.WHITE


                var x = 0f
                when (data.align){
                    ALIGN_LEFT -> {
                        this.textAlign = Paint.Align.LEFT
                        x = 24f
                    }
                    ALIGN_CENTER -> {
                        this.textAlign = Paint.Align.CENTER
                        x = canvas.width / 2f
                    }
                    ALIGN_RIGHT -> {
                        this.textAlign = Paint.Align.RIGHT
                        x = canvas.width - 24f
                    }
                }
                typeface = Typeface.SERIF
                canvas.drawText(data.text, x, y, this)
                y-= 200f
            }
        }
        return tmpBitmap
    }

    fun disablePhotoDataView(view: CompPhotoDataBinding) {
        view.root.alpha = 0.5F
        view.root.isEnabled = false
        view.photoTextInputLayout.isEnabled = false
        view.blackSwitch.isEnabled = false
        view.alignRightImageView.isEnabled = false
        view.alignCenterImageView.isEnabled = false
        view.alignLeftImageView.isEnabled = false
    }

    fun enablePhotoDataView(view: CompPhotoDataBinding) {
        view.root.alpha = 1F
        view.root.isEnabled = true
        view.photoTextInputLayout.isEnabled = true
        view.blackSwitch.isEnabled = true
        view.alignRightImageView.isEnabled = true
        view.alignCenterImageView.isEnabled = true
        view.alignLeftImageView.isEnabled = true
    }
}