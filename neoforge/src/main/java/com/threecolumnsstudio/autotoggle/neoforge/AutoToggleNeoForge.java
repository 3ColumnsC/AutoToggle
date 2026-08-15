package com.threecolumnsstudio.autotoggle.neoforge;

import com.threecolumnsstudio.autotoggle.AutoToggle;
import com.threecolumnsstudio.autotoggle.AutoToggleClient;
import com.threecolumnsstudio.autotoggle.feature.AutoJumpFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoLeftClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRightClickFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRunFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoShiftFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoToolFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoElytraFireworkFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AutoToggle.MOD_ID, dist = Dist.CLIENT)
public class AutoToggleNeoForge {

    public AutoToggleNeoForge(IEventBus modEventBus) {
        AutoToggle.init();
        modEventBus.addListener(this::onRegisterKeyMappings);
        modEventBus.addListener(this::onRegisterGuiLayers);

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event ->
            AutoToggleClient.onClientTick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
            AutoToggleClient.reset());
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(AutoRunFeature.KEY);
        event.register(AutoShiftFeature.KEY);
        event.register(AutoJumpFeature.KEY);
        event.register(AutoLeftClickFeature.KEY);
        event.register(AutoRightClickFeature.KEY);
        event.register(AutoToolFeature.KEY);
        event.register(AutoElytraFireworkFeature.KEY);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        float shift = 68.0F - AutoToggle.OVERLAY_MESSAGE_Y;
        event.wrapLayer(VanillaGuiLayers.OVERLAY_MESSAGE, layer -> (graphics, deltaTracker) -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate(0.0F, shift);
            layer.render(graphics, deltaTracker);
            graphics.pose().popMatrix();
        });
        event.registerAbove(
            VanillaGuiLayers.OVERLAY_MESSAGE,
            Identifier.fromNamespaceAndPath(AutoToggle.MOD_ID, "autotool_status"),
            AutoToolFeature::renderStatus
        );
        event.registerAbove(
            VanillaGuiLayers.OVERLAY_MESSAGE,
            Identifier.fromNamespaceAndPath(AutoToggle.MOD_ID, "autoelytrafirework_status"),
            AutoElytraFireworkFeature::renderStatus
        );
    }
}
