package com.kuit3.rematicserver.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Objects;

@Repository
public class PostImageDaoImpl implements PostImageDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostImageDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Long savePostImage(Long postId, String fileName, String description) {
        String sql = "insert into PostImage(description, image_url, post_id) values(:description, :image_url, :post_id)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("description", description)
                .addValue("image_url", fileName)
                .addValue("post_id", postId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Long savePostImageWithoutDescription(Long postId, String fileUrl) {
        String sql = "insert into PostImage(image_url, post_id) values(:image_url, :post_id)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("image_url", fileUrl)
                .addValue("post_id", postId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Boolean hasImageUrlAlready(long commentId) {
        String sql = "select exists (select 1 from Comment where comment_id = :commentId and comment_image_url is not null);";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("commentId", commentId);

        return Boolean.FALSE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

}
