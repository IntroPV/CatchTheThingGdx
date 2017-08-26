package ar.com.pablitar.catchthethinggdx

import ar.com.pablitar.libgdx.commons.ResourceManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Resources extends ResourceManager {
  lazy val defaultFont = new BitmapFont("simple-font.fnt")
  lazy val smallFont = new BitmapFont("simple-font-small.fnt")

  def atlas: TextureAtlas = {
    ??? //NO importa todavía
  }
  
  def dispose() = {
    defaultFont.dispose()
  }
}