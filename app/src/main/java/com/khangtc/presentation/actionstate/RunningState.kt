package com.khangtc.presentation.actionstate

import com.khangtc.R

class RunningState(override val controller: ActionController) : ActionState {
    companion object {
        const val Tag = "RunningState"
    }
    override fun applyUI() {
        controller.setCancelText(R.string.cancel, R.color.white)
        controller.setStartBackground(R.drawable.orange_circile)
        controller.setStartText(R.string.pause, R.color.orange)
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
        val newState = PausedState(controller)
        controller.changeState(newState)
        newState.applyUI()
        controller.pause()
    }
}