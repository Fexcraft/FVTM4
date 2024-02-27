package net.fexcraft.mod.fvtm.impl;

import io.netty.buffer.ByteBuf;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.UIPacket;
import net.fexcraft.mod.fvtm.data.block.BlockData;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.packet.*;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.VehicleInstance;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.WorldW;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
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

	@Override
	public void init(){
		INSTANCE = this;
		PACKETS.put(Packet_TagListener.class, PI_TagListener.class);
		LIS_SERVER.put("", (com, player) -> {
			//
		});
		if(EnvInfo.CLIENT){
			LIS_CLIENT.put("deco", (tag, player) -> {
				Level level = player.getWorld().local();
				Entity ent = level.getEntity(tag.getInteger("entid"));
				if(ent != null && ent instanceof Decoration){
					((Decoration)ent).readAdditionalSaveData(tag.local());
				}
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
	public void send(VehicleInstance vehicleInstance, TagCW com){

	}

	@Override
	public void send0(Class<? extends PacketBase> packet, Object... data){
		try{
			PacketDistributor.SERVER.noArg().send((Packet<?>)PACKETS.get(packet).newInstance().fill(data));
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
				PacketDistributor.PLAYER.with(player).send((Packet<?>)PACKETS.get(packet).newInstance().fill(data));
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
				PacketDistributor.PLAYER.with(player).send((Packet<?>)PACKETS.get(packet).newInstance().fill(data));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void sendTo0(Class<? extends PacketBase> packet, Passenger to, Object... data){
		try{
			PacketDistributor.PLAYER.with(to.local()).send((Packet<?>)PACKETS.get(packet).newInstance().fill(data));
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

}
