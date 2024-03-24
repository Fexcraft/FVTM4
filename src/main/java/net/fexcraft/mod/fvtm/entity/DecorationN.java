package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DecorationN extends Decoration implements IEntityWithComplexSpawn {

	public DecorationN(EntityType<Decoration> type, Level level){
		super(type, level);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer){
		super.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer){
		super.readSpawnData(buffer);
	}

}
