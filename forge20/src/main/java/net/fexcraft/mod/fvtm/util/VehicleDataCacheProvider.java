package net.fexcraft.mod.fvtm.util;

import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.item.VehicleItem;
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
public class VehicleDataCacheProvider implements ICapabilityProvider {

	public static final Capability<VehicleDataCacheF> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	private LazyOptional<VehicleDataCacheF> optional;
	private VehicleDataCacheF cache;

	public VehicleDataCacheProvider(ItemStack stack){
		cache = new VehicleDataCacheF(FvtmResources.wrapStack(stack));
		optional = LazyOptional.of(() -> cache);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction){
		return capability == CAPABILITY ? optional.cast() : LazyOptional.empty();
	}

	@AutoRegisterCapability
	public static class VehicleDataCacheF extends ItemDataCache.VehicleDataCache {

		public VehicleDataCacheF(StackWrapper stack){
			super(stack);
			content = ((VehicleItem)stack.getItem().local()).getData(stack);
		}

	}

}
