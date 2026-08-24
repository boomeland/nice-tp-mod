package net.nicetpmod.gui;

import net.nicetpmod.network.AddWaypointPayload;
import net.nicetpmod.network.RemoveWaypointPayload;
import net.nicetpmod.network.TeleportWaypointPayload;
import net.nicetpmod.teleport.Waypoint;
import net.nicetpmod.teleport.WaypointState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * The tablet has no client-side waypoint state of its own: this screen is
 * only ever built or refreshed from a server {@code sync_waypoints}
 * snapshot (see {@code NiceTPModClient}), and every button click just
 * sends a request and waits for the next snapshot to redraw itself.
 */
public class GuiTeleportationTablet extends Screen {
    private static final int ENTRY_HEIGHT = 22;
    private static final int LIST_TOP = 30;
    private static final int LIST_WIDTH = 220;

    private List<Waypoint> waypoints;
    private EditBox nameField;

    public GuiTeleportationTablet(List<Waypoint> waypoints) {
        super(Component.translatable("gui.nicetpmod.teleportation_tablet"));
        this.waypoints = waypoints;
    }

    /** Called when a fresh snapshot arrives while this screen is already open. */
    public void updateWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
        this.rebuildWidgets();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int left = centerX - LIST_WIDTH / 2;
        Identifier currentDimension = this.minecraft.level.dimension().identifier();

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint waypoint = waypoints.get(i);
            int index = i;
            int y = LIST_TOP + i * ENTRY_HEIGHT;

            Button teleportButton = Button.builder(
                    Component.literal(waypoint.name() + "  (" + (int) waypoint.x() + ", " + (int) waypoint.y() + ", " + (int) waypoint.z() + ")"),
                    button -> sendTeleport(index)
            ).bounds(left, y, LIST_WIDTH - 22, 20).build();
            // Grey out entries from other dimensions; the server rejects them too.
            teleportButton.active = waypoint.dimension().equals(currentDimension);
            this.addRenderableWidget(teleportButton);

            Button deleteButton = Button.builder(
                    Component.literal("x"),
                    button -> sendRemove(index)
            ).bounds(left + LIST_WIDTH - 20, y, 20, 20).build();
            this.addRenderableWidget(deleteButton);
        }

        int formY = LIST_TOP + waypoints.size() * ENTRY_HEIGHT + 10;
        this.nameField = new EditBox(this.font, left, formY, LIST_WIDTH - 62, 20, Component.translatable("gui.nicetpmod.waypoint_name"));
        this.nameField.setMaxLength(32);
        this.addRenderableWidget(this.nameField);
        this.setInitialFocus(this.nameField);

        Button addButton = Button.builder(Component.translatable("gui.nicetpmod.add_waypoint"), button -> sendAdd())
                .bounds(left + LIST_WIDTH - 60, formY, 60, 20).build();
        addButton.active = waypoints.size() < WaypointState.MAX_WAYPOINTS;
        this.addRenderableWidget(addButton);
    }

    private void sendTeleport(int index) {
        ClientPlayNetworking.send(new TeleportWaypointPayload(index));
    }

    private void sendRemove(int index) {
        ClientPlayNetworking.send(new RemoveWaypointPayload(index));
    }

    private void sendAdd() {
        String name = this.nameField.getValue().trim();
        ClientPlayNetworking.send(new AddWaypointPayload(name));
        this.nameField.setValue("");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // The base Screen already extracts the background; calling
        // extractBackground again here double-triggers the blur and crashes.
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
