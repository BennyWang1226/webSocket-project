package com.ws.benny.websocket.util;

import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangbinbin
 * @create 2023/3/13 9:50
 */
@Deprecated
public class RedisMachineCodeManager {
    private Jedis jedis;
    private String prefix;

    public RedisMachineCodeManager(String host, int port, String prefix) {
        this.jedis = new Jedis(host, port);
        this.prefix = prefix;
    }

    public void saveMachineCode(String userId, String machineCode) {
        String key = prefix + ":" + machineCode;
        jedis.set(key, userId);
    }

    public List<String> getOnlineUsers(String machineCode) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : jedis.hgetAll(prefix).entrySet()) {
            if (entry.getValue().equals(machineCode)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void removeMachineCode(String machineCode) {
        String key = prefix + ":" + machineCode;
        jedis.del(key);
    }

    public static String getMachineCode() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        byte[] hardwareAddress = NetworkInterface.getByInetAddress(address).getHardwareAddress();
        StringBuilder builder = new StringBuilder();
        for (byte b : hardwareAddress) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
