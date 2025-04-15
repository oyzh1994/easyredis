package cn.oyzh.easyredis.test;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.ssh.domain.SSHConnect;
import cn.oyzh.ssh.domain.SSHJumpConfig;
import cn.oyzh.ssh.jump.SSHJumper;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2023/12/14
 */
public class SSHTest {

    /**
     * @param localPort   本地host 建议mysql 3306 redis 6379
     * @param sshHost     ssh host
     * @param sshPort     ssh port
     * @param sshUserName ssh 用户名
     * @param sshPassWord ssh密码
     * @param remotoHost  远程机器地址
     * @param remotoPort  远程机器端口
     */
    public static void goSSH(int localPort, String sshHost, int sshPort,
                             String sshUserName, String sshPassWord,
                             String remotoHost, int remotoPort) {
        try {
            JSch jsch = new JSch();
            // 登陆跳板机
            Session session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setPassword(sshPassWord);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            // 通过ssh连接到mysql机器
            int assinged_port = session.setPortForwardingL(localPort, remotoHost, remotoPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test() {
        goSSH(26371, "192.168.189.130", 22, "root", "123456", "192.168.189.134", 6379);
        RedisConnect info = new RedisConnect();
        info.setHost("localhost:26371");
        info.setConnectTimeOut(3000);
        info.setExecuteTimeOut(3000);
        RedisClient client = new RedisClient(info);
        client.start();
        System.out.println(client.set(0, "key1", "val1"));
        System.out.println(client.get(0, "key1"));

    }

    @Test
    public void test1() {
        SSHConnect connectInfo = new SSHConnect();
        connectInfo.setHost("192.168.189.130");
        connectInfo.setUser("root");
        connectInfo.setPassword("123456");

        SSHJumpConfig forwardInfo = new SSHJumpConfig();
        forwardInfo.setRemotePort(6379);
        forwardInfo.setRemoteHost("192.168.189.134");

        SSHJumper forwarder = new SSHJumper();
        int localPort = forwarder.forward(null, null);

        RedisConnect info = new RedisConnect();
        info.setHost("127.0.0.1:" + localPort);
        info.setConnectTimeOut(3000);
        info.setExecuteTimeOut(3000);
        RedisClient client = new RedisClient(info);
        client.start();
        System.out.println(client.set(0, "key1", "val1"));
        System.out.println(client.get(0, "key1"));

    }
}
