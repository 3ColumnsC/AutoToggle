package com.threecolumnsstudio.autotoggle;

import com.threecolumnsstudio.autotoggle.feature.AutoJumpFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoLeftClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRightClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRunFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoShiftFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoToolFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoElytraFireworkFeature;
import net.minecraft.client.Minecraft;

public final class AutoToggleClient {

    private AutoToggleClient() {}

    public static void onClientTick(Minecraft minecraft) {
        AutoRunFeature.onClientTick(minecraft);
        AutoShiftFeature.onClientTick(minecraft);
        AutoJumpFeature.onClientTick(minecraft);
        AutoLeftClickFeature.onClientTick(minecraft);
        AutoRightClickFeature.onClientTick(minecraft);
        AutoToolFeature.onClientTick(minecraft);
        AutoElytraFireworkFeature.onClientTick(minecraft);
    }

    public static void reset() {
        AutoRunFeature.reset();
        AutoShiftFeature.reset();
        AutoJumpFeature.reset();
        AutoLeftClickFeature.reset();
        AutoRightClickFeature.reset();
        AutoToolFeature.reset();
        AutoElytraFireworkFeature.reset();
    }
}
