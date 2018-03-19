import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jonathan on 3/18/18.
 */
public class UserNameGenerator {

    private List<String> usernameList;
    private Random random;

    public UserNameGenerator() {
        usernameList = new ArrayList<>();
        usernameList.add("Bear");
        usernameList.add("Hippo");
        usernameList.add("Chicken");
        usernameList.add("Kitty");
        usernameList.add("Puppy");
        usernameList.add("Penguin");
        usernameList.add("Hamster");
        usernameList.add("Beaver");
        usernameList.add("Horse");

        random = new Random();
    }
    public String generateName() {
        return usernameList.get(random.nextInt(usernameList.size()));
    }
}
