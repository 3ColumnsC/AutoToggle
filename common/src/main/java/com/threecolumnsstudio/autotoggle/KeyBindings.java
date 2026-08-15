package com.threecolumnsstudio.autotoggle;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class KeyBindings {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(AutoToggle.MOD_ID, "autotoggle")
    );

    private KeyBindings() {}
}
