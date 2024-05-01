package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.ClientPacketPlayer;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.FvtmGetters;
import net.fexcraft.mod.fvtm.block.Asphalt;
import net.fexcraft.mod.fvtm.data.vehicle.SwivelPoint;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.packet.Packet_VehMove;
import net.fexcraft.mod.fvtm.sys.uni.FvtmWorld;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.fexcraft.mod.fvtm.sys.uni.VehicleInstance;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.StateWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

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
		level.setBlock(new BlockPos(pos.x, pos.y, pos.z), state.local(), flag);
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
	public StateWrapper getStateAt(V3I pos){
		return StateWrapper.of(level.getBlockState(new BlockPos(pos.x, pos.y, pos.z)));
	}

	@Override
	public SeatInstance getSeat(int entid, int seatid){
		Entity ent = level.getEntity(entid);
		if(ent == null || ent instanceof RootVehicle == false) return null;
		return ((RootVehicle)ent).vehicle.seats.get(seatid);
	}

	@Override
	public SwivelPoint getSwivelPoint(int entid, String pointid){
		Entity ent = level.getEntity(entid);
		if(ent == null || ent instanceof RootVehicle == false) return null;
		return ((RootVehicle)ent).vehicle.data.getRotationPoint(pointid);
	}

	@Override
	public Passenger getPassenger(int source){
		return PassengerUtil.get(level.getEntity(source));
	}

	@Override
	public void onVehicleMove(Packet_VehMove packet){
		Entity ent = level.getEntity(packet.entid);
		if(ent instanceof RootVehicle == false) return;
		((RootVehicle)ent).setPosRotMot(packet.pos, packet.yaw, packet.pitch, packet.roll, packet.throttle, packet.steering, packet.fuel);
	}

	@Override
	public VehicleInstance getVehicle(int entid){
		Entity ent = level.getEntity(entid);
		if(ent instanceof RootVehicle == false) return null;
		return ((RootVehicle)ent).vehicle;
	}

	@Override
	public boolean noViewEntity(){
		return ClientPacketPlayer.get() == null;
	}

	@Override
	public ArrayList<VehicleInstance> getVehicles(V3D pos){
		ArrayList<VehicleInstance> list = new ArrayList<>();
		AABB aabb = new AABB(pos.x - 16, pos.y - 16, pos.z - 16, pos.x + 16, pos.y + 16, pos.z + 16);
		float cr;
		for(Entity entity : level.getEntities(null, aabb)){
			if(entity instanceof RootVehicle == false) continue;
				cr = ((RootVehicle)entity).vehicle.data.getAttribute("collision_range").asFloat() + 1;
				if(cr < ((RootVehicle)entity).vehicle.entity.getPos().dis(pos)) continue;
				list.add(((RootVehicle)entity).vehicle);
		}
		return list;
	}

	@Override
	public Passenger getClientPassenger(){
		return PassengerUtil.get(ClientPacketPlayer.get());
	}

	@Override
	public boolean isFvtmRoad(StateWrapper state){
		return state.getBlock() instanceof Asphalt;
	}

	@Override
	public int getRoadHeight(StateWrapper state){
		if(state.getBlock() instanceof Asphalt){
			return ((Asphalt)state.getBlock()).height;
		}
		return 0;
	}

	@Override
	public StateWrapper getRoadWithHeight(StateWrapper block, int height){
		if(block.getBlock() instanceof Asphalt == false){
			ResourceLocation rl = BuiltInRegistries.BLOCK.getKey((Block)block.getBlock());
			String str = rl.toString();
			str = str.substring(0, str.lastIndexOf("_") + 1);
			return StateWrapper.of(BuiltInRegistries.BLOCK.get(new ResourceLocation(str + height)).defaultBlockState());
		}
		return StateWrapper.of(FvtmGetters.ASPHALT[height].get().defaultBlockState());
	}

	@Override
	public void handleBlockEntityPacket(TagCW com, Passenger player){

	}

}
