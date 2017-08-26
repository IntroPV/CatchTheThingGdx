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
  val catcherSpeedMagnitude = 800
  
  var score = 0

  val width = 200f
  val height = 80f

  def update(delta: Float) = {
    processMovement(delta)
    checkCollisionAgainstSeeds(delta)
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
    score+=1
    println("Score: " + score)
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
    val position = new Vector2(if(left) 0 else world.width, world.height)
    val speedDirection = if(left) 1 else -1
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

class World {
  val catcher = new Catcher(this)
  val seedSpawner = new SeedSpawner(this)
  val seeds = new DelayedRemovalBuffer[Seed]

  def update(delta: Float) = {
    catcher.update(delta)
    seedSpawner.update(delta)

    seeds.foreachWithRemoveSupport(_.update(delta))
  }

  def addSeed(aSeed: Seed) = {
    seeds.add(aSeed)
  }

  def removeSeed(aSeed: Seed) = {
    seeds.removeDelayed(aSeed)
  }
  
  def width = Configuration.VIEWPORT_WIDTH
  def height = Configuration.VIEWPORT_HEIGHT
}