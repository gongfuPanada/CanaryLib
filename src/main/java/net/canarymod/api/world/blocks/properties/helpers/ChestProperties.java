package net.canarymod.api.world.blocks.properties.helpers;

import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockFace;
import net.canarymod.api.world.blocks.properties.BlockDirectionProperty;

import static net.canarymod.api.world.blocks.BlockType.Chest;

/**
 * Chest properties helper
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class ChestProperties extends BlockProperties {
    public static final BlockDirectionProperty facing = getInstanceFor(Chest, "facing");

    /**
     * Applies rotation to the {@code Banner}
     *
     * @param block
     *         the {@link net.canarymod.api.world.blocks.Block} to be modified
     * @param value
     *         the {@link net.canarymod.api.world.blocks.BlockFace} value to apply
     *
     * @return the Block with adjusted state (NOTE: Original Block object is also modified, using the return is unnecessary)
     *
     * @throws java.lang.NullPointerException
     *         Should {@code block} or {@code value} be null
     * @throws java.lang.IllegalArgumentException
     *         Should an invalid property be applied
     */
    public static Block applyFacing(Block block, BlockFace value) {
        return apply(block, facing, value);
    }
}