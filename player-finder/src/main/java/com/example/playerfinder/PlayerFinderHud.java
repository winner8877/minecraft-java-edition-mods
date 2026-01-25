package com.example.playerfinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerFinderHud {
	public static void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity self = client.player;
		ClientWorld world = client.world;

		if (self == null || world == null) return;
		if (client.options.hudHidden) return;

		int baseX = 8;
		int lineHeight = client.textRenderer.fontHeight + 2;
		int screenHeight = context.getScaledWindowHeight();
		int y = screenHeight - 22 - 8;

		Vec3d selfPos = new Vec3d(self.getX(), self.getY(), self.getZ());

		for (var player : world.getPlayers()) {
			if (player == self) continue;

			Vec3d targetPos = new Vec3d(
					player.getX(),
					player.getY(),
					player.getZ()
			);

			double dx = targetPos.x - selfPos.x;
			double dz = targetPos.z - selfPos.z;

			int distance = (int) Math.sqrt(dx * dx + dz * dz);
			String arrow = getArrow(self, targetPos);
			float health = player.getHealth();

			int x = baseX;

			String name = player.getName().getString();
			context.drawText(client.textRenderer, name, x, y, 0xFFF0F0F0, false);
			x += client.textRenderer.getWidth(name + " ");

			context.drawText(client.textRenderer, arrow, x, y, 0xFF55FFFF, false);
			x += client.textRenderer.getWidth(arrow + " ");

			String distText = distance + "m";
			context.drawText(client.textRenderer, distText, x, y, 0xFFB0B0B0, false);
			x += client.textRenderer.getWidth(distText + " ");

			String healthText = "❤ " + String.format("%.1f", health);
			context.drawText(client.textRenderer, healthText, x, y, 0xFF55FFFF, false);
			x += client.textRenderer.getWidth(healthText + " ");

			String posText = String.format(
				"(%d, %d, %d)",
				(int) targetPos.x,
				(int) targetPos.y,
				(int) targetPos.z
			);

			context.drawText(
				client.textRenderer,
				posText,
				x,
				y,
				0xFF909090,
				false
			);
			x += client.textRenderer.getWidth(posText + " ");

			y -= lineHeight;
			if (y < 8) break;
		}
	}

	private static String getArrow(ClientPlayerEntity self, Vec3d targetPos) {
		Vec3d selfPos = new Vec3d(self.getX(), self.getY(), self.getZ());

		double dx = targetPos.x - selfPos.x;
		double dz = targetPos.z - selfPos.z;

		double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
		float selfYaw = MathHelper.wrapDegrees(self.getYaw());
		double diff = MathHelper.wrapDegrees(targetYaw - selfYaw);

		if (diff >= -22.5 && diff < 22.5) return "↑";
		if (diff >= 22.5 && diff < 67.5) return "↗";
		if (diff >= 67.5 && diff < 112.5) return "→";
		if (diff >= 112.5 && diff < 157.5) return "↘";
		if (diff >= -67.5 && diff < -22.5) return "↖";
		if (diff >= -112.5 && diff < -67.5) return "←";
		if (diff >= -157.5 && diff < -112.5) return "↙";
		return "↓";
	}
}
