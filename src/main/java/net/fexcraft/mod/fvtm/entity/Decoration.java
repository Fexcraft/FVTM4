package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.ui.UIKey;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class Decoration extends Entity {

	public ArrayList<DecorationData> decos = new ArrayList<>();
	private boolean locked;

	public Decoration(EntityType<Decoration> type, Level level){
		super(type, level);
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag){
		this.decos.clear();
		if(tag.contains("decorations")){
			ListTag list = (ListTag)tag.get("decorations");
			for(int i = 0; i < list.size(); i++){
				this.decos.add(new DecorationData(TagCW.wrap(list.get(i)), level().isClientSide));
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag){
		if(this.decos.size() == 0) return;
		ListTag list = new ListTag();
		for(DecorationData deco : this.decos) list.add(deco.write().local());
		tag.put("decorations", list);
	}

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

	public void readSpawnData(FriendlyByteBuf buffer){
		try{
			this.locked = buffer.readBoolean();
			this.decos.clear();
			int amount = buffer.readInt();
			for(int i = 0; i < amount; i++){
				this.decos.add(new DecorationData(TagCW.wrap(buffer.readNbt()), level().isClientSide));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(){
		return new ClientboundAddEntityPacket(this);
	}

	public boolean m_5829_(){
		return true;
	}

	public InteractionResult interact(Player player, InteractionHand hand){
		if(!isAlive() || level().isClientSide || hand == InteractionHand.OFF_HAND){
			return InteractionResult.PASS;
		}
		ItemStack stack = player.getItemInHand(hand);
		if(!stack.isEmpty() && stack.getItem() instanceof net.fexcraft.mod.fvtm.item.MaterialItem){
			this.locked = !this.locked;
			player.sendSystemMessage(Component.literal("Toggled Deco Lock status."));
			return InteractionResult.SUCCESS;
		}
		if(this.locked){
			player.sendSystemMessage(Component.literal("Deco is locked."));
			return InteractionResult.SUCCESS;
		}
		if(stack.isEmpty() || stack.getItem() instanceof DecorationItem){
			player.getData(UniversalAttachments.PASSENGER).openUI(UIKey.DECORATION_EDITOR.key, new V3I(getId(), 0, 0));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean hurt(DamageSource source, float am){
		if(level().isClientSide || !isAlive()) return false;
		if(source.getDirectEntity() instanceof Player){
			if(this.locked){
				source.getDirectEntity().sendSystemMessage(Component.literal("Deco is locked."));
				return true;
			}
			//ItemStack stack = getPickedResult(null);
			ItemEntity entity = new ItemEntity(EntityType.ITEM, level());
			entity.setPos(position().add(0.0D, 0.25D, 0.0D));
			entity.setItem(new ItemStack(DecorationItem.REGOBJ.get(), 1));
			level().addFreshEntity(entity);
			kill();
			return true;
		}
		return false;
	}


	public boolean m_6087_(){
		return true;
	}

	public void updateClient(){//TODO
		CompoundTag com = new CompoundTag();
		addAdditionalSaveData(com);
		com.putString("task", "deco_update");
		com.putInt("entid", getId());
		//TODO Packets.sendInRange(Packet_TagListener.class, null, null, 0, null);
	}

}