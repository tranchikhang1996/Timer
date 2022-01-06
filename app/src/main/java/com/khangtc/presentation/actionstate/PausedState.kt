package com.khangtc.presentation.actionstate

import com.khangtc.R

class PausedState(override val controller: ActionController) : ActionState {
    companion object {
        const val Tag = "PausedState"
    }

    override fun applyUI() {
        controller.setCancelText(R.string.cancel, R.color.white)
        controller.setStartBackground(R.drawable.green_circle)
        controller.setStartText(R.string.resume, R.color.green)
        controller.setTimerVisible(true)
        controller.setPickerVisible(false)
    }

    override val tag: String
        get() = Tag

    override fun clickCancel() {
        val newState = InitState(controller)
        controller.changeState(newState)
        newState.applyUI()
        controller.cancel()
    }

    override fun clickStart() {
        val newState = RunningState(controller)
        controller.changeState(newState)
        newState.applyUI()
        controller.resumeCountDown()
    }
}