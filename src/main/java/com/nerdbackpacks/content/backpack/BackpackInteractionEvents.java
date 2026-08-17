package com.nerdbackpacks.content.backpack;

import com.nerdbackpacks.network.ModNetwork;
import com.nerdbackpacks.network.OpenEquippedBackpackPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * Shift+right-click on a placed backpack picks it up (even when holding an item,
 * which normally skips {@link BackpackBlock#use}).
 * Empty-hand right-click in air opens a worn backpack (vanilla chestplate or Curios).
 */
@Mod.EventBusSubscriber(modid = com.nerdbackpacks.NerdBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BackpackInteractionEvents {
    private BackpackInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BackpackBlock backpackBlock)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);

        if (level.isClientSide) {
            return;
        }

        ItemStack stack = new ItemStack(backpackBlock);
        backpack.saveToItem(stack);
        if (!player.addItem(stack)) {
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }
        level.removeBlock(pos, false);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.swing(event.getHand(), true);
        }
    }

    /**
     * Client-only empty-hand air click. Requests the server to open an equipped backpack.
     */
    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().isShiftKeyDown()) {
            return;
        }
        if (BackpackOpener.resolveEquipped(event.getEntity()).isEmpty()) {
            return;
        }
        ModNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(), new OpenEquippedBackpackPacket());
    }
}
