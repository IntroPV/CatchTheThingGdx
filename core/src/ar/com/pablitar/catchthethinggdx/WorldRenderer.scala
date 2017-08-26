package ar.com.pablitar.catchthethinggdx

import ar.com.pablitar.libgdx.commons.rendering.Renderable
import ar.com.pablitar.libgdx.commons.rendering.Renderers
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import ar.com.pablitar.libgdx.commons.DelayedRemovalBuffer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align

class WorldRenderer(world: World) {
  
  def renderOn(renderers: Renderers): Unit = {
    renderCatcher(renderers, world.catcher)
    renderSeeds(renderers, world.seeds)
    renderScore(renderers, world.catcher.score)
    renderTurbo(renderers, world.catcher.turbo)
  }
  
  def renderCatcher(renderers: Renderers, catcher: Catcher) = {
    renderers.withShapes(ShapeType.Filled)(shapesRenderer => {
      shapesRenderer.setColor(Color.WHITE)
      shapesRenderer.rect(catcher.topLeft.x, catcher.topLeft.y, catcher.width, catcher.height)
    })
  }

  def renderSeeds(renderers: Renderers, seeds: DelayedRemovalBuffer[Seed]) = {
    seeds.foreach(renderSeed(renderers, _))
  }

  def renderSeed(renderers: Renderers, seed: Seed) = {
    renderers.withShapes(ShapeType.Filled)(shapesRenderer => {
      shapesRenderer.setColor(Color.GREEN)
      shapesRenderer.circle(seed.x, seed.y, seed.radius)
    })
  }

  def renderScore(renderers: Renderers, score: Int) = {
    renderers.withSprites { spriteRenderer =>
      Resources.defaultFont.draw(spriteRenderer, "Score: " + score, 30, Configuration.VIEWPORT_HEIGHT - 30)
    }
  }

  def renderTurbo(renderers: Renderers, turbo: Float) = {
    renderers.withSprites { spriteRenderer => 
      Resources.defaultFont.draw(spriteRenderer, f"Turbo $turbo%.2f", Configuration.VIEWPORT_WIDTH - 30,
          Configuration.VIEWPORT_HEIGHT - 30, 0, Align.right, false)
    }
  }
}