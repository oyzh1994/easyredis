package cn.oyzh.easyredis.util;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.dto.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageWrapper;
import lombok.experimental.UtilityClass;

/**
 * redis连接工具类
 *
 * @author oyzh
 * @since 2023/07/01
 */
//@Slf4j
@UtilityClass
public class RedisConnectUtil {

    /**
     * 测试连接
     *
     * @param view     页面
     * @param host     地址
     * @param password 密码
     * @param timeout  超时时间
     */
    public static void testConnect(StageWrapper view, String host, String password, int timeout) {
        ThreadUtil.startVirtual(() -> {
            try {
                view.disable();
                view.waitCursor();
                view.appendTitle("==连接测试中...");
                // 创建redis信息
                RedisInfo redisInfo = new RedisInfo();
                redisInfo.setHost(host);
                redisInfo.setPassword(password);
                redisInfo.setConnectTimeOut(timeout);
                redisInfo.setExecuteTimeOut(timeout);
                RedisClient client = new RedisClient(redisInfo);
                // 开始连接
                client.start();
                if (client.isConnected()) {
                    client.close();
                    MessageBox.okToast("连接成功！");
                } else {
                    MessageBox.warn("连接失败，请检查地址是否有效！");
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
     * 关闭连接
     *
     * @param client redis客户端
     * @param async  是否异步
     */
    public static void close(RedisClient client, boolean async) {
        try {
            if (client != null && client.isConnected()) {
                Runnable func = client::close;
                if (async) {
                    ThreadUtil.startVirtual(func);
                } else {
                    func.run();
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
    public static RedisConnect parse(String input) {
        if (input == null) {
            return null;
        }
        try {
            String[] words = input.split(" ");
            RedisConnect connect = new RedisConnect();
            int type = -1;
            for (String word : words) {
                if (type == 0) {
                    connect.setHost(word.trim());
                } else if (type == 1) {
                    connect.setPort(Integer.parseInt(word.trim()));
                } else if (type == 2) {
                    connect.setPassword(word.trim());
                } else if (type == 3) {
                    connect.setDb(Integer.parseInt(word.trim()));
                }
                if (word.equalsIgnoreCase("-h")) {
                    type = 0;
                } else if (word.equalsIgnoreCase("-p")) {
                    type = 1;
                } else if (word.equalsIgnoreCase("-a")) {
                    type = 2;
                } else if (word.equalsIgnoreCase("-n")) {
                    type = 3;
                } else {
                    type = -1;
                }
            }
            return connect;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
