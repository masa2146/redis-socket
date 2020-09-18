package io.hubbox.socket;

import io.lettuce.core.KillArgs;
import io.lettuce.core.UnblockType;
import io.lettuce.core.protocol.CommandType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fatih
 */
public interface RedisTemplate<K, V> {
    String bgrewriteaof();

    String bgsave();

    K clientGetname();

    String clientSetname(K var1);

    String clientKill(String var1);

    Long clientKill(KillArgs var1);

    Long clientUnblock(long var1, UnblockType var3);

    String clientPause(long var1);

    String clientList();

    List<Object> command();

    List<Object> commandInfo(String... var1);

    List<Object> commandInfo(CommandType... var1);

    Long commandCount();

    Map<String, String> configGet(String var1);

    String configResetstat();

    String configRewrite();

    String configSet(String var1, String var2);

    Long dbsize();

    String debugCrashAndRecover(Long var1);

    String debugHtstats(int var1);

    String debugObject(K var1);

    void debugOom();

    void debugSegfault();

    String debugReload();

    String debugRestart(Long var1);

    String debugSdslen(K var1);

    String flushall();

    String flushallAsync();

    String flushdb();

    String flushdbAsync();

    String info();

    String info(String var1);

    Date lastsave();

    String save();

    void shutdown(boolean var1);

    String slaveof(String var1, int var2);

    String slaveofNoOne();

    List<Object> slowlogGet();

    List<Object> slowlogGet(int var1);

    Long slowlogLen();

    String slowlogReset();

    List<V> time();

}
