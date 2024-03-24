package net.fexcraft.mod.fvtm;

import com.mojang.logging.LogUtils;
import net.fexcraft.mod.fvtm.entity.*;
import net.fexcraft.mod.fvtm.util.CTab;
import net.fexcraft.mod.fvtm.util.TabInitializerF;
import net.fexcraft.mod.uni.EnvInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.HashMap;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FVTM4.MODID)
public class FVTM4 {

	public static final String MODID = "fvtm";
	private static Logger LOGGER4 = LogUtils.getLogger();
	public static final HashMap<String, DeferredRegister<Item>> ITEM_REGISTRY = new HashMap<>();
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "fvtm");
	//
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "fvtm");
	public static final RegistryObject<EntityType<Decoration>> DECORATION_ENTITY = ENTITIES.register("decoration", () ->
		EntityType.Builder.of(DecorationF::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(10)
			.setTrackingRange(256)
			.build("decoration")
	);
	public static final RegistryObject<EntityType<WheelEntity>> WHEEL_ENTITY = ENTITIES.register("wheel", () ->
		EntityType.Builder.of(WheelEntityF::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("wheel")
	);
	public static final RegistryObject<EntityType<RootVehicle>> VEHICLE_ENTITY = ENTITIES.register("vehicle", () ->
		EntityType.Builder.of(RootVehicleF::new, MobCategory.MISC)
			.sized(1F, 1F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("vehicle")
	);
    /*public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
            }).build());*/

	public FVTM4(){
		FvtmRegistry.init("1.20", FMLPaths.CONFIGDIR.get().toFile());
		EnvInfo.CLIENT = FMLLoader.getDist().isClient();
		FvtmGetters.DECORATION_ENTITY = () -> DECORATION_ENTITY.get();
		FvtmGetters.DECORATION_IMPL = DecorationF.class;
		FvtmGetters.ROOTVEHICLE_ENTITY = () -> VEHICLE_ENTITY.get();
		FvtmGetters.ROOTVEHICLE_IMPL = RootVehicleF.class;
		FvtmGetters.WHEEL_ENTITY = () -> WHEEL_ENTITY.get();
		FvtmGetters.WHEEL_IMPL = WheelEntityF.class;
		if(EnvInfo.CLIENT){
			CTab.IMPL[0] = TabInitializerF.class;
		}
		FVTM20.init0();
		FvtmLogger.LOGGER = new FvtmLogger() {
			@Override
			protected void log0(Object obj){
				LOGGER4.info(obj == null ? "null " + new Exception().getStackTrace()[2].toString() : obj.toString());
			}
		};
		FvtmRegistry.ADDONS.forEach(addon -> ITEM_REGISTRY.put(addon.getID().id(), DeferredRegister.create(Registries.ITEM, addon.getID().id())));
		FVTM20.init1();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		//TODO pack adder
		ITEM_REGISTRY.values().forEach(reg -> reg.register(bus));
		CREATIVE_MODE_TABS.register(bus);
		ENTITIES.register(bus);
		//TODO packets
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		//
	}

}
