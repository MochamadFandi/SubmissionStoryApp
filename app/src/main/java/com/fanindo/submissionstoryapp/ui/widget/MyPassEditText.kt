package com.fanindo.submissionstoryapp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.fanindo.submissionstoryapp.R

class MyPassEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var startIconImage: Drawable
    private lateinit var visibleIconImage: Drawable
    private lateinit var visibleOffIconImage: Drawable

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
        hint = resources.getString(R.string.hint_pass)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        compoundDrawablePadding = 8


    }

    private fun init() {
        setOnTouchListener(this)
        startIconImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24) as Drawable
        visibleIconImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24) as Drawable
        visibleOffIconImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_off_24) as Drawable
        setButtonDrawables(
            startOfTheText = startIconImage,
            endOfTheText = visibleOffIconImage
        )

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 6) error = resources.getString(R.string.error_char_pass)
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val clearButtonStart: Float
        val clearButtonEnd: Float
        var isClearButtonClicked = false
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            clearButtonEnd = (visibleOffIconImage.intrinsicWidth + paddingStart).toFloat()
            when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
            }
        } else {
            clearButtonStart = (width - paddingEnd - visibleOffIconImage.intrinsicWidth).toFloat()
            when {
                event.x > clearButtonStart -> isClearButtonClicked = true
            }
        }

        if (isClearButtonClicked) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    visibleIconImage =
                        ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24) as Drawable
                    setButtonDrawables()
                    setButtonDrawables(
                        startOfTheText = startIconImage,
                        endOfTheText = visibleIconImage
                    )
                    transformationMethod = HideReturnsTransformationMethod.getInstance()
                    return true

                }
                MotionEvent.ACTION_UP -> {
                    visibleOffIconImage =
                        ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_off_24) as Drawable
                    setButtonDrawables()
                    setButtonDrawables(
                        startOfTheText = startIconImage,
                        endOfTheText = visibleOffIconImage
                    )
                    transformationMethod = PasswordTransformationMethod.getInstance()
                    return true
                }
                else -> return false
            }
        } else return false
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