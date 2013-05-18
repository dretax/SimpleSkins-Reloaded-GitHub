package dretax.simpleskins;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class SimpleSkinsFileManager
{
  private SimpleSkins _SimpleSkins;
  private File _skinFile;
  private File _capeFile;
  private File _urlFile;
  private YamlConfiguration _skinYAML;
  private YamlConfiguration _capeYAML;
  private final String _players = "Players";
  private final String _savedURLs = "SavedURLs";
  private final String _citizens = "Citizens";
  private static final String _groups = "Groups";
  private static String _pluginsFolder = "/plugins";

  protected SimpleSkinsFileManager(SimpleSkins instance, boolean save)
    throws IOException
  {
    this._SimpleSkins = instance;
    this._skinFile = new File(this._SimpleSkins.getDataFolder(), "skins.yml");
    this._capeFile = new File(this._SimpleSkins.getDataFolder(), "capes.yml");
    this._urlFile = new File(this._SimpleSkins.getDataFolder(), "urls.yml");
    try { checkFilesExist(); } catch (IOException ex) {
      ex.printStackTrace(); this._SimpleSkins._log.info("Failed to Check Files!");
    }this._skinYAML = YamlConfiguration.loadConfiguration(this._skinFile);
    this._capeYAML = YamlConfiguration.loadConfiguration(this._capeFile);
    if (save) {
      saveData();
    }
    else {
      loadData("Players");
      loadData("SavedURLs");
      loadData("Citizens");

      loadData("Groups");
    }
    this._skinYAML.save(this._skinFile);
    this._capeYAML.save(this._capeFile);
  }

  private void loadData(String type)
  {
    if ((this._skinYAML.isInt("AppearanceRefreshSeconds")) && (this._skinYAML.getInt("AppearanceRefreshSeconds") >= 1)) {
      this._SimpleSkins._refreshTime = (this._skinYAML.getInt("AppearanceRefreshSeconds") * 20);
    }
    else
    {
      this._skinYAML.set("AppearanceRefreshSeconds", Integer.valueOf(3));
    }

    if (this._skinYAML.getConfigurationSection(type) != null) {
      for (String name : this._skinYAML.getConfigurationSection(type).getKeys(false)) {
        name = name.toLowerCase();
        String url = this._skinYAML.getString(type + "." + name);
        if (!url.isEmpty()) {
          if (type.equalsIgnoreCase("Players")) {
            this._SimpleSkins._playerSkins.put(name, url);
          }
          else if ((type.equalsIgnoreCase("SavedURLs")) || (type.equalsIgnoreCase("Groups"))) {
            this._SimpleSkins._savedSkins.put(name, url);
            if (type.equalsIgnoreCase("Groups")) {
              this._skinYAML.set("SavedURLs." + name, url);
            }
          }
          else if (type.equalsIgnoreCase("Citizens")) {
            try {
              int id = Integer.parseInt(name);
              this._SimpleSkins._citizenSkins.put(id, url);
            }
            catch (NumberFormatException nfe) {
              this._SimpleSkins._log.info("Invalid Citizen ID: " + name + " - Skipping.");
            }
          }
        }
        else {
          this._skinYAML.set(type + "." + name, null);
        }
      }
    }
    else
    {
      this._skinYAML.createSection(type);
    }

    if (this._capeYAML.getConfigurationSection(type) != null) {
      for (String name : this._capeYAML.getConfigurationSection(type).getKeys(false)) {
        name = name.toLowerCase();
        String url = this._capeYAML.getString(type + "." + name);
        if (!url.isEmpty()) {
          if (type.equalsIgnoreCase("Players")) {
            this._SimpleSkins._playerCapes.put(name, url);
          }
          else if ((type.equalsIgnoreCase("SavedURLs")) || (type.equalsIgnoreCase("Groups"))) {
            this._SimpleSkins._savedCapes.put(name, url);
            if (type.equalsIgnoreCase("Groups")) {
              this._capeYAML.set("SavedURLs." + name, url);
            }
          }
          else if (type.equalsIgnoreCase("Citizens")) {
            try {
              int id = Integer.parseInt(name);
              this._SimpleSkins._citizenCapes.put(id, url);
            }
            catch (NumberFormatException nfe) {
              this._SimpleSkins._log.info("Invalid Citizen ID: " + name + " - Skipping.");
            }
          }
        }
        else {
          this._capeYAML.set(type + "." + name, null);
        }
      }
    }
    else
    {
      this._capeYAML.createSection(type);
    }

    if (type.equalsIgnoreCase("Groups"))
    {
      this._skinYAML.set("Groups", null);
      this._capeYAML.set("Groups", null);
    }
  }

  private void saveData()
  {
    for (String name : this._SimpleSkins._playerSkins.keySet()) {
      if (!this._SimpleSkins._playerSkins.get(name).isEmpty())
        this._skinYAML.set("Players." + name, this._SimpleSkins._playerSkins.get(name));
      else {
        this._skinYAML.set("Players." + name, null);
      }
    }
    for (String name : this._SimpleSkins._playerCapes.keySet()) {
      if (!(this._SimpleSkins._playerCapes.get(name)).isEmpty())
        this._capeYAML.set("Players." + name, this._SimpleSkins._playerCapes.get(name));
      else {
        this._capeYAML.set("Players." + name, null);
      }
    }
    for (String name : this._SimpleSkins._savedSkins.keySet()) {
      if (!(this._SimpleSkins._savedSkins.get(name)).isEmpty())
        this._skinYAML.set("SavedURLs." + name, this._SimpleSkins._savedSkins.get(name));
      else {
        this._skinYAML.set("SavedURLs." + name, null);
      }
    }
    for (String name : this._SimpleSkins._savedCapes.keySet()) {
      if (!(this._SimpleSkins._savedCapes.get(name)).isEmpty())
        this._capeYAML.set("SavedURLs." + name, this._SimpleSkins._savedCapes.get(name));
      else {
        this._capeYAML.set("SavedURLs." + name, null);
      }
    }
    for (Integer id : this._SimpleSkins._citizenSkins.keySet()) {
      if (!(this._SimpleSkins._citizenSkins.get(id)).isEmpty())
        this._skinYAML.set("Citizens." + String.valueOf(id), this._SimpleSkins._citizenSkins.get(id));
      else {
        this._skinYAML.set("Citizens." + String.valueOf(id), null);
      }
    }
    for (Integer id : this._SimpleSkins._citizenCapes.keySet()) {
      if (!(this._SimpleSkins._citizenCapes.get(id)).isEmpty())
        this._capeYAML.set("Citizens." + String.valueOf(id), this._SimpleSkins._citizenCapes.get(id));
      else
        this._capeYAML.set("Citizens." + String.valueOf(id), null);
    }
    this._SimpleSkins._canClear = true;
  }

  private void checkFilesExist()
    throws IOException
  {
    if (!this._SimpleSkins.getDataFolder().exists()) this._SimpleSkins.getDataFolder().mkdirs();
    if (!this._skinFile.exists()) this._skinFile.createNewFile();
    if (!this._capeFile.exists()) this._capeFile.createNewFile();
    if (!this._skinFile.exists()) this._urlFile.createNewFile();
  }

  public void importUtility(String plugname, CommandSender sender)
  {
    File dir = new File(SimpleSkins.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
    _pluginsFolder = dir.getParentFile().getPath();

    if (plugname.equalsIgnoreCase("all")) {
      importCitizenSkins(sender);
    }
    else if (plugname.equalsIgnoreCase("citizenskins")) {
      importCitizenSkins(sender);
    }
    else if (plugname.equalsIgnoreCase("iskin")) {
      importiskin(sender);
    }
    else if (plugname.equalsIgnoreCase("spoutplayers")) {
      importSpoutPlayers(sender);
    }
    else if (plugname.equalsIgnoreCase("spoutessentials")) {
      importSpoutEssentials(sender);
    }
    else {
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "The import utility for Simple Skins does not know a '" + plugname + "'. (Did you spell it right?)", false);
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Simple Skins can import citizenskins, iskin, spoutplayers, spoutessentials.", false);
    }
  }

  private void importCitizenSkins(CommandSender sender)
  {
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "Attempting to import from CitizenSkins.", true);

    File configFile = new File(_pluginsFolder + File.separator + "CitizenSkins" + File.separator + "config.yml");

    if (configFile.exists())
    {
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "CitizenSkins config file found. Loading...", false);
      this._skinYAML = YamlConfiguration.loadConfiguration(configFile);
      int skinCount = 0; int capeCount = 0;
      if (this._skinYAML.getConfigurationSection("skins") != null) {
        for (String sid : this._skinYAML.getConfigurationSection("skins").getKeys(false)) {
          String skinurl = this._skinYAML.getString("skins." + sid + "." + "skin");
          String capeurl = this._skinYAML.getString("skins." + sid + "." + "cape");
          int npcID = -1;
          try {
            npcID = Integer.parseInt(sid);
          }
          catch (NumberFormatException ex) {
            SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Invalid NPC ID '" + sid + "', skipping...", true);
          }
          if (npcID >= 0) {
            if ((skinurl != null) && (!skinurl.isEmpty())) {
              this._SimpleSkins._citizenSkins.put(npcID, skinurl);
              skinCount++;
            }
            if ((capeurl != null) && (!capeurl.isEmpty())) {
              this._SimpleSkins._citizenCapes.put(npcID, capeurl);
              capeCount++;
            }
            this._SimpleSkins.updateLiveCitizen(npcID);
          }
        }
      }
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "Loaded and saved " + skinCount + " skins from CitizenSkins!", true);
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "Loaded and saved " + capeCount + " capes from CitizenSkins!", true);
    }
    else {
      SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Unable to Load CitizenSkins config.yml file at: " + _pluginsFolder + File.separator + "CitizenSkins" + File.separator + "config.yml", true);
    }
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "Import of CitizenSkins to SimpleSkins complete.", true);
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.GREEN + "Your citizens thank you!", false);
    saveData();
  }

  private void importiskin(CommandSender sender)
  {
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Oops! Simple Skins does not yet support importing from iSkin but will soon. Check for the latest release!", false);
  }

  private void importSpoutPlayers(CommandSender sender)
  {
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Oops! Simple Skins does not yet support importing from SpoutPlayers but will soon. Check for the latest release!", false);
  }

  private void importSpoutEssentials(CommandSender sender)
  {
    SimpleSkinsExecutor.sendMessage(sender, ChatColor.RED + "Oops! Simple Skins does not yet support importing from SpoutEssentials but will soon. Check for the latest release!", false);
  }

public static String getGroups() {
	return _groups;
}

public String get_players() {
	return _players;
}

public String get_savedURLs() {
	return _savedURLs;
}

public String get_citizens() {
	return _citizens;
}
}