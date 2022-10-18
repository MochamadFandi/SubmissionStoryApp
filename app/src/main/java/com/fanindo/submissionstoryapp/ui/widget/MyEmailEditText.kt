package com.fanindo.submissionstoryapp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.fanindo.submissionstoryapp.R

class MyEmailEditText : AppCompatEditText {

    private lateinit var startIconImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.hint_email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        compoundDrawablePadding = 8

    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        startIconImage =  ContextCompat.getDrawable(context, R.drawable.ic_baseline_email_24) as Drawable
        setButtonDrawables(
            startOfTheText = startIconImage
        )

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!isValidEmail(s.toString())) error = resources.getString(R.string.error_char_email)
                // Do nothing.
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null

    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

    }
}