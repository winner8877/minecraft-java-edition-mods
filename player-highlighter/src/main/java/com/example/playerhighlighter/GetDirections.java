package com.example.playerhighlighter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.ArrayList;

public final class GetDirections {

	public record ScreenResult(float x, float y) {}

	public static List<ScreenResult> projectAllPlayersHud() {
		List<ScreenResult> result = new ArrayList<>();

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.world == null) {
			return result;
		}

		Entity camera = client.getCameraEntity();
		if (camera == null) {
			return result;
		}

		GameRenderer renderer = client.gameRenderer;

		int screenW = client.getWindow().getScaledWidth();
		int screenH = client.getWindow().getScaledHeight();

		float screenHalfW = screenW * 0.5f;
		float screenHalfH = screenH * 0.5f;

		Vec3d camPos = new Vec3d(
			camera.getX(),
			camera.getY(),
			camera.getZ()
		);

		Vec3d camForward = camera.getRotationVec(1.0f);

		for (PlayerEntity player : client.world.getPlayers()) {
			if (player == camera) {
				continue;
			}
			double px = player.getX();
			double py = player.getY() + player.getStandingEyeHeight();
			double pz = player.getZ();

			double dx = px - camPos.x;
			double dy = py - camPos.y;
			double dz = pz - camPos.z;

			if (camForward.x * dx + camForward.y * dy + camForward.z * dz <= 0.0) {
				continue;
			}

			Vec3d worldPos = new Vec3d(
				px,
				py,
				pz
			);

			Vec3d projected = renderer.project(worldPos);

			if (projected != null) {
				float ndscreenHalfW = (float) projected.x;
				float ndscreenHalfH = (float) projected.y;

				float x = (ndscreenHalfW + 1f) * screenHalfW;
				float y = (1f - ndscreenHalfH) * screenHalfH;

				if (x >= 0 && x <= screenW && y >= 0 && y <= screenH) {
					result.add(new ScreenResult(x, y));
				}
				continue;
			}
		}

		return result;
	}
}
