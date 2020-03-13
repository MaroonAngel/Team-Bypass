package com.github.maroonangel;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javafx.scene.control.TextFormatter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class TeamBypass implements ModInitializer {
	public static List<String> bypassTeams;

	public static final Logger LOGGER = (Logger) LogManager.getLogger();
	public static BypassConfig config;
	private static final File configDirectory = FabricLoader.getInstance().getConfigDirectory();

	private static final Text tbp = new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD));

	@Override
	public void onInitialize() {

		LOGGER.info("[TeamBypass] Loading TeamBypass...");
		config = BypassConfig.loadConfig(new File(configDirectory.toString() + "/TeamBypassConfig.json"));

		RegisterCommand();
	}

	private void RegisterCommand() {

		// Bypass command
		CommandRegistry.INSTANCE.register(true, serverCommandSourceCommandDispatcher -> serverCommandSourceCommandDispatcher.register(
			CommandManager.literal("bypass")
				.requires((source -> source.hasPermissionLevel(4)))
				.then(CommandManager.literal("add").then(CommandManager.argument("team", StringArgumentType.string())
					.executes(context -> addTeam(context.getSource(), getString(context, "team")))))
				.then(CommandManager.literal("remove").then(CommandManager.argument("team", StringArgumentType.string())
					.executes(context -> removeTeam(context.getSource(), getString(context, "team")))))
				.then(CommandManager.literal("list")
					.executes(context -> listTeams(context.getSource())))
		));
	}

	public static int addTeam(ServerCommandSource source, String team) throws CommandSyntaxException {
		PlayerEntity player = source.getPlayer();

		if (team == "") {
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("You must specify a team name.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return 0;
		}



		if (GetTeamNames(source).contains(team) && !config.bypassTeams.contains(team)) {
			config.bypassTeams.add(team);
			config.saveConfig(new File(configDirectory.toString() + "/TeamBypassConfig.json"));
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText(team + " was added to the bypass list.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return Command.SINGLE_SUCCESS;
		}
		else if (config.bypassTeams.contains(team)) {
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText(team + " is already in the bypass list.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return 0;
		}
		else {
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("That team does not exist.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return 0;
		}
	}

	private static int removeTeam(ServerCommandSource source, String team) throws CommandSyntaxException {
		PlayerEntity player = source.getPlayer();

		if (team == "") {
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("You must specify a team name.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return 0;
		}

		if (config.bypassTeams.contains(team)) {
			config.bypassTeams.remove(team);
			config.saveConfig(new File(configDirectory.toString() + "/TeamBypassConfig.json"));
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText(team + " was removed from the bypass list.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return Command.SINGLE_SUCCESS;
		} else {
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("That team is not in the bypass list.").setStyle(new Style().setColor(Formatting.YELLOW))));
			return 0;
		}
	}

	public static int listTeams(ServerCommandSource source) throws CommandSyntaxException {
		PlayerEntity player = source.getPlayer();
		if (config.bypassTeams.size() > 0)
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("Current bypass teams: " + config.bypassTeams.toString()).setStyle(new Style().setColor(Formatting.YELLOW))));
		else
			player.sendMessage(new TranslatableText("[TeamBypass] ").setStyle(new Style().setColor(Formatting.GOLD)).append(new TranslatableText("There are no teams in the bypass list.").setStyle(new Style().setColor(Formatting.YELLOW))));


		return Command.SINGLE_SUCCESS;
	}

	private static List<String> GetTeamNames(ServerCommandSource source) {
		List<String> currentTeamNames = new ArrayList<String>();
		for (net.minecraft.scoreboard.Team t : source.getMinecraftServer().getScoreboard().getTeams()) {
			currentTeamNames.add(t.getName());
		}
		return currentTeamNames;
	}
}
