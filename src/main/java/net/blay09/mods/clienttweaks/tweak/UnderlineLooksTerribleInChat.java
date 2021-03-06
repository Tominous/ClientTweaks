package net.blay09.mods.clienttweaks.tweak;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnderlineLooksTerribleInChat extends ClientTweak {

	public UnderlineLooksTerribleInChat() {
		super("Remove Underlines From Chat");
	}

	@SubscribeEvent
	public void onClientChatReceived(ClientChatReceivedEvent event) {
		if(isEnabled()) {
			event.setMessage(removeUnderline(event.getMessage()));
		}
	}

	private ITextComponent removeUnderline(ITextComponent textComponent) {
		textComponent.getStyle().setUnderlined(false);
		if(textComponent instanceof TextComponentTranslation) {
			for(Object arg : ((TextComponentTranslation) textComponent).getFormatArgs()) {
				if(arg instanceof ITextComponent) {
					removeUnderline((ITextComponent) arg);
				}
			}
		}
		for(ITextComponent sibling : textComponent.getSiblings()) {
			removeUnderline(sibling);
		}
		return textComponent;
	}

	@Override
	public boolean isEnabledDefault() {
		return true;
	}

	@Override
	public String getDescription() {
		return "This strips the underline formatting from chat because it looks terrible.";
	}
}
