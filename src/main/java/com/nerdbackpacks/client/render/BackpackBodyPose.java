package com.nerdbackpacks.client.render;

/**
 * Pose constants for worn backpacks.
 * <p>
 * Player body local space (after {@code body.translateAndRotate}):
 * <ul>
 *   <li>+X = player's left</li>
 *   <li>+Y = up</li>
 *   <li>-Z = chest / forward</li>
 *   <li>+Z = back face (where the backpack attaches)</li>
 * </ul>
 * Block models use 0–16 units. The backpack's player-facing face is its north face
 * (minimum Z in the JSON). Transforms place that face flush on the player's back, then
 * grow the mesh outward (+Z after the 180° yaw).
 */
public final class BackpackBodyPose {
    /**
     * Lateral offset on the torso (body local X), in blocks.
     * Positive moves toward the player's left; negative toward the right.
     */
    public static final float BODY_OFFSET_X = -0.5F;

    /**
     * Vertical offset on the torso (body local Y), in blocks.
     * Positive moves the backpack up toward the shoulders.
     */
    public static final float BODY_OFFSET_Y = 0.5F;

    /**
     * Distance from the body origin out to the back surface, in blocks.
     * Player body depth is roughly 0.25; a small positive value sits on the back face.
     */
    public static final float BODY_OFFSET_Z = 0.6F;

    /**
     * Uniform scale applied to the block model when worn.
     */
    public static final float SCALE = 1F;

    /**
     * Extra rotation in degrees (applied after sitting on the back).
     * Y=180 faces the pack outward; use X/Z to tilt.
     */
    public static final float ROTATION_X = 180.0F;
    public static final float ROTATION_Y = 180.0F;
    public static final float ROTATION_Z = 0.0F;

    /**
     * North-face Z of the backpack JSON models (pixels). Used so the texture/mesh
     * starts on the player's back instead of being centered through the torso.
     */
    public static final float MODEL_ATTACH_Z_PX = 3.125F;

    /**
     * Bottom Y of the backpack JSON models (pixels). Anchors the bottom of the pack
     * relative to the torso after the usual -0.5 block-model centering.
     */
    public static final float MODEL_BOTTOM_Y_PX = 1.8125F;

    /**
     * Extra lift after anchoring the model bottom (blocks, pre-scale local space).
     */
    public static final float MODEL_Y_LIFT = 0.35F;

    private BackpackBodyPose() {
    }

    public static float modelAttachZ() {
        return MODEL_ATTACH_Z_PX / 16.0F;
    }

    public static float modelBottomY() {
        return MODEL_BOTTOM_Y_PX / 16.0F;
    }
}
