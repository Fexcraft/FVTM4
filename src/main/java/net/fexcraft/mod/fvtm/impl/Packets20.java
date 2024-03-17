package net.fexcraft.mod.fvtm.impl;

import io.netty.buffer.ByteBuf;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.Config;
import net.fexcraft.mod.fvtm.data.block.BlockData;
import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.handler.DefaultPartInstallHandler;
import net.fexcraft.mod.fvtm.item.PartItem;
import net.fexcraft.mod.fvtm.packet.*;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.VehicleInstance;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.WorldW;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static net.fexcraft.mod.fcl.UniversalAttachments.PASSENGER;

public class Packets20 extends Packets {

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
		PACKETS.put(Packet_TagListener.class, PI_TagListener.class);
		PACKETS.put(Packet_VehMove.class, PI_VehMove.class);
		PACKETS.put(Packet_VehKeyPress.class, PI_VehKeyPress.class);
		PACKETS.put(Packet_VehKeyPressState.class, PI_VehKeyPressState.class);
		PACKETS.put(Packet_SeatUpdate.class, PI_SeatUpdate.class);
		PACKETS.put(Packet_SPUpdate.class, PI_SPUpdate.class);
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
				((Passenger)ent.getData(PASSENGER)).set(tag.getInteger("vehicle"), tag.getInteger("seat"));
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

	@Override
	public void send(VehicleInstance vehicle, TagCW com){
		com.set("entity", vehicle.entity.getId());
		sendInRange(Packet_TagListener.class, vehicle.entity.getWorld(), vehicle.entity.getPos(), Config.VEHICLE_UPDATE_RANGE, "vehicle_packet", com);
	}

	@Override
	public void send0(Class<? extends PacketBase> packet, Object... data){
		try{
			PacketDistributor.SERVER.noArg().send((CustomPacketPayload)PACKETS.get(packet).newInstance().fill(data));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void sendInRange0(Class<? extends PacketBase> packet, WorldW world, V3D pos, int range, Object... data){
		try{
			Vec3 vec = new Vec3(pos.x, pos.y, pos.z);
			for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
				if(player.position().distanceTo(vec) > range) continue;
				PacketDistributor.PLAYER.with(player).send((CustomPacketPayload)PACKETS.get(packet).newInstance().fill(data));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void sendToAll0(Class<? extends PacketBase> packet, Object... data){
		try{
			for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
				PacketDistributor.PLAYER.with(player).send((CustomPacketPayload)PACKETS.get(packet).newInstance().fill(data));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void sendTo0(Class<? extends PacketBase> packet, Passenger to, Object... data){
		try{
			PacketDistributor.PLAYER.with(to.local()).send((CustomPacketPayload)PACKETS.get(packet).newInstance().fill(data));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class PacketRegistry {

		@SubscribeEvent
		public static void register(final RegisterPayloadHandlerEvent event){
			final IPayloadRegistrar registrar = event.registrar("fvtm").versioned("1.0.0").optional();
			registrar.common(TAG_PACKET, PI_TagListener::read, handler -> {
				handler.server((packet, context) -> handlePacketServer((PI_TagListener)packet, context, HTL));
				handler.client((packet, context) -> handlePacketClient((PI_TagListener)packet, context, HTL));
			});
			registrar.common(VEHMOVE_PACKET, PI_VehMove::read, handler -> {
				handler.server((packet, context) -> {
					if(context.player().isPresent()) handlePacketServer((PI_VehMove)packet, context, HVM);
				});
				handler.client((packet, context) -> {
					if(context.player().isPresent()) handlePacketClient((PI_VehMove)packet, context, HVM);
				});
			});
			registrar.common(VEHKEYPRESS_PACKET, PI_VehKeyPress::read, handler -> {
				handler.server((packet, context) -> handlePacketServer((PI_VehKeyPress)packet, context, HVK));
			});
			registrar.common(VEHKEYSTATE_PACKET, PI_VehKeyPressState::read, handler -> {
				handler.server((packet, context) -> handlePacketServer((PI_VehKeyPressState)packet, context, HVKS));
				handler.client((packet, context) -> handlePacketClient((PI_VehKeyPressState)packet, context, HVKS));
			});
			registrar.common(SEATUPDATE_PACKET, PI_SeatUpdate::read, handler -> {
				handler.server((packet, context) -> handlePacketServer((PI_SeatUpdate)packet, context, HSU));
				handler.client((packet, context) -> handlePacketClient((PI_SeatUpdate)packet, context, HSU));
			});
			registrar.common(SPUPDATE_PACKET, PI_SPUpdate::read, handler -> {
				handler.server((packet, context) -> handlePacketServer((PI_SPUpdate)packet, context, HSPU));
				handler.client((packet, context) -> handlePacketClient((PI_SPUpdate)packet, context, HSPU));
			});
		}

		private static <T extends PacketBase> void handlePacketServer(T packet, IPayloadContext context, PacketHandler<T> handler){
			context.workHandler().submitAsync(handler.handleServer(packet, (Passenger)context.player().get().getData(PASSENGER)));
		}

		private static <T extends PacketBase> void handlePacketClient(T packet, IPayloadContext context, PacketHandler<T> handler){
			context.workHandler().submitAsync(handler.handleClient(packet, (Passenger)context.player().get().getData(PASSENGER)));
		}

	}

	//---//---//---//

	public static Handler_TagListener HTL = new Handler_TagListener();
	public static Handler_VehMove HVM = new Handler_VehMove();
	public static Handler_VehKeyPress HVK = new Handler_VehKeyPress();
	public static Handler_VehKeyPressState HVKS = new Handler_VehKeyPressState();
	public static Handler_SeatUpdate HSU = new Handler_SeatUpdate();
	public static Handler_SPUpdate HSPU = new Handler_SPUpdate();

	public static class PI_TagListener extends Packet_TagListener implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_TagListener pkt = new PI_TagListener();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return TAG_PACKET;
		}

	}

	public static class PI_VehMove extends Packet_VehMove implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_VehMove pkt = new PI_VehMove();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return VEHMOVE_PACKET;
		}

	}

	public static class PI_VehKeyPress extends Packet_VehKeyPress implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_VehKeyPress pkt = new PI_VehKeyPress();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return VEHKEYPRESS_PACKET;
		}

	}

	public static class PI_VehKeyPressState extends Packet_VehKeyPressState implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_VehKeyPressState pkt = new PI_VehKeyPressState();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return VEHKEYSTATE_PACKET;
		}

	}

	public static class PI_SeatUpdate extends Packet_SeatUpdate implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_SeatUpdate pkt = new PI_SeatUpdate();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return SEATUPDATE_PACKET;
		}

	}

	public static class PI_SPUpdate extends Packet_SPUpdate implements CustomPacketPayload {

		@Override
		public void write(FriendlyByteBuf buffer){
			encode(buffer);
		}

		public static CustomPacketPayload read(FriendlyByteBuf buffer){
			PI_SPUpdate pkt = new PI_SPUpdate();
			pkt.decode(buffer);
			return pkt;
		}

		@Override
		public ResourceLocation id(){
			return SPUPDATE_PACKET;
		}

	}

}
