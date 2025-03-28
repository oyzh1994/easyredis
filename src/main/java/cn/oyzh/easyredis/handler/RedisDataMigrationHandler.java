package cn.oyzh.easyredis.handler;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisKeyFilterHistory;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.easyredis.store.RedisKeyFilterHistoryStore;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.store.RedisStoreUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalHistory;
import cn.oyzh.easyredis.terminal.RedisTerminalHistoryStore;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/10/15
 */
public class RedisDataMigrationHandler extends DataHandler {

    private boolean groups;

    private boolean filters;

    private boolean keyFilters;

    private boolean connections;

    private boolean terminalHistory;

    private boolean applicationSetting;

    /**
     * 1: 合并 2: 覆盖
     */
    private String dataPolicy;

    private final RedisConnectStore infoStore = RedisConnectStore.INSTANCE;

    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    private final RedisSettingStore settingStore = RedisSettingStore.INSTANCE;

    private final RedisTerminalHistoryStore terminalHistoryStore = RedisTerminalHistoryStore.INSTANCE;

    public boolean isGroups() {
        return groups;
    }

    public void setGroups(boolean groups) {
        this.groups = groups;
    }

    public boolean isFilters() {
        return filters;
    }

    public void setFilters(boolean filters) {
        this.filters = filters;
    }

    public boolean isKeyFilters() {
        return keyFilters;
    }

    public void setKeyFilters(boolean keyFilters) {
        this.keyFilters = keyFilters;
    }

    public boolean isConnections() {
        return connections;
    }

    public void setConnections(boolean connections) {
        this.connections = connections;
    }

    public boolean isTerminalHistory() {
        return terminalHistory;
    }

    public void setTerminalHistory(boolean terminalHistory) {
        this.terminalHistory = terminalHistory;
    }

    public boolean isApplicationSetting() {
        return applicationSetting;
    }

    public void setApplicationSetting(boolean applicationSetting) {
        this.applicationSetting = applicationSetting;
    }

    public String getDataPolicy() {
        return dataPolicy;
    }

    public void setDataPolicy(String dataPolicy) {
        this.dataPolicy = dataPolicy;
    }

    private final RedisKeyFilterHistoryStore keyFilterHistoryJdbcStore = RedisKeyFilterHistoryStore.INSTANCE;

    /**
     * 执行传输
     */
    public void doMigration() {
        this.message("Migration Starting");

        if (this.groups) {
            this.message(I18nHelper.migratingGroups());
            List<RedisGroup> groups = RedisStoreUtil.loadGroups();
            this.message(I18nHelper.foundGroup() + " : " + groups.size());
            if ("1".equals(this.dataPolicy)) {
                for (RedisGroup group : groups) {
                    if (!this.groupStore.exist(group.getName())) {
                        this.groupStore.replace(group);
                        this.message(I18nHelper.group() + " : " + group.getName() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.groupStore.clear();
                this.message(I18nHelper.groupDataCleared());
                for (RedisGroup group : groups) {
                    this.groupStore.replace(group);
                }
                this.processedIncr(groups.size());
            }
            this.message(I18nHelper.migrationGroupsSuccessful());
        }

        if (this.connections) {
            this.message(I18nHelper.migratingConnections());
            List<RedisConnect> connects = RedisStoreUtil.loadConnects();
            this.message(I18nHelper.foundConnection() + " : " + connects.size());
            if ("1".equals(this.dataPolicy)) {
                for (RedisConnect connect : connects) {
                    if (!this.infoStore.exist(connect.getId())) {
                        this.infoStore.replace(connect);
                        this.message(I18nHelper.connect() + " : " + connect.getName() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.infoStore.clear();
                this.message(I18nHelper.connectionDataCleared());
                for (RedisConnect connect : connects) {
                    this.infoStore.replace(connect);
                }
                this.processedIncr(connects.size());
            }
            this.message(I18nHelper.migrationConnectionsSuccessful());
        }

        if (this.filters) {
            this.message(I18nHelper.migratingFilters());
            List<RedisFilter> filters = RedisStoreUtil.loadFilters();
            this.message(I18nHelper.foundFilter() + " : " + filters.size());
            if ("1".equals(this.dataPolicy)) {
                for (RedisFilter filter : filters) {
                    if (!this.filterStore.exist(filter.getKw())) {
                        this.filterStore.replace(filter);
                        this.message(I18nHelper.filter() + " : " + filter.getKw() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.filterStore.clear();
                this.message(I18nHelper.filterDataCleared());
                for (RedisFilter filter : filters) {
                    this.filterStore.replace(filter);
                }
                this.processedIncr(filters.size());
            }
            this.message(I18nHelper.migrationFiltersSuccessful());
        }

        if (this.keyFilters) {
            this.message(I18nHelper.migratingAuth());
            List<RedisKeyFilterHistory> histories = RedisStoreUtil.loadKeyFilterHistory();
            this.message(I18nHelper.foundAuth() + " : " + histories.size());
            if ("1".equals(this.dataPolicy)) {
                for (RedisKeyFilterHistory history : histories) {
                    if (!this.keyFilterHistoryJdbcStore.exist(history.getPattern())) {
                        this.keyFilterHistoryJdbcStore.replace(history);
                        this.message(I18nHelper.auth() + " : " + history.getPattern() + "] " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.keyFilterHistoryJdbcStore.clear();
                this.message(I18nHelper.authDataCleared());
                for (RedisKeyFilterHistory history : histories) {
                    this.keyFilterHistoryJdbcStore.replace(history);
                }
                this.processedIncr(histories.size());
            }
            this.message(I18nHelper.migrationAuthSuccessful());
        }

        if (this.terminalHistory) {
            this.message(I18nHelper.migratingTerminalHistory());
            List<RedisTerminalHistory> terminalHistories = RedisStoreUtil.loadTerminalHistory();
            this.message(I18nHelper.foundTerminalHistory() + " : " + terminalHistories.size());
            // 清理终端
            if ("2".equals(this.dataPolicy)) {
                this.terminalHistoryStore.clear();
            }
            this.message(I18nHelper.terminalHistoryDataCleared());
            for (RedisTerminalHistory history : terminalHistories) {
                this.terminalHistoryStore.replace(history);
            }
            this.processedIncr(terminalHistories.size());
            this.message(I18nHelper.migrationTerminalHistorySuccessful());
        }

        if (this.applicationSetting) {
            this.message(I18nHelper.migratingApplicationSetting());
            RedisSetting setting = RedisStoreUtil.loadSetting();
            // 清理设置
            if ("2".equals(this.dataPolicy)) {
                this.settingStore.clear();
                this.message(I18nHelper.applicationSettingDataCleared());
            }
            this.settingStore.replace(setting);
            this.processedIncr();
            this.message(I18nHelper.migrationApplicationSettingSuccessful());
        }

        this.message("Migration Finished");
        this.message(RedisI18nHelper.migrationTip1());
    }
}

