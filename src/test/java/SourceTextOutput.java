import bobthebuildtool.utils.MutableInteger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;

public class SourceTextOutput {

    public static void main(final String... args) throws IOException {
        System.out.println(countWords(Paths.get("wordcloud/src/main/java")).keySet());
    }

    private static Map<String, MutableInteger> countWords(final Path sourceDir) throws IOException {
        final var map = new HashMap<String, MutableInteger>();
        Files.walk(sourceDir).filter(Files::isRegularFile).forEach(sourceFile -> {
            try {
                final var words = Files.readString(sourceFile)
                        .replaceAll("[^0-9a-zA-Z]", " ").split(" ");
                for (final var word : words) {
                    if (isNullOrEmpty(word)) continue;
                    map.computeIfAbsent(word.toLowerCase(), s -> new MutableInteger()).increment();
                }
            } catch (Exception e) {}
        });
        return map;
    }

}
