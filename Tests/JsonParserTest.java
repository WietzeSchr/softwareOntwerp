import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class JsonParserTest {

    @Test
    void testJsonParser() throws FileNotFoundException {
        FileBufferView fbv1 = new FileBufferView(5,5, new Point(1,1), "/home/wietze/IdeaProjects/softwareOntwerp/testJson/jsonTest1.txt", "\n");
        fbv1.parseJson(new LayoutManager(fbv1, 1, "\n"));
    }
}
