package com.threecolumnsstudio.autotoggle.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor {

    @Accessor("value")
    Object autotoggle$getValue();

    @Accessor("value")
    void autotoggle$setValue(Object value);
}