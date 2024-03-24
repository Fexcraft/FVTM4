package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.mod.fvtm.data.vehicle.WheelSlot;
import net.fexcraft.mod.fvtm.sys.uni.WheelTireData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WheelEntity extends Entity {

	public RootVehicle root;
	private boolean found;
	private int vehid;
	public WheelSlot wheel;
	public V3D pos;
	public String wheelid;
	private float stepheight = 1.125f;
	public double motionX;
	public double motionY;
	public double motionZ;

	public WheelEntity(EntityType<WheelEntity> type, Level level){
		super(type, level);
	}

	public WheelEntity init(RootVehicle veh, String wid){
		vehid = (root = veh).getId();
		wheelid = wid;
		wheel = root.vehicle.data.getWheelSlots().get(wid);
		setStepHeight();
		//
		if(root.vehicle.data.getWheelPositions().isEmpty()){
			if(!root.isRemoved()){
				level().addFreshEntity(new ItemEntity(level(), position().x, position().y, position().z, root.vehicle.data.newItemStack().local()));
				root.kill();
			}
			return null;
		}
		if(!root.vehicle.data.getWheelPositions().containsKey(wheelid)){
			kill();
			return null;
		}
		pos = root.vehicle.data.getWheelPositions().get(wheelid);
		V3D vec = root.vehicle.pivot().get_vector(pos);
		setPos(root.position().x + vec.x, root.position().y + vec.y, root.position().z + vec.z);
		setOldPosAndRot();
		return this;
	}

	private void setStepHeight(){
		WheelTireData wtd = root.vehicle.wheeldata.get(wheelid);
		stepheight = wtd == null ? root.spdata == null ? 1f : root.spdata.wheel_step_height : wtd.function.step_height;
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag){
		kill();
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag){
		//
	}

	@Override
	public boolean isPickable(){
		return false;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	@Override
	public float getStepHeight(){
		return stepheight;
	}

	public void writeSpawnData(FriendlyByteBuf buffer){
		if(wheelid == null) return;
		buffer.writeInt(vehid);
		buffer.writeInt(wheelid.length());
		buffer.writeCharSequence(wheelid, StandardCharsets.UTF_8);
	}

	public void readSpawnData(FriendlyByteBuf buffer){
		vehid = buffer.readInt();
		wheelid = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();
		root = (RootVehicle)level().getEntity(vehid);
		if(root == null) return;
		setPos(root.position());
		if(root.vehicle.data == null) return;
		wheel = root.vehicle.data.getWheelSlots().get(wheelid);
	}

	@Override
	public void tick(){
		if(level().isClientSide && !found){
			root = (RootVehicle)level().getEntity(vehid);
			if(root == null) return;
			found = true;
			root.wheels.put(wheelid, this);
		}
		if(root == null) return;
	}

	public Vec3 motion(){
		return new Vec3(motionX, motionY, motionZ);
	}

	public double getHorSpeed(){
		return Math.sqrt(motionX * motionX + motionZ * motionZ);
	}

}