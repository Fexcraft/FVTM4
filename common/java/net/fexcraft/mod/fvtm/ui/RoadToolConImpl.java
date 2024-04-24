package net.fexcraft.mod.fvtm.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.impl.SWIE;
import net.fexcraft.mod.fvtm.ui.road.RoadToolCon;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.world.item.ItemStack;

/**
 * @author Ferdinand Calo' (FEX___96
 */
public class RoadToolConImpl extends RoadToolCon {

	private RoadInventory inv = new RoadInventory();
	private SWIE wrapper = new SWIE(ItemStack.EMPTY);

	public RoadToolConImpl(JsonMap map, EntityW player, V3I pos){
		super(map, player, pos);
		initInv();
	}

	@Override
	public Object getInventory(){
		return inv;
	}

	@Override
	public void setInventoryContent(int index, TagCW com){
		inv.setItem(index, ItemStack.of(com.local()));
	}

	@Override
	public StackWrapper getInventoryContent(int index){
		wrapper.stack = inv.getItem(index);
		return wrapper;
	}

	@Override
	public boolean isInventoryEmpty(int at){
		return inv.getItem(at).isEmpty();
	}

}
