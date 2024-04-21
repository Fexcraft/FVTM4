package net.fexcraft.mod.fvtm.ui;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author Ferdinand Calo' (FEX___96
 */
public class RoadInventory implements Container {
	
	private NonNullList<ItemStack> stacks;
	
	public RoadInventory(){
		stacks = NonNullList.withSize(6, ItemStack.EMPTY);
	}
	
	public RoadInventory(int size){
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}
	
	@Override
	public int getContainerSize(){
		return stacks.size();
	}

	@Override
	public boolean isEmpty(){
		for(ItemStack stack : stacks){
			if(!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int idx){
		return stacks.get(idx);
	}

	@Override
	public ItemStack removeItem(int idx, int by){
		return ContainerHelper.removeItem(stacks, idx, by);
	}

	@Override
	public ItemStack removeItemNoUpdate(int idx){
		ItemStack stack = stacks.get(idx);
		stacks.set(idx, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int idx, ItemStack stack){
		stacks.set(idx, stack);
	}

	@Override
	public int getMaxStackSize(){
		return 1;
	}

	@Override
	public void setChanged(){
		//
	}

	@Override
	public boolean stillValid(Player player){
		return player.isAlive();
	}

	@Override
	public void clearContent(){
		stacks.clear();
	}

}
