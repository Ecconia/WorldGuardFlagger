package de.ecconia.bukkit.plugin.worldguardflagger;

import java.util.Map;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardFlagger extends JavaPlugin implements Listener
{
	@Override
	public void onLoad()
	{
		saveDefaultConfig();
		
		ConfigurationSection flagsSection = getConfig().getConfigurationSection("flags");
		if(flagsSection == null)
		{
			getLogger().severe("Could not find 'flags' section in config file.");
			return;
		}
		Map<String, Object> flagsToInclude = flagsSection.getValues(false);
		
		FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
		int yesCounter = 0;
		int noCounter = 0;
		for(Map.Entry<String, Object> entry : flagsToInclude.entrySet())
		{
			if(entry.getValue() == null)
			{
				getLogger().severe("Flag entry with key '" + entry.getKey() + "' has boolean value 'null', this is not allowed.");
				noCounter++;
				continue;
			}
			if(entry.getValue().getClass() != Boolean.class)
			{
				getLogger().severe("Flag entry with key '" + entry.getKey() + "' was not of type boolean but: " + entry.getValue().getClass().getName());
				noCounter++;
				continue;
			}
			try
			{
				StateFlag newFlag = new StateFlag(entry.getKey(), (boolean) entry.getValue());
				flagRegistry.register(newFlag);
				yesCounter++;
			}
			catch(FlagConflictException e)
			{
				getLogger().severe("Custom flag '" + entry.getKey() + "' was already defined!");
				noCounter++;
			}
		}
		getLogger().info("Registered " + yesCounter + " flags.");
		if(noCounter != 0)
		{
			getLogger().severe("Failed to register " + noCounter + " flags.");
		}
	}
	
	@Override
	public void onEnable()
	{
	}
	
	@Override
	public void onDisable()
	{
	}
}
