package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public final class AutoJumpFeature {

    public static final KeyMapping KEY = new KeyMapping(
        "key.autotoggle.autojump.toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyBindings.CATEGORY,
        3
    );

    private static boolean enabled;

    private AutoJumpFeature() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static void reset() {
        enabled = false;
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            ToggleFeedback.show(minecraft, toggle(), SoundEvents.UI_BUTTON_CLICK.value(),
                "message.autotoggle.autojump.enabled", "message.autotoggle.autojump.disabled");
        }
    }
}
