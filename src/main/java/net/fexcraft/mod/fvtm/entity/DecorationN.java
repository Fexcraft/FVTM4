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
		try{
			buffer.writeBoolean(this.locked);
			buffer.writeInt(this.decos.size());
			for(DecorationData deco : this.decos){
				buffer.writeNbt(deco.write().local());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer){
		try{
			locked = buffer.readBoolean();
			decos.clear();
			int amount = buffer.readInt();
			for(int i = 0; i < amount; i++){
				this.decos.add(new DecorationData(TagCW.wrap(buffer.readNbt()), level().isClientSide));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
