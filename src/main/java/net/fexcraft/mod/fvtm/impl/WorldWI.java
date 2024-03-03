package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.data.vehicle.SwivelPoint;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.packet.Packet_VehMove;
import net.fexcraft.mod.fvtm.sys.uni.FvtmWorld;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.StateWrapper;
import net.fexcraft.mod.uni.world.WorldW;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WorldWI extends FvtmWorld {

	private Level level;

	public WorldWI(Level world){
		super();
		level = world;
	}

	@Override
	public boolean isClient(){
		return level.isClientSide;
	}

	@Override
	public boolean isTilePresent(V3I pos){
		return level.getBlockEntity(new BlockPos(pos.x, pos.y, pos.z)) != null;
	}

	@Override
	public Object getBlockEntity(V3I pos){
		return level.getBlockEntity(new BlockPos(pos.x, pos.y, pos.z));
	}

	@Override
	public <W> W local(){
		return (W)level;
	}

	@Override
	public Object direct(){
		return level;
	}

	@Override
	public void setBlockState(V3I pos, StateWrapper state, int flag){
		//
	}

	@Override
	public void spawnBlockSeat(V3D add, EntityW player){
		//
	}

	@Override
	public int dim(){
		return 0;
	}

	@Override
	public void drop(StackWrapper stack, V3D vec){
		level.addFreshEntity(new ItemEntity(level, vec.x, vec.y, vec.z, stack.local()));
	}

	@Override
	public SeatInstance getSeat(int entid, int seatid){
		return null;
	}

	@Override
	public SwivelPoint getSwivelPoint(int entid, String pointid){
		return null;
	}

	@Override
	public Passenger getPassenger(int source){
		return null;
	}

	@Override
	public void onVehicleMove(Packet_VehMove packet){
		Entity ent = level.getEntity(packet.entid);
		if(ent == null || ent instanceof RootVehicle == false) return;
		((RootVehicle)ent).setPosRotMot(packet.pos, packet.yaw, packet.pitch, packet.roll, packet.throttle, packet.steering, packet.fuel);
	}

}
