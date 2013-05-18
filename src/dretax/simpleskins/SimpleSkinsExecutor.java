package dretax.simpleskins;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleSkinsExecutor
  implements CommandExecutor
{
  private SimpleSkins _SimpleSkins;
  public final String _permPlayerAny = "simpleskins.player%t.any";
  public final String _permPlayerSelf = "simpleskins.player%t.self";
  public final String _permSaveAny = "simpleskins.save%t.any";
  public final String _permSaveSelf = "simpleskins.save%t.self";
  public final String _permSaveWild = "simpleskins.save%t.";
  public final String _permCitizen = "simpleskins.npc%t";
  public final String _permImport = "simpleskins.import";

  public SimpleSkinsExecutor(SimpleSkins instance) {
    this._SimpleSkins = instance;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    this._SimpleSkins._canClear = false;
    String change = "player";
    String type = cmd.getName().contains("skin") ? "skin" : "cape";
    String name = "";
    String url = "";

    if (cmd.getName().contains("group")) {
      sendMessage(sender, ChatColor.RED + "Group commands have been depreciated in favor of /saveskin and /savecape. Please use those instead.", false);
      sendMessage(sender, ChatColor.RED + "However, group commands will work the same as /saveskin and /savecape... for now. :)", false);
    }

    if (cmd.getName().equalsIgnoreCase("ssimport")) {
      if (sender.hasPermission("simpleskins.import")) {
        if (args.length == 1) {
          SimpleSkinsFileManager manager = null;
          try {
            manager = new SimpleSkinsFileManager(this._SimpleSkins, true);
            manager.importUtility(args[0], sender);
            return true;
          }
          catch (IOException e) {
            e.printStackTrace();
            sendMessage(sender, ChatColor.RED + "Failed to Load or Save Data for Import Utility!", true);
            return false;
          }
        }

        sendMessage(sender, ChatColor.RED + "This command takes only one argument. You entered " + args.length + ".", false);
        return false;
      }

      sendMessage(sender, ChatColor.RED + "You do not have permission to use the Simple Skins import command.", false);
      return false;
    }

    if ((args.length == 0) && (!(sender instanceof Player))) {
      sendMessage(sender, ChatColor.RED + "Console, You must enter at least a playername.", false);
      return false;
    }

    if ((args.length == 0) && ((sender instanceof Player))) {
      name = sender.getName();
    }
    else if ((args.length == 1) || (args.length == 2)) {
      name = args[0];
      url = args.length == 2 ? args[1] : "";
    }
    else
    {
      sendMessage(sender, ChatColor.RED + "Requres 0 to 2 arguments. You entered " + Integer.toString(args.length) + ".", false);
      return false;
    }

    if ((this._SimpleSkins.isValidURL(name)) && (args.length == 1) && ((sender instanceof Player))) {
      url = name;
      name = sender.getName();
    }
    else if ((this._SimpleSkins.isValidURL(name)) && (args.length == 1) && (!(sender instanceof Player))) {
      sendMessage(sender, ChatColor.RED + "Console, You must enter a playername to assign the URL to.", false);
      return false;
    }

    if ((cmd.getName().contains("save")) || (cmd.getName().contains("group"))) {
      change = "save";
    }
    else if (cmd.getName().contains("npc")) {
      change = "npc";
    }

    if ((change.equals("npc")) && (this._SimpleSkins._citizensVersion <= 0)) {
      this._SimpleSkins.loadCitizensSupport();
    }

    if ((change.equals("npc")) && (this._SimpleSkins._citizensVersion <= 0)) {
      sendMessage(sender, ChatColor.RED + "This server is not running Citizens. To use this command, please install Citizens 1.1, 1.2, or 2.0.", false);
      return false;
    }

    int npcID = 0;
    if (change.equals("npc")) try {
        npcID = Integer.parseInt(name);
      } catch (NumberFormatException nfe) {
        sendMessage(sender, ChatColor.RED + "You must enter an NPC ID number to assign a skin or cape to a Citizen.", false);
        return false;
      }


    name = name.toLowerCase();

    if (name.equals("help")) {
      helpMessage(sender);
      return true;
    }

    if (((sender instanceof Player)) || ((sender instanceof SpoutPlayer)))
    {
      if (change.equals("save")) {
        if (sender.hasPermission("simpleskins.save%t.any".replace("%t", type))) {
          commandChangeSave(sender, type, name, url);
          return true;
        }
        if ((type.equals("skin")) && 
          ((sender.hasPermission("simpleskins.save%t.self".replace("%t", type))) || (sender.hasPermission("simpleskins.save%t.".replace("%t", type) + name))) && 
          (sender.hasPermission(this._SimpleSkins._savedSkinPerm + name))) {
          commandChangeSave(sender, type, name, url);
          return true;
        }
        if ((type.equals("cape")) && 
          ((sender.hasPermission("simpleskins.save%t.self".replace("%t", type))) || (sender.hasPermission("simpleskins.save%t.".replace("%t", type) + name))) && 
          (sender.hasPermission(this._SimpleSkins._savedCapePerm + name))) {
          commandChangeSave(sender, type, name, url);
          return true;
        }

        sendMessage(sender, ChatColor.RED + "You do not have permission to change this saved " + type + " URL.", false);
        return false;
      }

      if (change.equals("npc")) {
        if (sender.hasPermission("simpleskins.npc%t".replace("%t", type))) {
          commandChangeCitizen(sender, type, npcID, url);
          return true;
        }

        sendMessage(sender, ChatColor.RED + "You do not have permission to change NPCs' " + type + ".", false);
        return false;
      }

      if (sender.hasPermission("simpleskins.player%t.any".replace("%t", type))) {
        commandChangePlayer(sender, type, name, url);
        return true;
      }
      if ((sender.hasPermission("simpleskins.player%t.self".replace("%t", type))) && (name.equalsIgnoreCase(sender.getName()))) {
        commandChangePlayer(sender, type, name, url);
        return true;
      }

      sendMessage(sender, ChatColor.RED + "You do not have permission to change this player's " + type + ".", false);
      return false;
    }

    if (change.equals("save")) {
      commandChangeSave(sender, type, name, url);

      return true;
    }
    if (change.equals("npc")) {
      commandChangeCitizen(sender, type, npcID, url);
      return true;
    }

    commandChangePlayer(sender, type, name, url);
    return true;
  }

  private void commandChangeCitizen(CommandSender sender, String type, int id, String url)
  {
    if (type.equals("skin"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._citizenSkins.put(id, "");
        sendMessage(sender, "NPC " + Integer.toString(id) + " has their default skin back.", true);
      }
      else
      {
        this._SimpleSkins._citizenSkins.put(id, url);
        sendMessage(sender, "NPC " + Integer.toString(id) + "'s skin has been set and saved.", true);
      }
    }
    else if (type.equals("cape"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._citizenCapes.put(id, "");
        sendMessage(sender, "NPC " + Integer.toString(id) + " has their default cape back.", true);
      }
      else
      {
        this._SimpleSkins._citizenCapes.put(id, url);
        sendMessage(sender, "NPC " + Integer.toString(id) + "'s cape has been set and saved.", true);
      }
    }

    this._SimpleSkins.updateLiveCitizen(id);

    this._SimpleSkins.saveDataToFiles();
  }

  private void commandChangePlayer(CommandSender sender, String type, String name, String url)
  {
    if (type.equals("skin"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._playerSkins.put(name, "");
        sendMessage(sender, "Player " + name + " has their default skin back.", true);
      }
      else
      {
        this._SimpleSkins._playerSkins.put(name, url);
        sendMessage(sender, "Player " + name + "'s skin has been set and saved.", true);
      }
    }
    else if (type.equals("cape"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._playerCapes.put(name, "");
        sendMessage(sender, "Player " + name + " has their default cape back.", true);
      }
      else
      {
        this._SimpleSkins._playerCapes.put(name, url);
        sendMessage(sender, "Player " + name + "'s cape has been set and saved.", true);
      }
    }

    SpoutPlayer sp = getSpoutPlayer(name);
    if (sp != null) {
      this._SimpleSkins.updateLivePlayer(sp);
    }

    this._SimpleSkins.saveDataToFiles();
  }

  private void commandChangeSave(CommandSender sender, String type, String name, String url)
  {
    if (type.equals("skin"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._savedSkins.put(name, "");
        sendMessage(sender, "The saved Skin URL '" + name + "' has been removed.", true);
      }
      else
      {
        this._SimpleSkins._savedSkins.put(name, url);
        sendMessage(sender, "The Skin URL '" + name + "' has been saved and any players with the permission " + this._SimpleSkins._savedSkinPerm + name + " have updated.", true);
      }

    }
    else if (type.equals("cape"))
    {
      if ((url == null) || (url.isEmpty())) {
        this._SimpleSkins._savedCapes.put(name, "");
        sendMessage(sender, "The saved Cape URL '" + name + "' has been removed.", true);
      }
      else
      {
        this._SimpleSkins._savedCapes.put(name, url);
        sendMessage(sender, "The Cape URL '" + name + "' has been saved and any players with the permission " + this._SimpleSkins._savedCapePerm + name + " have updated.", true);
      }
    }

    this._SimpleSkins.updateAllLivePlayersWithPermission(name);

    this._SimpleSkins.saveDataToFiles();
  }

  private void helpMessage(CommandSender cs)
  {
    cs.sendMessage(ChatColor.GOLD + "------Simple Skins Help-----");
    cs.sendMessage(ChatColor.GREEN + "/playerskin <playername> <URL>");
    cs.sendMessage(ChatColor.GREEN + "/playercape <playername> <URL>");
    cs.sendMessage(ChatColor.GREEN + "/saveskin <uniquename> <URL>");
    cs.sendMessage(ChatColor.GREEN + "/savecape <uniquename> <URL>");
    cs.sendMessage(ChatColor.GREEN + "/npcskin <npc ID> <URL>");
    cs.sendMessage(ChatColor.GREEN + "/npccape <npc ID> <URL>");
    cs.sendMessage(ChatColor.GREEN + "- Leave the URL blank to reset the cape or skin.");
    cs.sendMessage(ChatColor.GREEN + "- You can enter a minecraft name or a saved name as the URL too.");
    cs.sendMessage(ChatColor.GREEN + "- Players or groups with " + this._SimpleSkins._savedSkinPerm + "<uniquename> or " + this._SimpleSkins._savedCapePerm + "<uniquename>");
    cs.sendMessage(ChatColor.GREEN + "will have the saved skin or cape of that unique name.");
  }

  public static void sendMessage(CommandSender sender, String message, boolean informConsole)
  {
    if ((sender instanceof Player)) {
      sender.sendMessage(ChatColor.GREEN + message);
      if (informConsole) {
        SimpleSkins.sendConsoleMessage(ChatColor.GREEN + message);
        SimpleSkins.sendConsoleMessage("Set by " + sender.getName() + ".");
      }
    }
    else
    {
      sender.sendMessage(SimpleSkins._prefix + ChatColor.GREEN + message);
    }
  }

  public SpoutPlayer getSpoutPlayer(String name)
  {
    Player p = Bukkit.getPlayer(name);
    if (p != null) {
      SpoutPlayer sp = SpoutManager.getPlayer(p);
      if (sp != null) {
        return sp;
      }
    }
    return null;
  }
}