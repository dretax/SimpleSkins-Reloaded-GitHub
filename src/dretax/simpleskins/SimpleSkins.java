package dretax.simpleskins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dretax.simpleskins.citizens.CitizensOneSupport;
import dretax.simpleskins.citizens.CitizensTwoSupport;
import dretax.simpleskins.metrics.Metrics;

public class SimpleSkins extends JavaPlugin {
	public Logger _log;
	public PluginManager _pm;
	public static ConsoleCommandSender _cs;
	public static final String _prefix = ChatColor.AQUA
			+ "[SimpleSkins Reloaded] ";
	public Map<String, String> _playerSkins = new HashMap<String, String>();
	public Map<String, String> _playerCapes = new HashMap<String, String>();
	public Map<Integer, String> _citizenSkins = new HashMap<Integer, String>();
	public Map<Integer, String> _citizenCapes = new HashMap<Integer, String>();
	public Map<String, String> _savedSkins = new HashMap<String, String>();
	public Map<String, String> _savedCapes = new HashMap<String, String>();
	public Metrics metrics;
	public long _refreshTime = 40L;
	public String _savedSkinPerm = "simpleskins.skin.";
	public String _savedCapePerm = "simpleskins.cape.";
	public boolean _spoutLoaded = false;
	public int _citizensVersion = 0;
	public boolean _canClear = false;
	public boolean _checkedForCitizensOnJoin = false;
	private String _minecraftSkinLoc = "http://s3.amazonaws.com/MinecraftSkins/";
	private String _minecraftCapeLoc = "http://s3.amazonaws.com/MinecraftCloaks/";
	private SimpleSkinsExecutor executor;
	private CitizensOneSupport _citizensOne = null;
	private CitizensTwoSupport _citizensTwo = null;

	public void onEnable() {
		this._log = getLogger();
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();

		if (!checkDependency("Spout")) {
			sendConsoleMessage(ChatColor.RED
					+ "No SpoutPlugin Found! Disabling Simple Skins Reloaded.");
			this._pm.disablePlugin(this);
		} else {
			this._spoutLoaded = true;

			loadCitizensSupport();

			this._pm.registerEvents(new SimpleSkinsListener(this), this);

			this.executor = new SimpleSkinsExecutor(this);
			getCommand("playerskin").setExecutor(this.executor);
			getCommand("playercape").setExecutor(this.executor);
			getCommand("saveskin").setExecutor(this.executor);
			getCommand("savecape").setExecutor(this.executor);
			getCommand("npcskin").setExecutor(this.executor);
			getCommand("npccape").setExecutor(this.executor);
			getCommand("ssimport").setExecutor(this.executor);

			getCommand("groupskin").setExecutor(this.executor);
			getCommand("groupcape").setExecutor(this.executor);
			try {
				new SimpleSkinsFileManager(this, false);
			} catch (IOException ex) {
				ex.printStackTrace();
				this._log.info("Failed to Load Skin Manager on Enable!");
			}
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();
			    sendConsoleMessage(ChatColor.GREEN + "Simple Skins Metrics Enabled!");
			} catch (IOException e) {
			    // Failed to submit the stats :-(
			}
			getServer().getScheduler().scheduleSyncRepeatingTask(this,
					new Runnable() {
						public void run() {
							for (SpoutPlayer sp : SpoutManager
									.getOnlinePlayers()) {
								String name = sp.getName().toLowerCase();

								SimpleSkins.this.updateLivePermissionPlayer(sp);
								SimpleSkins.this.updateLivePlayer(sp);

                                if ((SimpleSkins.this._playerSkins.containsKey(name))
                                        && ((SimpleSkins.this._playerSkins.get(name)).isEmpty())
                                        && (SimpleSkins.this._canClear)) {
                                    SimpleSkins.this._playerSkins.remove(name);
                                        }
								if ((SimpleSkins.this._playerCapes.containsKey(name))
										&& ((SimpleSkins.this._playerCapes.get(name)).isEmpty())
										&& (SimpleSkins.this._canClear)) {
									SimpleSkins.this._playerCapes.remove(name);
								}
							}

							if (SimpleSkins.this._citizensVersion > 0) {
								Set<Integer> ids = new HashSet<Integer>(SimpleSkins.this._citizenSkins.keySet());
								ids.addAll(SimpleSkins.this._citizenCapes.keySet());
								for (Integer id : ids) {
                                    SimpleSkins.this.updateLiveCitizen(id.intValue());
								}
							}
							SimpleSkins.this._canClear = false;
						}
					}, 0L, this._refreshTime);
		}
		sendConsoleMessage(ChatColor.GREEN + "Simple Skins Reloaded v"
				+ getDescription().getVersion() + " Enabled!");
	}

	public static void sendConsoleMessage(String msg) {
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}

	public void setSkin(SpoutPlayer sp, String skin) {
		Validate.notNull(sp, "Player cannot be null!");

		if ((!isValidURL(skin)) && (this._savedSkins.containsKey(skin))) {
			skin = this._savedSkins.get(skin);
		}

		if (isValidURL(skin)) {
			sp.setSkin(skin);
		} else if ((skin != null) && (skin.length() > 0)) {
			sp.setSkin(this._minecraftSkinLoc + skin + ".png");
		} else {
			sp.setSkin(this._minecraftSkinLoc + sp.getName() + ".png");
		}
	}

	public void setCape(SpoutPlayer sp, String cape) {
		Validate.notNull(sp, "Player cannot be null!");

		if ((!isValidURL(cape)) && (this._savedCapes.containsKey(cape))) {
			cape = this._savedCapes.get(cape);
		}

		if (isValidURL(cape)) {
			sp.setCape(cape);
		} else if ((cape != null) && (!cape.isEmpty())) {
			sp.setCape(this._minecraftCapeLoc + cape + ".png");
		} else {
			sp.setCape("http://blank.png");
		}
	}

	public void updateLivePlayer(SpoutPlayer sp) {
		if (sp != null) {
			String name = sp.getName().toLowerCase();
			if (this._playerSkins.containsKey(name)) {
				setSkin(sp, this._playerSkins.get(name));
			}
			if (this._playerCapes.containsKey(name))
				setCape(sp, this._playerCapes.get(name));
		}
	}

	public void updateLivePermissionPlayer(SpoutPlayer sp) {
		if (sp != null) {
			if (!this._savedSkins.isEmpty()) {
				for (String pname : this._savedSkins.keySet()) {
					if (hasSavedSkinPermission(sp, pname)) {
						setSkin(sp, this._savedSkins.get(pname));
						break;
					}
				}
			}

			if (!this._savedCapes.isEmpty())
				for (String pname : this._savedCapes.keySet())
					if (hasSavedCapePermission(sp, pname)) {
						setCape(sp, this._savedCapes.get(pname));
						break;
					}
		}
	}

	public void updateAllLivePlayersWithPermission(String pname) {
		if (pname.length() > 0) {
			pname = pname.toLowerCase();
			for (SpoutPlayer sp : SpoutManager.getOnlinePlayers()) {
				if (hasSavedSkinPermission(sp, pname)) {
					setSkin(sp, this._savedSkins.get(pname));
				}
				if (hasSavedCapePermission(sp, pname))
					setCape(sp, this._savedCapes.get(pname));
			}
		}
	}

	public void updateLiveCitizen(int id) {
		SpoutPlayer sp = null;
		// Get the NPC as a Spout Player
	    
	    if (this._citizensVersion == 1) {
	      CitizensOneSupport cone = new CitizensOneSupport(id);
	      sp = cone._sp;
	    }
	    else if (this._citizensVersion == 2) {
	      CitizensTwoSupport ctwo = new CitizensTwoSupport(id);
	      sp = ctwo._sp;
	    }

		if (sp != null) {
			setSkin(sp, this._citizenSkins.get(id));
			setCape(sp, this._citizenCapes.get(id));
		}
	}

	public boolean hasSavedSkinPermission(SpoutPlayer sp, String perm) {
		if ((sp.hasPermission(this._savedSkinPerm + perm)) && ((!sp.hasPermission("*")) || (!sp.isOp()))) {
			return true;
		}
		return false;
	}

	public boolean hasSavedCapePermission(SpoutPlayer sp, String perm) {
		if ((sp.hasPermission(this._savedCapePerm + perm)) && ((!sp.hasPermission("*")) || (!sp.isOp()))) {
			return true;
		}
		return false;
	}

	public boolean checkDependency(String plugname) {
		Plugin plug = this._pm.getPlugin(plugname);
		if ((plug != null) && (plug.isEnabled())) {
			return true;
		}

		return false;
	}

	public void loadCitizensSupport() {
		this._citizensVersion = getCitizensVersion();

		if (this._citizensVersion == 0)  {
			sendConsoleMessage(ChatColor.GREEN + "No Citizens Plugin found. Citizens Support is offline for now.");
            sendConsoleMessage(ChatColor.GREEN + "If You have Citizens, then don't worry, just join the srv.");
        }

		else if (this._citizensVersion == 1) {
			sendConsoleMessage(ChatColor.GREEN + "Citizens v1.2.3 Support Enabled!");
		}

		else if (this._citizensVersion == 2)   {
			sendConsoleMessage(ChatColor.GREEN + "Citizens v2.0.X Support Enabled!");
        }
	}

	public int getCitizensVersion() {
		if (checkDependency("Citizens")) {
			ArrayList<Integer> numbers = new ArrayList<Integer>();
			String ver = this._pm.getPlugin("Citizens").getDescription().getVersion();
			// find the first number
			Pattern p = Pattern.compile("[0-9]+");
			Matcher m = p.matcher(ver);
			while (m.find()) {
				numbers.add(Integer.parseInt(m.group()));
			}
			// check version number
			if (numbers.size() >= 3) {
				// version number has 3 digits version bigger than 2.0.6
				if (numbers.get(0) == 2 && numbers.get(1) == 0 && numbers.get(2) >= 6) {
					return numbers.get(0);
				}
			}
			// if the number is much higher than 2.0.x check only the first two
			// digits
			if (numbers.size() >= 2) {
				// 2 digits - version > 2.1
				if (numbers.get(0) == 2 && numbers.get(1) >= 1) {
					return numbers.get(0);
				}
			}
			// if the number is much higher than 2.x.x check only the first two
			// digits
			if (numbers.size() >= 1) {
				// 2 digits - version > 2
				if (numbers.get(0) >= 3) {
					return numbers.get(0);
				}
			}
			if (ver.equals("1.2.4")) {
				set_citizensOne(new CitizensOneSupport());
				return 1;
			}
		}
		return 0;
	}

	public boolean isValidURL(String url) {
		if ((url != null) && (!url.isEmpty())
				&& ((url.contains("http://")) || (url.contains("https://")))
				&& (url.endsWith(".png"))) {
			return true;
		}
		return false;
	}

	public void saveDataToFiles() {
		try {
			new SimpleSkinsFileManager(this, true);
		} catch (IOException ex) {
			ex.printStackTrace();
			this._log.info("Failed to Load Skin Manager on Disable!");
		}
	}

	public void onDisable() {
		if (this._spoutLoaded)
			saveDataToFiles();
	}

	public CitizensOneSupport get_citizensOne() {
		return _citizensOne;
	}

	public void set_citizensOne(CitizensOneSupport _citizensOne) {
		this._citizensOne = _citizensOne;
	}

	public CitizensTwoSupport get_citizensTwo() {
		return _citizensTwo;
	}

	public void set_citizensTwo(CitizensTwoSupport _citizensTwo) {
		this._citizensTwo = _citizensTwo;
	}
}