package github.dqw4w9wgxcq.botapi.wrappers

import github.dqw4w9wgxcq.botapi.commons.byEquals

interface Interactable {
    val actions: Array<String?>?
    fun interact(actionMatches: (String) -> Boolean): Any

    val filteredActions: List<String>
        get() = actions?.filterNotNull() ?: emptyList()

    fun interact(actionIgnoreCase: String): Any {
        return interact(byEquals(actionIgnoreCase))
    }

    fun hasAction(actionMatches: (String) -> Boolean): Boolean {
        return filteredActions.any(actionMatches)
    }

    fun hasAction(actionIgnoreCase: String): Boolean {
        return hasAction(byEquals(actionIgnoreCase))
    }
}
