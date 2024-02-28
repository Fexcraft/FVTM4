package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fvtm.Config;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.data.root.Lockable;
import net.fexcraft.mod.fvtm.data.vehicle.SimplePhysData;
import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.fexcraft.mod.fvtm.function.part.EngineFunction;
import net.fexcraft.mod.fvtm.function.part.TireFunction;
import net.fexcraft.mod.fvtm.handler.TireInstallationHandler;
import net.fexcraft.mod.fvtm.handler.WheelInstallationHandler;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.item.MaterialItem;
import net.fexcraft.mod.fvtm.item.PartItem;
import net.fexcraft.mod.fvtm.item.VehicleItem;
import net.fexcraft.mod.fvtm.sys.uni.*;
import net.fexcraft.mod.fvtm.ui.UIKey;
import net.fexcraft.mod.fvtm.util.PassImplPlus;
import net.fexcraft.mod.fvtm.util.function.InventoryFunction;
import net.fexcraft.mod.uni.impl.TagCWI;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.HashMap;
import java.util.Map;

import static net.fexcraft.mod.fvtm.ui.UIKey.VEHICLE_MAIN;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RootVehicle extends Entity implements IEntityWithComplexSpawn {

	public VehicleInstance vehicle;
	public WheelTireData w_front_l;
	public WheelTireData w_front_r;
	public WheelTireData w_rear_l;
	public WheelTireData w_rear_r;
	protected SimplePhysData spdata;
	public HashMap<String, WheelEntity> wheels = new HashMap<>();
	public BoundingBox renderbox;
	public float rotZ = 0;
	public float protZ = 0;
	public float wheel_radius = 0;
	public float wheel_rotation = 0;
	public boolean should_sit = true;
	//
	public double serverX;
	public double serverY;
	public double serverZ;
	public double serverYaw;
	public double serverPitch;
	public double serverRoll;
	public double serverSteer;
	public byte server_sync;

	public RootVehicle(EntityType<?> type, Level level){
		super(type, level);
		vehicle = new VehicleInstance(new PassImplPlus(this), null);
	}

	public void init(VehicleData data){
		vehicle.init(data);
		init();
	}

	private void init(){
		spdata = vehicle.data.getType().getSphData();
		wheels.clear();
		wheel_radius = 0;
		for(Map.Entry<String, V3D> entry : vehicle.data.getWheelPositions().entrySet()){
			WheelTireData wheel = new WheelTireData(entry.getKey());
			wheel.pos = entry.getValue();
			PartData part = vehicle.data.getPart(entry.getKey());
			if(!((WheelInstallationHandler.WheelData)part.getType().getInstallHandlerData()).hasTire()){
				part = vehicle.data.getPart(entry.getKey() + ":tire");
				wheel_radius += ((TireInstallationHandler.TireData)part.getType().getInstallHandlerData()).getOuterRadius();
			}
			else{
				wheel_radius += ((WheelInstallationHandler.WheelData)part.getType().getInstallHandlerData()).getRadius();
			}
			wheel.function = part.getFunction(TireFunction.class, "fvtm:tire").getTireAttr(part);
			vehicle.wheeldata.put(entry.getKey(), wheel);
		}
		assignWheels();
		assignWheels();
		wheel_radius /= vehicle.wheeldata.size();
		vehicle.seats.clear();
		for(int i = 0; i < vehicle.data.getSeats().size(); i++){
			vehicle.seats.add(new SeatInstance(vehicle, i));
		}
		if(!level().isClientSide && vehicle.front != null){
			//TODO send connection state update
		}
		if(level().isClientSide){
			int cr = (int)vehicle.data.getAttributeFloat("collision_range", 2f);
			renderbox = new BoundingBox(-cr, -cr, -cr, cr, cr, cr);
		}
	}

	private void assignWheels(){
		w_front_l = w_front_r = w_rear_l = w_rear_r = new WheelTireData();
		for(WheelTireData wheel : vehicle.wheeldata.values()){
			if(wheel.pos.x <= w_front_l.pos.x && wheel.pos.z <= w_front_l.pos.z){
				w_front_l = wheel;
				continue;
			}
			if(wheel.pos.x >= w_front_r.pos.x && wheel.pos.z <= w_front_r.pos.z){
				w_front_r = wheel;
				continue;
			}
			if(wheel.pos.x <= w_rear_l.pos.x && wheel.pos.z >= w_rear_l.pos.z){
				w_rear_l = wheel;
				continue;
			}
			if(wheel.pos.x >= w_rear_r.pos.x && wheel.pos.z >= w_rear_r.pos.z){
				w_rear_r = wheel;
			}
		}
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag){
		TagCW com = TagCW.wrap(tag);
		if(vehicle.data == null){
			vehicle.init(FvtmResources.INSTANCE.getVehicleData(com));
		}
		else{
			vehicle.data.read(com);
		}
		setXRot(com.getFloat("RotationPitch"));
		setYRot(com.getFloat("RotationYaw"));
		protZ = rotZ = com.getFloat("RotationYaw");
		setOldPosAndRot();
		vehicle.point.loadPivot(com);
		init();
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag){
		TagCW com = TagCW.wrap(tag);
		vehicle.data.write(com);
		vehicle.point.savePivot(com);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer){
		TagCW com = TagCW.create();
		vehicle.point.savePivot(com);
		if(vehicle.front != null){
			com.set("TruckId", vehicle.front.entity.getId());
		}
		vehicle.data.write(com);
		buffer.writeNbt(com.local());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer){
		try{
			TagCW com = TagCW.wrap(buffer.readNbt());
			vehicle.init(FvtmResources.INSTANCE.getVehicleData(com));
			vehicle.point.loadPivot(com);
			setYRot(vehicle.point.getPivot().deg_yaw());
			setXRot(vehicle.point.getPivot().deg_pitch());
			protZ = rotZ = vehicle.point.getPivot().deg_roll();
			setOldPosAndRot();
			if(com.has("TruckId")){
				vehicle.front = ((RootVehicle)level().getEntity(com.getInteger("TruckId"))).vehicle;
				vehicle.front.rear = vehicle;
			}
			init();
		}
		catch(Exception e){
			e.printStackTrace();
			FvtmLogger.LOGGER.log("Failed to read additional spawn data for vehicle entity with ID " + getId() + "!");
		}
	}

	@Override
	public void kill(){
		if(Config.VEHICLES_DROP_CONTENTS && !level().isClientSide){
			for(String part : vehicle.data.getInventories()){
				InventoryFunction func = vehicle.data.getPart(part).getFunction("fvtm:inventory");
				if(func == null) continue;
				//TODO func.inventory().dropAllAt(this);
			}
		}
		super.kill();
		if(!wheels.isEmpty()){
			for(WheelEntity wheel : wheels.values()) wheel.kill();
		}
		if(vehicle.front != null) vehicle.front.rear = null;
		if(vehicle.rear != null) vehicle.rear.front = null;
	}

	public void setPosRotMot(V3D pos, double yaw, double pit, double rol, double thr, double steer, int fuel){
		serverX = pos.x;
		serverY = pos.y;
		serverZ = pos.z;
		serverYaw = yaw;
		serverPitch = pit;
		serverRoll = rol;
		serverSteer = steer;
		server_sync = Config.VEHICLE_SYNC_RATE;
		vehicle.throttle = thr;
		vehicle.data.getAttribute("fuel_stored").set(fuel);
	}

	@Override
	public boolean isPickable(){
		return true;
	}

	@Override
	public boolean canBeCollidedWith(){
		return true;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand){
		if(isRemoved() || hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		StackWrapper wrapper = FvtmResources.INSTANCE.newStack(stack);
		if(level().isClientSide){
			if(!stack.isEmpty() && stack.getItem() instanceof PartItem == false) return InteractionResult.SUCCESS;
			if(Lockable.isKey(wrapper.getItem())) return InteractionResult.SUCCESS;
			if(vehicle.data.getLock().isLocked()){
				player.sendSystemMessage(Component.translatable("interact.fvtm.vehicle.locked"));
				return InteractionResult.SUCCESS;
			}
			//TODO ToggableHandler.handleClick(KeyPress.MOUSE_RIGHT, this, null, player, stack);
			return InteractionResult.SUCCESS;
		}
		Passenger pass = (Passenger)player.getData(UniversalAttachments.PASSENGER);
		if(Lockable.isKey(wrapper.getItem()) && !isFuelContainer(stack.getItem())){
			vehicle.data.getLock().toggle(pass, wrapper);
			vehicle.sendLockUpdate();
			return InteractionResult.SUCCESS;
		}
		if(!stack.isEmpty()){
			if(stack.getItem() instanceof MaterialItem && ((MaterialItem)stack.getItem()).getContent().isFuelContainer()){
				//TODO open fuel UI
				return InteractionResult.SUCCESS;
			}
			/*else if(stack.getItem() instanceof ToolboxItem){
				if(stack.getMetadata() == 0){

				}
				else if(stack.getMetadata() == 1){

				}
				else if(stack.getMetadata() == 2){
					player.openGui(FVTM.getInstance(), TOOLBOX_COLORS, world, getEntityId(), 0, 0);
				}
				return InteractionResult.SUCCESS;
			}
			else if(stack.getItem() instanceof VehicleItem){
				//TODO check if trailer and connect
				return InteractionResult.SUCCESS;
			}
			else if(stack.getItem() instanceof ContainerItem){
				//TODO open container ui
				return InteractionResult.SUCCESS;
			}*/
			else{
				if(vehicle.data.hasPart("engine") && vehicle.data.getPart("engine").getFunction(EngineFunction.class, "fvtm:engine").isOn()){
					player.sendSystemMessage(Component.translatable("interact.fvtm.vehicle.engine_on"));
				}
				else{
					pass.openUI(VEHICLE_MAIN, new V3I(0, getId(), 0));
				}
				return InteractionResult.SUCCESS;
			}
		}
		if(vehicle.data.getLock().isLocked()){
			player.sendSystemMessage(Component.translatable("interact.fvtm.vehicle.locked"));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	private boolean isFuelContainer(Item item){
		if(item instanceof MaterialItem == false) return false;
		return ((MaterialItem)item).getContent().isFuelContainer();
	}
}
