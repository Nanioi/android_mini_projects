package com.nanioi.audiorecorder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

// subclass a view 보기 ( creating a custom view class )

class RecordButton(
        context: Context,
        attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING,
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
        }
    }
}