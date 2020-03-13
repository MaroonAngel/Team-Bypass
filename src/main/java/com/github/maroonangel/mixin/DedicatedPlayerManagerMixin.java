package com.github.maroonangel.mixin;

import com.github.maroonangel.TeamBypass;
import com.mojang.authlib.GameProfile;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DedicatedPlayerManager.class)
public class DedicatedPlayerManagerMixin extends PlayerManager {

	public DedicatedPlayerManagerMixin(MinecraftServer server, int maxPlayers) {
		super(server, maxPlayers);
	}

	@Overwrite
	public boolean canBypassPlayerLimit(GameProfile gameProfile) {
		Team playerTeam = getServer().getScoreboard().getPlayerTeam(gameProfile.getName());

		if (playerTeam != null) {
			String teamName = playerTeam.getDisplayName().asFormattedString();
			return (this.getOpList().isOp(gameProfile) || TeamBypass.config.bypassTeams.contains(teamName));
		} else
			return this.getOpList().isOp(gameProfile);
	}
}
