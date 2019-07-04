package com.example.demo.service.impl;

import com.example.demo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("RedisService")
public class RedisServiceImpl implements RedisService {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Value("${sql.spxx}")
    String SQL_SPXX;

    @Override
    public void batchSaveSPXX() {
        jdbcTemplate.setFetchSize(2000);
        log.info(">>>>>>>导出SPXX");
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SPXX);
        stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection)connection;
                stringRedisConnection.openPipeline();
                while (rs.next()) {
                    String pk = rs.getString("BARCODE");
                    stringRedisConnection.sAdd("SPXX", pk);
                    stringRedisConnection.hMSet(("SPXX:" + pk), toHash(rs));
                }
                return null;
            }
        });
    }

    @Value("${sql.spkczt-fdbh}")
    String SQL_SPKCZT_FDBH;

    @Value("${sql.spkczt}")
    String SQL_SPKCZT;

    @Override
    public void batchSaveSPKCZT() {
        jdbcTemplate.setFetchSize(2000);
        List<Integer> listFDBH = jdbcTemplate.queryForList(SQL_SPKCZT_FDBH, Integer.class);
        log.info(">>>>>>>导出SPKCZT,分店数量: " + listFDBH.size());
        listFDBH.forEach(fdbh -> {
            log.info("分店: " + fdbh);
            SqlRowSet rs = jdbcTemplate.queryForRowSet(String.format(SQL_SPKCZT, fdbh));
            saveSPKCZT(rs, fdbh);
            rs = null;
        });
    }

    private void saveSPKCZT(SqlRowSet rs, Integer fdbh) {
        stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection)connection;
                stringRedisConnection.openPipeline();
                List<String> subs = new ArrayList<>();
                while (rs.next()) {
                    String barcode = rs.getString("BARCODE");
                    subs.add(barcode);
                    stringRedisConnection.hMSet(("SPKCZT:FD:" + fdbh + ":BARCODE:" + barcode), toHash(rs));
                    stringRedisConnection.sAdd(("SPKCZT:BARCODE:" + barcode), fdbh.toString());
                }
                stringRedisConnection.sAdd(("SPKCZT:FD:" + fdbh), subs.toArray(new String[subs.size()]));
                return null;
            }
        });
    }

    private Map<String, String> toHash(SqlRowSet rs) {
        SqlRowSetMetaData meta = rs.getMetaData();
        Map<String, String> hash = new LinkedHashMap<>();
        for (int i = 1, count = meta.getColumnCount(); i <= count; i++) {
            String val = rs.getString(i);
            hash.put(meta.getColumnLabel(i), (val == null) ? "" : val);
        }
        return hash;
    }

}
