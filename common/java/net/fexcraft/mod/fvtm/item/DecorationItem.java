package net.fexcraft.mod.fvtm.item;

import java.util.List;
import javax.annotation.Nullable;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.FvtmGetters;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.ui.UIKeys;
import net.fexcraft.mod.fvtm.util.GenericUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DecorationItem extends Item {

	public DecorationItem(){
		super(new Properties().stacksTo(64));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(GenericUtils.format("&9Rightclick on a block to place a decoration."));
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(context.getLevel().isClientSide) return InteractionResult.PASS;
		ItemStack stack = context.getItemInHand();
		final Decoration decoen = FvtmGetters.getNewDecoration(context.getLevel());
		decoen.setPos(context.getClickLocation());
		context.getLevel().addFreshEntity(decoen);
		if(!context.getPlayer().isCreative()) stack.shrink(1);
		PassengerUtil.get(context.getPlayer()).openUI(UIKeys.DECORATION_EDITOR.key, new V3I(decoen.getId(), 0, 0));
		return InteractionResult.SUCCESS;
	}

}