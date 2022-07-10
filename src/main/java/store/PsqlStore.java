package store;

import parsing.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A class stores all parsed vacancies to a database.
 * It implements Store and AutoCloseable interfaces.
 *
 * @author Alena Ageeva
 */
public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    /**
     * A constructor that creates initial connection to a database
     *
     * @param cfg Properties for a connection
     */
    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * A method gets a Post object in arguments and put it to a database table through prepareStatement.
     * In addition, using generatedKeys it sets id por a Post object.
     *
     * @param post post object in arguments
     */
    @Override
    public void save(Post post) {
        try (PreparedStatement p = cnn.prepareStatement("insert into post(name,text,link,created) "
                + "values(?,?,?,?) ON CONFLICT (link) DO NOTHING", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, post.getTitle());
            p.setString(2, post.getDescription());
            p.setString(3, post.getLink());
            p.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            p.execute();
            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method get all uploaded Posts to a database table using prepareStatement
     *
     * @return a list of Posts
     */
    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement p = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = p.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(generatePost(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * A method finds a certain Post object by ID in a database table
     *
     * @param id id for a search
     * @return new Post object related to an ID in a database
     */
    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement p = cnn.prepareStatement("select * from post where id = ?")) {
            p.setInt(1, id);
            try (ResultSet resultSet = p.executeQuery()) {
                if (resultSet.next()) {
                    post = generatePost(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    /**
     * A method for generating Post object using ResultSet
     *
     * @param resultSet ResultSet from arguments
     * @return new Post object
     * @throws SQLException
     */
    private Post generatePost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("link"),
                resultSet.getString("text"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    /**
     * Overridden method of a AutoCloseable interface
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
