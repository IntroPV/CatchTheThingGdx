package ar.com.pablitar.catchthethinggdx

import com.badlogic.gdx.ApplicationAdapter
import ar.com.pablitar.libgdx.commons.rendering.Renderers
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class CatchTheThingGdxGame extends ApplicationAdapter {
  lazy val renderers = new Renderers
  var world = new World(this)
  lazy val worldRenderer = new WorldRenderer(() => world)

  override def render() = {
    val delta = Gdx.graphics.getDeltaTime()

    world.update(delta)

    renderers.withRenderCycle() {
      worldRenderer.renderOn(renderers)
    }    
  }
  
  override def dispose() {
    Resources.dispose()
  }

  def restart() = {
    world = new World(this)
  }
}