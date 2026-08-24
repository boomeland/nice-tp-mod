package net.boomeland.nicetpmod.teleport;

import net.minecraft.util.Identifier;

public record Waypoint(String name, double x, double y, double z, Identifier dimension) {
}
