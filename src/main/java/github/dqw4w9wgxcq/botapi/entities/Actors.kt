package github.dqw4w9wgxcq.botapi.entities

import github.dqw4w9wgxcq.botapi.wrappers.entity.actor.Actor

abstract class Actors<A : Actor<out net.runelite.api.Actor>> : SceneEntities<A>()