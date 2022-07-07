package github.dqw4w9wgxcq.botapi.sceneentities.actors

import github.dqw4w9wgxcq.botapi.sceneentities.SceneEntities
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor.Actor

abstract class Actors<A : Actor<out net.runelite.api.Actor>> : SceneEntities<A>()