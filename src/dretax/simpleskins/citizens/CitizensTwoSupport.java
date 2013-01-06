package dretax.simpleskins.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CitizensTwoSupport
{
  public SpoutPlayer _sp;

  public CitizensTwoSupport(int id)
  {
    SpoutPlayer sp = null;
    NPC npc = CitizensAPI.getNPCRegistry().getById(id);
    if ((npc != null) && 
      (npc.getBukkitEntity() != null) && 
      ((npc.getBukkitEntity() instanceof LivingEntity))) {
      sp = SpoutManager.getPlayer((Player)npc.getBukkitEntity());
    }

    this._sp = sp;
  }
}