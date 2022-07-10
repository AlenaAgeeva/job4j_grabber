package parsing;

import java.util.List;

/**
 * An interface with one method for parsing.
 *
 * @author Alena Ageeva
 */
public interface Parse {
    List<Post> list(String link);
}
