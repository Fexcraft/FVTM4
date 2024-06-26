package net.fexcraft.mod.fvtm;

import com.mojang.logging.LogUtils;
import net.fexcraft.mod.fvtm.data.addon.Addon;
import net.fexcraft.mod.fvtm.entity.*;
import net.fexcraft.mod.fvtm.impl.*;
import net.fexcraft.mod.fvtm.util.*;
import net.fexcraft.mod.uni.EnvInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod(FVTM4.MODID)
public class FVTM4 {

	public static final String MODID = "fvtm";
	private static Logger LOGGER4 = LogUtils.getLogger();
	public static final HashMap<String, DeferredRegister<Item>> ITEM_REGISTRY = new HashMap<>();
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "fvtm");

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "fvtm");
	public static final DeferredHolder<EntityType<?>, EntityType<DecorationN>> DECORATION_ENTITY = ENTITIES.register("decoration", () ->
		EntityType.Builder.of(DecorationN::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(10)
			.setTrackingRange(256)
			.build("decoration")
	);
	public static final DeferredHolder<EntityType<?>, EntityType<RoadMarkerN>> ROAD_MARKER_ENTITY = ENTITIES.register("road_marker", () ->
		EntityType.Builder.of(RoadMarkerN::new, MobCategory.MISC)
			.sized(0.24F, 0.48F)
			.setUpdateInterval(10)
			.setTrackingRange(256)
			.build("road_marker")
	);
	public static final DeferredHolder<EntityType<?>, EntityType<WheelEntityN>> WHEEL_ENTITY = ENTITIES.register("wheel", () ->
		EntityType.Builder.of((EntityType.EntityFactory<WheelEntityN>)(type, level) -> new WheelEntityN(type, level), MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("wheel")
	);
	public static final DeferredHolder<EntityType<?>, EntityType<RootVehicleN>> VEHICLE_ENTITY = ENTITIES.register("vehicle", () ->
		EntityType.Builder.of(RootVehicleN::new, MobCategory.MISC)
			.sized(1F, 1F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("vehicle")
	);
	//
	public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(Registries.BLOCK, "fvtm");

	public FVTM4(IEventBus event){
		FvtmRegistry.init("1.20", FMLPaths.CONFIGDIR.get().toFile());
		FvtmLogger.LOGGER = new FvtmLogger() {
			@Override
			protected void log0(Object obj){
				LOGGER4.info(obj == null ? "null " + new Exception().getStackTrace()[2].toString() : obj.toString());
			}
		};
		EnvInfo.CLIENT = FMLLoader.getDist().isClient();
		FvtmGetters.DECORATION_ENTITY = () -> DECORATION_ENTITY.get();
		FvtmGetters.ROAD_MARKER_ENTITY = () -> ROAD_MARKER_ENTITY.get();
		FvtmGetters.ROOTVEHICLE_ENTITY = () -> VEHICLE_ENTITY.get();
		FvtmGetters.WHEEL_ENTITY = () -> WHEEL_ENTITY.get();
		FvtmGetters.WHEEL_ENTITY_CLASS = WheelEntityN.class;
		if(EnvInfo.CLIENT){
			CTab.IMPL[0] = TabInitializerN.class;
		}
		FVTM20.init0();
		FvtmAttachments.register(event);
		FvtmRegistry.ADDONS.forEach(addon -> ITEM_REGISTRY.put(addon.getID().id(), DeferredRegister.create(BuiltInRegistries.ITEM, addon.getID().id())));
		FVTM20.init1();
		//
		event.register(new PackAdder());
		//register packets
		ITEM_REGISTRY.values().forEach(reg -> reg.register(event));
		CREATIVE_MODE_TABS.register(event);
		ENTITIES.register(event);
		BLOCK_REGISTRY.register(event);
		//NeoForge.EVENT_BUS.register(this);
		new Packets20N().init();
	}

	public static class PackAdder {

		@SubscribeEvent
		public void addPacks(AddPackFindersEvent event){
			for(Addon addon : FvtmRegistry.ADDONS){
				if(!addon.getLocation().isConfigPack() || addon.getFile() == null) continue;
				Pack.ResourcesSupplier ressupp = null;
				if(addon.getFile().isDirectory()){
					ressupp = new PathPackResources.PathResourcesSupplier(addon.getFile().toPath(), true);
				}
				else{
					ressupp = new FilePackResources.FileResourcesSupplier(addon.getFile(), true);
				}
				Pack pack = Pack.create("fvtm/" + addon.getID().id(), Component.literal(addon.getName()), true, ressupp, new Pack.Info(Component.literal("FVTM Auto-loaded Pack"), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), Collections.emptyList(), false), Pack.Position.BOTTOM, false, PackSource.DEFAULT);
				event.addRepositorySource(cons -> {
					if(pack != null) cons.accept(pack);
				});
			}
		}

	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static class Events {

		@SubscribeEvent
		public static void onCmdReg(RegisterCommandsEvent event){
			event.getDispatcher().register(FVTM20.genCommand());
		}

	}

}
