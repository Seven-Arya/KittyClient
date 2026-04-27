package com.kittyclient;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethodStage;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KittyClient implements ClientModInitializer {
	public static final String MOD_ID = "kittyclient";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			// Do something
			String msg = message.getString();
			if (msg.contains("ASHFANG DOWN!")) {
					ashfangTracker();
				sendPartyMessage();

			}

			if (msg.contains("DOWN!")) {
				if (msg.contains("ASHFANG")) return;
				if (msg.contains("ARACHNE")) return;
				if (msg.contains("KUUDRA")) return;
				if (msg.contains("ENDSTONE")) return;
				if (msg.contains("DRAGON")) return;

				ashfangTime();
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
					ClientCommandManager.literal("startTimer").executes(context -> {
						// Do something
						ashfangTimeStart = System.currentTimeMillis();
						return 1;
					})
			);
		});


		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(MOD_ID, "before_chat"), (guiGraphics, deltaTracker) -> {
			String Counting = "Counter: " + counter;
			String Cycling = "Cycles: " + cycles;

			guiGraphics.drawString(Minecraft.getInstance().font, Counting, 40, 200, 0xFF55FFFF, true);
			guiGraphics.drawString(Minecraft.getInstance().font, Cycling, 40, 190, 0xFFFFAA00, true);
		});

	}




	// variables
	int counter;
	int help;
	int cycles;
	double ashfangTimer;
	double finalAshTime;
	int minutes;
	int seconds;
	double ashfangTimeStart;


	//functions
	private void ashfangTracker() {
		counter += 1;
		help += 1;


		if (help % 4 == 0) {
			cycles += 1;
		}

		if (counter == 4) {
			counter = 0;
		}

	}

	private void sendPartyMessage() {
		if (counter == 0) {
			sendChatCommand("pc 4/4 reset");
		} else {
			sendChatCommand("pc " + counter + "/4");
		}
	}

	private void ashfangTime() {
		ashfangTimer = System.currentTimeMillis();

		// timer in minutes
		ashfangTimer = ashfangTimer / 60000.0;
		finalAshTime = Double.parseDouble(
				String.format("%.2f", (ashfangTimer - ashfangTimeStart))
		);

		convertMinutesToMinutesAndSeconds(finalAshTime);

		// send message and reset timer
		sendChatCommand("pc KittyClient: Cycle took: " + minutes + "min and " + seconds + "s");

		ashfangTimeStart = System.currentTimeMillis();
		ashfangTimeStart = ashfangTimeStart / 60000.0;
	}

	private void convertMinutesToMinutesAndSeconds(double time) {
		double time2 = time % 1;
		double time3 = time2 * 60;
		double time4 = time3 % 1;

		seconds = (int)(time3 - time4);
		minutes = (int)(time - time2);
	}

	// util functions
	public void sendChatCommand(String command) {
		var connection = Minecraft.getInstance().getConnection();
		if (connection == null) return;

		connection.sendCommand(command);
	}
}