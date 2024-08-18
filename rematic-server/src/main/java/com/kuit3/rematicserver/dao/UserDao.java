package com.kuit3.rematicserver.dao;


import com.kuit3.rematicserver.dto.user.*;
import com.kuit3.rematicserver.dto.auth.CreateUserDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
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
        String sql = "select exists(select * from User where status='active' and user_email = :email)";
        Map<String, String> param = Map.of("email", email);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, Boolean.class));
    }

    public long createUser(CreateUserDTO dto) {
        String sql = "insert into User(user_email, nickname, profile_image_url) values(:email, :nickname, :profile_image_url)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(dto);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public long getUserIdByEmail(String email) {
        String sql = "select user_id from User where user_email = :email and status = 'active'";
        Map<String, String> param = Map.of("email", email);
        return jdbcTemplate.queryForObject(sql, param, Long.class);
    }

    public int changeUserNickname(Long userId, String newNickname) {
        String sql = "update User set nickname= :nickname where user_id = :userId";
        Map<String, Object> param = Map.of("nickname", newNickname, "userId", userId);
        return jdbcTemplate.update(sql, param);
    }

    public boolean hasUserWithDuplicateNickname(String nickname) {
        String sql = "select exists(select * from User where nickname = :nickname)";
        Map<String, String> param = Map.of("nickname", nickname);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, Boolean.class));
    }


    public int updateUserInfo(UpdateUserInfoRequest request) {
        String sql = "UPDATE User SET nickname = :nickname, introduction = :introduction, profile_image_url = :profile_image_url WHERE user_id = :user_id";
        SqlParameterSource param = new BeanPropertySqlParameterSource(request);
        return jdbcTemplate.update(sql, param);
    }

    public UserCheckDto checkExistsOrDormant(long userId) {
        String sql = "select count(*) as userCount, if(count(*) > 0 and status = 'active', TRUE, FALSE) as isActive " +
                "from User where user_id = :userId;";
        Map<String, Object> param = Map.of("userId", userId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> {
            return new UserCheckDto(
                    rs.getInt("userCount"),
                    rs.getInt("isActive")
            );
        });
    }

    public UserMyPageResponse getMyPageInfo(long userId) {
        String sql = "select u.nickname, u.profile_image_url, u.introduction, " +
                "(select count(*) from Post p where p.user_id = u.user_id) as myPosts, " +
                "(SELECT COUNT(*) FROM Comment c WHERE c.user_id = u.user_id) AS myComments, " +
                "(SELECT COUNT(*) FROM UserScrap us WHERE us.user_id = u.user_id) AS myScraps " +
                "from User u where u.user_id = :userId;";
        Map<String, Object> param = Map.of("userId", userId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> {
            return new UserMyPageResponse(
                    rs.getString("nickname"),
                    rs.getString("profile_image_url"),
                    rs.getString("introduction"),
                    rs.getLong("myPosts"),
                    rs.getLong("myComments"),
                    rs.getLong("myScraps")
            );
        });

    }

    public int modifyStatus(long userId, String status) {
        String sql = "UPDATE User SET status = :status where user_id = :userId ";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("userId", userId);
        return jdbcTemplate.update(sql, param);
    }


    public String getUserNickName(long userId) {
        String sql = "select nickname from User where user_id = :userId;";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbcTemplate.queryForObject(sql, param, String.class);
    }

    public List<UserPunishmentInfo> getUserPunishmentsList(long userId) {
        String sql = "select p.punishment_id, p.content, b.name as bulletin_name, p.reason, p.created_at " +
                "from Punishment p join Bulletin b on p.bulletin_id = b.bulletin_id where p.user_id = :userId;";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new UserPunishmentInfo(
                    rs.getLong("punishment_id"),
                    rs.getString("content"),
                    rs.getString("bulletin_name"),
                    rs.getString("reason"),
                    rs.getTimestamp("created_at")
            );
        });
    }
}
