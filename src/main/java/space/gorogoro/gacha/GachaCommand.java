package space.gorogoro.gacha;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * FrameGuardCommand
 * @license    LGPLv3
 * @copyright  Copyright gorogoro.space 2018
 * @author     kubotan
 * @see        <a href="http://blog.gorogoro.space">Kubotan's blog.</a>
 */
public class GachaCommand {
  private Gacha gacha;
  private CommandSender sender;
  private String[] args;
  protected static final String META_CHEST = "gacha.chest";
  protected static final String FORMAT_TICKET_CODE = "GACHA CODE:%s";

  /**
   * Constructor of GachaCommand.
   * @param Gacha gacha
   */
  public GachaCommand(Gacha gacha) {
    try{
      this.gacha = gacha;
    } catch (Exception e){
      GachaUtility.logStackTrace(e);
    }
  }

  /**
   * Initialize
   * @param CommandSender CommandSender
   * @param String[] Argument
   */
  public void initialize(CommandSender sender, String[] args){
    try{
      this.sender = sender;
      this.args = args;
    } catch (Exception e){
      GachaUtility.logStackTrace(e);
    }
  }

  /**
   * Finalize
   */
  public void finalize() {
    try{
      this.sender = null;
      this.args = null;
    } catch (Exception e){
      GachaUtility.logStackTrace(e);
    }
  }

  /**
   * Processing of command list.
   * @return boolean true:Success false:Failure
   */
  public boolean list() {
    List<String> glist = gacha.getDatabase().list();
    if(glist.size() <= 0) {
      GachaUtility.sendMessage(sender, "Record not found.");
      return true;
    }
    
    for(String msg: glist) {
      GachaUtility.sendMessage(sender, msg);
    }
    return true;
  }

  /**
   * Processing of command modify.
   * @return boolean true:Success false:Failure
   */
  public boolean modify() {
    if(args.length != 2) {
      return false;
    }
    
    if(!(sender instanceof Player)) {
      return false;
    }
    
    String gachaName = args[1];
    if(gacha.getDatabase().getGacha(gachaName) == null) {
      GachaUtility.sendMessage(sender, "Record not found. gacha_name=" + gachaName);
      return true;
    }
    GachaUtility.setPunch((Player)sender, gacha, gachaName);
    GachaUtility.sendMessage(sender, "Please punching(right click) a chest of gachagacha. gacha_name=" + gachaName);
    return true;
  }
  
  /**
   * Processing of command delete.
   * @return boolean true:Success false:Failure
   */
  public boolean delete() {
    if(args.length != 2) {
      return false;
    }
    
    String gachaName = args[1];
    if(gacha.getDatabase().deleteGacha(gachaName)) {
      GachaUtility.sendMessage(sender, "Deleted. gacha_name=" + gachaName);
      return true;
    }
    return false;
  }

  /**
   * Processing of command ticket.
   * @return boolean true:Success false:Failure
   */
  public boolean ticket(Player p) {
    if(args.length != 2) {
      return false;
    }

    int emptySlot = p.getInventory().firstEmpty();
    if (emptySlot == -1) {
      // not empty
      return false;
    }

    String ticketCode = gacha.getDatabase().getTicket();
    if(ticketCode == null) {
      GachaUtility.sendMessage(sender, "Failure generate ticket code.");
      return false;
    }

    ItemStack ticket = new ItemStack(Material.PAPER, 1);
    ItemMeta im = ticket.getItemMeta();
    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("ticket-display-name")));
    ArrayList<String> lore = new ArrayList<String>();
    lore.add(ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("ticket-lore1")));
    lore.add(ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("ticket-lore2")));
    lore.add(String.format(FORMAT_TICKET_CODE, ticketCode));
    im.setLore(lore);
    ticket.setItemMeta(im);
    p.getInventory().setItem(emptySlot, ticket);
    
    GachaUtility.sendMessage(sender, "Issue a ticket. player_name=" + p.getDisplayName());
    return true;
  }

  /**
   * Processing of command reload.
   * @return boolean true:Success false:Failure
   */
  public boolean reload() {
    gacha.reloadConfig();
    GachaUtility.sendMessage(sender, "reloaded.");
    return true;
  }

  /**
   * Processing of command enable.
   * @return boolean true:Success false:Failure
   */
  public boolean enable() {
    gacha.onEnable();
    GachaUtility.sendMessage(sender, "enabled.");
    return true;
  }

  /**
   * Processing of command fgdisable.
   * @return boolean true:Success false:Failure
   */
  public boolean disable() {
    gacha.onDisable();
    GachaUtility.sendMessage(sender, "disabled.");
    return true;
  }
}
