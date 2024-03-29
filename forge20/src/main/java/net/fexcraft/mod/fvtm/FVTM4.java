package net.fexcraft.mod.fvtm;

import com.mojang.logging.LogUtils;
import net.fexcraft.mod.fcl.util.PassProvider;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.data.addon.Addon;
import net.fexcraft.mod.fvtm.entity.*;
import net.fexcraft.mod.fvtm.impl.Packets20F;
import net.fexcraft.mod.fvtm.util.CTab;
import net.fexcraft.mod.fvtm.util.RenderCacheProvider;
import net.fexcraft.mod.fvtm.util.TabInitializerF;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.impl.IDLM;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

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
	//
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "fvtm");
	public static final RegistryObject<EntityType<DecorationF>> DECORATION_ENTITY = ENTITIES.register("decoration", () ->
		EntityType.Builder.of(DecorationF::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(10)
			.setTrackingRange(256)
			.build("decoration")
	);
	public static final RegistryObject<EntityType<WheelEntityF>> WHEEL_ENTITY = ENTITIES.register("wheel", () ->
		EntityType.Builder.of(WheelEntityF::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("wheel")
	);
	public static final RegistryObject<EntityType<RootVehicleF>> VEHICLE_ENTITY = ENTITIES.register("vehicle", () ->
		EntityType.Builder.of(RootVehicleF::new, MobCategory.MISC)
			.sized(1F, 1F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("vehicle")
	);
	//
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("fvtm", "channel"))
		.clientAcceptedVersions(pro -> true)
		.serverAcceptedVersions(pro -> true)
		.networkProtocolVersion(() -> "fvtm4")
		.simpleChannel();

	public FVTM4(){
		LOGGER4.info("MARKER " + IDLManager.INSTANCE[0]);
		IDLManager.INSTANCE[0] = new IDLM();
		FvtmRegistry.init("1.20", FMLPaths.CONFIGDIR.get().toFile());
		FvtmLogger.LOGGER = new FvtmLogger() {
			@Override
			protected void log0(Object obj){
				LOGGER4.info(obj == null ? "null " + new Exception().getStackTrace()[2].toString() : obj.toString());
			}
		};
		EnvInfo.CLIENT = FMLLoader.getDist().isClient();
		FvtmGetters.DECORATION_ENTITY = () -> DECORATION_ENTITY.get();
		FvtmGetters.ROOTVEHICLE_ENTITY = () -> VEHICLE_ENTITY.get();
		FvtmGetters.WHEEL_ENTITY = () -> WHEEL_ENTITY.get();
		FvtmGetters.RENDERCACHE = entity -> entity.getCapability(RenderCacheProvider.CAPABILITY).resolve().get();
		if(EnvInfo.CLIENT){
			CTab.IMPL[0] = TabInitializerF.class;
		}
		FVTM20.init0();
		FvtmRegistry.ADDONS.forEach(addon -> ITEM_REGISTRY.put(addon.getID().id(), DeferredRegister.create(Registries.ITEM, addon.getID().id())));
		FVTM20.init1();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		bus.register(new PackAdder());
		ITEM_REGISTRY.values().forEach(reg -> reg.register(bus));
		CREATIVE_MODE_TABS.register(bus);
		ENTITIES.register(bus);
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		new Packets20F().init();
	}

	public static class PackAdder {

		@SubscribeEvent
		public void addPacks(AddPackFindersEvent event){
			for(Addon addon : FvtmRegistry.ADDONS){
				if(!addon.getLocation().isConfigPack() || addon.getFile() == null) continue;
				Pack pack = Pack.create("fvtm/" + addon.getID().id(), Component.literal(addon.getName()), true, path -> {
					if(addon.getFile().isDirectory()) return new PathPackResources(path, addon.getFile().toPath(), true);
					else return new FilePackResources(addon.getName(), addon.getFile(), true);
				}, new Pack.Info(Component.literal("FVTM Auto-loaded Pack"), 15, 15, FeatureFlagSet.of(), false), event.getPackType(), Pack.Position.BOTTOM, false, PackSource.DEFAULT);
				event.addRepositorySource(cons -> {
					if(pack != null) cons.accept(pack);
				});
			}
		}

	}

	@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {

		@SubscribeEvent
		public static void onAttachCapsEvent(AttachCapabilitiesEvent<Entity> event){
			if(event.getObject() instanceof Decoration || event.getObject() instanceof RootVehicle){
				event.addCapability(new ResourceLocation("fvtm:rendercache"), new RenderCacheProvider(event.getObject()));
			}
		}

	}

}
