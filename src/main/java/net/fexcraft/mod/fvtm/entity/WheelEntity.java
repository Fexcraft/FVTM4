package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fvtm.FVTM4;
import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.fvtm.data.vehicle.WheelSlot;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.sys.uni.WheelTireData;
import net.fexcraft.mod.fvtm.ui.UIKey;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WheelEntity extends Entity implements IEntityWithComplexSpawn {

	public RootVehicle root;
	private boolean found;
	private int vehid;
	public WheelSlot wheel;
	public V3D pos;
	public String wheelid;
	private float stepheight = 1.125f;

	public WheelEntity(EntityType<WheelEntity> type, Level level){
		super(type, level);
	}

	private void init(RootVehicle veh, String wid){
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
			return;
		}
		if(!root.vehicle.data.getWheelPositions().containsKey(wheelid)){
			kill();
			return;
		}
		pos = root.vehicle.data.getWheelPositions().get(wheelid);
		V3D vec = root.vehicle.pivot().get_vector(pos);
		setPos(root.position().x + vec.x, root.position().y + vec.y, root.position().z + vec.z);
		setOldPosAndRot();;
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

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer){
		buffer.writeInt(vehid);
		buffer.writeInt(wheelid.length());
		buffer.writeCharSequence(wheelid, StandardCharsets.UTF_8);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer){
		vehid = buffer.readInt();
		wheelid = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();
		root = (RootVehicle)level().getEntity(vehid);
		if(root == null) return;
		setPos(root.position());
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

}