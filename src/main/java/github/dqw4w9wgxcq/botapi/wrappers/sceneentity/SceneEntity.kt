package github.dqw4w9wgxcq.botapi.wrappers.sceneentity

import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.withDescription
import github.dqw4w9wgxcq.botapi.interact.Interact
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.wrappers.Identifiable
import github.dqw4w9wgxcq.botapi.wrappers.Interactable
import github.dqw4w9wgxcq.botapi.wrappers.Locatable
import github.dqw4w9wgxcq.botapi.wrappers.Nameable

interface SceneEntity : Identifiable, Interactable, Locatable, Nameable {
    override fun interact(actionMatches: (String) -> Boolean): Boolean {
        debug { "door handle interact" }

        if (!Movement.checkDoor(sceneLocation, ignoreEndObject = true)) {
            return false
        }

        Interact.withEntity(this, actionMatches)
        return true
    }

    override fun interact(actionIgnoreCase: String): Boolean {
        return interact { it.equals(actionIgnoreCase, ignoreCase = true) }
    }

    fun interactUnchecked(actionMatches: (String) -> Boolean) {
        Interact.withEntity(this, actionMatches)
    }

    fun interactUnchecked(actionIgnoreCase: String) {
        interactUnchecked(
            { action: String -> action.equals(actionIgnoreCase, ignoreCase = true) }
                .withDescription("action[$actionIgnoreCase]")
        )
    }
}