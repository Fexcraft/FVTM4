package net.fexcraft.mod.fvtm.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.uni.Pos;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.ContainerInterface;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.world.level.Level;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DecoContainer extends ContainerInterface {
	
	protected Decoration entity;
	protected DecorationData selected;

	public DecoContainer(JsonMap map, EntityW player, V3I pos){
		super(map, player, pos);
		Level level = player.getWorld().local();
		entity = (Decoration)level.getEntity(pos.x);
	}

	@Override
	public Object get(String key, Object... objs){
		switch(key){
			case "decos.size": return Integer.valueOf(entity.decos.size());
			case "decos.key": return entity.decos.get(((Integer)objs[0]).intValue()).key();
			case "decos.at": return entity.decos.get(((Integer)objs[0]).intValue());
		}
		return null;
	}

	public void packet(TagCW com, boolean client){
		DecorationData deco;
		Pos pos;
		int sel;
		DecoEditor editor;
		String task = com.getString("task");
		switch(task){
			case "add":
				deco = FvtmRegistry.DECORATIONS.get(com.getString("key"));
				entity.decos.add(deco.copy());
				if(!client){
					SEND_TO_CLIENT.accept(com);
				}
				else{
					entity.decos.get(entity.decos.size() - 1).copy(deco);
					((DecoEditor)ui).updateEntries();
				}
				return;
			case "rem":
				entity.decos.remove(com.getInteger("idx"));
				if(!client){
					SEND_TO_CLIENT.accept(com);
				}
				else{
					((DecoEditor)ui).updateEntries();
					((DecoEditor)ui).select(-1, -1);
				}
				return;
			case "pos":
				deco = entity.decos.get(com.getInteger("idx"));
				pos = null;
				switch(com.getInteger("axis")){
					case 0:
						pos = new Pos(com.getFloat("value"), deco.offset.y, deco.offset.z);
						break;
					case 1:
						pos = new Pos(deco.offset.x, com.getFloat("value"), deco.offset.z);
						break;
					case 2:
						pos = new Pos(deco.offset.x, deco.offset.y, com.getFloat("value"));
						break;
					default:
						return;
				}
				deco.offset = pos;
				if(!client) SEND_TO_CLIENT.accept(com);
				break;
			case "rot":
				deco = entity.decos.get(com.getInteger("idx"));
				switch(com.getInteger("axis")){
					case 0:
						deco.rotx = com.getFloat("value");
						break;
					case 1:
						deco.roty = com.getFloat("value");
						break;
					case 2:
						deco.rotz = com.getFloat("value");
						break;
					default:
						return;
				}
				if(!client) SEND_TO_CLIENT.accept(com);
				break;
			case "scale":
				deco = entity.decos.get(com.getInteger("idx"));
				switch(com.getInteger("axis")){
					case 0:
						deco.sclx = com.getFloat("value");
						break;
					case 1:
						deco.scly = com.getFloat("value");
						break;
					case 2:
						deco.sclz = com.getFloat("value");
						break;
					default:
						return;
				}
				if(!client) SEND_TO_CLIENT.accept(com);
				break;
			case "tex":
				deco = entity.decos.get(com.getInteger("idx"));
				sel = com.getInteger("sel");
				if(sel >= 0 && sel < deco.textures.size()){
					deco.seltex = sel;
					if(!client){
						SEND_TO_CLIENT.accept(com);
						break;
					}
					DecoEditor decoEditor = (DecoEditor)ui;
					decoEditor.select(decoEditor.selected, decoEditor.selcol);
				}
				break;
			case "color":
				deco = entity.decos.get(com.getInteger("idx"));
				(deco.getColorChannel(com.getString("channel"))).packed = com.getInteger("rgb");
				if(!client){
					SEND_TO_CLIENT.accept(com);
					break;
				}
				editor = (DecoEditor)ui;
				editor.select(editor.selected, editor.selcol);
				break;
		}
	}

	@Override
	public void onClosed(){
		if(entity != null && !entity.level().isClientSide)this.entity.updateClient();
	}

}