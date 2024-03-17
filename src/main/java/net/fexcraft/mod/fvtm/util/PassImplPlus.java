package net.fexcraft.mod.fvtm.util;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
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
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PassImplPlus extends Passenger {

	private Entity entity;
	private boolean notified;
	private int vehicle;
	private int seat;

	public PassImplPlus(IAttachmentHolder iah){
		super();
		entity = (Entity)iah;
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
		Entity ent = net.minecraft.client.Minecraft.getInstance().crosshairPickEntity;
		return ent == null ? V3D.NULL : new V3D(ent.getEyePosition().x, ent.getEyePosition().y, ent.getEyePosition().z);
	}

	@Override
	public V3D getLookVec(){
		Entity ent = net.minecraft.client.Minecraft.getInstance().crosshairPickEntity;
		return ent == null ? V3D.NULL : new V3D(ent.getLookAngle().x, ent.getLookAngle().y, ent.getLookAngle().z);
	}

	@Override
	public boolean isOnClient(){
		return entity.level().isClientSide;
	}

	@Override
	public int getId(){
		return entity.getId();
	}

	@Override
	public WorldW getWorld(){
		return WrapperHolder.getWorld(entity.level());
	}

	@Override
	public boolean isPlayer(){
		return entity instanceof Player;
	}

	@Override
	public boolean isAnimal(){
		return entity instanceof Animal;
	}

	@Override
	public boolean isHostile(){
		return entity instanceof Mob;
	}

	@Override
	public boolean isLiving(){
		return entity instanceof LivingEntity;
	}

	@Override
	public boolean isRiding(){
		return entity.isPassenger();
	}

	@Override
	public String getRegName(){
		return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
	}

	@Override
	public <E> E local(){
		return (E)entity;
	}

	@Override
	public Object direct(){
		return entity;
	}

	@Override
	public V3D getPos(){
		return new V3D(entity.position().x, entity.position().y, entity.position().z);
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

	@Override
	public void openUI(String id, V3I pos){
		((Player)entity).openMenu(new MenuProvider() {
			@Override
			public Component getDisplayName(){
				return Component.literal("Fexcraft Universal UI");
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player){
				return new UniCon(i, inventory, id, null, pos);
			}
		}, buf -> {
			buf.writeInt(id.length());
			buf.writeUtf(id);
			buf.writeInt(pos.x);
			buf.writeInt(pos.y);
			buf.writeInt(pos.z);
		});
	}

	@Override
	public String getName(){
		return entity.getName().getString();
	}

	@Override
	public void drop(StackWrapper stack, float height){
		entity.spawnAtLocation(stack.local(), height);
	}

	@Override
	public void send(String s){
		entity.sendSystemMessage(Component.translatable(s));
	}

	@Override
	public void send(String str, Object... args){
		entity.sendSystemMessage(Component.translatable(str, args));
	}

	@Override
	public void bar(String s){
		entity.sendSystemMessage(Component.translatable(s));//TODO
	}

	@Override
	public void dismount(){
		entity.unRide();
	}

}
