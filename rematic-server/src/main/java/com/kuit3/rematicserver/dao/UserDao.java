package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.CreateUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Boolean hasUserWithDuplicateEmail(String email) {
        String sql = "select exists(select * from User where user_email = :email)";
        Map<String, String> param = Map.of("email", email);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, Boolean.class));
    }

    public long createUser(CreateUserDTO dto) {
        String sql = "insert into User(user_email, nickname) values(:email, :nickname)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(dto);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public long findUserByEmail(String email) {
        String sql = "select user_id from User where user_email = :email and status = 'active'";
        Map<String, String> param = Map.of("email", email);
        return jdbcTemplate.queryForObject(sql, param, Long.class);
    }
}
