package xin.xinx.webhookTool.listener;

import com.google.gson.JsonObject;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.slf4j.LoggerFactory;
import xin.xinx.webhookTool.WebhookTool;
import xin.xinx.webhookTool.utils.TranslationTool;
import xin.xinx.webhookTool.utils.WebHookSender;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Logger;


public class PlayerListener implements Listener {
    private final WebhookTool plugin;
    private final Logger logger;
    public PlayerListener(WebhookTool plugin) {
        this.plugin = plugin;
        this.logger = plugin.logger;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String eventName = "player_join";
        Player player = event.getPlayer();
        String playerName = player.getName();
        Component component = event.joinMessage();
        String message = TranslationTool.serialize(component);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("player", playerName);
        jsonObject.addProperty("message", message);

        WebHookSender.sendAll(jsonObject);
        plugin.debug(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String eventName = "player_quit";
        Player player = event.getPlayer();
        String playerName = player.getName();
        Component component = event.quitMessage();
        String message = TranslationTool.serialize(component);

        long lastLogin = player.getLastLogin();
        long now = Instant.now().toEpochMilli();
        long duration = now - lastLogin;
        long hour = duration / 1000 / 3600;
        long min = duration / 1000 % 3600 / 60;
        long sec = duration / 1000 % 60;
        String formattedDuration = String.format("%d时%d分%d秒", hour, min, sec);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("player", playerName);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("duration", formattedDuration);
        logger.info("在线时长 " + formattedDuration);

        WebHookSender.sendAll(jsonObject);
        plugin.debug(message + formattedDuration);
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        String eventName = "player_dead";
        Player player = event.getPlayer();
        String playerName = player.getName();
        Component component = event.deathMessage();
        String message = TranslationTool.serialize(component);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("player", playerName);
        jsonObject.addProperty("message", message);

        WebHookSender.sendAll(jsonObject);
        plugin.debug(message);
    }

    @EventHandler
    public  void onPlayerChat(AsyncChatEvent event) {
        String eventName = "player_chat";
        Player player = event.getPlayer();
        String playerName = player.getName();
        Component component = event.message();
        String message = TranslationTool.serialize(component);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("player", playerName);
        jsonObject.addProperty("message", message);

        WebHookSender.sendAll(jsonObject);
        plugin.debug("<" + playerName + "> " + message);
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        String eventName = "player_advancement";
        Player player = event.getPlayer();
        String playerName = player.getName();
        Component component = event.message();
        if (component == null) {
            return;
        }
        String message = TranslationTool.serialize(component);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("player", playerName);
        jsonObject.addProperty("message", message);
        Bukkit.getConsoleSender().sendMessage(component);

        WebHookSender.sendAll(jsonObject);
        plugin.debug(message);
    }
}
