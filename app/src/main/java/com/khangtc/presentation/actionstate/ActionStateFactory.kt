package com.khangtc.presentation.actionstate

object ActionStateFactory {
    fun getActionState(tag: String, controller: ActionController) = when (tag) {
        PausedState.Tag -> PausedState(controller)
        RunningState.Tag -> RunningState(controller)
        else -> InitState(controller)
    }
}