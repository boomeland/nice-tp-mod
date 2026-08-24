package net.nicetpmod;

import net.nicetpmod.gui.GuiTeleportationTablet;
import net.nicetpmod.network.SyncWaypointsPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NiceTPModClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        // The server sends this snapshot both to open the GUI (tablet right-click)
        // and to refresh it after add/remove/teleport, so this one receiver covers both.
        ClientPlayNetworking.registerGlobalReceiver(SyncWaypointsPayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (context.client().gui.screen() instanceof GuiTeleportationTablet screen) {
                        screen.updateWaypoints(payload.waypoints());
                    } else {
                        context.client().gui.setScreen(new GuiTeleportationTablet(payload.waypoints()));
                    }
                }));
    }
}
