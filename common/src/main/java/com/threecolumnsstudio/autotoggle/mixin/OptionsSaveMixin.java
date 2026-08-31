package com.threecolumnsstudio.autotoggle.mixin;

import com.threecolumnsstudio.autotoggle.feature.GammaBrightnessManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsSaveMixin {

    @Inject(method = "save", at = @At("HEAD"))
    private void autotoggle$restoreGammaBeforeSave(CallbackInfo ci) {
        if (GammaBrightnessManager.isOverridden()) {
            GammaBrightnessManager.restore(Minecraft.getInstance());
        }
    }
}