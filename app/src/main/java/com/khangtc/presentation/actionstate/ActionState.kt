package com.khangtc.presentation.actionstate

sealed interface ActionState {
    companion object {
        const val TAG = "ActionState"
    }
    val controller: ActionController
    val tag: String
    fun clickCancel() = Unit
    fun clickStart() = Unit
    fun applyUI() = Unit
}