package dretax.simpleskins.citizens;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;


public class CitizensOneSupport {
	
	public SpoutPlayer getCitizenAsPlayer(int id) {
		SpoutPlayer sp = null;
		HumanNPC npc = CitizensManager.getNPC(id);
		if(npc != null) {
			if(npc.getPlayer() instanceof Player) {
				sp = SpoutManager.getPlayer((Player) npc.getPlayer());
			}
		}
		return sp;
	}

}
