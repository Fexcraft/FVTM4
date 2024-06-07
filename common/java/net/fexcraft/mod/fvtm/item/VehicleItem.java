package net.fexcraft.mod.fvtm.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.mod.fvtm.FvtmGetters;
import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.data.ContentItem.ContentDataItem;
import net.fexcraft.mod.fvtm.data.ContentType;
import net.fexcraft.mod.fvtm.data.attribute.Attribute;
import net.fexcraft.mod.fvtm.data.root.ItemTextureable.TextureableItem;
import net.fexcraft.mod.fvtm.data.vehicle.Vehicle;
import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.function.part.EngineFunction;
import net.fexcraft.mod.fvtm.function.part.TransmissionFunction;
import net.fexcraft.mod.fvtm.model.DefaultModel;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.fexcraft.mod.fvtm.util.GenericUtils;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleItem extends Item implements ContentDataItem<Vehicle, VehicleData>, TextureableItem<Vehicle> {

	private Vehicle vehicle;

	public VehicleItem(Vehicle content){
		super(new Properties().stacksTo(1));
		vehicle = content;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(GenericUtils.format("&9Name: &7" + vehicle.getName()));
		for(String s : vehicle.getDescription()){
			tooltip.add(GenericUtils.format(I18n.get(s)));
		}
		VehicleData data = getDataFromTag(stack.getTag());
		if(data == null) return;
		tooltip.add(GenericUtils.format("&9Texture: &7" + getTexTitle(data)));
		if(data.hasPart("engine")){
			tooltip.add(GenericUtils.format("&9Engine: &7" + data.getPart("engine").getType().getName()));
			tooltip.add(GenericUtils.format("&9Fuel Group: &7" + data.getPart("engine").getFunction(EngineFunction.class, "fvtm:engine").getFuelGroup()[0]));
			tooltip.add(GenericUtils.format("&9Fuel Stored: &7" + data.getAttribute("fuel_stored").asInteger() + "mB"));
		}
		if(data.hasPart("transmission")){
			TransmissionFunction func = data.getFunctionInPart("transmission", "fvtm:transmission");
			tooltip.add(GenericUtils.format("&9Transmission: &7" + (func == null ? "disfunctional" : func.isAutomatic() ? "automatic" : "manual")));
		}
		tooltip.add(GenericUtils.format("&9Weight: &7" + data.getAttribute("weight").asFloat() + "kg"));
		tooltip.add(GenericUtils.format("&9Seats: &7" + data.getSeats().size()));
		tooltip.add(GenericUtils.format("&9LockCode: &7" + data.getLock().getCode()));
		if(flag.isAdvanced() && !data.getAttributes().isEmpty()){
			for(Attribute<?> attr : data.getAttributes().values()){
				tooltip.add(GenericUtils.format("&9" + attr.id + ": &7" + attr.asString()));
			}
		}
		if(vehicle.getModel() != null && vehicle.getModel().getCreators().size() > 0){
			tooltip.add(GenericUtils.format("&9Model by:"));
			for(String str : vehicle.getModel().getCreators()){
				tooltip.add(GenericUtils.format("&7- " + str));
			}
		}
		//TODO other data
	}

	private String getTexTitle(VehicleData data){
		if(data.getSelectedTexture() >= 0){
			return "[" + data.getSelectedTexture() + "] " + data.getType().getDefaultTextures().get(data.getSelectedTexture()).name();
		}
		else return data.isTextureExternal() ? "external" : "internal";
	}

	@Override
	public VehicleData getData(StackWrapper stack){
		if(!stack.hasTag()) stack.setTag(TagCW.create());
		return getData(stack.getTag());
	}

	@Override
	public VehicleData getData(TagCW compound){
		return new VehicleData(vehicle).read(compound);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(context.getLevel().isClientSide) return InteractionResult.PASS;
		ItemStack stack = context.getItemInHand();
		VehicleData data = getDataFromTag(stack.getTag());
		RootVehicle veh = FvtmGetters.getNewVehicle(context.getLevel());
		veh.setPos(context.getClickLocation().add(0, 2, 0));
		veh.init(data);
		context.getLevel().addFreshEntity(veh);
		if(!context.getPlayer().isCreative()) stack.shrink(1);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Vehicle getContent(){
		return vehicle;
	}

	@Override
	public ContentType getType(){
		return ContentType.VEHICLE;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer){
		consumer.accept(new IClientItemExtensions() {
			private static final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()){
				@Override
				public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource src, int v0, int v1){
					if(!stack.hasTag()) return;
					VehicleData data = FvtmResources.getVehicleData(stack.getTag());
					if(data != null && data.getType().getModel() != null){
						Renderer120.set(pose, src, v0);
						Renderer120.set(RenderType.entityCutout(data.getCurrentTexture().local()));
						data.getType().getModel().render(DefaultModel.RENDERDATA);
					}
				}
			});

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer(){
				return renderer.get();
			}
		});
	}

}
