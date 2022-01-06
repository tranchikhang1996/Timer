package com.khangtc.presentation.actionstate

import com.khangtc.R

class InitState(override val controller: ActionController) : ActionState {
    companion object {
        const val Tag = "InitState"
    }

    override fun applyUI() {
        controller.setCancelText(R.string.cancel, R.color.gray)
        controller.setStartBackground(R.drawable.green_circle)
        controller.setStartText(R.string.start, R.color.green)
        controller.setTimerVisible(false)
        controller.setPickerVisible(true)
    }

    override val tag: String
        get() = Tag

    override fun clickCancel() {
        applyUI()
    }

    override fun clickStart() {
        val newState = RunningState(controller)
        controller.changeState(newState)
        newState.applyUI()
        controller.start()
    }
}