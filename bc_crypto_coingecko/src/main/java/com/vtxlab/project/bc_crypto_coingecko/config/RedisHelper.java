package com.vtxlab.project.bc_crypto_coingecko.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.annotation.PropertyAccessor;

@Slf4j
public class RedisHelper {
  // 直接用RedisTemplate操作Redis，需要很多行代码，因此直接封装好一个RedisUtil，这样写代码更方便点。这个RedisUtil交给Spring容器实例化，使用时直接注解注入。
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();
  // =============================common============================

  // This factory is used to create a connection to the Redis server.
  public RedisHelper(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public RedisHelper(RedisConnectionFactory factory) {
    // ObjectMapper objectMapper = new ObjectMapper() //
    // .registerModule(new ParameterNamesModule())
    // .registerModule(new Jdk8Module()) //
    // .registerModule(new JavaTimeModule());
    // this.redisTemplate = template(factory, objectMapper);
    RedisTemplate template = new RedisTemplate();
    template.setConnectionFactory(factory);// 配置连接工厂

    // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer(Object.class);

    ObjectMapper objectMapper = new ObjectMapper();// 自定义ObjectMapper
    // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
    objectMapper.setVisibility(PropertyAccessor.ALL,
        JsonAutoDetect.Visibility.ANY);
    // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    // String序列化配置
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    // key采用String的序列化方式
    template.setKeySerializer(stringRedisSerializer);
    // hash的key也采用String的序列化方式
    template.setHashKeySerializer(stringRedisSerializer);
    // value序列化方式采用jackson
    template.setValueSerializer(jackson2JsonRedisSerializer);
    // hash的value序列化方式采用jackson
    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    template.afterPropertiesSet();
  }

  // takes a RedisConnectionFactory and an ObjectMapper as arguments.
  // The ObjectMapper is used to serialize and deserialize objects to and from JSON.
  public RedisHelper(RedisConnectionFactory factory,
      ObjectMapper redisObjectMapper) {
    this.redisTemplate = template(factory, redisObjectMapper);
  }

  @Operation(
      summary = "The RedisConnectionFactory is used to create a connection to the Redis server."//
          + "The ObjectMapper is used to serialize and deserialize objects to and from JSON."//
          + "create a RedisTemplate object that is customized for your specific needs.")
  public static RedisTemplate<String, Object> template(
      RedisConnectionFactory factory, ObjectMapper redisObjectMapper) { // method name -> bean name
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setValueSerializer(RedisSerializer.json());
    // redisTemplate.setHashKeySerializer(RedisSerializer.string());
    // redisTemplate.setHashValueSerializer(RedisSerializer.json());
    redisTemplate.afterPropertiesSet();
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(redisObjectMapper, Object.class);
    redisTemplate.setValueSerializer(serializer);
    return redisTemplate;
  }

  public static RedisTemplate<String, Object> template(
      RedisConnectionFactory factory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate
        .setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  /**
   * 指定缓存失效时间
   *
   * @param key 键
   * @param time 时间(秒)
   * @return
   */
  public boolean expire(String key, long time) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 根据key 获取过期时间
   *
   * @param key 键 不能为null
   * @return 时间(秒) 返回0代表为永久有效
   */
  public long getExpire(String key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * 判断key是否存在
   *
   * @param key 键
   * @return true 存在 false不存在
   */
  public boolean hasKey(String key) {
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 删除缓存
   *
   * @param key ...表示可以传一个值 或多个
   */
  @SuppressWarnings("unchecked")
  public void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        redisTemplate
            .delete((Collection<String>) CollectionUtils.arrayToList(key));
      }
    }
  }
  // ============================String=============================

  /**
   * 普通缓存获取
   *
   * @param key 键
   * @return 值
   */
  public Object get(String key) {
    return key == null ? null : redisTemplate.opsForValue().get(key);
  }

  /**
   * 普通缓存获取
   *
   * @param key 键
   * @return 值
   */
  public <T> T get(String key, Class<T> clazz) throws JsonProcessingException {
    String value = (String) this.redisTemplate.opsForValue().get(key);
    return objectMapper.readValue(value, clazz);
  }

  /**
   * 普通缓存放入
   *
   * @param key 键
   * @param value 值
   * @return true成功 false失败
   */
  public boolean set(String key, Object value) {
    try {
      String json = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, json);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 普通缓存放入并设置时间
   *
   * @param key 键
   * @param value 值
   * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
   * @return true成功 false 失败
   */
  public boolean set(String key, Object value, long time) {
    try {
      if (time > 0) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
      } else {
        set(key, value);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 递增
   *
   * @param key 键
   * @param delta 要增加几(大于0)
   * @return
   */
  public long incr(String key, long delta) {
    if (delta < 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return redisTemplate.opsForValue().increment(key, delta);
  }

  /**
   * 递减
   *
   * @param key 键
   * @param delta 要减少几(小于0)
   * @return
   */
  public long decr(String key, long delta) {
    if (delta < 0) {
      throw new RuntimeException("递减因子必须大于0");
    }
    return redisTemplate.opsForValue().increment(key, -delta);
  }

  // ================================Map=================================

  /**
   * HashGet
   *
   * @param key 键 不能为null
   * @param item 项 不能为null
   * @return 值
   */
  public <T> T hget(String key, String item, Class<T> clazz) {
    try {
      Object value = redisTemplate.opsForHash().get(key, item);
      if (value != null) {
        String jsonValue = objectMapper.writeValueAsString(value);
        return objectMapper.readValue(jsonValue, clazz);
      }
    } catch (Exception e) {
      log.error("Error getting value from Redis for key: {} and item: {}", key,
          item, e);
    }
    return null;
  }

  /**
   * 获取所有给定字段的值 author: xu
   * 
   * @param key
   * @return
   */
  public <T> Map<String, T> hgetall(String key, Class<T> clazz) {
    try {
      Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
      Map<String, T> resultMap = new LinkedHashMap<>();
      for (Map.Entry<Object, Object> entry : hashEntries.entrySet()) {
        String hashKey = (String) entry.getKey();
        String jsonValue = objectMapper.writeValueAsString(entry.getValue());
        T value = objectMapper.readValue(jsonValue, clazz);
        resultMap.put(hashKey, value);
      }
      return resultMap;
    } catch (Exception e) {
      log.error("Error getting all values from Redis for key: {}", key, e);
    }
    return Collections.emptyMap();
  }

  /**
   * 获取hashKey对应的所有键值
   *
   * @param key 键
   * @return 对应的多个键值
   */
  public <T> Map<String, T> hmget(String key, Class<T> clazz) {
    try {
      Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
      Map<String, T> resultMap = new LinkedHashMap<>();
      for (Map.Entry<Object, Object> entry : hashEntries.entrySet()) {
        String hashKey = (String) entry.getKey();
        String jsonValue = objectMapper.writeValueAsString(entry.getValue());
        T value = objectMapper.readValue(jsonValue, clazz);
        resultMap.put(hashKey, value);
      }
      return resultMap;
    } catch (Exception e) {
      log.error("Error getting multiple values from Redis for key: {}", key, e);
    }
    return Collections.emptyMap();
  }

  /**
   * HashSet
   *
   * @param key 键
   * @param map 对应多个键值
   * @return true 成功 false 失败
   */
  public boolean hmset(String key, Map<String, Object> map) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * HashSet 并设置时间
   *
   * @param key 键
   * @param map 对应多个键值
   * @param time 时间(秒)
   * @return true成功 false失败
   */
  public boolean hmset(String key, Map<String, Object> map, long time) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 向一张hash表中放入数据,如果不存在将创建
   *
   * @param key 键
   * @param item 项
   * @param value 值
   * @return true 成功 false失败
   */
  public boolean hset(String key, String item, Object value) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 向一张hash表中放入数据,如果不存在将创建
   *
   * @param key 键
   * @param item 项
   * @param value 值
   * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
   * @return true 成功 false失败
   */
  public boolean hset(String key, String item, Object value, long time) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 删除hash表中的值
   *
   * @param key 键 不能为null
   * @param item 项 可以使多个 不能为null
   */

  public void hdel(String key, Object... item) {
    redisTemplate.opsForHash().delete(key, item);
  }

  /**
   * 判断hash表中是否有该项的值
   *
   * @param key 键 不能为null
   * @param item 项 不能为null
   * @return true 存在 false不存在
   */
  public boolean hHasKey(String key, String item) {
    return redisTemplate.opsForHash().hasKey(key, item);
  }

  /**
   * hash递增 如果不存在,就会创建一个 并把新增后的值返回
   *
   * @param key 键
   * @param item 项
   * @param by 要增加几(大于0)
   * @return
   */
  public double hincr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, by);
  }

  /**
   * hash递减
   *
   * @param key 键
   * @param item 项
   * @param by 要减少记(小于0)
   * @return
   */
  public double hdecr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, -by);
  }
  // ============================set=============================

  /**
   * 根据key获取Set中的所有值
   *
   * @param key 键
   * @return
   */
  public <T> Set<T> sGet(String key, Class<T> clazz) {
    try {
      Set<Object> members = redisTemplate.opsForSet().members(key);
      Set<T> resultSet = new HashSet<>();
      for (Object member : members) {
        String jsonValue = objectMapper.writeValueAsString(member);
        T value = objectMapper.readValue(jsonValue, clazz);
        resultSet.add(value);
      }
      return resultSet;
    } catch (Exception e) {
      log.error("Error getting set values from Redis for key: {}", key, e);
    }
    return Collections.emptySet();
  }

  /**
   * 根据value从一个set中查询,是否存在
   *
   * @param key 键
   * @param value 值
   * @return true 存在 false不存在
   */
  public boolean sHasKey(String key, Object value) {
    try {
      return redisTemplate.opsForSet().isMember(key, value);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将数据放入set缓存
   *
   * @param key 键
   * @param values 值 可以是多个
   * @return 成功个数
   */
  public long sSet(String key, Object... values) {
    try {
      return redisTemplate.opsForSet().add(key, values);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 将set数据放入缓存
   *
   * @param key 键
   * @param time 时间(秒)
   * @param values 值 可以是多个
   * @return 成功个数
   */
  public long sSetAndTime(String key, long time, Object... values) {
    try {
      Long count = redisTemplate.opsForSet().add(key, values);
      if (time > 0)
        expire(key, time);
      return count;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 获取set缓存的长度
   *
   * @param key 键
   * @return
   */
  public long sGetSetSize(String key) {
    try {
      return redisTemplate.opsForSet().size(key);
    } catch (Exception e) {
      log.error("Error getting size of set in Redis for key: {}", key, e);
      return 0;
    }
  }

  /**
   * 移除值为value的
   *
   * @param key 键
   * @param values 值 可以是多个
   * @return 移除的个数
   */
  public long setRemove(String key, Object... values) {
    try {
      Long count = redisTemplate.opsForSet().remove(key, values);
      return count;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
  // ===============================list=================================

  /**
   * 获取list缓存的内容
   *
   * @param key 键
   * @param start 开始
   * @param end 结束 0 到 -1代表所有值
   * @return
   */
  public <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
    try {
      List<Object> range = redisTemplate.opsForList().range(key, start, end);
      List<T> resultList = new ArrayList<>();
      for (Object obj : range) {
        String jsonValue = objectMapper.writeValueAsString(obj);
        T value = objectMapper.readValue(jsonValue, clazz);
        resultList.add(value);
      }
      return resultList;
    } catch (Exception e) {
      log.error("Error getting list values from Redis for key: {}", key, e);
    }
    return Collections.emptyList();
  }

  /**
   * 获取list缓存的长度
   *
   * @param key 键
   * @return
   */
  public long lGetListSize(String key) {
    try {
      return redisTemplate.opsForList().size(key);
    } catch (Exception e) {
      log.error("Error getting size of list in Redis for key: {}", key, e);
      return 0;
    }
  }

  /**
   * 通过索引 获取list中的值
   *
   * @param key 键
   * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
   * @return
   */
  public <T> T lGetIndex(String key, long index, Class<T> clazz) {
    try {
      Object value = redisTemplate.opsForList().index(key, index);
      if (value != null) {
        String jsonValue = objectMapper.writeValueAsString(value);
        return objectMapper.readValue(jsonValue, clazz);
      }
    } catch (Exception e) {
      log.error(
          "Error getting value from list in Redis for key: {} and index: {}",
          key, index, e);
    }
    return null;
  }

  /**
   * 将list放入缓存
   *
   * @param key 键
   * @param value 值
   * @return
   */
  public boolean lSet(String key, Object value) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key 键
   * @param value 值
   * @param time 时间(秒)
   * @return
   */
  public boolean lSet(String key, Object value, long time) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      if (time > 0)
        expire(key, time);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key 键
   * @param value 值
   * @return
   */
  public boolean lSet(String key, List<Object> value) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key 键
   * @param value 值
   * @param time 时间(秒)
   * @return
   */
  public boolean lSet(String key, List<Object> value, long time) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      if (time > 0)
        expire(key, time);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 根据索引修改list中的某条数据
   *
   * @param key 键
   * @param index 索引
   * @param value 值
   * @return
   */
  public boolean lUpdateIndex(String key, long index, Object value) {
    try {

      redisTemplate.opsForList().set(key, index, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 移除N个值为value
   *
   * @param key 键
   * @param count 移除多少个
   * @param value 值
   * @return 移除的个数
   */
  public long lRemove(String key, long count, Object value) {
    try {
      Long remove = redisTemplate.opsForList().remove(key, count, value);
      return remove;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
