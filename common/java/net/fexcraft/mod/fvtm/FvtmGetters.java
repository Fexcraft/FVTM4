package net.fexcraft.mod.fvtm;

import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.entity.WheelEntity;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.item.ToolboxItem;
import net.fexcraft.mod.fvtm.model.RenderCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmGetters {

	public static Supplier<EntityType<? extends Decoration>> DECORATION_ENTITY;
	public static Supplier<EntityType<? extends RootVehicle>> ROOTVEHICLE_ENTITY;
	public static Supplier<EntityType<? extends WheelEntity>> WHEEL_ENTITY;
	//
	public static Supplier<DecorationItem> DECORATION_ITEM;
	public static Supplier<ToolboxItem> TOOLBOX0;
	public static Supplier<ToolboxItem> TOOLBOX1;
	public static Supplier<ToolboxItem> TOOLBOX2;
	//
	public static Function<Entity, RenderCache> RENDERCACHE;

	public static Decoration getNewDecoration(Level level){
		return DECORATION_ENTITY.get().create(level);
	}

	public static RenderCache getRenderCache(Entity entity){
		return RENDERCACHE.apply(entity);
	}

	public static RootVehicle getNewVehicle(Level level){
		return ROOTVEHICLE_ENTITY.get().create(level);
	}

	public static WheelEntity getNewWheel(Level level){
		return WHEEL_ENTITY.get().create(level);
	}

}
