package com.minecraftabnormals.mindful_eating.core.mixin;

import com.minecraftabnormals.mindful_eating.client.HungerOverlay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class HungerGuiMixin {
    @Redirect(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    //@Inject(method = "renderFood", at = @At(value = "HEAD"),remap = false, cancellable = true)
    public void renderFood(GuiGraphics instance, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
       instance.blit(HungerOverlay.GUI_EMPTY_ICONS_LOCATION,pX,pY,0,0,0,0);
        //i.cancel();
        //RenderSystem.setShaderTexture(0, HungerOverlay.GUI_EMPTY_ICONS_LOCATION);
    }
}
