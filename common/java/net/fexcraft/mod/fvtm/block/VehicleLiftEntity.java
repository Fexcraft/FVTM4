package net.fexcraft.mod.fvtm.block;

import net.fexcraft.mod.fvtm.FvtmGetters;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

	@Override
	public void saveAdditional(CompoundTag com){
		super.saveAdditional(com);

	}

	@Override
	public void load(CompoundTag com){
		super.load(com);

	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag tag = new CompoundTag();
		//
		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag){
		//
	}

}
