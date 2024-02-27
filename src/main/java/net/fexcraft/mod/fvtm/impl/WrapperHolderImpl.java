package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.uni.world.BlockSide;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WorldW;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WrapperHolderImpl extends WrapperHolder {

	@Override
	public EntityW getEntity0(Object o){
		return ((Entity)o).getData(UniversalAttachments.PASSENGER).local();
	}

	@Override
	public WorldW getWorld0(Object o){
		if(o == null) return null;
		if(!WORLDS.containsKey(o)){
			WORLDS.put(o, new WorldWI((Level)o));
		}
		return WORLDS.get(o);
	}

	@Override
	public V3I getPos0(Object o){
		BlockPos pos = (BlockPos)o;
		return new V3I(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public BlockSide getSide0(Object o){
		BlockSide facing = (BlockSide)o;
		switch(facing){
			case UP: return BlockSide.UP;
			case DOWN: return BlockSide.DOWN;
			case NORTH: return BlockSide.NORTH;
			case WEST: return BlockSide.WEST;
			case EAST: return BlockSide.EAST;
			case SOUTH: return BlockSide.SOUTH;
		}
		return BlockSide.NORTH;
	}

}
