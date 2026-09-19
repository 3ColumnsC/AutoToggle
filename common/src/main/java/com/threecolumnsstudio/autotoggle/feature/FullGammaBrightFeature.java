package com.threecolumnsstudio.autotoggle.feature;

import com.mojang.blaze3d.platform.InputConstants;
import com.threecolumnsstudio.autotoggle.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public final class FullGammaBrightFeature {

    public static final float FULL_BRIGHTNESS = 15.0F;

    public static final KeyMapping KEY = new KeyMapping(
        "key.autotoggle.fullgammabright.toggle",
        InputConstants.Type.KEYBOARD,
        InputConstants.KEY_Z,
        KeyBindings.CATEGORY,
        7
    );

    private static boolean enabled;

    private FullGammaBrightFeature() {}

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
            GammaBrightnessManager.restore(minecraft);
        }
    }

    public static void onClientTick(Minecraft minecraft) {
        if (KEY.consumeClick()) {
            boolean toggled = toggle();
            if (toggled) {
                GammaBrightnessManager.ensureActive(minecraft);
            } else {
                GammaBrightnessManager.restore(minecraft);
            }
            ToggleFeedback.show(minecraft, toggled, SoundEvents.UI_BUTTON_CLICK.value(),
                "message.autotoggle.fullgammabright.enabled", "message.autotoggle.fullgammabright.disabled");
            return;
        }
        if (enabled) {
            GammaBrightnessManager.ensureActive(minecraft);
        }
    }
}