package net.fexcraft.mod.fvtm.util;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.fvtm.FVTM4;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.data.Content;
import net.fexcraft.mod.fvtm.data.ContentType;
import net.fexcraft.mod.fvtm.data.addon.AddonLocation;
import net.fexcraft.mod.fvtm.data.attribute.Attribute;
import net.fexcraft.mod.fvtm.data.part.Part;
import net.fexcraft.mod.fvtm.item.ConsumableItem;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.fexcraft.mod.fvtm.item.MaterialItem;
import net.fexcraft.mod.fvtm.item.PartItem;
import net.fexcraft.mod.fvtm.model.Transforms;
import net.fexcraft.mod.fvtm.render.Transforms120;
import net.fexcraft.mod.fvtm.sys.uni.KeyPress;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.impl.IWI;
import net.fexcraft.mod.uni.impl.IWR;
import net.fexcraft.mod.uni.impl.SWI;
import net.fexcraft.mod.uni.item.ItemWrapper;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ResourcesImpl extends FvtmResources {

	public void searchASMPacks(){}

	public boolean searchPacksInResourcePacks(){
		return true;
	}

	public void loadPackTextures(){}


	public void checkForCustomModel(AddonLocation loc, ContentType contype, Content<?> content){}

	@Override
	public void createContentBlocks(){

	}


	public void createContentItems(){
		FvtmRegistry.MATERIALS.forEach(mat -> mat.setItemWrapper(wrapwrapper(mat.getID(), () -> new MaterialItem(mat))));
		FvtmRegistry.CONSUMABLES.forEach(con -> con.setItemWrapper(wrapwrapper(con.getID(), () -> new ConsumableItem(con))));
		FvtmRegistry.PARTS.forEach(part -> part.setItemWrapper(wrapwrapper(part.getID(), () -> new PartItem(part))));
	}

	private ItemWrapper wrapwrapper(IDL id, Supplier<Item> item){
		IWR iwr = new IWR(FVTM4.ITEM_REGISTRY.get(id.space()).register(id.id(), item));
		FvtmRegistry.CONTENT_ITEMS.put(id, iwr);
		FvtmRegistry.ITEMS.put(id.colon(), iwr);
		return iwr;
	}

	public ItemWrapper getItemWrapper(String id){
		Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
		return item == null ? null : new IWI(item);
	}

	public StackWrapper newStack(ItemWrapper item){
		return new SWI(item);
	}

	@Override
	public StackWrapper newStack(Object local){
		return null;
	}

	public JsonMap getJsonC(String loc){
		return null;
	}

	public void initModelPrograms(){
		Transforms.GET_TRANSFORM = (args -> {
			switch(args[0]){
				case "translation":
				case "translate":
				case "trans":
				case "tra":
				case "tr":
					return (Transforms.Transformer)new Transforms120.TF_Translate(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
				case "rotation":
				case "rotate":
				case "rot":
					return (Transforms.Transformer)new Transforms120.TF_Rotate(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]));
				case "scale":
					if(args.length < 3){
						float scale = Float.parseFloat(args[1]);
						return (Transforms.Transformer)new Transforms120.TF_Scale(scale, scale, scale);
					}
					return (Transforms.Transformer)new Transforms120.TF_Scale(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
				case "gl_rescale_normal":
				case "rescale_normal":
					return (Transforms.Transformer)Transforms120.TF_RESCALE_NORMAL;
			}
			return null;
		});
	}

	@Override
	public InputStream getAssetInputStream(IDL loc, boolean log){
		return null;
	}

	@Override
	public boolean isModPresent(String s){
		return false;
	}

	@Override
	public IDL getExternalTexture(String custom){
		return null;
	}

	@Override
	public void registerFvtmBlocks(){

	}

	@Override
	public void registerFvtmItems(){
		DecorationItem.REGOBJ = FVTM4.ITEM_REGISTRY.get("fvtm").register("decoration", () -> new DecorationItem());
	}

	@Override
	public boolean handleClick(KeyPress key, EntityW vehicle, SeatInstance seatInstance, EntityW player, StackWrapper stack){
		return false;
	}

	@Override
	public boolean sendToggle(Attribute<?> attr, EntityW vehicle, KeyPress key, Float val, EntityW player){
		return false;
	}

	@Override
	public double getMouseSensitivity(){
		return 0;
	}

	@Override
	public Object getBlockMaterial(String key, boolean allownull){
		return null;
	}


	public InputStream getModelInputStream(IDL loc, boolean log){
		try{
			return Minecraft.getInstance().getResourceManager().getResource((ResourceLocation)loc).get().open();
		}
		catch(Throwable e){
			e.printStackTrace();
			return null;
		}
	}

}