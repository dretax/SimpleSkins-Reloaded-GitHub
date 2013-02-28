package dretax.simpleskins.citizens;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CitizensOneSupport {
	public SpoutPlayer _sp;

	public CitizensOneSupport(int id) {
		SpoutPlayer sp = null;
		HumanNPC npc = CitizensManager.getNPC(id);
		if ((npc != null) && ((npc.getPlayer() instanceof Player))) {
			sp = SpoutManager.getPlayer(npc.getPlayer());
		}

		this._sp = sp;
	}

	public CitizensOneSupport() {
		// TODO Auto-generated constructor stub
	}
}