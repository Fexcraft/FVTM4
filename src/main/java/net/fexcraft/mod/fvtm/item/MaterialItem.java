package net.fexcraft.mod.fvtm.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.data.Content;
import net.fexcraft.mod.fvtm.data.ContentItem;
import net.fexcraft.mod.fvtm.data.ContentType;
import net.fexcraft.mod.fvtm.data.Fuel;
import net.fexcraft.mod.fvtm.data.Material;
import net.fexcraft.mod.fvtm.util.GenericUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MaterialItem extends Item implements ContentItem<Material> {

	private Material material;

	public MaterialItem(Material material){
		super((new Properties())
			.stacksTo(material.isFuelContainer() ? 1 : material.getMaxStack())
			.durability(material.getMaxHealth())
			.defaultDurability(material.getMaxHealth()));
		this.material = material;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(GenericUtils.format("&9Name: &7" + material.getName()));
		for(String s : material.getDescription())
			tooltip.add(GenericUtils.format(I18n.get(s)));
		if(material.getOreDictId() != null){
			tooltip.add(GenericUtils.format("&9OreDict: &7" + material.getOreDictId()));
		}
		if(material.isVehicleKey()){
			tooltip.add(GenericUtils.format("&9LockCode: &7" + getLockCode(stack)));
		}
		if(material.isFuelContainer()){
			tooltip.add(GenericUtils.format("&9Container: &7" + (material.isUniversalFuelContainer() ? "universal" : ((material.getFuelType() == null) ? material.getFuelGroup() : material.getFuelType().getName()))));
			tooltip.add(GenericUtils.format("&9Fuel Stored: &7" + getStoredFuelName(stack)));
			tooltip.add(GenericUtils.format("&9Fuel Amount: &7" + getStoredFuelAmount(stack) + "mB"));
		}
	}

	public String getLockCode(ItemStack stack){
		if(!material.isVehicleKey()) return null;
		if(stack.getTag() == null) stack.setTag(new CompoundTag());
		if(!stack.getTag().contains("LockCode"))
			stack.getTag().putString("LockCode", UUID.randomUUID().toString().replace("-", "").substring(0, 7));
		return stack.getTag().getString("LockCode");
	}

	public Fuel getStoredFuelType(ItemStack stack){
		if(!material.isFuelContainer()) return null;
		if(material.getFuelType() != null) return material.getFuelType();
		if(stack.hasTag()) return FvtmRegistry.getFuel(stack.getTag().getString("StoredFuelType"));
		return null;
	}

	public int getStoredFuelAmount(ItemStack stack){
		if(!material.isFuelContainer() || !stack.hasTag()) return 0;
		return stack.getTag().getInt("StoredFuelAmount");
	}

	public String getStoredFuelName(ItemStack stack){
		if(!material.isFuelContainer()) return "Nothing.";
		if(material.getFuelType() != null) return material.getFuelType().getName();
		if(stack.hasTag()) return "//TODO";
		return "none";
	}

	public void extractFuel(ItemStack stack, int stored){
		if(!stack.hasTag()) stack.setTag(new CompoundTag());
		stack.getTag().putInt("StoredFuelAmount", stack.getTag().getInt("StoredFuelAmount") - stored);
		if(stack.getTag().getInt("StoredFuelAmount") < 0){
			stack.getTag().putInt("StoredFuelAmount", 0);
		}
	}

	public void insertFuel(ItemStack stack, int stored){
		if(!stack.hasTag()) stack.setTag(new CompoundTag());
		stack.getTag().putInt("StoredFuelAmount", stack.getTag().getInt("StoredFuelAmount") + stored);
		if(stack.getTag().getInt("StoredFuelAmount") > material.getFuelCapacity()){
			stack.getTag().putInt("StoredFuelAmount", material.getFuelCapacity());
		}
	}

	@Override
	public Material getContent(){
		return material;
	}

	@Override
	public ContentType getType(){
		return ContentType.MATERIAL;
	}

}