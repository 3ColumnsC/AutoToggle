package com.threecolumnsstudio.autotoggle.fabric;

import com.threecolumnsstudio.autotoggle.AutoToggle;
import com.threecolumnsstudio.autotoggle.AutoToggleClient;
import com.threecolumnsstudio.autotoggle.feature.AutoJumpFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoLeftClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRightClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRunFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoShiftFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoToolFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoElytraFireworkFeature;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class AutoToggleFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoToggle.init();
        KeyMappingHelper.registerKeyMapping(AutoRunFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoShiftFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoJumpFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoLeftClickFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoRightClickFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoToolFeature.KEY);
        KeyMappingHelper.registerKeyMapping(AutoElytraFireworkFeature.KEY);
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.OVERLAY_MESSAGE,
            Identifier.fromNamespaceAndPath(AutoToggle.MOD_ID, "autotool_status"),
            AutoToolFeature::renderStatus
        );
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.OVERLAY_MESSAGE,
            Identifier.fromNamespaceAndPath(AutoToggle.MOD_ID, "autoelytrafirework_status"),
            AutoElytraFireworkFeature::renderStatus
        );
        ClientTickEvents.END_CLIENT_TICK.register(AutoToggleClient::onClientTick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AutoToggleClient.reset());
    }
}
