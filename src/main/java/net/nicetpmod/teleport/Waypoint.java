package net.nicetpmod.teleport;

import net.minecraft.util.Identifier;

/**
 * A saved location. {@code dimension} is the registry id of the world it
 * was saved in (e.g. {@code minecraft:overworld}), used to grey out and
 * reject teleports made from a different dimension.
 */
public record Waypoint(String name, double x, double y, double z, Identifier dimension) {
}
