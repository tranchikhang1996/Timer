package com.khangtc.presentation.actionstate

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ActionController {
    fun changeState(state: ActionState)
    fun setStartBackground(@DrawableRes id: Int)
    fun setStartText(@StringRes id: Int, @ColorRes color: Int)
    fun setCancelText(@StringRes id: Int, @ColorRes color: Int)
    fun setPickerVisible(isVisible: Boolean)
    fun setTimerVisible(isVisible: Boolean)
    fun start()
    fun pause()
    fun resumeCountDown()
    fun cancel()
}