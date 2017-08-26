package ar.com.pablitar.catchthethinggdx

import ar.com.pablitar.libgdx.commons.traits.Positioned
import ar.com.pablitar.libgdx.commons.traits.SpeedBehaviour
import ar.com.pablitar.libgdx.commons.extensions.VectorExtensions._
import ar.com.pablitar.libgdx.commons.extensions.ShapeExtensions._
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import ar.com.pablitar.libgdx.commons.traits.RectangularPositioned
import com.badlogic.gdx.math.MathUtils
import ar.com.pablitar.libgdx.commons.traits.CircularPositioned
import scala.collection.mutable.ArrayBuffer
import ar.com.pablitar.libgdx.commons.DelayedRemovalBuffer
import ar.com.pablitar.libgdx.commons.traits.AcceleratedSpeedBehaviour
import com.badlogic.gdx.math.Intersector

class Catcher(world: World) extends RectangularPositioned with SpeedBehaviour {
  position = new Vector2(world.width / 2, 100)

  val baseCatcherSpeedMagnitude = 800

  var score = 0

  val width = 200f
  val height = 80f

  val maxTurbo = 1.0f
  val turboRechargeRate = 0.5f
  var turbo = maxTurbo
  val turboRechargeCooldown = 0.5f
  var turboRechargeCooldownCounter = 0f

  def update(delta: Float) = {
    processTurbo(delta)
    processMovement(delta)
    checkCollisionAgainstSeeds(delta)
  }

  def catcherSpeedMagnitude = {
    if (turbo > 0 && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
      baseCatcherSpeedMagnitude * 2
    } else {
      baseCatcherSpeedMagnitude
    }
  }

  def processMovement(delta: Float) = {
    speed = (0, 0)
    if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      speed.add(-catcherSpeedMagnitude, 0)
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      speed.add(catcherSpeedMagnitude, 0)
    }

    applySpeed(delta)
  }

  def checkCollisionAgainstSeeds(delta: Float) = {
    world.seeds.foreachWithRemoveSupport(checkCollisionAgainst(_))
  }

  def checkCollisionAgainst(aSeed: Seed) = {
    aSeed.circle.checkCollision(rectangle).foreach(aCollision => {
      sumScore(aSeed)
      world.removeSeed(aSeed)
    })
  }

  def sumScore(aSeed: Seed) = {
    score += 1
  }

  def processTurbo(delta: Float) = {
    if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
      turbo = Math.max(turbo - delta, 0)
      turboRechargeCooldownCounter = turboRechargeCooldown
    } else if (turboRechargeCooldownCounter <= 0) {
      turbo = Math.min(turbo + turboRechargeRate * delta, maxTurbo)
    } else {
      turboRechargeCooldownCounter -= delta
    }
  }
}

class SeedSpawner(world: World) {
  var nextSpawnTime = generateSpawnTime
  def update(delta: Float) = {
    nextSpawnTime -= delta
    if (nextSpawnTime <= 0) {
      spawnSeed
      nextSpawnTime = generateSpawnTime
    }
  }

  def generateSpawnTime: Float = {
    MathUtils.random(0.7f, 1.5f)
  }

  def spawnSeed = {
    val left = MathUtils.randomBoolean()
    val position = new Vector2(if (left) 0 else world.width, world.height)
    val speedDirection = if (left) 1 else -1
    world.addSeed(new Seed(position, new Vector2(speedDirection * randomSpeedMagnitude, 0), world))
  }

  def randomSpeedMagnitude = MathUtils.random(50f, 900f)

  val POSITION_MARGIN = 100

}

class Seed(p: Vector2, aSpeed: Vector2, world: World) extends CircularPositioned with AcceleratedSpeedBehaviour {
  val radius = 40f
  this.position = p
  this.speed = aSpeed

  val acceleration = new Vector2(0, -800)

  def update(delta: Float) = {
    updateValues(delta)
    if (position.y < -radius) {
      world.removeSeed(this)
    }
  }
}

trait WorldState {
  def update(world: World, delta: Float)
}
object Ended extends WorldState {
  def update(world: World, delta: Float): Unit = {
    if (Gdx.input.isKeyPressed(Keys.ENTER)) {
      world.restart()
    }
  }
}
object Started extends WorldState {
  def update(world: World, delta: Float): Unit = {
    world.catcher.update(delta)
    world.seedSpawner.update(delta)

    world.seeds.foreachWithRemoveSupport(_.update(delta))
    world.remaining -= delta
    if(world.remaining <= 0) {
      world.remaining = 0
      world.finishRound()
    }
  }
}
object Initial extends WorldState {
  def update(world: World, delta: Float): Unit = {
    if (Gdx.input.isKeyPressed(Keys.ENTER)) {
      world.start()
    }
  }
}

class World(game: CatchTheThingGdxGame) {
  val catcher = new Catcher(this)
  val seedSpawner = new SeedSpawner(this)
  val seeds = new DelayedRemovalBuffer[Seed]
  var state: WorldState = Initial
  val roundDuration = 30f
  var remaining = roundDuration

  def update(delta: Float) = {
    state.update(this, delta)
  }

  def addSeed(aSeed: Seed) = {
    seeds.add(aSeed)
  }

  def removeSeed(aSeed: Seed) = {
    seeds.removeDelayed(aSeed)
  }

  def width = Configuration.VIEWPORT_WIDTH
  def height = Configuration.VIEWPORT_HEIGHT

  def start() = {
    state = Started
  }

  def finishRound() = {
    state = Ended
  }

  def restart() = {
    game.restart()
  }
}