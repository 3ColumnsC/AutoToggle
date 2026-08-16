package com.threecolumnsstudio.autotoggle.mixin;

import com.threecolumnsstudio.autotoggle.AutoToggle;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public abstract class HudOverlayMixin {

    @ModifyConstant(method = "extractOverlayMessage", constant = @Constant(intValue = 68))
    private int autotoggle$lowerOverlayMessage(int value) {
        return AutoToggle.OVERLAY_MESSAGE_Y;
    }
}
