package com.placeholderswarning;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PlaceholdersWarningPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PlaceholdersWarningPlugin.class);
		RuneLite.main(args);
	}
}