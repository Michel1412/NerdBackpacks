package com.nerdbackpacks.content.backpack;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Five backpack tiers. Slot counts grow by +18 each upgrade (vanilla chest row size):
 * 27 → 45 → 63 → 81 → 99.
 *
 * <p>Hitbox shapes are axis-aligned bounds from each tier's block model elements
 * ({@code mochila_N.json}), in Blockbench 0–16 coordinates.
 */
public enum BackpackTier {
    TIER_1("mochila_1", 1, 3, box(3.0, 1.8125, 3.125, 13.0, 13.0625, 12.825)),
    TIER_2("mochila_2", 2, 5, box(2.125, 2.375, 3.125, 13.375, 13.625, 12.825)),
    TIER_3("mochila_3", 3, 7, box(2.125, 2.375, 3.125, 13.375, 13.625, 12.825)),
    TIER_4("mochila_4", 4, 9, box(1.75, 2.375, 3.125, 14.25, 13.625, 12.825)),
    /** Model dips slightly below y=0; keep that for outline fidelity. */
    TIER_5("mochila_5", 5, 11, box(1.75, -0.1875, 2.875, 14.25, 15.0625, 13.075));

    private final String id;
    private final int level;
    private final int rows;
    private final VoxelShape shape;

    BackpackTier(String id, int level, int rows, VoxelShape shape) {
        this.id = id;
        this.level = level;
        this.rows = rows;
        this.shape = shape;
    }

    private static VoxelShape box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getRows() {
        return rows;
    }

    public int getSlotCount() {
        return rows * 9;
    }

    /** Outline / collision shape matching the placed backpack model AABB. */
    public VoxelShape getShape() {
        return shape;
    }
}
