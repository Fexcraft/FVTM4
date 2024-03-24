package net.fexcraft.mod.fvtm;

import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.item.DecorationItem;
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

	public static Supplier<EntityType<Decoration>> DECORATION_ENTITY;
	public static Supplier<DecorationItem> DECORATION_ITEM;
	public static Class<? extends Decoration> DECORATION_IMPL;
	//
	public static Supplier<EntityType<RootVehicle>> ROOTVEHICLE_ENTITY;
	public static Class<? extends RootVehicle> ROOTVEHICLE_IMPL;
	//
	public static Function<Entity, RenderCache> RENDERCACHE;

	public static Decoration getNewDecoration(Level level){
		try{
			return DECORATION_IMPL.getConstructor(EntityType.class, Level.class).newInstance(DECORATION_ENTITY.get(), level);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public static RenderCache getRenderCache(Entity entity){
		return RENDERCACHE.apply(entity);
	}

}
