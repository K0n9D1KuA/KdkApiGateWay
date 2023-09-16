package com.qyh.gateway.core.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 封装了jedis客户端，主要执行限流Lua脚本
 * @email 3161788646@qq.com
 * @date 2023/9/4 21:35
 */

@Slf4j
public class JedisUtil {
    private JedisPool jedisPool = new JedisPool();

    public Object executeScript(String key, int limit, int expire) {
        Jedis jedis = jedisPool.getJedis();
        String lua = buildLuaScript();
        String scriptLoad = jedis.scriptLoad(lua);
        try {
            Object result = jedis.evalsha(scriptLoad, Arrays.asList(key), Arrays.asList(String.valueOf(expire), String.valueOf(limit)));
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * @description: 这段Lua脚本的主要功能是用于实现一个简单的计数器功能，并控制计数器的最大值和过期时间。
     * KEY[1] 是限流的主体
     * ARGV[1] 是 n秒 内允许 m个请求 中的 n
     * ARGV[2] 是 n秒 内允许 m个请求 中的 m
     * - `local num = redis.call('incr', KEYS[1])`：使用Redis的`INCR`命令对指定的键执行自增操作，将自增后的结果赋值给变量`num`。
     * - `if tonumber(num) == 1 then`：如果`num`的值等于1，表示计数器是第一次被增加，执行以下操作：
     * - `redis.call('expire', KEYS[1], ARGV[1])`：使用Redis的`EXPIRE`命令设置键的过期时间为`ARGV[1]`，单位为秒。
     * - `return 1`：返回结果为1，表示计数器已经被增加。
     * - `elseif tonumber(num) > tonumber(ARGV[2]) then`：如果`num`的值大于`ARGV[2]`，表示计数器已经达到最大值，执行以下操作：
     * - `return 0`：返回结果为0，表示计数器已经达到最大值，无法再增加。
     * - `else`：在其他情况下，执行以下操作：
     * - `return 1`：返回结果为1，表示计数器可以继续增加。
     * 总的来说，这段Lua脚本用于实现一个计数器，当计数器第一次增加时，设置键的过期时间；当计数器达到最大值时，阻止继续增加；否则，允许继续增加。
     * @email 3161788646@qq.com
     * @author K0n9D1KuA
     * @date 2023/9/4 21:37
     * @version 1.0
     */

    private static String buildLuaScript() {
        String lua = "local num = redis.call('incr', KEYS[1])\n" +
                "if tonumber(num) == 1 then\n" +
                "\tredis.call('expire', KEYS[1], ARGV[1])\n" +
                "\treturn 1\n" +
                "elseif tonumber(num) > tonumber(ARGV[2]) then\n" +
                "\treturn 0\n" +
                "else \n" +
                "\treturn 1\n" +
                "end\n";
        return lua;
    }


}
