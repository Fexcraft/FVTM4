package net.fexcraft.mod.fvtm.util;

import net.fexcraft.mod.fvtm.FVTM4;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.data.addon.Addon;
import net.fexcraft.mod.fvtm.item.DecorationItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TabInitializer implements CTab {

	public TabInitializer(Addon addon, String id){
		FvtmLogger.LOGGER.debug("Registering CTab " + addon.getID().colon() + "-" + id);
		FVTM4.CREATIVE_MODE_TABS.register(addon.getID().id() + addon.getID().id(), () -> CreativeModeTab.builder().withTabsBefore(new ResourceKey[]{ CreativeModeTabs.TOOLS_AND_UTILITIES }).title(Component.literal(addon.getName())).build());
	}

}