package xin.xinx.webhookTool;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xin.xinx.webhookTool.listener.PlayerListener;
import xin.xinx.webhookTool.listener.ServerListener;
import xin.xinx.webhookTool.utils.TranslationTool;
import xin.xinx.webhookTool.utils.WebHookSender;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class WebhookTool extends JavaPlugin {
    private static WebhookTool instance;
    public boolean DEBUG;
    public Map<String, Boolean> webhookMap = new HashMap<>();
    public List<String> urls;
    public Logger logger = getLogger();
    public String webhookToken;
    @Override
    public void onEnable() {
        // Plugin startup login
        initialize();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ServerListener.onServerStop();
    }

    public static WebhookTool getInstance() {
        return instance;
    }

    private void initialize() {
        instance = this;
        saveDefaultConfig();
        DEBUG = getConfig().getBoolean("debug", false);
        ConfigurationSection section = getConfig().getConfigurationSection("webhooks");
        if (section != null) {
            for(String key : section.getKeys(false)) {
                boolean enabled = section.getBoolean(key);
                webhookMap.put(key, enabled);
            }
        }
        urls = getConfig().getStringList("urls");
        webhookToken = getConfig().getString("webhook_token");

        final TranslationStore<MessageFormat> translationStore = TranslationStore.messageFormat(Key.key("webhooktool:translation"));
        try {
            InputStream inputStream = getResource("zh_cn.json");
            if (inputStream == null) {
                logger.warning("未能成功加载zh_cn.json");
                return;
            }
            Map<String, String> translations = TranslationTool.loadTranslations(inputStream);
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                String key = entry.getKey();
                String value = TranslationTool.toMessageFormat(entry.getValue());
                translationStore.register(key, Locale.SIMPLIFIED_CHINESE, new MessageFormat(value, Locale.SIMPLIFIED_CHINESE));
            }
            GlobalTranslator.translator().addSource(translationStore);
        } catch (IOException e) {
            logger.warning("未能成功加载zh_cn.json");
            return;
        }
        logger.info("初始化完成!");
    }

    public void debug(String message) {
        if(DEBUG)
            logger.info("[DEBUG]" + message);
    }
}
