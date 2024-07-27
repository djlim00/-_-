package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.PostImage;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class PostImageDaoImpl implements PostImageDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostImageDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
//    public Long save(Long postId, String fileName, String description, Long order) {
//        String sql = "insert into PostImage(description, image_url, post_id, image_order) values(:description, :image_url, :post_id, :image_order)";

    public Long save(Long postId, String fileName, String description) {
        String sql = "insert into PostImage(description, image_url, post_id) values(:description, :image_url, :post_id)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("description", description)
                .addValue("image_url", fileName)
//                .addValue("image_order", order)
                .addValue("post_id", postId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public PostImage getById(Long postImageId) {
        String sql = "select * from PostImage where post_image_id = :postImageId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postImageId", postImageId);
        return jdbcTemplate.queryForObject(sql, param, postImageRowMapper());
    }

    @Override
//    public Long savePostImageWithoutDescription(Long postId, String fileUrl, Long order) {
//        String sql = "insert into PostImage(image_url, post_id, image_order) values(:image_url, :post_id, :image_order)";

    public Long savePostImageWithoutDescription(Long postId, String fileUrl) {
        String sql = "insert into PostImage(image_url, post_id) values(:image_url, :post_id, :image_order)";

    MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("image_url", fileUrl)
//                .addValue("image_order", order)
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

    public int modifyStatusDormantByPostId(Long postId) {
        String sql = "update PostImage set status='dormant' WHERE post_id = :post_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.update(sql, param);
    }

    @Override
    public List<PostImage> getByPostId(Long postId) {
        String sql = "SELECT * FROM PostImage WHERE post_id = :postId AND status = 'active' ORDER BY image_order";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);
        return jdbcTemplate.query(sql, param, postImageRowMapper());
    }

    @Override
    public int modifyStatusDormant(Long postImageId) {
        String sql = "update PostImage set status='dormant' WHERE post_image_id = :postImageId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postImageId", postImageId);
        return jdbcTemplate.update(sql, param);
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
//                    .order(rs.getLong("order"))
                    .build();
            return postImage;
        };
    }

    @Override
    public void update(Long postImageId, String imageDescription, int order) {

    }

    @Override
    public void deleteById(Long postImageId) {
        String sql = "DELETE FROM PostImage WHERE post_image_id = :postImageId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postImageId", postImageId);
        jdbcTemplate.update(sql, param);
    }
}
