package cn.oyzh.easyredis.util;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.dto.RedisConnectInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.i18n.I18nHelper;
import lombok.experimental.UtilityClass;

/**
 * redis连接工具类
 *
 * @author oyzh
 * @since 2023/07/01
 */
@UtilityClass
public class RedisConnectUtil {

    /**
     * 测试连接
     *
     * @param view 页面
     * @param info redis信息
     */
    public static void testConnect(StageAdapter view, RedisConnect info) {
        ThreadUtil.startVirtual(() -> {
            try {
                view.disable();
                view.waitCursor();
                view.appendTitle("===" + I18nHelper.connectTesting() + "===");
                if (info.getName() == null) {
                    info.setName(I18nHelper.testConnection());
                }
                RedisClient client = new RedisClient(info);
                // 开始连接
                client.start(3_000);
                view.enable();
                view.defaultCursor();
                view.restoreTitle();
                if (client.isConnected()) {
                    client.close();
                    MessageBox.okToast(I18nHelper.connectSuccess());
                } else {
                    MessageBox.warn(I18nHelper.connectFail());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                view.enable();
                view.defaultCursor();
                view.restoreTitle();
            }
        });
    }

    /**
     * 关闭客户端
     *
     * @param client redis客户端
     * @param async  是否异步
     */
    public static void close(RedisClient client, boolean async) {
        try {
            if (client != null && client.isConnected()) {
                if (async) {
                    ThreadUtil.startVirtual(client::close);
                } else {
                    client.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 解析连接
     *
     * @param input 输入内容
     * @return 连接
     */
    public static RedisConnectInfo parse(String input) {
        if (input == null) {
            return null;
        }
        try {
            String[] words = input.split(" ");
            RedisConnectInfo connect = new RedisConnectInfo();
            connect.setInput(input);
            int type = -1;
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (word.equalsIgnoreCase("-h")) {
                    type = 0;
                } else if (word.equalsIgnoreCase("-p")) {
                    type = 1;
                } else if (word.equalsIgnoreCase("-a")) {
                    type = 2;
                } else if (word.equalsIgnoreCase("-n")) {
                    type = 3;
                } else if (word.equalsIgnoreCase("-r")) {
                    type = 4;
                } else if (word.equalsIgnoreCase("-u")) {
                    type = 5;
                } else if (word.equalsIgnoreCase("-timeout")) {
                    type = 6;
                } else {
                    type = -1;
                }
                if (type == 0) {
                    connect.setHost(words[i + 1].trim());
                } else if (type == 1) {
                    connect.setPort(Integer.parseInt(words[i + 1].trim()));
                } else if (type == 2) {
                    connect.setPassword(words[i + 1].trim());
                } else if (type == 3) {
                    connect.setDb(Integer.parseInt(words[i + 1].trim()));
                } else if (type == 4) {
                    connect.setReadonly(true);
                } else if (type == 5) {
                    connect.setUser(words[i + 1].trim());
                } else if (type == 6) {
                    connect.setTimeout(Integer.parseInt(words[i + 1].trim()) / 1000);
                }
            }
            return connect;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 复制连接
     *
     * @param connectInfo 连接信息
     * @param info        redis对象
     */
    public static void copyConnect(RedisConnectInfo connectInfo, RedisConnect info) {
        if (connectInfo != null && info != null) {
            info.setUser(connectInfo.getUser());
            info.setReadonly(connectInfo.isReadonly());
            info.setPassword(connectInfo.getPassword());
            info.setConnectTimeOut(connectInfo.getTimeout());
            info.setExecuteTimeOut(connectInfo.getTimeout());
            info.setHost(connectInfo.getHost() + ":" + connectInfo.getPort());
        }
    }
}
