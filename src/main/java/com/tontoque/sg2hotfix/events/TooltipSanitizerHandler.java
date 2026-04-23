package com.tontoque.sg2hotfix.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = "sg2_tooltip_hotfix", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipSanitizerHandler {

    private static final Component CORRUPTED_TOOLTIP_COMPONENT = Component.literal("§c[Corrupted Tooltip Data]");

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        List<Component> toolTip = event.getToolTip();
        ListIterator<Component> iterator = toolTip.listIterator();

        while (iterator.hasNext()) {
            Component component = iterator.next();

            if (component == null) {
                iterator.set(CORRUPTED_TOOLTIP_COMPONENT);
                continue;
            }

            try {
                component.getString();
            } catch (Exception e) {
                iterator.set(CORRUPTED_TOOLTIP_COMPONENT);
            }
        }
    }
}
