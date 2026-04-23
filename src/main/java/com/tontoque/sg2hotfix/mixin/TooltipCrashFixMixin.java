package com.tontoque.sg2hotfix.mixin;

import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StringSplitter.class)
public class TooltipCrashFixMixin {

    // Inject into StringSplitter.splitLines (m_92384_) which is where the NPE is thrown
    @Inject(method = "splitLines(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/network/chat/Style;)Ljava/util/List;", 
            at = @At("HEAD"), cancellable = true, require = 0)
    private void sg2_hotfix$catchStringSplitterNpe(net.minecraft.network.chat.FormattedText text, int maxWidth, net.minecraft.network.chat.Style style, CallbackInfoReturnable<List<net.minecraft.network.chat.FormattedText>> cir) {
        if (text instanceof Component) {
            try {
                // Pre-evaluate the component to see if it causes a NullPointerException in StringDecomposer
                ((Component) text).getString(); 
            } catch (NullPointerException | IllegalArgumentException e) {
                // If it crashes, we cancel the split process and return a safe, pre-formatted error component list
                cir.setReturnValue(List.of(Component.literal("§cTooltip Error: Corrupted Item (NPE Prevented)")));
            }
        }
    }

    // Secondary target for SRG names in production
    @Inject(method = "m_92384_", 
            at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void sg2_hotfix$catchStringSplitterNpeObf(net.minecraft.network.chat.FormattedText text, int maxWidth, net.minecraft.network.chat.Style style, CallbackInfoReturnable<List<net.minecraft.network.chat.FormattedText>> cir) {
        if (text instanceof Component) {
            try {
                ((Component) text).getString(); 
            } catch (NullPointerException | IllegalArgumentException e) {
                cir.setReturnValue(List.of(Component.literal("§cTooltip Error: Corrupted Item (NPE Prevented)")));
            }
        }
    }
}
