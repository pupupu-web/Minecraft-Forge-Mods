package dev.forgebuildcheck;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Tiny test mod: no networking, data collection or gameplay changes. */
@Mod(ForgeBuildCheck.MOD_ID)
public final class ForgeBuildCheck {
    public static final String MOD_ID = "forgebuildcheck";

    public ForgeBuildCheck(FMLJavaModLoadingContext context) {
        // Forge constructs this class when loading the mod.
    }
}
