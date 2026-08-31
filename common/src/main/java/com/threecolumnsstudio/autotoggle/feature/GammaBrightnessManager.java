package com.threecolumnsstudio.autotoggle.feature;

import com.threecolumnsstudio.autotoggle.mixin.OptionInstanceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

public final class GammaBrightnessManager {

    private static Double savedGamma;

    private GammaBrightnessManager() {}

    public static boolean isOverridden() {
        return savedGamma != null;
    }

    public static void ensureActive(Minecraft minecraft) {
        OptionInstance<Double> gamma = minecraft.options.gamma();
        if (savedGamma == null) {
            savedGamma = gamma.get();
        }
        OptionInstanceAccessor accessor = (OptionInstanceAccessor) (Object) gamma;
        accessor.autotoggle$setValue((double) FullGammaBrightFeature.FULL_BRIGHTNESS);
    }

    public static void restore(Minecraft minecraft) {
        if (savedGamma != null) {
            OptionInstanceAccessor accessor = (OptionInstanceAccessor) (Object) minecraft.options.gamma();
            accessor.autotoggle$setValue(savedGamma);
            savedGamma = null;
        }
    }
}