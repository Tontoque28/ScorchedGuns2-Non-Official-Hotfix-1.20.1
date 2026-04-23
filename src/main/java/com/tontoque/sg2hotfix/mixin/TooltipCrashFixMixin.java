package com.tontoque.sg2hotfix.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class TooltipCrashFixMixin {

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void sg2_hotfix$catchTooltipCrash(Font p_282675_, List<ClientTooltipComponent> p_282615_, int p_283230_, int p_283417_, ClientTooltipPositioner p_282442_, CallbackInfo ci) {
        if (p_282615_ != null) {
            for (ClientTooltipComponent component : p_282615_) {
                if (component == null) continue;
                
                try {
                    // Try to access the width/height, if the inner component is broken it might crash here or during render
                    component.getWidth(p_282675_);
                    component.getHeight();
                } catch (NullPointerException | IllegalArgumentException e) {
                    GuiGraphics graphics = (GuiGraphics) (Object) this;
                    // Replace the whole list with a single error component, but since we can't easily modify the list, 
                    // we just cancel and draw a simple string.
                    graphics.drawString(p_282675_, "§cTooltip Error: Corrupted Item (NPE Prevented)", p_283230_, p_283417_, 0xFFFFFF);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
