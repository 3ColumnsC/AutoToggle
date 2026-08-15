package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

public final class AutoRightClickFeature {

    private static final int MESSAGE_COLOR = 0xFFAA00;

    public static final KeyMapping KEY = new KeyMapping(
        "key.autotoggle.autorightclick.toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyBindings.CATEGORY,
        5
    );

    private static boolean enabled;

    private AutoRightClickFeature() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static void reset() {
        enabled = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options != null) {
            minecraft.options.keyUse.setDown(false);
        }
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            boolean toggled = toggle();
            ToggleFeedback.show(minecraft, toggled, SoundEvents.EXPERIENCE_ORB_PICKUP,
                "message.autotoggle.autorightclick.enabled", "message.autotoggle.autorightclick.disabled", MESSAGE_COLOR);
            if (!toggled) {
                minecraft.options.keyUse.setDown(false);
            }
            return;
        }
        if (!enabled) {
            return;
        }

        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || !minecraft.mouseHandler.isMouseGrabbed()) {
            minecraft.options.keyUse.setDown(false);
            return;
        }

        ItemStack stack = player.isUsingItem() ? player.getUseItem() : player.getMainHandItem();
        if (isChargeableWeapon(stack)) {
            if (!player.isUsingItem()) {
                minecraft.options.keyUse.setDown(true);
            } else if (isFullyCharged(player, stack)) {
                minecraft.options.keyUse.setDown(false);
            } else {
                minecraft.options.keyUse.setDown(true);
            }
        } else {
            minecraft.options.keyUse.setDown(true);
        }
    }

    private static boolean isChargeableWeapon(ItemStack stack) {
        return stack.getItem() instanceof BowItem
            || stack.getItem() instanceof CrossbowItem
            || stack.getItem() instanceof TridentItem;
    }

    private static boolean isFullyCharged(LocalPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof BowItem) {
            return player.getTicksUsingItem() >= BowItem.MAX_DRAW_DURATION;
        }
        if (stack.getItem() instanceof CrossbowItem) {
            return CrossbowItem.isCharged(stack);
        }
        if (stack.getItem() instanceof TridentItem) {
            return player.getTicksUsingItem() >= TridentItem.THROW_THRESHOLD_TIME;
        }
        return false;
    }
}
