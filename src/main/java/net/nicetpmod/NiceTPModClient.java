package net.boomeland.nicetpmod;

import net.boomeland.nicetpmod.GUI.teleportationTabletGui.GuiTeleportationTablet;
import net.boomeland.nicetpmod.network.ModNetworking;
import net.boomeland.nicetpmod.teleport.Waypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class NiceTPModClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        // The server sends this snapshot both to open the GUI (tablet right-click)
        // and to refresh it after add/remove/teleport, so this one receiver covers both.
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_WAYPOINTS, (client, handler, buf, sender) -> {
            int count = buf.readInt();
            List<Waypoint> waypoints = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String name = buf.readString(32);
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                Identifier dimension = new Identifier(buf.readString());
                waypoints.add(new Waypoint(name, x, y, z, dimension));
            }

            client.execute(() -> {
                if (client.currentScreen instanceof GuiTeleportationTablet screen) {
                    screen.updateWaypoints(waypoints);
                } else {
                    client.setScreen(new GuiTeleportationTablet(waypoints));
                }
            });
        });
    }
}
