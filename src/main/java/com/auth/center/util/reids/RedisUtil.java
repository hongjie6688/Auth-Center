package com.auth.center.util.reids;

import com.alibaba.fastjson.JSON;
import com.auth.center.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * redis操作类
 *
 * @author zhnaghongjie
 */
public class RedisUtil {


    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 插入数据到HASH表中
     *
     * @param tableName hash表名
     * @param key       字段名
     * @param object    存入的对象
     * @return
     */
    public static boolean hput(String tableName, String key, Object object) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.hset(tableName.getBytes(), key.getBytes(), SerializeUtil.serialize(object));
        } catch (Exception e) {
            log.error(tableName + "哈希表put操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return false;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return true;
    }

    /**
     * 获取HASH表中指定key的数据
     *
     * @param tableName HASH表名
     * @param key       要查看的字段
     */
    public static Object hget(String tableName, String key) {

        Jedis jedis = null;
        Object obj = null;
        try {
            jedis = RedisSource.getConnection();
            obj = SerializeUtil.unserialize(jedis.hget(tableName.getBytes(), key.getBytes()));
        } catch (Exception e) {
            log.error(tableName + "哈希表get操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return obj;
    }

    /**
     * 删除HASH表中指定key的数据
     *
     * @param tableName HASH表名
     * @param key       要删除的字段
     */
    public static void hdel(String tableName, String key) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.hdel(tableName.getBytes(), key.getBytes());
        } catch (Exception e) {
            log.error(tableName + "哈希表del操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
    }

    /**
     * 根据关键词得到Hash的内容
     *
     * @param tableName hash表名
     * @return
     */
    public static Map<String, Object> hgetAll(String tableName) {

        Jedis jedis = null;
        Map<String, Object> map = new Hashtable<String, Object>();
        try {
            jedis = RedisSource.getConnection();

            Map<byte[], byte[]> map1 = jedis.hgetAll(tableName.getBytes());
            for (Map.Entry<byte[], byte[]> entry : map1.entrySet()) {
                map.put(new String(entry.getKey()), SerializeUtil.unserialize(entry.getValue()));
            }
        } catch (Exception e) {
            log.error(tableName + "哈希表getAll操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return map;
    }

    /**
     * 通过HASH表名获取数量
     *
     * @param tableName hash表名
     * @return
     */
    public static Long getHashCount(String tableName) {

        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSource.getConnection();
            result = jedis.hlen(tableName);
        } catch (Exception e) {
            log.error(tableName + "哈希表len操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return result;
    }

    /**
     * 获取制定队列名称的数量
     *
     * @param tableName 队列名称
     * @return 队列数据数量
     */
    public static long llen(String tableName) {

        Jedis jedis = null;
        long re = 0l;
        try {
            jedis = RedisSource.getConnection();
            if (null != jedis)
                re = jedis.llen(tableName.getBytes());
        } catch (Exception e) {
            log.error(tableName + "队列len操作产生异常：" + e);
            if (null != jedis)
                RedisSource.destoryConnection(jedis);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return re;
    }

    /**
     * 存入队列
     *
     * @param tableName list名称
     * @param object    存入的对象
     */
    public static boolean lput(String tableName, Object object) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.lpush(tableName.getBytes(), SerializeUtil.serialize(object));
        } catch (Exception e) {
            log.error(tableName + "队列put的操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return false;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return true;
    }

    /**
     * 根据关键词key得到对象
     *
     * @param key 要查询的关键词
     * @return 对象结果
     */
    public static Object get(String key) {

        Jedis jedis = null;
        Object obj = null;
        try {
            jedis = RedisSource.getConnection();
            obj = SerializeUtil.unserialize(jedis.get(key.getBytes()));
        } catch (Exception e) {
            log.error("根据" + key + "获取对象的操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return obj;
    }

    /**
     * 获取incr值
     *
     * @param key
     * @return
     */
    public static Long getIncr(String key) {

        Jedis jedis = null;
        Long incr = null;
        try {
            jedis = RedisSource.getConnection();
            incr = Long.parseLong(new String(jedis.get(key.getBytes())));
        } catch (Exception e) {
            log.error("执行key为" + key + "的自增长操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return new Long(0);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return incr;
    }

    /**
     * 插入数据
     *
     * @param key    关键词key
     * @param object 插入的对象
     * @param expire 过期时间 零或负数:不设置过期时间(单位：秒)
     * @return
     */
    public static boolean set(String key, Object object, int expire) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.set(key.getBytes(), SerializeUtil.serialize(object));
            if (expire > 0)
                jedis.expire(key.getBytes(), expire);
        } catch (Exception e) {
            log.error("插入key为" + key + "的操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return false;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return true;
    }

    /**
     * 获得满足正则pre的所有redis键
     *
     * @param pre 要匹配的正则
     * @return
     */
    public static Set<String> keys(String pre) {

        Jedis jedis = null;
        Set<String> re = null;
        try {
            jedis = RedisSource.getConnection();
            if (null != jedis)
                re = jedis.keys(pre);
        } catch (Exception e) {
            log.error("获取正则符合" + pre + "的所有redis键操作产生异常：" + e);
            if (null != jedis)
                RedisSource.destoryConnection(jedis);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return re;
    }

    /**
     * 删除缓存
     *
     * @param key 关键词key
     */
    public static boolean delete(String key) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            log.error("删除key为" + key + "的操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return false;
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return true;
    }

    /**
     * 键值自增长
     *
     * @param key 返回增长后的值
     */
    public static Long incr(String key) {

        Jedis jedis = null;
        Long incr = null;
        try {
            jedis = RedisSource.getConnection();
            incr = jedis.incr(key.getBytes());
        } catch (Exception e) {
            log.error("执行key为" + key + "的自增长操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return new Long(0);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return incr;
    }

    /**
     * 设置key的生存周期
     *
     * @param key    key名称
     * @param expire 生存周期，以秒为单位
     * @return 成功返回1，失败放回0
     */
    public static Long setKeyExpireTime(String key, int expire) {

        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSource.getConnection();
            result = jedis.expire(key, expire);
        } catch (Exception e) {
            log.error("设置key为" + key + "的expire操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return new Long(0);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return result;
    }

    /**
     * 获取key的还有多久被删除
     *
     * @param key key名称
     * @return 成功返回生存时间，失败放回0
     */
    public static Long getKeyExpireTime(String key) {

        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSource.getConnection();
            result = jedis.ttl(key);
        } catch (Exception e) {
            log.error("获取key为" + key + "的操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
            return new Long(0);
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return result;
    }

    /**
     * hash计数器
     *
     * @param tableName hash表名
     * @param key       存储的key
     * @param number    增长数
     */
    public static void hashcounter(String tableName, String key, int number) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            jedis.hincrBy(tableName.getBytes(), key.getBytes(), number);
        } catch (Exception e) {
            log.error(tableName + "哈希表计数操作产生异常：" + e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
    }

    /**
     * 释放同步锁
     *
     * @param key
     */
    public static void delLock(String key) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            long oldtime = Long.parseLong(jedis.get(key));
            long current = System.currentTimeMillis();
            if (current < oldtime)
                jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
    }

    /**
     * 获得同步锁
     *
     * @param key
     * @return
     */
    public static int getLock(String key, long timeout) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            long now = System.currentTimeMillis();
            long timestamp = now + timeout;
            long lock = jedis.setnx(key, String.valueOf(timestamp));
            String ob = jedis.get(key);
            if (null != ob && (lock == 1
                    || (now > Long.parseLong(ob) && now > Long.parseLong(jedis.getSet(key,
                    String.valueOf(timestamp)))))) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return 0;
    }

    /**
     * @param key
     * @param timeout
     * @return
     * @throws
     * @method comments: 注释
     * @author name  : wangchao
     * @create time  : 2016年11月3日 下午5:12:12
     * @modify list  : 修改时间和内容
     * 2016年11月3日 下午5:12:12 wangchao 创建
     */
    public static int getXLock(String key, long timeout) {
        int flag = RedisUtil.getLock(key, timeout);
        while (flag != 1) {// 获取当前锁
            flag = RedisUtil.getLock(key, timeout);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return flag;
    }

    /**
     * 拉取redis队列中的数据
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static final <T> T poll(String key, Class<T> clazz) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            if (!jedis.exists(key))
                return null;
            String jsonT = jedis.rpop(key);
            return jsonT == null ? null : JSON.parseObject(jsonT, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return null;
    }

    /**
     * push到队列中
     *
     * @param key
     * @param object
     */
    public static final void push(String key, Object object) {

        Jedis jedis = null;
        try {
            jedis = RedisSource.getConnection();
            String json = JSON.toJSONString(object);
            jedis.lpush(key, json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
    }

    /**
     * 获取HASH表中所有key名称
     *
     * @param tableName HASH表名
     */
    public static Set<String> hkeys(String tableName) {

        Jedis jedis = null;
        Set<String> keys = new HashSet<String>();
        try {
            jedis = RedisSource.getConnection();

            Set<byte[]> keysbytes = jedis.hkeys(tableName.getBytes());
            for (byte[] b : keysbytes) {
                if (b.length != 0) {
                    keys.add(new String(b));
                }
            }
        } catch (Exception e) {
            log.error(tableName + "哈希表del操作产生异常", e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            RedisSource.closeConnection(jedis);
        }
        return keys;
    }


    /**
     * 判断key是否存在
     *
     * @param key key关键字
     * @return 存在返回true, 不存在返回false
     */
    public static boolean exists(String key) {

        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = RedisSource.getConnection();
            flag = jedis.exists(key);
        } catch (Exception e) {
            log.error("判断key是否存在操作产生异常", e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return flag;
    }

    /**
     * 将 key 所储存的值原子性的加上增量 increment 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误
     *
     * @param key
     * @param integer
     * @return : Long
     * @createDate : 2016年11月3日 下午4:45:33
     * @author : zhangyan
     * @version : v1.0
     * @updateDate : 2016年11月3日 下午4:45:33
     * @updateAuthor :
     */
    public static Long incrBy(String key, long integer) {

        Jedis jedis = null;
        long result = 0;
        try {
            jedis = RedisSource.getConnection();
            result = jedis.incrBy(key, integer);
            return result;
        } catch (Exception e) {
            log.error("类型错误", e);
            RedisSource.destoryConnection(jedis);
            jedis = null;
        } finally {
            if (null != jedis)
                RedisSource.closeConnection(jedis);
        }
        return result;
    }

}
