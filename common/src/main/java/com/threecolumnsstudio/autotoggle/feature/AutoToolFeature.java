package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.AutoToggle;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class AutoToolFeature {

    private static final String TOGGLE_KEY = "key.autotoggle.autotool.toggle";
    private static final String ENABLED_MESSAGE_KEY = "message.autotoggle.autotool.enabled";
    private static final String DISABLED_MESSAGE_KEY = "message.autotoggle.autotool.disabled";
    private static final SoundEvent TOGGLE_SOUND = SoundEvents.NOTE_BLOCK_PLING.value();
    private static final int STATUS_COLOR = 0xFF55FFFF;
    private static final int STATUS_LINE_GAP = 16;
    private static final int DISABLED_MESSAGE_DURATION = 60;
    private static final int DISABLED_MESSAGE_FADE_TICKS = 20;

    public static final KeyMapping KEY = new KeyMapping(
        TOGGLE_KEY,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_K,
        KeyBindings.CATEGORY,
        0
    );

    private static boolean enabled;
    private static int disabledMessageTicks;

    private AutoToolFeature() {}

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
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            if (toggle()) {
                disabledMessageTicks = 0;
            } else {
                disabledMessageTicks = DISABLED_MESSAGE_DURATION;
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
        autoSelectTool(minecraft);
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

    private static void autoSelectTool(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        if (!minecraft.mouseHandler.isMouseGrabbed()) {
            return;
        }
        if (player.getAbilities().instabuild || player.isUsingItem()) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        boolean mining = minecraft.options.keyAttack.isDown() || AutoLeftClickFeature.isEnabled();
        if (!mining) {
            return;
        }
        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = minecraft.level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(minecraft.level, pos) == 0.0F) {
            return;
        }
        Inventory inventory = player.getInventory();
        int bestSlot = findBestSlot(inventory, state);
        if (bestSlot != -1 && bestSlot != inventory.getSelectedSlot()) {
            inventory.setSelectedSlot(bestSlot);
        }
    }

    private static int findBestSlot(Inventory inventory, BlockState state) {
        int currentSlot = inventory.getSelectedSlot();
        ToolCandidate best = evaluate(inventory.getItem(currentSlot), state);
        int bestSlot = best.speed() > 1.0F ? currentSlot : -1;
        for (int slot = 0; slot < 9; slot++) {
            if (slot == currentSlot) {
                continue;
            }
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ToolCandidate candidate = evaluate(stack, state);
            if (candidate.betterThan(best)) {
                best = candidate;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }

    private static ToolCandidate evaluate(ItemStack stack, BlockState state) {
        float speed = specializedSpeed(stack, state);
        if (speed > 1.0F) {
            speed += miningEfficiency(stack);
        }
        return new ToolCandidate(speed, stack.isCorrectToolForDrops(state), fortuneLevel(stack));
    }

    private static float specializedSpeed(ItemStack stack, BlockState state) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            return 1.0F;
        }
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent() && state.is(rule.blocks())) {
                return rule.speed().get();
            }
        }
        return 1.0F;
    }

    private static float miningEfficiency(ItemStack stack) {
        float[] total = {0.0F};
        stack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier, display) -> {
            if (attribute.value() == Attributes.MINING_EFFICIENCY.value()
                    && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                total[0] += (float) modifier.amount();
            }
        });
        return total[0];
    }

    private static int fortuneLevel(ItemStack stack) {
        int[] level = {0};
        stack.getEnchantments().entrySet().forEach(entry -> {
            if (entry.getKey().is(Enchantments.FORTUNE)) {
                level[0] = entry.getIntValue();
            }
        });
        return level[0];
    }

    private record ToolCandidate(float speed, boolean correctForDrops, int fortune) {

        boolean betterThan(ToolCandidate other) {
            if (speed != other.speed) {
                return speed > other.speed;
            }
            if (correctForDrops != other.correctForDrops) {
                return correctForDrops;
            }
            return fortune > other.fortune;
        }
    }
}
