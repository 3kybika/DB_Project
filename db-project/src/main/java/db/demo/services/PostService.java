package db.demo.services;

import db.demo.mappers.PostDBMapper;
import db.demo.mappers.PostMapper;
import db.demo.models.PostDBModel;
import db.demo.views.PostModel;
import db.demo.views.PostUpdateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PostService {

    private JdbcTemplate jdbcTemplate;
    private PostDBMapper postDBMapper = new PostDBMapper();
    private PostMapper postMapper = new PostMapper();

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear(){
        jdbcTemplate.update("TRUNCATE TABLE posts CASCADE;");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts", Integer.class);
    }
    public PostModel getPostById(int id){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM posts " +
                            "WHERE id = ? ",
                    postMapper,
                    id
            );
        } catch (Exception e){
            return null;
        }
    }

    public PostDBModel getPostDBById(int id){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM posts " +
                            "WHERE id = ? ",

                    postDBMapper,
                    id
            );
        } catch (Exception e){
            return null;
        }
    }

    //ToDo наверное нужен рут!
    /*public List<PostDBModel> createPostInThread(List<PostDBModel> posts, int forumId, int threadId) throws SQLException {
        String query =
                //                  1       2             3            4         5        6          7        8      9          10    11
                "INSERT INTO posts(id,  author_id, author_nickname, created, forum_id, forum_slug, message, parent, thread_id, path, root_post) " +
                        //      1  2  3          4             5  6          7  8  9  10 11
                        "SELECT ?, ?, ?::citext, ?::TIMESTAMP WITH TIME ZONE, ?, ?::citext, ?, ?, ?, ?, ? ";
        //ToDo: timeStamp опять делает ересь! Может, при записи проблема?
        try (
                Connection con = this.jdbcTemplate.getDataSource().getConnection();
                PreparedStatement ps = con.prepareStatement(query, Statement.NO_GENERATED_KEYS)
        ) {
            SqlRowSet ids = jdbcTemplate.queryForRowSet("SELECT nextval('posts_id_seq') FROM generate_series(1, ?);", posts.size());

            for (PostDBModel post : posts) {
                ids.next();
                post.setId(ids.getInt(1));

                ps.setInt(1, post.getId());
                ps.setInt(2, post.getAuthor());
                ps.setString(3, post.getAuthorNickname());
                ps.setString(4, post.getCreated());
                ps.setInt(5, forumId);
                ps.setString(6, post.getForumSlug());
                ps.setString(7, post.getMessage());
                ps.setInt(8, post.getParent());
                ps.setInt(9, threadId);

                if(post.getParent()!= 0) {
                    ArrayList arr = new ArrayList<Object>(Arrays.asList(post.getPath()));
                    arr.add(post.getId());
                    ps.setArray(10, con.createArrayOf("INT", arr.toArray()));
                    ps.setInt(11, (Integer) arr.get(0));
                }
                else{
                    ps.setArray(10, con.createArrayOf("INT", new Object[]{post.getId()}));
                    ps.setInt(11, post.getId());
                }
                ps.addBatch();
            }
            ps.executeBatch();

            this.jdbcTemplate.update(
                    "UPDATE forums " +
                            "SET posts = posts + ?" +
                            "WHERE id = ? ",
                    posts.size(),
                    forumId
            );

        } catch(Exception err) {
            throw new SQLException();
        }
        return posts;
    }*/

    public List<PostDBModel> createPostInThread(List<PostDBModel> posts, int forumId, int threadId) throws SQLException{

        SqlRowSet ids = jdbcTemplate.queryForRowSet("SELECT nextval('posts_id_seq') FROM generate_series(1, ?);", posts.size());

        for (PostDBModel post : posts) {
            ids.next();
            post.setId(ids.getInt(1));

            if(post.getParent()!= 0) {
                ArrayList arr = new ArrayList<Object>(Arrays.asList(post.getPath()));
                arr.add(post.getId());
                post.setRootPost((Integer) arr.get(0));
                post.setPath(arr.toArray());
            }
            else{
                post.setRootPost( post.getId());
                post.setPath(new Object[]{post.getId()});
            }

            String path = "";
            for (Object postId : post.getPath()) {
                path += String.valueOf((Integer) postId);
                path+= ",";
            }
            path = path.substring(0, path.length() - 1);
            path = "'{" + path +"}'";

            jdbcTemplate.update(
                    "INSERT INTO posts( id, author_id, author_nickname, created, forum_id, forum_slug, message, parent, thread_id, path, root_post) " +
                            "SELECT ?, ?, ?::citext, ?::TIMESTAMP WITH TIME ZONE, ?, ?::citext, ?, ?, ?, "+path+" , ? ",
                    post.getId(),
                    post.getAuthor(),
                    post.getAuthorNickname(),
                    post.getCreated(),
                    forumId,
                    post.getForumSlug(),
                    post.getMessage(),
                    post.getParent(),
                    threadId,
                    post.getRootPost()
            );
        }

        this.jdbcTemplate.update(
                "UPDATE forums " +
                        "SET posts = posts + ?" +
                        "WHERE id = ? ",
                posts.size(),
                forumId
        );

        return posts;
    }

    public List<PostModel> getPosts(int threadId, int since, boolean desc, String sortType, int limit) {
        switch (sortType) {
            case "flat": {
                return getPostsInFlatSort(threadId, since, desc, limit);
            }
            case "tree": {
                return getPostsInTreeSort(threadId, since, desc, limit);
            }
            case "parent_tree": {
                return getPostsInParentTreeSort(threadId, since, desc, limit);
            }
            default: {
                return null;
            }
        }
    }

    public List<PostModel> getPostsInFlatSort(int threadId, int since, boolean desc, int limit) {
        String query = "SELECT * FROM posts " +
                "WHERE thread_id = ? ";
        ArrayList params = new ArrayList();
        params.add(threadId) ;

        if (since != -1) {
            if (desc) {
                query += "AND id < ? ";
            } else {
                query += "AND id > ? ";
            }
            params.add(since);
        }

        if (desc) {
            query += " ORDER BY created DESC, id DESC ";
        } else {
            query += " ORDER BY created ASC, id ASC ";
        }

        if (limit != 0) {
            query += "LIMIT ?;";
            params.add(limit);
        }

        return jdbcTemplate.query(
                query,
                params.toArray(),
                postMapper
        );
    }

    public List<PostModel> getPostsInTreeSort(int threadId, int since, boolean desc, int limit) {
        String query = "";
        ArrayList params = new ArrayList();

        if(since != -1) {
            query = "SELECT pp.* FROM (SELECT path FROM posts WHERE id = ?) gp " +
                    "INNER JOIN LATERAL ( " +
                    "SELECT p.* FROM posts p WHERE p.thread_id = ? AND p.path ";
            params.add(since);
            params.add(threadId);
            if (desc) {
                query += " < gp.path ORDER BY p.path DESC ";
            } else {
                query += " > gp.path ORDER BY p.path ASC  ";
            }

            if (limit != 0) {
                query += "LIMIT ? ";
                params.add(limit);
            }
            query += ") pp ON true;";

        } else {
            query = "SELECT * FROM posts " +
                    "WHERE thread_id = ? ";
            params = new ArrayList();
            params.add(threadId) ;

            if (desc) {
                query += " ORDER BY path DESC ";
            } else {
                query += " ORDER BY path ASC ";
            }

            if (limit != 0) {
                query += "LIMIT ?;";
                params.add(limit);
            }
        }
        return jdbcTemplate.query(
                query,
                params.toArray(),
                postMapper
        );
    }

    public List<PostModel>  getPostsInParentTreeSort(int threadId, int since, boolean desc, int limit) {
        ArrayList<Object> params = new ArrayList<>();
        String query = "WITH root_list as ( " +
                " SELECT DISTINCT id FROM posts " +
                "WHERE " +
                "parent = 0 " +
                "AND thread_id = ? ";
        params.add(threadId);

        if (since != -1) {
            query += "AND id ";

            if (desc) {
                query += " < ";
            } else {
                query += " > ";
            }
            //Получем самый поcледний (ограничивающий) пост самого верхнего уровня
            query += "(SELECT root_post FROM posts WHERE id = ?) ";
            params.add(since);
        }

        query += " ORDER BY id ";
        if (desc) {
            query += " DESC ";
        }

        if (limit != 0) {
            query += " LIMIT ? ";
            params.add(limit);
        }
        query += " ) " +
                "SELECT p.* " +
                "FROM root_list rl " +
                "INNER JOIN posts p ON p.root_post = rl.id " +
                "ORDER BY ";
        if (desc) {
            query += " p.root_post DESC, ";
        }
        query += " p.path;";

        return jdbcTemplate.query(
                query,
                params.toArray(),
                postMapper
        );
    }

    /*public List<PostModel>  getPostsInParentTreeSort(int threadId, int since, boolean desc, int limit) {

        ArrayList<Object> params = new ArrayList<>();
        String query ="SELECT * " +
                " FROM posts" +
                " WHERE root_post IN ( " +
                //Ищем все посты верхнего уровня, которые будут в списке
                "SELECT DISTINCT id FROM posts " +
                "WHERE parent = 0 AND thread_id = ? ";
        params.add(threadId);

        if (since != -1) {
            query += "AND id ";

            if (desc) {
                query += " < ";
            } else {
                query += " > ";
            }
            //Получем самый поcледний (ограничивающий) пост самого верхнего уровня
            query += "(SELECT root_post FROM posts WHERE id = ?) ";
            params.add(since);
        }

        query += " ORDER BY id ";
        if (desc) {
            query += " DESC ";
        }

        if (limit != 0) {
            query += " LIMIT ? ";
            params.add(limit);
        }

        query += " ) ORDER BY ";
        if (desc ) {
            query += " root_post DESC, ";
        }
        query += " path;";

        return jdbcTemplate.query(
                query,
                params.toArray(),
                postMapper
        );
    }*/

   /* public List<PostModel>  getPostsInParentTreeSort(int threadId, int since, boolean desc, int limit) {

        String query = "";
        ArrayList<Object> params = new ArrayList<>();
        if (since != -1) {
            query += "WITH border_root_post as (SELECT root_post rp FROM posts WHERE id = ? LIMIT 1) ";
            params.add(since);
        }

        query +=" SELECT * " +
                " FROM posts" +
                " WHERE root_post IN ( " +
                //Ищем все посты верхнего уровня, которые будут в списке
                "SELECT DISTINCT id FROM posts " ;

        if (since != -1) {
            query += " CROSS JOIN border_root_post ";
        }
        query +=  "WHERE parent = 0 AND thread_id = ? ";
        params.add(threadId);

        if (since != -1) {
            query += "AND id ";

            if (desc) {
                query += " < ";
            } else {
                query += " > ";
            }
            //Получем самый поcледний (ограничивающий) пост самого верхнего уровня
            query += " border_root_post.rp ";
        }

        query += " ORDER BY id ";
        if (desc) {
            query += " DESC ";
        }

        if (limit != 0) {
            query += " LIMIT ? ";
            params.add(limit);
        }

        query += " ) ORDER BY ";
        if (desc ) {
            query += " root_post DESC, ";
        }
        query += " path;";

        return jdbcTemplate.query(
                query,
                params.toArray(),
                postMapper
        );
    }*/

    public void updatePostData(int id, PostUpdateModel newData){
        jdbcTemplate.update(
                "UPDATE posts SET message = ?, isEdited = TRUE  WHERE id = ?;",
                newData.getMessage(),
                id
        );
    }

}
