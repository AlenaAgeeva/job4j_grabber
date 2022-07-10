package store;

import parsing.Post;

import java.util.List;

/**
 *An interface for storing and transport data to a database
 */
public interface Store {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}
