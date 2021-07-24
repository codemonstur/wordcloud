import bobthebuildtool.utils.MutableInteger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static wordcloud.WordCloudCommand.countWordsInFile;

public class SourceTextOutput {

    public static void main(final String... args) throws IOException {
        System.out.println(countWords(Paths.get("wordcloud/src/main/java")).keySet());
    }

    private static Map<String, MutableInteger> countWords(final Path sourceDir) throws IOException {
        final var map = new HashMap<String, MutableInteger>();
        Files.walk(sourceDir)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(path -> countWordsInFile(map, path));
        return map;
    }

}
