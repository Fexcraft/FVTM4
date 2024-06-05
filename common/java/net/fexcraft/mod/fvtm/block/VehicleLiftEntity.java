package net.fexcraft.mod.fvtm.block;

import net.fexcraft.mod.fvtm.FvtmGetters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleLiftEntity extends BlockEntity {

	public VehicleLiftEntity(BlockPos pPos, BlockState pBlockState){
		super(FvtmGetters.LIFT_ENTITY.get(), pPos, pBlockState);
	}

}
