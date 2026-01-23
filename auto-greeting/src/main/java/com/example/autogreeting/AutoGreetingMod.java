package com.example.autogreeting;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class AutoGreetingMod implements ClientModInitializer {

    public static final AutoGreetingConfig CONFIG = AutoGreetingConfig.load();

    @Override
    public void onInitializeClient() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!CONFIG.enabled) return;

            client.execute(() -> {
                if (client.player == null) return;

                for (String msg : CONFIG.greetings) {
                    if (msg == null || msg.isBlank()) continue;

                    if (msg.startsWith("/")) {
                        client.player.networkHandler.sendChatCommand(msg.substring(1));
                    } else {
                        client.player.networkHandler.sendChatMessage(msg);
                    }
                }
            });
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(literal("autogreet")

                .then(literal("status").executes(ctx -> {
                    ctx.getSource().sendFeedback(
                        Text.literal("Auto greeting is " + (CONFIG.enabled ? "ON" : "OFF"))
                    );
                    return 1;
                }))

                .then(literal("on").executes(ctx -> {
                    CONFIG.enabled = true;
                    CONFIG.save();
                    ctx.getSource().sendFeedback(Text.literal("Auto greeting enabled."));
                    return 1;
                }))
                .then(literal("off").executes(ctx -> {
                    CONFIG.enabled = false;
                    CONFIG.save();
                    ctx.getSource().sendFeedback(Text.literal("Auto greeting disabled."));
                    return 1;
                }))
                .then(literal("toggle").executes(ctx -> {
                    CONFIG.enabled = !CONFIG.enabled;
                    CONFIG.save();
                    ctx.getSource().sendFeedback(
                        Text.literal("Auto greeting is now " + (CONFIG.enabled ? "ON" : "OFF"))
                    );
                    return 1;
                }))

                .then(literal("add")

                    .then(argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            CONFIG.greetings.add(
                                StringArgumentType.getString(ctx, "message")
                            );
                            CONFIG.save();
                            ctx.getSource().sendFeedback(Text.literal("Greeting appended."));
                            return 1;
                        })

                        .then(argument("index", IntegerArgumentType.integer(1))
                            .executes(ctx -> {
                                String msg = StringArgumentType.getString(ctx, "message");
                                int index = IntegerArgumentType.getInteger(ctx, "index");

                                int pos = Math.min(index - 1, CONFIG.greetings.size());
                                CONFIG.greetings.add(pos, msg);
                                CONFIG.save();

                                ctx.getSource().sendFeedback(
                                    Text.literal("Greeting inserted at position " + index + ".")
                                );
                                return 1;
                            })
                        )
                    )
                )

                .then(literal("list").executes(ctx -> {
                    if (CONFIG.greetings.isEmpty()) {
                        ctx.getSource().sendFeedback(Text.literal("Greeting list is empty."));
                        return 1;
                    }

                    int i = 1;
                    for (String msg : CONFIG.greetings) {
                        ctx.getSource().sendFeedback(
                            Text.literal(i++ + ". " + msg)
                        );
                    }
                    return 1;
                }))

                .then(literal("remove")

                    .executes(ctx -> {
                        if (CONFIG.greetings.isEmpty()) {
                            ctx.getSource().sendFeedback(Text.literal("Nothing to remove."));
                            return 1;
                        }

                        CONFIG.greetings.remove(CONFIG.greetings.size() - 1);
                        CONFIG.save();
                        ctx.getSource().sendFeedback(Text.literal("Last greeting removed."));
                        return 1;
                    })

                    .then(argument("index", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            int index = IntegerArgumentType.getInteger(ctx, "index");

                            if (index > CONFIG.greetings.size()) {
                                ctx.getSource().sendFeedback(Text.literal("Index out of range."));
                                return 1;
                            }

                            CONFIG.greetings.remove(index - 1);
                            CONFIG.save();
                            ctx.getSource().sendFeedback(
                                Text.literal("Greeting " + index + " removed.")
                            );
                            return 1;
                        })
                    )
                )

                .then(literal("removeAll").executes(ctx -> {
                    CONFIG.greetings.clear();
                    CONFIG.save();
                    ctx.getSource().sendFeedback(Text.literal("All greetings removed."));
                    return 1;
                }))
            );
        });
    }
}
