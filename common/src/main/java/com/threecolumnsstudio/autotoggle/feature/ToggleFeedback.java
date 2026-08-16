package com.threecolumnsstudio.autotoggle.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public final class ToggleFeedback {

    private ToggleFeedback() {}

    public static void playSound(Minecraft minecraft, SoundEvent sound) {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static void show(Minecraft minecraft, boolean enabled, SoundEvent sound, String enabledKey, String disabledKey) {
        show(minecraft, enabled, sound, enabledKey, disabledKey, 0xFFFFFF);
    }

    public static void show(Minecraft minecraft, boolean enabled, SoundEvent sound, String enabledKey, String disabledKey, int color) {
        playSound(minecraft, sound);
        minecraft.gui.setOverlayMessage(
            Component.translatable(enabled ? enabledKey : disabledKey).withColor(color),
            false
        );
    }
}
