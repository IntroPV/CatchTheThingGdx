package ar.com.pablitar.catchthethinggdx

import ar.com.pablitar.libgdx.commons.ResourceManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Resources extends ResourceManager {
  lazy val defaultFont = managedFont("simple-font.fnt")
  lazy val smallFont = managedFont("simple-font-small.fnt")
}