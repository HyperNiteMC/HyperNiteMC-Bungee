package com.ericlam.mc.bungee.hnmc.commands.hnmcbs.versions;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.exceptions.PluginNotFoundException;
import com.ericlam.mc.bungee.hnmc.exceptions.ResourceNotFoundException;
import com.ericlam.mc.bungee.hnmc.main.HNBungeeConfig;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.bungee.hnmc.managers.ResourceManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VersionCommandNode extends CommandNode {

    private static final List<String> plugins = ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(p -> p.getDescription().getName()).collect(Collectors.toList());
    protected static final HNBungeeConfig config = HyperNiteMC.getHnBungeeConfig();

    public VersionCommandNode(CommandNode parent, String command, String permission, String description, String placeholder, String... alias) {
        super(parent, command, permission, description, placeholder, alias);
    }

    @Override
    public void executeCommand(CommandSender sender, List<String> args) {
        var plugin = args.get(0);
        ResourceManager manager;
        if (config.getVersionChecker().resourceId_to_checks.containsKey(plugin)) {
            manager = HyperNiteMC.getAPI().getResourceManager(ResourceManager.Type.SPIGOT);
        } else {
            manager = HyperNiteMC.getAPI().getResourceManager(ResourceManager.Type.HYPERNITE);
        }
        try {
            Plugin resource = ProxyServer.getInstance().getPluginManager().getPlugin(plugin);
            if (resource == null) throw new PluginNotFoundException(plugin);
            String currentVersion = resource.getDescription().getVersion();
            this.executeChecker(sender, manager, plugin, currentVersion);
        } catch (PluginNotFoundException e) {
            MessageBuilder.sendMessage(sender, config.getPrefix() + "??c?????????????????????");
        } catch (ResourceNotFoundException e) {
            MessageBuilder.sendMessage(sender, config.getPrefix() + "??c????????????????????????????????????");
        }
    }

    public abstract void executeChecker(CommandSender sender, ResourceManager manager, String plugin, String version) throws ResourceNotFoundException, PluginNotFoundException;

    @Override
    public List<String> executeTabCompletion(CommandSender sender, List<String> args) {
        if (this.getPlaceholder() == null) return null;
        List<Integer> pluginTab = new LinkedList<>();
        String[] papis = this.getPlaceholder().split(" ");
        for (int i = 0; i < papis.length; i++) {
            if (papis[i].contains("plugin")) {
                pluginTab.add(i);
            }
        }
        return pluginTab.contains(args.size() - 1) ? plugins : null;
    }
}
