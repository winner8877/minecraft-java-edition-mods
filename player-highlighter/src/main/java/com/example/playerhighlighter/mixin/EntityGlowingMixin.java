package com.example.playerhighlighter.mixin;

import com.example.playerhighlighter.PlayerHighlighterMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGlowingMixin {

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void playerhighlighter$glowOnlyWhenTabPressed(CallbackInfoReturnable<Boolean> cir) {
		Entity self = (Entity) (Object) this;

		if (!(self instanceof PlayerEntity)) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.options == null) {
			return;
		}

		if (PlayerHighlighterMod.HOLD_KEY.isPressed() || PlayerHighlighterMod.config.keep) {
			cir.setReturnValue(true);
		}
	}
}
