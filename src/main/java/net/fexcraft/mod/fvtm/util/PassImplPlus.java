package net.fexcraft.mod.fvtm.util;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.Passenger;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.fexcraft.mod.fvtm.ui.UIKey;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.uimpl.UniCon;
import net.fexcraft.mod.uni.world.WorldW;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PassImplPlus extends Passenger implements net.fexcraft.mod.fvtm.sys.uni.Passenger {

	private boolean notified;
	private int vehicle;
	private int seat;

	public PassImplPlus(Entity iah){
		super(iah);
	}

	@Override
	public SeatInstance getSeatOn(){
		if(entity.getVehicle() instanceof RootVehicle == false) return null;
		return ((RootVehicle)entity.getVehicle()).getSeatOf(entity);
	}

	@Override
	public void set(int veh, int seatid){
		if(entity.level().isClientSide && entity.isPassenger() && seatid > -1){
			RootVehicle root = (RootVehicle)entity.getVehicle();
			for(SeatInstance seat : root.vehicle.seats){
				if(seat.passenger_direct() == entity) seat.passenger(null);
			}
		}
		vehicle = veh;
		seat = seatid;
		if(!entity.level().isClientSide){
			update_packet();
			if(entity instanceof Player && !notified){
				try{
					//TODO send controls info/link in chat // "https://fexcraft.net/wiki/mod/fvtm/controls"
					notified = true;
				}
				catch(Exception e){
					//
				}
			}
		}
	}

	private void update_packet(){
		TagCW packet = TagCW.create();
		packet.set("entity", entity.getId());
		packet.set("vehicle", vehicle);
		packet.set("seat", seat);
		Packets.sendToAll(Packet_TagListener.class, "passenger_update", packet);
	}

	@Override
	public int vehicle(){
		return vehicle;
	}

	@Override
	public int seat(){
		return seat;
	}

	@Override
	public V3D getEyeVec(){
		//Entity ent = net.minecraft.client.Minecraft.getInstance().crosshairPickEntity;
		//return ent == null ? V3D.NULL : new V3D(ent.getEyePosition().x, ent.getEyePosition().y, ent.getEyePosition().z);
		return new V3D(entity.getEyePosition().x, entity.getEyePosition().y, entity.getEyePosition().z);
	}

	@Override
	public V3D getLookVec(){
		//Entity ent = net.minecraft.client.Minecraft.getInstance().crosshairPickEntity;
		//return ent == null ? V3D.NULL : new V3D(ent.getLookAngle().x, ent.getLookAngle().y, ent.getLookAngle().z);
		return new V3D(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z);
	}

	@Override
	public void decreaseXZMotion(double x){
		//
	}

	@Override
	public void setYawPitch(float oyaw, float opitch, float yaw, float pitch){
		//
	}

	@Override
	public void openUI(UIKey key, V3I pos){
		openUI(key.key, pos);
	}

}
