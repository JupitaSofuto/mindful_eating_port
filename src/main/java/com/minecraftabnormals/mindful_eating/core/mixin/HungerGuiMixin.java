package com.minecraftabnormals.mindful_eating.core.mixin;

import com.minecraftabnormals.mindful_eating.client.HungerOverlay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class HungerGuiMixin {
    @Inject(method = "renderFood", at = @At(value = "HEAD"),remap = false, cancellable = true)
    public void renderFood(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        //RenderSystem.setShaderTexture(0, HungerOverlay.GUI_EMPTY_ICONS_LOCATION);
    }
}
