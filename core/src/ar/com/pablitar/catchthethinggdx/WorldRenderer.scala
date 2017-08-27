package ar.com.pablitar.catchthethinggdx

import ar.com.pablitar.libgdx.commons.rendering.Renderable
import ar.com.pablitar.libgdx.commons.rendering.Renderers
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import ar.com.pablitar.libgdx.commons.DelayedRemovalBuffer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.graphics.g2d.GlyphLayout

class WorldRenderer(world: () => World) {

  val hudPadding = 30f

  def renderOn(renderers: Renderers): Unit = {
    renderCatcher(renderers, world().catcher)
    renderSeeds(renderers, world().seeds)
    renderScore(renderers, world().catcher.score)
    renderTurbo(renderers, world().catcher)
    renderRemainingTime(renderers, world())
    renderWorldState(renderers, world())
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
    renderers.withShapes()(shapesRenderer => {
      shapesRenderer.setColor(Color.GREEN)
      shapesRenderer.circle(seed.x, seed.y, seed.radius)
    })
  }

  def renderScore(renderers: Renderers, score: Int) = {

    renderers.withSprites { batch =>
      val glyph = Resources.smallFont.draw(batch, "Score", hudPadding, Configuration.VIEWPORT_HEIGHT - hudPadding)
      Resources.defaultFont.draw(batch, score.toString(), 
          hudPadding + glyph.width / 2, Configuration.VIEWPORT_HEIGHT - hudPadding - glyph.height - 10, 0, Align.center, false)
    }
  }

  def renderTurbo(renderers: Renderers, catcher: Catcher) = {
    val turboWidth = 300f
    val turboHeight = 40f
    val turboLeft = Configuration.VIEWPORT_WIDTH - turboWidth - hudPadding
    val turboPadding = 5f
    val labelYPosition = Configuration.VIEWPORT_HEIGHT - hudPadding
    var glyph: GlyphLayout = null

    renderers.withSprites { spriteRenderer =>
      glyph = Resources.smallFont.draw(spriteRenderer, "TURBO", turboLeft, labelYPosition)
    }

    renderers.withShapes() { shapeRenderer =>
      shapeRenderer.setColor(Color.WHITE)
      shapeRenderer.rect(turboLeft, labelYPosition - (glyph.height + 10f + turboHeight), turboWidth, turboHeight)
      shapeRenderer.setColor(Color.CHARTREUSE)
      shapeRenderer.rect(turboLeft + turboPadding, labelYPosition - (glyph.height + 10f + turboHeight) + turboPadding,
        (turboWidth - turboPadding * 2) * (catcher.turbo / catcher.maxTurbo), turboHeight - turboPadding * 2)
    }
  }

  def renderRemainingTime(renderers: Renderers, world: World) = {
    renderers.withSprites { batch =>
      val glyph = Resources.smallFont.draw(batch, "Time Left", Configuration.VIEWPORT_WIDTH / 2, Configuration.VIEWPORT_HEIGHT - hudPadding, 0, Align.center, false)
      Resources.defaultFont.draw(batch, f"${world.remaining}%.1fs",
        Configuration.VIEWPORT_WIDTH / 2, Configuration.VIEWPORT_HEIGHT - hudPadding - glyph.height - 10, 0, Align.center, false)
    }
  }

  def renderWorldState(renderers: Renderers, world: World) = {
    renderers.withSprites { batch =>

      val message = world.state match {
        case Initial => Some("Press enter to start game")
        case Ended   => Some(s"Game Over. Your score was: ${world.catcher.score}.\nPress enter to restart")
        case _       => None
      }

      Resources.defaultFont.setColor(Color.GOLD)

      message.foreach(Resources.defaultFont.draw(batch, _,
        Configuration.VIEWPORT_WIDTH / 2, Configuration.VIEWPORT_HEIGHT / 2, 0, Align.center, true))

      Resources.defaultFont.setColor(Color.WHITE)
    }
  }
}