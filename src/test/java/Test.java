import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

public class Test extends Representer {
    public Test() {
        super(new DumperOptions());
    }
}
