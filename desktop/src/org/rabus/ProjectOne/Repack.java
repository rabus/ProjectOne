package org.rabus.ProjectOne;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

import java.io.IOException;

public class Repack
{
    public static void main(String[] args) throws IOException
    {
        Settings settings = new Settings();
        Settings settingsArt = new Settings();
        settings.format = Format.RGBA4444;
        settings.filterMin = TextureFilter.Nearest;
        settings.filterMag = TextureFilter.Nearest;
        settings.alphaThreshold = 0;
        settings.pot = true;
        settings.paddingX = 0;
        settings.paddingY = 0;
        settings.duplicatePadding = true;
        settings.minWidth = 16;
        settings.minHeight = 16;
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.debug = false;
        settingsArt.format = Format.RGB565;

        TexturePacker2.process(settings, "../development/gfx/creatures/", "../android/assets/gfx/", "creatures");
        TexturePacker2.process(settings, "../development/gfx/textures/", "../android/assets/gfx/", "textures");
        TexturePacker2.process(settings, "../development/gfx/sprites/", "../android/assets/gfx/", "sprites");
        TexturePacker2.process(settingsArt, "../development/gfx/backgrounds/", "../android/assets/gfx/", "backgrounds");
    }
}
