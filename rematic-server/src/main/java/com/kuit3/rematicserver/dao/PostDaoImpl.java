package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.GetSearchPostDto;
import com.kuit3.rematicserver.entity.Post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class PostDaoImpl implements PostDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<GetSearchPostDto> getPage(String keyword, String category, Long lastId, Long limit) {
        String sql = "SELECT *, (SELECT image_url FROM PostImage WHERE post_id = p.post_id LIMIT 1) AS image_url FROM Post as p" +
                " WHERE (title LIKE :keyword OR content LIKE :keyword)" +
                " AND status = 'active'";
        if(lastId != null) {
            sql += " AND post_id < :lastId";
        }
        if(!category.equals("all")){
            sql += " AND category = :category";
        }
        sql += " ORDER BY post_id DESC LIMIT :limit";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword",  "%" + keyword + "%")
                .addValue("lastId", lastId)
                .addValue("limit", limit);
        if(!category.equals("all")){
            param.addValue("category", category);
        }

        return jdbcTemplate.query(sql, param, getPostDtoRowMapper());
    }

    private RowMapper<GetSearchPostDto> getPostDtoRowMapper(){
        return (ResultSet rs, int rowNum)->{
            GetSearchPostDto dto = GetSearchPostDto.builder()
                    .post_id(rs.getLong("post_id"))
                    .title(rs.getString("title"))
                    .content(rs.getString("content"))
                    .likes(rs.getLong("likes"))
                    .hates(rs.getLong("hates"))
                    .views(rs.getLong("views"))
                    .scraps(rs.getLong("scraps"))
                    .image_url(rs.getString("image_url"))
                    .build();
            return dto;
        };
    }

    @Override
    public boolean hasNextPage(String keyword, String category, Long lastId) {
        String sql = "SELECT EXISTS(SELECT * FROM Post " +
                "WHERE (title LIKE :keyword OR content LIKE :keyword)" +
                " AND post_id < :lastId";
        if(!category.equals("all")){
            sql += " AND category = :category";
        }
        sql += ")";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("keyword",  "%" + keyword + "%")
                .addValue("lastId", lastId);
        if(!category.equals("all")){
            param.addValue("category", category);
        }

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }
    @Override
    public long createPost(CreatePostRequest request) {
        String sql = "insert into Post(title, content, has_image, category, anonymity, user_id, bulletin_id)" +
                " values(:title, :content, :has_image, :category, :anonymity, :user_id, :bulletin_id)";

       MapSqlParameterSource param = new MapSqlParameterSource()
               .addValue("title", request.getTitle())
               .addValue("content", request.getContent())
               .addValue("has_image", request.getHas_image() ? "있음" : "없음")
               .addValue("category", request.getCategory())
               .addValue("anonymity", request.getAnonymity() ? "공개" : "익명")
               .addValue("user_id", request.getUser_id())
               .addValue("bulletin_id", request.getBulletin_id());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Post findById(Long postId) {
        String sql = "select * from Post where post_id = :post_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);

        return jdbcTemplate.queryForObject(sql, param, postRowMapper());
    }

    private RowMapper<Post> postRowMapper(){
        return (ResultSet rs, int rowNum)->{
           Post post = Post.builder()
                   .postId(rs.getLong("post_id"))
                   .title(rs.getString("title"))
                   .content(rs.getString("content"))
                   .hasImage(rs.getString("has_image") == "있음")
                   .category(rs.getString("category"))
                   .hates(rs.getLong("hates"))
                   .likes(rs.getLong("likes"))
                   .scraps(rs.getLong("scraps"))
                   .views(rs.getLong("views"))
                   .realtimeViews(rs.getLong("realtime_views"))
                   .anonymity(rs.getString("anonymity") == "공개")
                   .status(rs.getString("status"))
                   .userId(rs.getLong("user_id"))
                   .bulletinId(rs.getLong("bulletin_id")).build();
           return post;
        };
    }

    @Override
    public boolean hasPostWithId(Long postId) {
        String sql = "select exists(select * from Post where post_id = :post_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    @Override
    public void updateLike(Long postId, int delta) {
        String sql = "UPDATE Post SET likes = likes + :delta WHERE post_id = :post_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("delta", delta)
                .addValue("post_id", postId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateHate(Long postId, int delta) {
        String sql = "UPDATE Post SET hates = hates + :delta WHERE post_id = :post_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("delta", delta)
                .addValue("post_id", postId);
        jdbcTemplate.update(sql, params);
    }
}

