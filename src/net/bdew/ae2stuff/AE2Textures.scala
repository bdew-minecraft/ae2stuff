/*
 * Copyright (c) bdew, 2014 - 2020
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdew.ae2stuff

import net.bdew.lib.gui.Texture
import net.minecraft.util.ResourceLocation

object AE2Textures {
  val inscriberTexture = new ResourceLocation("appliedenergistics2", "textures/guis/inscriber.png")
  val inscriberBackground = Texture(inscriberTexture, 0, 0, 176, 176)
  val inscriberProgress = Texture(inscriberTexture, 135, 177, 6, 18)
  val upgradesBackground3 = Texture(inscriberTexture, 179, 0, 32, 68)
  val toolBoxBackground = Texture(inscriberTexture, 178, 86, 68, 68)

  val upgradesBackground5 = Texture("appliedenergistics2", "textures/guis/mac.png", 179, 0, 32, 104)
}
