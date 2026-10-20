package dev.pupupu.cozybeds;

import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Tucks vanilla bed occupants under a colored, collision-free display blanket.
 *
 * Using native block displays keeps the mod independent of fragile entity and
 * block renderer mixins (and therefore from Sodium / Iris rendering internals).
 */
@Mod(CozyBeds.MOD_ID)
public final class CozyBeds {
    public static final String MOD_ID = "cozybeds";
    private static final String ROOT_TAG = "cozybeds_generated";
    private long ticks;

    public CozyBeds(IEventBus modBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (++ticks % 10 != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            updateLevel(level);
        }
    }

    private static void updateLevel(ServerLevel level) {
        // Snapshot entities first; spawning/removing while iterating getAllEntities
        // directly may invalidate its iterator.
        List<Display.BlockDisplay> oldDisplays = new ArrayList<>();
        List<Sleeper> sleepers = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Display.BlockDisplay display
                    && display.getPersistentData().getBooleanOr(ROOT_TAG, false)) {
                oldDisplays.add(display);
            } else if ((entity instanceof Player || entity instanceof Villager)
                    && entity instanceof LivingEntity living && living.isSleeping()) {
                living.getSleepingPos().ifPresent(pos -> {
                    Sleeper sleeper = inspectBed(level, living, pos);
                    if (sleeper != null) sleepers.add(sleeper);
                });
            }
        }

        Set<Display.BlockDisplay> retained = new HashSet<>();
        for (Sleeper sleeper : sleepers) {
            ensureLayer(level, sleeper, "quilt", oldDisplays, retained);
            ensureLayer(level, sleeper, "cuff", oldDisplays, retained);
            ensureLayer(level, sleeper, "pillow", oldDisplays, retained);
        }

        // Also cleans orphan decorations after sleep ends, a bed breaks,
        // a sleeper logs out, or a chunk is loaded after a server restart.
        for (Display.BlockDisplay display : oldDisplays) {
            if (!retained.contains(display)) display.discard();
        }
    }

    private static Sleeper inspectBed(ServerLevel level, LivingEntity sleeper, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BedBlock bed)) return null;
        Direction facing = state.getValue(BedBlock.FACING);
        BlockPos head = state.getValue(BedBlock.PART) == BedPart.HEAD
                ? pos : pos.relative(facing);
        BlockPos foot = head.relative(facing.getOpposite());
        return new Sleeper(sleeper.getUUID().toString().replace("-", ""),
                head, foot, facing, bed.getColor());
    }

    private static void ensureLayer(ServerLevel level, Sleeper sleeper, String part,
                                    List<Display.BlockDisplay> existing,
                                    Set<Display.BlockDisplay> retained) {
        String ownerTag = sleeper.owner;
        long bedTag = sleeper.head.asLong();
        String colorTag = sleeper.color.getSerializedName();
        String partTag = part;

        // Reuse saved display entities. This prevents duplicates on server restart.
        for (Display.BlockDisplay display : existing) {
            CompoundTag tags = display.getPersistentData();
            if (!retained.contains(display) && ownerTag.equals(tags.getStringOr("cb_owner", ""))
                    && bedTag == tags.getLongOr("cb_bed", Long.MIN_VALUE)
                    && colorTag.equals(tags.getStringOr("cb_color", ""))
                    && partTag.equals(tags.getStringOr("cb_part", ""))) {
                retained.add(display);
                return;
            }
        }

        boolean axisX = sleeper.facing.getAxis() == Direction.Axis.X;
        double centerX;
        double centerZ;
        float sx;
        float sy;
        float sz;
        float height;
        Block block;

        if ("pillow".equals(part)) {
            centerX = sleeper.head.getX() + .5 + sleeper.facing.getStepX() * .16;
            centerZ = sleeper.head.getZ() + .5 + sleeper.facing.getStepZ() * .16;
            sx = axisX ? .34f : .78f;
            sz = axisX ? .78f : .34f;
            sy = .105f;
            height = .575f;
            block = Blocks.WOOL.white();
        } else {
            centerX = sleeper.foot.getX() + .5 + sleeper.facing.getStepX() * .21;
            centerZ = sleeper.foot.getZ() + .5 + sleeper.facing.getStepZ() * .21;
            if ("cuff".equals(part)) {
                // Thin white folded edge at the head end of the quilt.
                centerX += sleeper.facing.getStepX() * .62;
                centerZ += sleeper.facing.getStepZ() * .62;
                sx = axisX ? .09f : .93f;
                sz = axisX ? .93f : .09f;
                sy = .025f;
                height = .922f;
                block = Blocks.WOOL.white();
            } else {
                sx = axisX ? 1.38f : .93f;
                sz = axisX ? .93f : 1.38f;
                sy = .21f;
                height = .715f;
                block = wool(sleeper.color);
            }
        }

        double x = centerX - sx * .5;
        double z = centerZ - sz * .5;
        double y = sleeper.head.getY() + height;

        Display.BlockDisplay display = new Display.BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);
        CompoundTag nbt = new CompoundTag();
        nbt.store("block_state", BlockState.CODEC, block.defaultBlockState());
        nbt.store("transformation", Transformation.EXTENDED_CODEC,
                new Transformation(new Vector3f(), new Quaternionf(),
                        new Vector3f(sx, sy, sz), new Quaternionf()));
        // Explicit render bounds prevent the long thin quilt being culled.
        nbt.putFloat("width", 3.0f);
        nbt.putFloat("height", 1.5f);
        display.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), nbt));
        display.setPos(x, y, z);
        CompoundTag data = display.getPersistentData();
        data.putBoolean(ROOT_TAG, true);
        data.putString("cb_owner", ownerTag);
        data.putLong("cb_bed", bedTag);
        data.putString("cb_color", colorTag);
        data.putString("cb_part", partTag);
        level.addFreshEntity(display);
    }

    private static Block wool(DyeColor color) {
        return Blocks.WOOL.pick(color);
    }

    private record Sleeper(String owner, BlockPos head, BlockPos foot,
                           Direction facing, DyeColor color) {}
}
