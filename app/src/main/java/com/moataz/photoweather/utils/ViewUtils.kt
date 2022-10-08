package com.moataz.photoweather.utils

import android.text.Editable
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
        onChange: (Int,String,Boolean,Int) -> Unit) {
        var text = ""
        var color = false
        var align = 0
        view.photoTextInputLayout.hint = title
        view.photoInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
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
}