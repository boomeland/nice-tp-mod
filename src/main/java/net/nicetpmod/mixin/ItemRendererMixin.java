package net.nicetpmod.mixin;

import net.nicetpmod.NiceTPMod;
import net.nicetpmod.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * The item's own model file (used for the inventory icon) stays a flat
 * 2D {@code item/generated} model, since {@code ModelLoaderMixin} only
 * gets the separate {@code teleportation_tablet_3d} model baked. This
 * swaps to that 3D model at render time for every mode except GUI, so
 * the flat icon is kept for inventory slots while hand/ground/etc. use
 * the Blockbench model.
 */
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useTeleportationTablet(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.TELEPORTATION_TABLET) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(NiceTPMod.MOD_ID, "teleportation_tablet_3d", "inventory"));
        }
        return value;
    }
}