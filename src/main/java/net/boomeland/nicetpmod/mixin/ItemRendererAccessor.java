package net.boomeland.nicetpmod.mixin;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes {@code ItemRenderer}'s private model cache so {@code ItemRendererMixin} can look up the 3D model. */
@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    @Accessor("models")
    ItemModels mccourse$getModels();
}
