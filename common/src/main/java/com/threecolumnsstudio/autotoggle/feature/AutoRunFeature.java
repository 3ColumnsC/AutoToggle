package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public final class AutoRunFeature {

    public static final KeyMapping KEY = new KeyMapping(
        "key.autotoggle.autorun.toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_R,
        KeyBindings.CATEGORY,
        1
    );

    private static boolean enabled;

    private AutoRunFeature() {}

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
                "message.autotoggle.autorun.enabled", "message.autotoggle.autorun.disabled");
        }
    }
}
