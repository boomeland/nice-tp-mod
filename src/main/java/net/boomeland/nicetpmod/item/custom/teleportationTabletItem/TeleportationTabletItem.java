package net.boomeland.nicetpmod.item.custom.teleportationTabletItem;

import net.boomeland.nicetpmod.network.ServerNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TeleportationTabletItem extends Item {

    public TeleportationTabletItem(Settings settings) {
        super(settings);
    }

    /**
     * Right-click just asks the server for the player's current waypoint
     * list; the GUI itself is opened client-side once that snapshot packet
     * arrives (see {@code NiceTPModClient}), so it always opens with
     * up-to-date data instead of a stale client-side copy.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            ServerNetworking.sendSync(serverPlayer);
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}
