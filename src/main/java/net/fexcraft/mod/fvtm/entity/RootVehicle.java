package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.mod.fvtm.data.vehicle.SimplePhysData;
import net.fexcraft.mod.fvtm.sys.uni.VehicleInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RootVehicle extends Entity {

	public VehicleInstance vehicle;
	protected SimplePhysData spdata;
	public HashMap<String, WheelEntity> wheels = new HashMap<>();

	public RootVehicle(EntityType<?> type, Level level){
		super(type, level);
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag){

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag){

	}

}
