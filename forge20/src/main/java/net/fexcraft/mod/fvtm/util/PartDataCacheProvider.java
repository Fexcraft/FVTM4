package net.fexcraft.mod.fvtm.util;

import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.item.PartItem;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartDataCacheProvider implements ICapabilityProvider {

	public static final Capability<PartDataCacheF> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	private LazyOptional<PartDataCacheF> optional;
	private PartDataCacheF cache;

	public PartDataCacheProvider(ItemStack stack){
		cache = new PartDataCacheF(FvtmResources.wrapStack(stack));
		optional = LazyOptional.of(() -> cache);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction){
		return capability == CAPABILITY ? optional.cast() : LazyOptional.empty();
	}

	@AutoRegisterCapability
	public static class PartDataCacheF extends ItemDataCache.PartDataCache {

		public PartDataCacheF(StackWrapper stack){
			super(stack);
			content = ((PartItem)stack.getItem().local()).getData(stack);
		}

	}

}
