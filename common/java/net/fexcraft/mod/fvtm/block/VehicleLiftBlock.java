package net.fexcraft.mod.fvtm.block;

import com.mojang.serialization.MapCodec;
import net.fexcraft.mod.fvtm.FvtmGetters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleLiftBlock extends Block implements EntityBlock {

	public VehicleLiftBlock(){
		super(Properties.of().noOcclusion().explosionResistance(64).strength(2));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx){
		return Asphalt.SHAPES[1];
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		return FvtmGetters.LIFT_ENTITY.get().create(pPos, pState);
	}

	@Override
	public RenderShape getRenderShape(BlockState pState){
		return RenderShape.INVISIBLE;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int i, int j){
		super.triggerEvent(state, level, pos, i, j);
		BlockEntity be = level.getBlockEntity(pos);
		return be == null ? false : be.triggerEvent(i, j);
	}

}
