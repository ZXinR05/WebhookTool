package xin.xinx.webhookTool.listener;

import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import xin.xinx.webhookTool.WebhookTool;
import xin.xinx.webhookTool.utils.WebHookSender;

import java.util.logging.Logger;

public class ServerListener implements Listener {
    private final WebhookTool plugin;
    private final Logger logger;
    public ServerListener(WebhookTool plugin) {
        this.plugin = plugin;
        this.logger = plugin.logger;
    }
    @EventHandler
    public void onServerStart(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD)
            return;
        String eventName = "server_start";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("message", "服务器已启动");

        plugin.debug(eventName);
        WebHookSender.sendAll(jsonObject);
    }

    public static void onServerStop() {
        String eventName = "server_stop";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", eventName);
        jsonObject.addProperty("message", "服务器已关闭");

        WebhookTool.getInstance().debug(eventName);
        WebHookSender.sendAll(jsonObject);
    }
}
