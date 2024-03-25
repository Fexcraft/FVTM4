package net.fexcraft.mod.fvtm.impl;

import io.netty.buffer.ByteBuf;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.data.block.BlockData;
import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.handler.DefaultPartInstallHandler;
import net.fexcraft.mod.fvtm.item.PartItem;
import net.fexcraft.mod.fvtm.packet.PacketBase;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.WorldW;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Packets20 extends Packets {

	public static final HashMap<Class<? extends PacketBase>, Class<? extends PacketBase>> PACKETS = new LinkedHashMap<>();
	public static final ResourceLocation TAG_PACKET = new ResourceLocation("fvtm", "tag");
	public static final ResourceLocation VEHMOVE_PACKET = new ResourceLocation("fvtm", "veh_move");
	public static final ResourceLocation VEHKEYPRESS_PACKET = new ResourceLocation("fvtm", "veh_key");
	public static final ResourceLocation VEHKEYSTATE_PACKET = new ResourceLocation("fvtm", "veh_keystate");
	public static final ResourceLocation SEATUPDATE_PACKET = new ResourceLocation("fvtm", "seat_upd");
	public static final ResourceLocation SPUPDATE_PACKET = new ResourceLocation("fvtm", "sp_upd");

	@Override
	public void init(){
		INSTANCE = this;
		LIS_SERVER.put("vehicle_packet", (com, player) -> {
			Level level = player.getWorld().local();
			Entity ent = level.getEntity(com.getInteger("entity"));
			if(ent == null) return;
			((RootVehicle)ent).vehicle.packet(com, player);
		});
		LIS_CLIENT.put("vehicle", (com, player) -> {
			Player entity = player.local();
			RootVehicle vehicle = (RootVehicle)entity.level().getEntity(com.getInteger("entity"));
			if(vehicle == null) return;
			vehicle.vehicle.packet(com, player);
		});
		LIS_SERVER.put("mount_seat", (com, player) -> {
			Player entity = player.local();
			RootVehicle vehicle = (RootVehicle)entity.level().getEntity(com.getInteger("entity"));
			int index = com.getInteger("seat");
			if(index < 0 || index > vehicle.vehicle.seats.size()) return;
			vehicle.processSeatInteract(index, player.local(), InteractionHand.MAIN_HAND);
		});
		LIS_SERVER.put("install_part", (com, player) -> {
			Player entity = player.local();
			ItemStack stack = entity.getMainHandItem();
			PartData data = ((PartItem)stack.getItem()).getData(TagCW.wrap(stack.getTag()));
			RootVehicle vehicle = (RootVehicle)entity.level().getEntity(com.getInteger("entity"));
			String category = com.getString("category");
			if(vehicle.vehicle.data.getPart(category) != null){
				PartData oldpart = vehicle.vehicle.data.getPart(category);
				boolean valid = oldpart.getType().getInstallHandlerData() instanceof DefaultPartInstallHandler.DPIHData && ((DefaultPartInstallHandler.DPIHData)oldpart.getType().getInstallHandlerData()).swappable;
				if(valid && vehicle.vehicle.data.deinstallPart(player, category, true)){
					entity.addItem(oldpart.getNewStack().local());
				}
				else return;
			}
			data = vehicle.vehicle.data.installPart(player, data, com.getString("source") + ":" + category, true);
			if(data == null){
				entity.getMainHandItem().shrink(1);
				vehicle.vehicle.sendVehicleData();
			}
		});
		if(EnvInfo.CLIENT){
			LIS_CLIENT.put("deco", (tag, player) -> {
				Level level = player.getWorld().local();
				Entity ent = level.getEntity(tag.getInteger("entid"));
				if(ent != null && ent instanceof Decoration){
					((Decoration)ent).readAdditionalSaveData(tag.local());
				}
			});
			LIS_CLIENT.put("passenger_update", (tag, player) -> {
				Level level = player.getWorld().local();
				Entity ent = level.getEntity(tag.getInteger("entity"));
				if(ent == null) return;
				((Passenger)PassengerUtil.get(ent)).set(tag.getInteger("vehicle"), tag.getInteger("seat"));
			});
			LIS_CLIENT.put("vehicle_color", (tag, player) -> {
				Level level = player.getWorld().local();
				Entity ent = level.getEntity(tag.getInteger("vehicle"));
				if(ent == null) return;
				((RootVehicle)ent).vehicle.data.getColorChannel(tag.getString("channel")).packed = tag.getInteger("color");
			});
			LIS_CLIENT.put("vehicle_packet", (tag, player) -> {
				Level level = player.getWorld().local();
				Entity ent = level.getEntity(tag.getInteger("entity"));
				if(ent == null) return;
				((RootVehicle)ent).vehicle.packet(tag, player);
			});
			LIS_CLIENT.put("vehicle", (tag, player) -> {
				Player entity = player.local();
				RootVehicle vehicle = (RootVehicle)entity.level().getEntity(tag.getInteger("entity"));
				if(vehicle == null) return;
				vehicle.vehicle.packet(tag, player);
			});
		}
	}

	@Override
	public void writeTag(ByteBuf buffer, TagCW tag){
		((FriendlyByteBuf)buffer).writeNbt(tag.local());
	}

	@Override
	public TagCW readTag(ByteBuf buffer){
		return TagCW.wrap(((FriendlyByteBuf)buffer).readNbt());
	}

	@Override
	public void send(BlockData blockdata, V3I pos, int dim){

	}

	@Override
	public void send(WorldW world, V3I pos){

	}

}