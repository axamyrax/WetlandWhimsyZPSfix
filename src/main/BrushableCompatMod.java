package com.example.brushablecompat;

import net.neoforged.fml.common.Mod;

/**
 * A tiny standalone compatibility patch. It has no dependency on Zero
 * Point System or Wetland Whimsy - it just relaxes a NeoForge check for
 * any mods that touch the vanilla BrushableBlock family, so it's safe to
 * keep installed even after the underlying mods update.
 */
@Mod(BrushableCompatMod.MOD_ID)
public class BrushableCompatMod {
    public static final String MOD_ID = "brushablecompat";

    public BrushableCompatMod() {
        // Nothing to do here - the whole fix lives in the Mixin.
    }
}
