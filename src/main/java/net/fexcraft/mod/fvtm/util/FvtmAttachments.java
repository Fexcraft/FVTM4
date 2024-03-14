package net.fexcraft.mod.fvtm.util;

import net.fexcraft.mod.fvtm.model.RenderCache;
import net.fexcraft.mod.fvtm.model.RenderCacheI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmAttachments {

	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "fvtm");
	public static final Supplier<AttachmentType<RenderCacheI>> RENDERCACHE = ATTACHMENT_TYPES
		.register("rendercache", () -> AttachmentType.builder(iah -> new RenderCacheI()).build());

	public static void register(IEventBus bus){
		ATTACHMENT_TYPES.register(bus);
	}

}
