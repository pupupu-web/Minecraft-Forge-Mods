package dev.whiteenderman;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(WhiteEndermanMod.MOD_ID)
public final class WhiteEndermanMod {
    public static final String MOD_ID = "whiteenderman";

    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<EnderMan>> WHITE_ENDERMAN =
            ENTITIES.register("white_enderman", () ->
                    EntityType.Builder.of(EnderMan::new, MobCategory.MONSTER)
                            .sized(0.6F, 2.9F)
                            .clientTrackingRange(8)
                            .build(ENTITIES.key("white_enderman")));

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<SpawnEggItem> WHITE_ENDERMAN_SPAWN_EGG =
            ITEMS.register("white_enderman_spawn_egg", () ->
                    new SpawnEggItem(new Item.Properties()
                            .setId(ITEMS.key("white_enderman_spawn_egg"))
                            .spawnEgg(WHITE_ENDERMAN.get())));

    public WhiteEndermanMod(FMLJavaModLoadingContext context) {
        var group = context.getModBusGroup();
        ENTITIES.register(group);
        ITEMS.register(group);

        EntityAttributeCreationEvent.BUS.addListener(event ->
                event.put(WHITE_ENDERMAN.get(), EnderMan.createAttributes().build()));

        BuildCreativeModeTabContentsEvent.BUS.addListener(event -> {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(WHITE_ENDERMAN_SPAWN_EGG.get());
            }
        });
    }
}
