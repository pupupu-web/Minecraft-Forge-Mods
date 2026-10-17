package dev.forgevisuals;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Forge 26.3 entry point.
 *
 * The first milestone uses Minecraft 26.3's own supported post-effect
 * system. All GPU work is performed by vanilla post-effect passes,
 * not by invasive renderer mixins or third-party OptiFine code.
 *
 * This is NOT an OptiFine/Iris shaderpack loader.
 */
@Mod(ForgeVisualShadersMod.MOD_ID)
public final class ForgeVisualShadersMod {
    public static final String MOD_ID = "forgevisuals";

    public ForgeVisualShadersMod(FMLJavaModLoadingContext context) {
        // Client resource assets (post effects / GLSL) are bundled with this mod.
    }
}
