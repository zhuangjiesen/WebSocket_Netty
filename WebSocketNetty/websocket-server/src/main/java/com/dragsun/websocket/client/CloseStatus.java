package com.dragsun.websocket.client;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public final class CloseStatus {

    public static final CloseStatus NORMAL = new CloseStatus(1000);
    public static final CloseStatus GOING_AWAY = new CloseStatus(1001);
    public static final CloseStatus PROTOCOL_ERROR = new CloseStatus(1002);
    public static final CloseStatus NOT_ACCEPTABLE = new CloseStatus(1003);
    public static final CloseStatus NO_STATUS_CODE = new CloseStatus(1005);
    public static final CloseStatus NO_CLOSE_FRAME = new CloseStatus(1006);
    public static final CloseStatus BAD_DATA = new CloseStatus(1007);
    public static final CloseStatus POLICY_VIOLATION = new CloseStatus(1008);
    public static final CloseStatus TOO_BIG_TO_PROCESS = new CloseStatus(1009);
    public static final CloseStatus REQUIRED_EXTENSION = new CloseStatus(1010);
    public static final CloseStatus SERVER_ERROR = new CloseStatus(1011);
    public static final CloseStatus SERVICE_RESTARTED = new CloseStatus(1012);
    public static final CloseStatus SERVICE_OVERLOAD = new CloseStatus(1013);
    public static final CloseStatus TLS_HANDSHAKE_FAILURE = new CloseStatus(1015);
    public static final CloseStatus SESSION_NOT_RELIABLE = new CloseStatus(4500);
    private final int code;
    private final String reason;

    public CloseStatus(int code) {
        this(code, (String)null);
    }

    public CloseStatus(int code, String reason) {
        Assert.isTrue(code >= 1000 && code < 5000, "Invalid status code");
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return this.code;
    }

    public String getReason() {
        return this.reason;
    }

    public CloseStatus withReason(String reason) {
        Assert.hasText(reason, "Reason must not be empty");
        return new CloseStatus(this.code, reason);
    }

    public boolean equalsCode(CloseStatus other) {
        return this.code == other.code;
    }

    public boolean equals(Object other) {
        if(this == other) {
            return true;
        } else if(!(other instanceof CloseStatus)) {
            return false;
        } else {
            CloseStatus otherStatus = (CloseStatus)other;
            return this.code == otherStatus.code && ObjectUtils.nullSafeEquals(this.reason, otherStatus.reason);
        }
    }

    public int hashCode() {
        return this.code * 29 + ObjectUtils.nullSafeHashCode(this.reason);
    }

    public String toString() {
        return "CloseStatus[code=" + this.code + ", reason=" + this.reason + "]";
    }


}
