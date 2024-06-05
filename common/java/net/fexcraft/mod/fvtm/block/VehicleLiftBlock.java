package net.fexcraft.mod.fvtm.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleLiftBlock extends BaseEntityBlock {

	public VehicleLiftBlock(){
		super(Properties.of().noOcclusion().explosionResistance(64).strength(2));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx){
		return Asphalt.SHAPES[1];
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		return null;
	}

}
