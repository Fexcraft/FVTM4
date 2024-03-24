package net.fexcraft.mod.fvtm.entity;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.fvtm.ui.UIKey;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Decoration extends Entity {

	public ArrayList<DecorationData> decos = new ArrayList<>();
	protected boolean locked;

	protected Decoration(EntityType<Decoration> type, Level level){
		super(type, level);
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag){
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

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(){
		return new ClientboundAddEntityPacket(this);
	}

	@Override
	public boolean isPickable(){
		return true;
	}

	public InteractionResult interact(Player player, InteractionHand hand){
		if(isRemoved() || level().isClientSide || hand == InteractionHand.OFF_HAND){
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
			PassengerUtil.get(player).openUI(UIKey.DECORATION_EDITOR.key, new V3I(getId(), 0, 0));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean hurt(DamageSource source, float am){
		if(level().isClientSide || isRemoved()) return false;
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

	@Override
	public boolean canBeCollidedWith(){
		return true;
	}

	public void updateClient(){
		TagCW com = TagCW.create();
		addAdditionalSaveData(com.local());
		com.set("entid", getId());
		Packets.sendToAll(Packet_TagListener.class, "deco", TagCW.wrap(com));//TODO change to ranged packet
	}

}