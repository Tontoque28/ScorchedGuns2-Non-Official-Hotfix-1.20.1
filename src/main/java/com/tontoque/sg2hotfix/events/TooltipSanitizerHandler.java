package com.tontoque.sg2hotfix.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = "sg2_tooltip_hotfix", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipSanitizerHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        // General Null Check for Scorched Guns or any other mod passing null into the tooltip components list
        boolean hasCorruptedComponent = false;
        
        ListIterator<Component> iterator = event.getToolTip().listIterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component == null) {
                iterator.set(Component.literal("§c[Null Component Removed]"));
                continue;
            }
            
            try {
                // Pre-evaluate the string to catch the NullPointerException before ForgeHooksClient does
                component.getString();
            } catch (Exception e) {
                hasCorruptedComponent = true;
                iterator.set(Component.literal("§c[Corrupted Tooltip Removed]"));
            }
        }

        // Specific handling for Scorched Guns 2 items missing expected NBT
        // (Assuming the mod uses "scorchedguns" as its namespace, update if it's "sg2" etc.)
        if (stack.getItem().getCreatorModId(stack) != null && stack.getItem().getCreatorModId(stack).contains("scorchedguns")) {
            // Usually the crash happens when NBT is completely null or missing a specific key.
            if (hasCorruptedComponent || stack.getTag() == null) {
                 event.getToolTip().add(Component.literal("§4[SG2 Fix]: Blueprint/Item lacks valid NBT."));
                 event.getToolTip().add(Component.literal("§eDiscard this item to prevent issues."));
            }
        }
    }
}
