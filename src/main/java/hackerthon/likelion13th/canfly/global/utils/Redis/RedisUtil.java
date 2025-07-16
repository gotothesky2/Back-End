package hackerthon.likelion13th.canfly.global.utils.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    // 데이터 저장 (유효 시간 지정)
    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, duration, TimeUnit.SECONDS);
    }

    // 데이터 조회
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    // 데이터 삭제
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}