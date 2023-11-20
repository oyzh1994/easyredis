package cn.oyzh.easyredis.exception;

/**
 * @author oyzh
 * @since 2023/08/04
 */
public class ClusterOperationException extends RuntimeException {

    public ClusterOperationException() {
        this("Cluster集群不支持此操作");
    }

    public ClusterOperationException(String msg) {
        super(msg);
    }
}
