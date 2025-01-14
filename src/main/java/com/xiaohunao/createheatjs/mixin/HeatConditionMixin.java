package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatConditionMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    @Shadow public abstract String serialize();

    @Invoker("<init>")
    public static HeatCondition heatExpansion$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    }
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        CreateHeatJS.heatDataMap.forEach((condition, heatData) -> {
            HeatCondition heatCondition = heatExpansion$addVariant(condition, heatData.getColor());
            heatData.setHeatCondition(heatCondition);
        });
    }


    @Unique
    private static HeatCondition heatExpansion$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        return heat;
    }

    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void testBlazeBurnerMixin(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}