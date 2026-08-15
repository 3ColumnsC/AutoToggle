package com.threecolumnsstudio.autotoggle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AutoToggle {

    public static final String MOD_ID = "autotoggle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int OVERLAY_MESSAGE_Y = 67;

    private AutoToggle() {}

    public static void init() {
        LOGGER.info("{} initialized", MOD_ID);
    }
}
