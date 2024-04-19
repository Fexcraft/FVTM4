package net.fexcraft.mod.fvtm.item;

import net.fexcraft.mod.fvtm.sys.road.UniRoadTool;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RoadToolItem extends Item {

	public RoadToolItem(){
		super(new Properties().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		ArrayList<String> list = new ArrayList<>();
		if(!stack.hasTag()) stack.setTag(new CompoundTag());
		UniRoadTool.addTooltip(TagCW.wrap(stack.getTag()), list, (str, objs) -> I18n.get(str, objs));
		for(String str : list) tooltip.add(Component.literal(str));
	}

}
