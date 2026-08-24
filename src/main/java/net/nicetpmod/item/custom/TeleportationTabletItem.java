package net.nicetpmod.item.custom;

import net.nicetpmod.network.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class TeleportationTabletItem extends Item {

    public TeleportationTabletItem(Properties properties) {
        super(properties);
    }

    /**
     * Right-click just asks the server for the player's current waypoint
     * list; the GUI itself is opened client-side once that snapshot packet
     * arrives (see {@code NiceTPModClient}), so it always opens with
     * up-to-date data instead of a stale client-side copy.
     */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ServerNetworking.sendSync(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
}
