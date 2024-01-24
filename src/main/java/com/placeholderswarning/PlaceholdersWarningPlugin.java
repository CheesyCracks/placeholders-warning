package com.placeholderswarning;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;

import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Placeholders Warning",
	description = "Alerts you when \"Always Set Placeholders\" is off in your bank",
	tags = {"always", "set", "placeholders", "bank", "organization", "sorting"}
)
public class PlaceholdersWarningPlugin extends Plugin
{
	@Inject
	private Notifier notifier;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private PlaceholdersWarningConfig config;

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals("placeholderswarning")) return;
		if (!configChanged.getKey().equals("blink")) return;
		Widget button = client.getWidget(12, 38);
		clientThread.invokeLater(() ->{if (button != null && !button.isHidden()) Objects.requireNonNull(client.getWidget(12, 39)).setHidden(false);});
	}
	@Subscribe
	public void onGameTick(GameTick gameTick){
		if (!config.blink()) return;
		Widget button = client.getWidget(12, 38);
		if (button == null || button.isHidden()) return;
		Widget icon = client.getWidget(12, 39);
		assert icon != null;
		if (button.getSpriteId() != 170) icon.setHidden(false);
		else icon.setHidden(!icon.isHidden());
	}
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
		if (widgetLoaded.getGroupId() != InterfaceID.BANK) return;
		if (Objects.requireNonNull(client.getWidget(12, 38)).getSpriteId() != 170) return;
		if (config.notification()) notifier.notify("Always Set Placeholders is turned off!");
		if (config.chatmessage()) client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Always Set Placeholders is turned off!</col>", null);
	}
	@Provides
	PlaceholdersWarningConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlaceholdersWarningConfig.class);
	}
}
