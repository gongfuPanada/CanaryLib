package net.canarymod.api.world.blocks.properties.helpers;

import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.properties.BlockBooleanProperty;

import static net.canarymod.api.world.blocks.BlockType.Mycelium;

/**
 * Mycelium properties helper
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class MyceliumProperties extends BlockProperties {
    public static final BlockBooleanProperty snowy = getInstanceFor(Mycelium, "snowy");

    public static Block applySnowy(Block block, boolean value) {
        return apply(block, snowy, value);
    }
}
