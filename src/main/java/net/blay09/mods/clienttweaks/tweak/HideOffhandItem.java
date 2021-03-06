package net.blay09.mods.clienttweaks.tweak;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class HideOffhandItem extends ClientTweak {

	public HideOffhandItem() {
		super("Hide Offhand Item");
	}

	@Override
	public String getDescription() {
		return "This option will hide your offhand item. It can be toggled via an optional keybind.";
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderHand(RenderSpecificHandEvent event) {
		if (isEnabled()) {
			// TODO Tinkers inverts this event by rendering manually with its dual harvesting, come up with a solution
			if (event.getHand() == EnumHand.OFF_HAND) {
				if (event.getSwingProgress() == 0f) {
					event.setCanceled(true);
				}
			}
		}
	}

	@Nullable
	@Override
	protected KeyBinding createToggleKeybind() {
		return new KeyBinding("key.clienttweaks.hide_offhand_item", KeyConflictContext.IN_GAME, KeyModifier.NONE, 0, "key.categories.clienttweaks");
	}
}
