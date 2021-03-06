package net.blay09.mods.clienttweaks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.clienttweaks.tweak.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod(modid = ClientTweaks.MOD_ID, name = "ClientTweaks", acceptedMinecraftVersions = "[1.12]", clientSideOnly = true,
		guiFactory = "net.blay09.mods.clienttweaks.GuiFactory")
public class ClientTweaks {

	public static final String MOD_ID = "clienttweaks";

	@Mod.Instance
	public static ClientTweaks instance;

	public static Configuration config;

	private static Map<String, ClientTweak> tweaks = Maps.newHashMap();
	private static List<ClientTweak> toggleTweaks = Lists.newArrayList();
	public static Set<String> torchItems = Sets.newHashSet();
	public static Set<String> torchTools = Sets.newHashSet();
	public static Set<String> offhandTorchTools = Sets.newHashSet();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if(!FMLClientHandler.instance().hasOptifine()) {
			registerTweak(new Screw3dAnaglyph());
		}
		registerTweak(new AutoJumpMoreLikeAutoDumbAmirite());
		registerTweak(new MasterVolumeSlider());
		registerTweak(new UnderlineLooksTerribleInChat());
		registerTweak(new NoOffhandTorchWithBlock());
		registerTweak(new NoOffhandTorchWithEmptyHand());
		registerTweak(new OffhandTorchWithToolOnly());
		registerTweak(new HideOwnEffectParticles());
		registerTweak(new HideOffhandItem());
		registerTweak(new StepAssistIsAnnoying());
		registerTweak(new AutoLadder());
		registerTweak(new DisablePotionShift());
		registerTweak(new HideShieldUnlessHoldingWeapon());
		registerTweak(new DoNotUseLastTorch());

		config = new Configuration(event.getSuggestedConfigurationFile());
		reloadConfig();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		for(ClientTweak tweak : tweaks.values()) {
			tweak.init(event);

			KeyBinding keyBinding = tweak.registerToggleKeybind();
			if(keyBinding != null) {
				toggleTweaks.add(tweak);
			}
		}
	}

	private void reloadConfig() {
		torchItems = Sets.newHashSet(config.getStringList("Torch Items", "general", new String[] {
				"minecraft:torch",
				"tconstruct:stone_torch"
		}, "Items that count as torches for the offhand-torch tweak options."));
		torchTools = Sets.newHashSet(config.getStringList("Torch Tools", "general", new String[] {
				"minecraft:wooden_pickaxe",
				"minecraft:stone_pickaxe",
				"minecraft:iron_pickaxe",
				"minecraft:golden_pickaxe",
				"minecraft:diamond_pickaxe",
				"tconstruct:pickaxe",
				"tconstruct:hammer"
		}, "Items that will place torches from your hotbar on right-click if enabled."));
		offhandTorchTools = Sets.newHashSet(config.getStringList("Offhand Torch Tools", "general", new String[] {
				"tconstruct:shovel",
				"tconstruct:excavator"
		}, "Items that will not prevent offhand-torch placement while in offhand, but do not place torches by themselves"));
		for(ClientTweak tweak : tweaks.values()) {
			tweak.setEnabled(config.getBoolean(tweak.getName(), "tweaks", tweak.isEnabledDefault(), tweak.getDescription()));
		}
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(MOD_ID)) {
			reloadConfig();
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			for(ClientTweak tweak : toggleTweaks) {
				if(tweak.getKeyBinding().isActiveAndMatches(Keyboard.getEventKey())) {
					tweak.setEnabled(!tweak.isEnabled());
					config.get("tweaks", tweak.getName(), tweak.isEnabledDefault(), tweak.getDescription()).set(tweak.isEnabled());
					if(config.hasChanged()) {
						config.save();
					}
					ITextComponent root = new TextComponentString(tweak.getName() + ": ");
					root.appendSibling(new TextComponentTranslation(tweak.isEnabled() ? "clienttweaks.on" : "clienttweaks.off"));
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(root, 5);
				}
			}
		}
	}

	private void registerTweak(ClientTweak tweak) {
		tweaks.put(tweak.getName(), tweak);
	}

}
