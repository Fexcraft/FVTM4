package net.fexcraft.mod.fvtm.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.data.root.Colorable;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.packet.Packet_TagListener;
import net.fexcraft.mod.fvtm.packet.Packets;
import net.fexcraft.mod.uni.impl.TagCWI;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.ContainerInterface;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ToolboxPaintContainer extends ContainerInterface {

	protected Colorable colorable;
	protected RootVehicle vehicle;

	public ToolboxPaintContainer(JsonMap map, EntityW player, V3I vec){
		super(map, player, vec);
		vehicle = (RootVehicle)((Level)player.getWorld().local()).getEntity(vec.x);
		colorable = vehicle.vehicle.data;
	}

	@Override
	public Object get(String key, Object... objs){
		switch(key){
			case "channel_keys":{
				return colorable.getColorChannels().keySet();
			}
			case "color":{
				return colorable.getColorChannel(objs[0].toString());
			}
			case "open_wiki":{
				if(player.getWorld().isClient()){
					((Player)player.direct()).sendSystemMessage(Component.literal("https://fexcraft.net/wiki/mod/fvtm/toolbox#painter"));
					//TODO open link dialog instead
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public void packet(TagCW com, boolean client){
		String task = com.getString("task");
		switch(task){
			case "apply":{
				colorable.getColorChannel(com.getString("channel")).packed = com.getInteger("color");
				TagCW tag = TagCW.create();
				tag.set("vehicle", vehicle.getId());
				tag.set("channel", com.getString("channel"));
				tag.set("color", com.getInteger("color"));
				Packets.sendToAll(Packet_TagListener.class, "vehicle_color", tag);
				break;
			}
		}
	}

	@Override
	public void onClosed(){
		//
	}

}
