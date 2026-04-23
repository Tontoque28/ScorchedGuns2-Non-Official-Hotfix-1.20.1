package com.tontoque.sg2hotfix.mixin;

import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StringSplitter.class)
public class TooltipCrashFixMixin {

    // 1. Catch NPE when calculating the string width (This is the one from your crash report: m_92384_)
    @Inject(method = "stringWidth(Lnet/minecraft/network/chat/FormattedText;)F", at = @At("HEAD"), cancellable = true)
    private void sg2_hotfix$catchStringSplitterWidthNpe(FormattedText text, CallbackInfoReturnable<Float> cir) {
        if (text instanceof Component) {
            try {
                // Pre-evaluate the component to see if it causes a NullPointerException in StringDecomposer
                ((Component) text).getString(); 
            } catch (NullPointerException | IllegalArgumentException e) {
                // If it crashes, we return a width of 0 to prevent the game from collapsing
                cir.setReturnValue(0.0f);
            }
        }
    }

    // 2. Catch NPE when splitting lines (just in case it survives the width calculation)
    @Inject(method = "splitLines(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/network/chat/Style;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private void sg2_hotfix$catchStringSplitterSplitNpe(FormattedText text, int maxWidth, Style style, CallbackInfoReturnable<List<FormattedText>> cir) {
        if (text instanceof Component) {
            try {
                ((Component) text).getString(); 
            } catch (NullPointerException | IllegalArgumentException e) {
                // If it crashes, return a safe error component
                cir.setReturnValue(List.of(Component.literal("§cTooltip Error: Corrupted Item (NPE Prevented)")));
            }
        }
    }
}
