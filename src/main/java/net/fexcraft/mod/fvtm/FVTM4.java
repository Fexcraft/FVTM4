package net.fexcraft.mod.fvtm;

import com.mojang.logging.LogUtils;
import net.fexcraft.lib.frl.GLO;
import net.fexcraft.lib.frl.Renderer;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fvtm.data.addon.Addon;
import net.fexcraft.mod.fvtm.data.block.AABB;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.entity.WheelEntity;
import net.fexcraft.mod.fvtm.impl.AABBI;
import net.fexcraft.mod.fvtm.impl.Packets20;
import net.fexcraft.mod.fvtm.impl.WrapperHolderImpl;
import net.fexcraft.mod.fvtm.model.GLObject;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.fexcraft.mod.fvtm.ui.*;
import net.fexcraft.mod.fvtm.util.CTab;
import net.fexcraft.mod.fvtm.util.PassImplPlus;
import net.fexcraft.mod.fvtm.util.ResourcesImpl;
import net.fexcraft.mod.fvtm.util.TabInitializer;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.UniReg;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.nio.file.Path;
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
	public static final DeferredHolder<EntityType<?>, EntityType<Decoration>> DECORATION_ENTITY = ENTITIES.register("decoration", () ->
		EntityType.Builder.of(Decoration::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(10)
			.setTrackingRange(256)
			.build("decoration")
	);
	public static final DeferredHolder<EntityType<?>, EntityType<WheelEntity>> WHEEL_ENTITY = ENTITIES.register("wheel", () ->
		EntityType.Builder.of(WheelEntity::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("wheel")
	);
	public static final DeferredHolder<EntityType<?>, EntityType<RootVehicle>> VEHICLE_ENTITY = ENTITIES.register("vehicle", () ->
		EntityType.Builder.of(RootVehicle::new, MobCategory.MISC)
			.sized(1F, 1F)
			.setUpdateInterval(1)
			.setTrackingRange(256)
			.build("vehicle")
	);

	public FVTM4(IEventBus event){
		EnvInfo.CLIENT = FMLLoader.getDist().isClient();
		UniversalAttachments.PASS_IMPL[0] = PassImplPlus.class;
		WrapperHolder.INSTANCE = new WrapperHolderImpl();
		AABB.SUPPLIER = () -> new AABBI();
		FvtmLogger.LOGGER = new FvtmLogger() {
			@Override
			protected void log0(Object obj){
				LOGGER4.info(obj == null ? "null " + new Exception().getStackTrace()[2].toString() : obj.toString());
			}
		};
		if(EnvInfo.CLIENT){
			Renderer.RENDERER = new Renderer120();
			GLO.SUPPLIER = (() -> new GLObject());
		}
		FvtmRegistry.init("1.20", FMLPaths.CONFIGDIR.get().toFile());
		FvtmResources.INSTANCE = new ResourcesImpl();
		//NeoForge.EVENT_BUS.register(FvtmResources.INSTANCE);
		Config.addListener(() -> {
			//
		});
		if(EnvInfo.CLIENT){
			CTab.IMPL[0] = TabInitializer.class;
			//TODO Config.addListener(() -> ConditionRegistry.BUILDER = CondBuilder.run());
		}
		//
		UniReg.registerUI(UIKey.DECORATION_EDITOR.key, DecoEditor.class);
		UniReg.registerMenu(UIKey.DECORATION_EDITOR.key, "assets/fvtm/uis/deco_editor", DecoContainer.class);
		UniReg.registerUI(UIKey.TOOLBOX_COLORS.key, ToolboxPainter.class);
		UniReg.registerMenu(UIKey.TOOLBOX_COLORS.key, "assets/fvtm/uis/toolbox_colors", ToolboxPaintContainer.class);
		//
		FvtmResources.INSTANCE.init();
		FvtmRegistry.ADDONS.forEach(addon -> ITEM_REGISTRY.put(addon.getID().id(), DeferredRegister.create(BuiltInRegistries.ITEM, addon.getID().id())));
		//
		FvtmResources.INSTANCE.registerFvtmBlocks();
		FvtmResources.INSTANCE.registerFvtmItems();
		FvtmResources.INSTANCE.registerAttributes();
		FvtmResources.INSTANCE.registerFunctions();
		FvtmResources.INSTANCE.registerHandlers();
		FvtmResources.INSTANCE.searchContent();
		FvtmResources.INSTANCE.createContentBlocks();
		FvtmResources.INSTANCE.createContentItems();
		if(EnvInfo.CLIENT){
			FvtmResources.initModelSystem();
		}
		//
		event.register(new PackAdder());
		//register packets
		ITEM_REGISTRY.values().forEach(reg -> reg.register(event));
		CREATIVE_MODE_TABS.register(event);
		ENTITIES.register(event);
		//NeoForge.EVENT_BUS.register(this);
		new Packets20().init();
	}

	public static class PackAdder {

		@SubscribeEvent
		public void addPacks(AddPackFindersEvent event){
			if(event.getPackType() == PackType.CLIENT_RESOURCES){
				for(Addon addon : FvtmRegistry.ADDONS){
					if(!addon.getLocation().isConfigPack() || addon.getFile() == null) continue;
					Path rpath = addon.getFile().toPath();
					Pack pack = Pack.create("fvtm/" + addon.getID().id(), Component.literal(addon.getName()), true, new Pack.ResourcesSupplier() {
							private PackResources packres = new PathPackResources(addon.getName(), rpath, true);

							@Override
							public PackResources openPrimary(String s){
								return packres;
							}

							@Override
							public PackResources openFull(String s, Pack.Info info){
								return packres;
							}
						}, new Pack.Info(Component.literal("FVTM Autoloaded Pack"), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), Collections.emptyList(), false),
						Pack.Position.BOTTOM, true, PackSource.BUILT_IN);
					event.addRepositorySource(cons -> {
						if(pack != null) cons.accept(pack);
					});
				}
			}
		}

	}

}
