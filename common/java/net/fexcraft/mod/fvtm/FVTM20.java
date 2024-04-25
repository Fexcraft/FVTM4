package net.fexcraft.mod.fvtm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.lib.frl.GLO;
import net.fexcraft.lib.frl.Renderer;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.data.block.AABB;
import net.fexcraft.mod.fvtm.impl.AABBI;
import net.fexcraft.mod.fvtm.impl.SWIE;
import net.fexcraft.mod.fvtm.impl.WrapperHolderImpl;
import net.fexcraft.mod.fvtm.model.GLObject;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.fexcraft.mod.fvtm.sys.road.RoadPlacingCache;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.ui.*;
import net.fexcraft.mod.fvtm.ui.road.RoadToolCustomUI;
import net.fexcraft.mod.fvtm.ui.road.RoadToolUI;
import net.fexcraft.mod.fvtm.ui.vehicle.*;
import net.fexcraft.mod.fvtm.util.PassImplPlus;
import net.fexcraft.mod.fvtm.util.ResourcesImpl;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.UniReg;
import net.fexcraft.mod.uni.item.ItemWrapper;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.ui.UISlot;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FVTM20 {

	public static void init0(){
		PassengerUtil.PASS_IMPL = PassImplPlus.class;
		WrapperHolder.INSTANCE = new WrapperHolderImpl();
		AABB.SUPPLIER = () -> new AABBI();
		StackWrapper.SUPPLIER = obj -> {
			if(obj instanceof ItemWrapper) return new SWIE((ItemWrapper)obj);
			if(obj instanceof ItemStack) return new SWIE((ItemStack)obj);
			return null;
		};
		if(EnvInfo.CLIENT){
			Renderer.RENDERER = new Renderer120();
			GLO.SUPPLIER = (() -> new GLObject());
		}
		FvtmResources.INSTANCE = new ResourcesImpl();
		Config.addListener(() -> {
			//
		});
		//
		UISlot.SLOT_GETTER = (type, args) -> {
			switch(type){
				case "default":
				default:
					return new Slot((Container)args[0], (Integer)args[1], (Integer)args[2], (Integer)args[3]);
			}
		};
		UniReg.registerUI(UIKey.DECORATION_EDITOR.key, DecoEditor.class);
		UniReg.registerMenu(UIKey.DECORATION_EDITOR.key, "assets/fvtm/uis/deco_editor", DecoContainer.class);
		UniReg.registerUI(UIKey.TOOLBOX_COLORS.key, ToolboxPainter.class);
		UniReg.registerMenu(UIKey.TOOLBOX_COLORS.key, "assets/fvtm/uis/toolbox_colors", ToolboxPaintContainer.class);
		//
		UniReg.registerUI(UIKey.ROAD_TOOL.key, RoadToolUI.class);
		UniReg.registerMenu(UIKey.ROAD_TOOL.key, "assets/fvtm/uis/road_tool", RoadToolConImpl.class);
		UniReg.registerUI(UIKey.ROAD_TOOL_CUSTOM.key, RoadToolCustomUI.class);
		UniReg.registerMenu(UIKey.ROAD_TOOL_CUSTOM.key, "assets/fvtm/uis/road_tool_custom", RoadToolCustomConImpl.class);
		//
		UniReg.registerUI(UIKey.VEHICLE_MAIN.key, VehicleMain.class);
		UniReg.registerMenu(UIKey.VEHICLE_MAIN.key, "assets/fvtm/uis/vehicle_main", VehicleMainCon.class);
		UniReg.registerUI(UIKey.VEHICLE_FUEL.key, VehicleFuel.class);
		UniReg.registerMenu(UIKey.VEHICLE_FUEL.key, "assets/fvtm/uis/vehicle_fuel", VehicleFuelConImpl.class);
		UniReg.registerUI(UIKey.VEHICLE_ATTRIBUTES.key, VehicleAttributes.class);
		UniReg.registerMenu(UIKey.VEHICLE_ATTRIBUTES.key, "assets/fvtm/uis/vehicle_attributes", VehicleAttributesCon.class);
		UniReg.registerUI(UIKey.VEHICLE_INVENTORIES.key, VehicleInventories.class);
		UniReg.registerMenu(UIKey.VEHICLE_INVENTORIES.key, "assets/fvtm/uis/vehicle_inventories", VehicleInventoriesCon.class);
		UniReg.registerUI(UIKey.VEHICLE_CATALOG.key, VehicleCatalog.class);
		UniReg.registerMenu(UIKey.VEHICLE_CATALOG.key, "assets/fvtm/uis/vehicle_catalog", VehicleCatalogCon.class);
		//
		FvtmResources.INSTANCE.init();
	}

	public static void init1(){
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
	}

	public static LiteralArgumentBuilder<CommandSourceStack> genCommand(){
		return Commands.literal("fvtm").then(Commands.literal("undo").then(Commands.literal("road").executes(ctx -> {
			Player player = ctx.getSource().getPlayerOrException();
			Passenger pass = PassengerUtil.get(player);
			JsonMap map = RoadPlacingCache.getLastEntry(player.getGameProfile().getId(), player.level().dimension().location().toString());
			if(map == null || map.empty()){
				pass.send("No last road data in item.");
				return 0;
			}
			String dim = map.getString("LastRoadDim", "minecraft:overworld");
			if(!dim.equals(player.level().dimension().location().toString())){
				pass.send("Last road was placed in &6DIM" + map.getString("LastRoadDim", "unknown"));
				pass.send("You are currenctly in &6DIM" + player.level().dimension().location());
				return 0;
			}
			map.rem("LastRoadDim");
			pass.send("&oUndo-ing last placed road...");
			for(String str : map.value.keySet()){
				JsonArray array = map.getArray(str);
				V3I vec = V3I.fromString(str);
				BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
				Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(array.get(0).string_value()));
				player.level().setBlock(pos, block.defaultBlockState(), 3);
			}
			RoadPlacingCache.remLastEntry(player.getGameProfile().getId(), player.level().dimension().location().toString());
			pass.send("&7Last road undone.");
			return 0;
		})));
	}

}
