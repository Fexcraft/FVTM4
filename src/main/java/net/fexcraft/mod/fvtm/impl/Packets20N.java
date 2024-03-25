package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.packet.*;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.uni.world.WorldW;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Packets20N extends Packets20 {

	@Override
	public void init(){
		super.init();
		PACKETS.put(Packet_TagListener.class, PI_TagListener.class);
		PACKETS.put(Packet_VehMove.class, PI_VehMove.class);
		PACKETS.put(Packet_VehKeyPress.class, PI_VehKeyPress.class);
		PACKETS.put(Packet_VehKeyPressState.class, PI_VehKeyPressState.class);
		PACKETS.put(Packet_SeatUpdate.class, PI_SeatUpdate.class);
		PACKETS.put(Packet_SPUpdate.class, PI_SPUpdate.class);
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
			context.workHandler().submitAsync(handler.handleServer(packet, PassengerUtil.get(context.player().get())));
		}

		private static <T extends PacketBase> void handlePacketClient(T packet, IPayloadContext context, PacketHandler<T> handler){
			context.workHandler().submitAsync(handler.handleClient(packet, PassengerUtil.get(context.player().get())));
		}

	}

	//---//---//---//

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
