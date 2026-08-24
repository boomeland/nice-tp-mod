package net.nicetpmod.teleport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/**
 * A saved location. {@code dimension} is the registry id of the world it
 * was saved in (e.g. {@code minecraft:overworld}), used to grey out and
 * reject teleports made from a different dimension.
 */
public record Waypoint(String name, double x, double y, double z, Identifier dimension) {
    public static final Codec<Waypoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("Name").forGetter(Waypoint::name),
            Codec.DOUBLE.fieldOf("X").forGetter(Waypoint::x),
            Codec.DOUBLE.fieldOf("Y").forGetter(Waypoint::y),
            Codec.DOUBLE.fieldOf("Z").forGetter(Waypoint::z),
            Identifier.CODEC.fieldOf("Dimension").forGetter(Waypoint::dimension)
    ).apply(instance, Waypoint::new));
}
