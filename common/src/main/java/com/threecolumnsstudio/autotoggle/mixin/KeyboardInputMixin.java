package com.threecolumnsstudio.autotoggle.mixin;

import com.threecolumnsstudio.autotoggle.feature.AutoJumpFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoRunFeature;
import com.threecolumnsstudio.autotoggle.feature.AutoShiftFeature;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {

    @Inject(method = "tick", at = @At("TAIL"))
    private void autotoggle$onTick(CallbackInfo ci) {
        boolean autoRunning = AutoRunFeature.isEnabled();
        boolean autoSneaking = AutoShiftFeature.isEnabled();
        boolean autoJumping = AutoJumpFeature.isEnabled();
        if (!autoRunning && !autoSneaking && !autoJumping) {
            return;
        }
        Input input = this.keyPresses;
        boolean sneaking = autoSneaking || input.shift();
        boolean jumping = autoJumping || input.jump();
        this.keyPresses = new Input(
            autoRunning || input.forward(),
            input.backward(),
            input.left(),
            input.right(),
            jumping,
            sneaking,
            !sneaking
        );
        if (autoRunning) {
            float lateral = input.left() == input.right() ? 0.0F : (input.left() ? 1.0F : -1.0F);
            this.moveVector = new Vec2(lateral, 1.0F).normalized();
        }
    }
}
