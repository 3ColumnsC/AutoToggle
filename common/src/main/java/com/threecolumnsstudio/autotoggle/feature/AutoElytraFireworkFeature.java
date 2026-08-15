package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.AutoToggle;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class AutoElytraFireworkFeature {

    private static final String TOGGLE_KEY = "key.autotoggle.autoelytrafirework.toggle";
    private static final String ENABLED_MESSAGE_KEY = "message.autotoggle.autoelytrafirework.enabled";
    private static final String DISABLED_MESSAGE_KEY = "message.autotoggle.autoelytrafirework.disabled";
    private static final SoundEvent TOGGLE_SOUND = SoundEvents.NOTE_BLOCK_PLING.value();
    private static final int STATUS_COLOR = 0xFF55FF55;
    private static final int STATUS_LINE_GAP = 28;
    private static final int DISABLED_MESSAGE_DURATION = 60;
    private static final int DISABLED_MESSAGE_FADE_TICKS = 20;
    private static final double FIRE_SPEED_THRESHOLD = 1.0;
    private static final int MIN_REFIRE_INTERVAL = 10;
    private static final int MAX_FIRE_INTERVAL = 80;

    public static final KeyMapping KEY = new KeyMapping(
        TOGGLE_KEY,
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyBindings.CATEGORY,
        6
    );

    private static boolean enabled;
    private static int disabledMessageTicks;
    private static int ticksSinceFire;

    private AutoElytraFireworkFeature() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static void reset() {
        enabled = false;
        disabledMessageTicks = 0;
        ticksSinceFire = 0;
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            if (toggle()) {
                disabledMessageTicks = 0;
            } else {
                disabledMessageTicks = DISABLED_MESSAGE_DURATION;
                ticksSinceFire = 0;
            }
            ToggleFeedback.playSound(minecraft, TOGGLE_SOUND);
            return;
        }
        if (disabledMessageTicks > 0) {
            disabledMessageTicks--;
        }
        if (!enabled) {
            return;
        }
        tryFire(minecraft);
    }

    public static void renderStatus(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!enabled && disabledMessageTicks <= 0) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        int alpha = 255;
        Component text;
        if (enabled) {
            text = Component.translatable(ENABLED_MESSAGE_KEY);
        } else {
            int remaining = disabledMessageTicks;
            alpha = remaining * 255 / DISABLED_MESSAGE_FADE_TICKS;
            if (alpha > 255) {
                alpha = 255;
            }
            if (alpha <= 0) {
                return;
            }
            text = Component.translatable(DISABLED_MESSAGE_KEY);
        }
        int color = (alpha << 24) | (STATUS_COLOR & 0x00FFFFFF);
        int baseY = graphics.guiHeight() - AutoToggle.OVERLAY_MESSAGE_Y;
        graphics.nextStratum();
        graphics.pose().pushMatrix();
        graphics.centeredText(minecraft.font, text,
            graphics.guiWidth() / 2, baseY - STATUS_LINE_GAP, color);
        graphics.pose().popMatrix();
    }

    private static void tryFire(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        if (!minecraft.mouseHandler.isMouseGrabbed()) {
            return;
        }
        if (player.isSpectator() || player.isUsingItem()) {
            return;
        }
        if (!player.isFallFlying()) {
            return;
        }
        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return;
        }
        ticksSinceFire++;
        if (ticksSinceFire < MIN_REFIRE_INTERVAL) {
            return;
        }
        Vec3 motion = player.getDeltaMovement();
        boolean slowEnough = motion.lengthSqr() < FIRE_SPEED_THRESHOLD * FIRE_SPEED_THRESHOLD;
        if (!slowEnough && ticksSinceFire < MAX_FIRE_INTERVAL) {
            return;
        }
        InteractionHand hand = fireworkHand(player);
        if (hand == null) {
            return;
        }
        minecraft.gameMode.useItem(player, hand);
        ticksSinceFire = 0;
    }

    private static InteractionHand fireworkHand(LocalPlayer player) {
        if (player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
            return InteractionHand.OFF_HAND;
        }
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(Items.FIREWORK_ROCKET)) {
                inventory.setSelectedSlot(slot);
                return InteractionHand.MAIN_HAND;
            }
        }
        return null;
    }
}
