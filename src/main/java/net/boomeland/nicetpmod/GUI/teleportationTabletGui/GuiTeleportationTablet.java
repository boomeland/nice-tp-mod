package net.boomeland.nicetpmod.GUI.teleportationTabletGui;

import net.boomeland.nicetpmod.network.ModNetworking;
import net.boomeland.nicetpmod.teleport.Waypoint;
import net.boomeland.nicetpmod.teleport.WaypointState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    private TextFieldWidget nameField;

    public GuiTeleportationTablet(List<Waypoint> waypoints) {
        super(Text.translatable("gui.nicetpmod.teleportation_tablet"));
        this.waypoints = waypoints;
    }

    /** Called when a fresh snapshot arrives while this screen is already open. */
    public void updateWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
        this.clearAndInit();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int left = centerX - LIST_WIDTH / 2;
        Identifier currentDimension = this.client.world.getRegistryKey().getValue();

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint waypoint = waypoints.get(i);
            int index = i;
            int y = LIST_TOP + i * ENTRY_HEIGHT;

            ButtonWidget teleportButton = ButtonWidget.builder(
                    Text.literal(waypoint.name() + "  (" + (int) waypoint.x() + ", " + (int) waypoint.y() + ", " + (int) waypoint.z() + ")"),
                    button -> sendTeleport(index)
            ).dimensions(left, y, LIST_WIDTH - 22, 20).build();
            // Grey out entries from other dimensions; the server rejects them too.
            teleportButton.active = waypoint.dimension().equals(currentDimension);
            this.addDrawableChild(teleportButton);

            ButtonWidget deleteButton = ButtonWidget.builder(
                    Text.literal("x"),
                    button -> sendRemove(index)
            ).dimensions(left + LIST_WIDTH - 20, y, 20, 20).build();
            this.addDrawableChild(deleteButton);
        }

        int formY = LIST_TOP + waypoints.size() * ENTRY_HEIGHT + 10;
        this.nameField = new TextFieldWidget(this.textRenderer, left, formY, LIST_WIDTH - 62, 20, Text.translatable("gui.nicetpmod.waypoint_name"));
        this.nameField.setMaxLength(32);
        this.addDrawableChild(this.nameField);
        this.setInitialFocus(this.nameField);

        ButtonWidget addButton = ButtonWidget.builder(Text.translatable("gui.nicetpmod.add_waypoint"), button -> sendAdd())
                .dimensions(left + LIST_WIDTH - 60, formY, 60, 20).build();
        addButton.active = waypoints.size() < WaypointState.MAX_WAYPOINTS;
        this.addDrawableChild(addButton);
    }

    private void sendTeleport(int index) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(index);
        ClientPlayNetworking.send(ModNetworking.TELEPORT_WAYPOINT, buf);
    }

    private void sendRemove(int index) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(index);
        ClientPlayNetworking.send(ModNetworking.REMOVE_WAYPOINT, buf);
    }

    private void sendAdd() {
        String name = this.nameField.getText().trim();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name);
        ClientPlayNetworking.send(ModNetworking.ADD_WAYPOINT, buf);
        this.nameField.setText("");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
