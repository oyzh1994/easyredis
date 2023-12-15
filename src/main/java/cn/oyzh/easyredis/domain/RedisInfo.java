package cn.oyzh.easyredis.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.ssh.SSHConnectInfo;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisInfo implements Comparable<RedisInfo>, ObjectComparator<RedisInfo> {

    /**
     * 数据id
     */
    @Getter
    @Setter
    private String id;

    /**
     * 连接地址
     */
    @Getter
    @Setter
    private String host;

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 备注信息
     */
    @Getter
    @Setter
    private String remark;

    /**
     * 分组id
     */
    @Getter
    @Setter
    private String groupId;

    /**
     * 认证用户
     */
    @Getter
    @Setter
    private String user;

    /**
     * 认证密码
     */
    @Getter
    @Setter
    private String password;

    /**
     * master用户名
     */
    @Getter
    @Setter
    private String masterUser;

    /**
     * master密码
     */
    @Getter
    @Setter
    private String masterPassword;

    /**
     * 是否重定向到master
     */
    @Setter
    @Getter
    private Boolean redirectMaster;

    /**
     * 只读模式
     */
    @Setter
    @Getter
    private Boolean readonly;

    /**
     * 收藏的键
     */
    @Getter
    @Setter
    private List<String> collects;

    /**
     * 连接超时时间
     */
    @Setter
    private Integer connectTimeOut;

    /**
     * 执行超时时间
     */
    @Setter
    private Integer executeTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Setter
    @Getter
    private Boolean sshForward;

    /**
     * ssh信息
     */
    @Setter
    @Getter
    private SSHConnectInfo sshInfo;

    /**
     * 复制对象
     *
     * @param info redis信息
     * @return 当前对象
     */
    public RedisInfo copy(@NonNull RedisInfo info) {
        this.id = info.id;
        this.name = info.name;
        this.host = info.host;
        this.user = info.user;
        this.remark = info.remark;
        this.groupId = info.groupId;
        this.sshInfo = info.sshInfo;
        this.readonly = info.readonly;
        this.collects = info.collects;
        this.password = info.password;
        this.sshForward = info.sshForward;
        this.masterUser = info.masterUser;
        this.redirectMaster = info.redirectMaster;
        this.masterPassword = info.masterPassword;
        this.connectTimeOut = info.connectTimeOut;
        return this;
    }

    /**
     * 是否被收藏
     *
     * @param dbIndex 数据库索引
     * @param key     键
     * @return 结果
     */
    public boolean isCollect(int dbIndex, @NonNull String key) {
        return CollUtil.isNotEmpty(this.collects) && this.collects.contains(this.getCollectName(dbIndex, key));
    }

    /**
     * 添加收藏
     *
     * @param dbIndex 数据库索引
     * @param key     键
     */
    public void addCollect(int dbIndex, @NonNull String key) {
        if (this.collects == null) {
            this.collects = new ArrayList<>();
        }
        String name = this.getCollectName(dbIndex, key);
        if (!this.collects.contains(name)) {
            this.collects.add(name);
        }
    }

    /**
     * 取消收藏
     *
     * @param dbIndex 数据库索引
     * @param key     键
     * @return 结果
     */
    public boolean removeCollect(int dbIndex, @NonNull String key) {
        if (this.collects != null) {
            return this.collects.remove(this.getCollectName(dbIndex, key));
        }
        return false;
    }

    /**
     * 获取收藏名称
     *
     * @param dbIndex db索引
     * @param key     键名称
     * @return 收藏名称
     */
    private String getCollectName(int dbIndex, String key) {
        return dbIndex + "_@coll@_" + key;
    }

    /**
     * 是否重定向到master
     *
     * @return 结果
     */
    public boolean isRedirectMaster() {
        return BooleanUtil.isTrue(this.redirectMaster);
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return BooleanUtil.isTrue(this.readonly);
    }

    /**
     * 是否ssh转发
     *
     * @return 结果
     */
    public boolean isSSHForward() {
        return BooleanUtil.isTrue(this.sshForward);
    }

    /**
     * 获取连接超时
     *
     * @return 连接超时
     */
    public Integer getConnectTimeOut() {
        return this.connectTimeOut == null || this.connectTimeOut < 3 ? 5 : this.connectTimeOut;
    }

    /**
     * 获取连接超时毫秒值
     *
     * @return 连接超时毫秒值
     */
    public int connectTimeOutMs() {
        return this.getConnectTimeOut() * 1000;
    }

    /**
     * 获取执行超时
     *
     * @return 执行超时
     */
    public Integer getExecuteTimeOut() {
        return this.executeTimeOut == null || this.executeTimeOut < 3 ? 5 : this.executeTimeOut;
    }

    /**
     * 获取执行超时毫秒值
     *
     * @return 执行超时毫秒值
     */
    public int executeTimeOutMs() {
        return this.getExecuteTimeOut() * 1000;
    }

    @Override
    public int compareTo(RedisInfo o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(o.getName());
    }

    /**
     * 获取连接ip
     *
     * @return 连接ip
     */
    public String hostIp() {
        if (StrUtil.isBlank(this.host)) {
            return "";
        }
        return this.host.split(":")[0];
    }

    /**
     * 获取连接端口
     *
     * @return 连接端口
     */
    public int hostPort() {
        if (StrUtil.isBlank(this.host)) {
            return -1;
        }
        try {
            return Integer.parseInt(this.host.split(":")[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean compare(RedisInfo t1) {
        if (Objects.equals(this, t1)) {
            return true;
        }
        return Objects.equals(t1.name, this.name);
    }

    /**
     * 获取认证方式
     *
     * @return 0:无需认证 1:密码认证 2:用户密码认证
     */
    public int getAuthType() {
        if (StrUtil.isNotBlank(this.user) && StrUtil.isNotBlank(this.password)) {
            return 2;
        }
        if (StrUtil.isNotBlank(this.password)) {
            return 1;
        }
        return 0;
    }
}
