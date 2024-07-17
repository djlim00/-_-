package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.kuit3.rematicserver.dto.post.postresponse.ImageInfo;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@Slf4j
@Repository
public class PostInfoDao {
    NamedParameterJdbcTemplate jdbcTemplate;

    public PostInfoDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public boolean isPostExists(long postId) {
        String sql = "select exists (select 1 from Post where post_id = :postId);";
        Map<String, Object> param = Map.of("postId", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public boolean isPostAnonymous(long postId) {
        String sql = "select anonymity = '공개' from Post where post_id = :postId;";
        Map<String, Object> param = Map.of("postId", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public UserInfo getWriterInfo(long postId) {
        String sql = "select u.nickname, u.profile_image_url from User u " +
                "join Post p on u.user_id = p.user_id " +
                "where p.post_id = :postId;";
        Map<String, Object> param = Map.of("postId", postId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> {
            return new UserInfo(
                    rs.getString("nickname"),
                    rs.getString("profile_image_url")
            );
        });
    }

    public String getBulletinInfo(long postId) {
        String sql = "select b.name from Bulletin b join Post p on p.bulletin_id = b.bulletin_id " +
                "where p.post_id = :postId;";
        return jdbcTemplate.queryForObject(sql, Map.of("postId", postId), String.class);
    }

    public PostInfo getPostInfo(long postId) {
        String sql = "select title, content, likes, hates, scraps from Post where post_id = :postId;";
        return jdbcTemplate.queryForObject(sql, Map.of("postId", postId), (rs, rowNum) -> {
            return new PostInfo(
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getLong("likes"),
                    rs.getLong("hates"),
                    rs.getLong("scraps")
            );
        });
    }

    public List<ImageInfo> getImageInfo(long postId) {
        String sql = "select image_url, description from PostImage where post_id = :postId and status = 'active';";
        return jdbcTemplate.query(sql, Map.of("postId", postId), (rs, rowNum) -> {
            return new ImageInfo(
                    rs.getString("image_url"),
                    rs.getString("description")
            );
        });
    }

    public boolean imageExists(long postId) {
        String sql = "select has_image = '있음' from Post where post_id = :postId;";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("postId", postId), boolean.class));
    }

}
