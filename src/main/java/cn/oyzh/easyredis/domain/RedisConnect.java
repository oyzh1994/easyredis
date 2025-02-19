package cn.oyzh.easyredis.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.BooleanUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/6/16
 */
@Setter
@Table("t_connect")
public class RedisConnect implements Comparable<RedisConnect>, ObjectComparator<RedisConnect>, Serializable {

    /**
     * 数据id
     */
    @Getter
    @Column
    @PrimaryKey
    private String id;

    /**
     * 连接地址
     */
    @Getter
    @Column
    private String host;

    /**
     * 名称
     */
    @Getter
    @Column
    private String name;

    /**
     * 备注信息
     */
    @Getter
    @Column
    private String remark;

    /**
     * 分组id
     */
    @Getter
    @Column
    private String groupId;

    /**
     * 认证用户
     */
    @Getter
    @Column
    private String user;

    /**
     * 认证密码
     */
    @Getter
    @Column
    private String password;

    /**
     * 只读模式
     */
    @Setter
    @Column
    private Boolean readonly;

    /**
     * 收藏的键
     */
    @Getter
    private List<String> collects;

    /**
     * 过滤列表
     */
    @Getter
    private List<RedisFilter> filters;

    /**
     * 连接超时时间
     */
    @Column
    private Integer connectTimeOut;

    /**
     * 执行超时时间
     */
    @Column
    private Integer executeTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Getter
    @Column
    private Boolean sshForward;

    /**
     * ssh信息
     */
    @Getter
    private RedisSSHConfig sshConfig;

    /**
     * 复制对象
     *
     * @param redisConnect redis连接
     * @return 当前对象
     */
    public RedisConnect copy(@NonNull RedisConnect redisConnect) {
//        this.id = redisConnect.id;
        this.name = redisConnect.name;
        this.host = redisConnect.host;
        this.user = redisConnect.user;
        this.remark = redisConnect.remark;
        this.groupId = redisConnect.groupId;
        this.readonly = redisConnect.readonly;
        this.password = redisConnect.password;
        this.connectTimeOut = redisConnect.connectTimeOut;
        // ssh配置
        this.sshConfig = redisConnect.sshConfig;
        this.sshForward = redisConnect.sshForward;
        // 过滤
        this.filters = redisConnect.filters;
        // 收藏
        this.collects = redisConnect.collects;
        return this;
    }

    // /**
    //  * 是否被收藏
    //  *
    //  * @param dbIndex 数据库索引
    //  * @param key     键
    //  * @return 结果
    //  */
    // public boolean isCollect(int dbIndex, @NonNull String key) {
    //     return CollectionUtil.isNotEmpty(this.collects) && this.collects.contains(this.getCollectName(dbIndex, key));
    // }

    // /**
    //  * 添加收藏
    //  *
    //  * @param dbIndex 数据库索引
    //  * @param key     键
    //  */
    // public void addCollect(int dbIndex, @NonNull String key) {
    //     if (this.collects == null) {
    //         this.collects = new ArrayList<>();
    //     }
    //     String name = this.getCollectName(dbIndex, key);
    //     if (!this.collects.contains(name)) {
    //         this.collects.add(name);
    //     }
    // }
    //
    // /**
    //  * 取消收藏
    //  *
    //  * @param dbIndex 数据库索引
    //  * @param key     键
    //  * @return 结果
    //  */
    // public boolean removeCollect(int dbIndex, @NonNull String key) {
    //     if (this.collects != null) {
    //         return this.collects.remove(this.getCollectName(dbIndex, key));
    //     }
    //     return false;
    // }

    // /**
    //  * 获取收藏名称
    //  *
    //  * @param dbIndex db索引
    //  * @param key     键名称
    //  * @return 收藏名称
    //  */
    // private String getCollectName(int dbIndex, String key) {
    //     return dbIndex + "_@coll@_" + key;
    // }

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
    public int compareTo(RedisConnect o) {
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
        if (StringUtil.isBlank(this.host)) {
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
        if (StringUtil.isBlank(this.host)) {
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
    public boolean compare(RedisConnect t1) {
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
        if (StringUtil.isNotBlank(this.user) && StringUtil.isNotBlank(this.password)) {
            return 2;
        }
        if (StringUtil.isNotBlank(this.password)) {
            return 1;
        }
        return 0;
    }
}
