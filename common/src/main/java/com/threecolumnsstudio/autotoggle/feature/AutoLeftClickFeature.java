package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import com.threecolumnsstudio.autotoggle.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.HitResult;

public final class AutoLeftClickFeature {

    private static final int MESSAGE_COLOR = 0xFFAA00;

    public static final KeyMapping KEY = new KeyMapping(
        "key.autotoggle.autoleftclick.toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_I,
        KeyBindings.CATEGORY,
        4
    );

    private static boolean enabled;

    private AutoLeftClickFeature() {}

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
            minecraft.options.keyAttack.setDown(false);
        }
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            boolean toggled = toggle();
            ToggleFeedback.show(minecraft, toggled, SoundEvents.EXPERIENCE_ORB_PICKUP,
                "message.autotoggle.autoleftclick.enabled", "message.autotoggle.autoleftclick.disabled", MESSAGE_COLOR);
            if (!toggled) {
                minecraft.options.keyAttack.setDown(false);
            }
            return;
        }
        if (!enabled) {
            return;
        }

        KeyMapping attack = minecraft.options.keyAttack;
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || !minecraft.mouseHandler.isMouseGrabbed() || player.isUsingItem()) {
            attack.setDown(false);
            return;
        }

        HitResult hit = minecraft.hitResult;
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            attack.setDown(false);
            return;
        }

        if (hit.getType() == HitResult.Type.ENTITY) {
            attack.setDown(false);
            if (player.getAttackStrengthScale(0.0F) >= 1.0F) {
                KeyMapping.click(((KeyMappingAccessor) attack).autotoggle$getKey());
            }
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            attack.setDown(true);
            if (!minecraft.gameMode.isDestroying()) {
                KeyMapping.click(((KeyMappingAccessor) attack).autotoggle$getKey());
            }
        } else {
            attack.setDown(false);
        }
    }
}
