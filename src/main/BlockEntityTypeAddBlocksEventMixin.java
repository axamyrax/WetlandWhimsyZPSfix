package com.example.brushablecompat.mixin;

import net.minecraft.world.level.block.BrushableBlock;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Compatibility fix for a conflict between mods that replace vanilla
 * {@link BrushableBlock} instances with their own subclass (e.g. Zero Point
 * System's {@code ZPSBrushableBlock}) and mods that register additional
 * brushable-family blocks via {@link BlockEntityTypeAddBlocksEvent}
 * (e.g. Wetland Whimsy's {@code suspicious_mud}).
 *
 * <p>NeoForge's {@code addValidBlock} requires every newly-added block's
 * class to be assignable to the "common superclass" it already computed
 * for the block entity type's currently-valid blocks. If another mod has
 * silently swapped the vanilla brushable blocks for a stricter subclass,
 * that common superclass becomes that subclass instead of the original
 * {@code BrushableBlock}, and any third mod's plain {@code BrushableBlock}
 * subclass then fails the check and crashes the game at startup.
 *
 * <p>This mixin relaxes that check, but only for the brushable-block
 * family specifically: if the strict check fails, it still allows the
 * new block through as long as BOTH the computed base class and the new
 * block's class are themselves subclasses of vanilla {@code BrushableBlock}.
 * Every other use of {@link BlockEntityTypeAddBlocksEvent} in the game
 * (signs, chests, etc.) keeps NeoForge's original strict behavior.
 */
@Mixin(BlockEntityTypeAddBlocksEvent.class)
public abstract class BlockEntityTypeAddBlocksEventMixin {

    @Redirect(
            method = "addValidBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Class;isAssignableFrom(Ljava/lang/Class;)Z"
            )
    )
    private boolean brushablecompat$relaxBrushableFamilyCheck(Class<?> baseClass, Class<?> newBlockClass) {
        if (baseClass.isAssignableFrom(newBlockClass)) {
            // Original check already passes, nothing to do.
            return true;
        }

        // Strict check failed. Allow it anyway if both classes ultimately
        // descend from vanilla BrushableBlock - that's the case that
        // breaks when one mod (e.g. ZPS) swaps in its own BrushableBlock
        // subclass and another mod (e.g. Wetland Whimsy) registers its own
        // separate BrushableBlock subclass afterwards.
        return BrushableBlock.class.isAssignableFrom(baseClass)
                && BrushableBlock.class.isAssignableFrom(newBlockClass);
    }
}
