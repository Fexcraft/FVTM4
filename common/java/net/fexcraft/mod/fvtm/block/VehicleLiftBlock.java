package net.fexcraft.mod.fvtm.block;

import net.fexcraft.mod.fvtm.FvtmGetters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

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

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, tile, tool);
		if(tile != null){
			VehicleLiftEntity lift = (VehicleLiftEntity)tile;
			if(lift.getVehicleData() == null) return;
			ItemEntity ent = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, lift.getVehicleData().newItemStack().local());
			level.addFreshEntity(ent);
		}
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		BlockEntity entity = level.getBlockEntity(pos);
		if(entity == null) return;
		VehicleLiftEntity lift = (VehicleLiftEntity)entity;
		lift.rot = placer.getDirection().ordinal();
    }

}
