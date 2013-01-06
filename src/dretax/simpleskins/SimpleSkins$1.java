package dretax.simpleskins;

import java.util.HashSet;
import java.util.Set;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

class SimpleSkins$1
  implements Runnable
{
SimpleSkins this$0 = new SimpleSkins();
  SimpleSkins$1(SimpleSkins paramSimpleSkins)
  {
	  this$0 = paramSimpleSkins;
  }

  public void run()
  {
    for (SpoutPlayer sp : SpoutManager.getOnlinePlayers()) {
      String name = sp.getName().toLowerCase();

      this.this$0.updateLivePermissionPlayer(sp);
      this.this$0.updateLivePlayer(sp);

      if ((this.this$0._playerSkins.containsKey(name)) && (((String)this.this$0._playerSkins.get(name)).isEmpty()) && (this.this$0._canClear)) {
        this.this$0._playerSkins.remove(name);
      }
      if ((this.this$0._playerCapes.containsKey(name)) && (((String)this.this$0._playerCapes.get(name)).isEmpty()) && (this.this$0._canClear)) {
        this.this$0._playerCapes.remove(name);
      }
    }

    if (this.this$0._citizensVersion > 0) {
      Set<Integer> ids = new HashSet<Integer>(this.this$0._citizenSkins.keySet());
      ids.addAll(this.this$0._citizenCapes.keySet());
      for (Integer id : ids) {
        this.this$0.updateLiveCitizen(id.intValue());
      }
    }
    this.this$0._canClear = false;
  }
}