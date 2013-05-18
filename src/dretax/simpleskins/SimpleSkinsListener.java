package dretax.simpleskins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleSkinsListener
  implements Listener
{
	private SimpleSkins _instance;

	public SimpleSkinsListener(SimpleSkins instance)
	{
		this._instance = instance;
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onSpoutcraftEnable(SpoutCraftEnableEvent e)
	{
		if ((e.getPlayer() instanceof SpoutPlayer)) {
			SpoutPlayer sp = e.getPlayer();
			this._instance.updateLivePermissionPlayer(sp);
			this._instance.updateLivePlayer(sp);
		}
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlayerTeleport(PlayerTeleportEvent e)
	{
		if ((e.getPlayer() instanceof SpoutPlayer)) {
			SpoutPlayer sp = (SpoutPlayer)e.getPlayer();
			this._instance.updateLivePermissionPlayer(sp);
			this._instance.updateLivePlayer(sp);
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		if ((e.getPlayer() instanceof SpoutPlayer)) {
			SpoutPlayer sp = (SpoutPlayer)e.getPlayer();
			this._instance.updateLivePermissionPlayer(sp);
			this._instance.updateLivePlayer(sp);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if (!this._instance._checkedForCitizensOnJoin) {
			if (this._instance._citizensVersion == 0)
				this._instance.loadCitizensSupport();
			this._instance._checkedForCitizensOnJoin = true;
		}
	}
}