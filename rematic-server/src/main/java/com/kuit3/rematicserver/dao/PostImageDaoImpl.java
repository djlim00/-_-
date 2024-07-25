package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.PostImage;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
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
    public int modifyStatusDormantByPostId(Long postId) {
        String sql = "update PostImage set status='dormant' WHERE post_id = :post_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.update(sql, param);
    }

    @Override
    public List<PostImage> getByPostId(Long postId) {
        String sql = "SELECT * FROM PostImage WHERE post_id = :postId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);
        return jdbcTemplate.query(sql, param, postImageRowMapper());
    }

    private RowMapper<PostImage> postImageRowMapper() {
        return (rs, rowNum) -> {
            PostImage postImage = PostImage
                    .builder()
                    .postImageId(rs.getLong("post_image_id"))
                    .description(rs.getString("description"))
                    .imageUrl(rs.getString("image_url"))
                    .status(rs.getString("status"))
                    .postId(rs.getLong("post_id"))
                    .build();
            return postImage;
        };
    }
}
