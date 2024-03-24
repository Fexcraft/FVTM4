package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.uni.world.CubeSide;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WorldW;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WrapperHolderImpl extends WrapperHolder {

	private WorldW client;

	@Override
	public EntityW getEntity0(Object o){
		return PassengerUtil.get((Entity)o);
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
	public <W extends WorldW> W getClientWorld0(){
		if(client == null){
			client = getWorld0(net.minecraft.client.Minecraft.getInstance().level);
		}
		return (W)client;
	}

	@Override
	public V3I getPos0(Object o){
		BlockPos pos = (BlockPos)o;
		return new V3I(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public CubeSide getSide0(Object o){
		CubeSide facing = (CubeSide)o;
		switch(facing){
			case UP: return CubeSide.UP;
			case DOWN: return CubeSide.DOWN;
			case NORTH: return CubeSide.NORTH;
			case WEST: return CubeSide.WEST;
			case EAST: return CubeSide.EAST;
			case SOUTH: return CubeSide.SOUTH;
		}
		return CubeSide.NORTH;
	}

	@Override
	public <S> S getLocalSide0(CubeSide side){
		Direction dir = Direction.NORTH;
		switch(side){
			case UP: return (S)Direction.UP;
			case DOWN: return (S)Direction.DOWN;
			case NORTH: return (S)Direction.NORTH;
			case WEST: return (S)Direction.WEST;
			case EAST: return (S)Direction.EAST;
			case SOUTH: return (S)Direction.SOUTH;
		}
		return (S)dir;
	}

	@Override
	public void reset(){
		client = null;
	}

}
