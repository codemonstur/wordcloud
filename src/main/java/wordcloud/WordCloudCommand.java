package wordcloud;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.utils.MutableInteger;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static jcli.CliParserBuilder.newCliParser;

public enum WordCloudCommand {;

    public static final String DESCRIPTION = "Generates a wordcloud of the source code";

    @CliCommand(name = "wordcloud", description = DESCRIPTION)
    private static class Arguments {
        @CliOption(name = 't', longName = "target", description = "The target directory, if not set we write to project target dir")
        private String target;
        @CliOption(name = 'o', longName = "output", defaultValue = "wordcloud.png", description = "The file name of the wordcloud")
        private String outputFileName;
    }

    public static int run(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        final Arguments arguments = newCliParser(Arguments::new)
            .onErrorPrintHelpAndExit()
            .onHelpPrintHelpAndExit()
            .parse(args);

        generateWordCloud(toList(countWords(project)), toOutputFile(project, arguments));

        return 0;
    }

    private static String toOutputFile(final Project project, final Arguments arguments) {
        final String target = isNullOrEmpty(arguments.target) ? project.getBuildTarget().toString() : arguments.target;
        return target + File.separator + arguments.outputFileName;
    }

    private static Map<String, MutableInteger> countWords(final Project project) throws IOException {
        final var map = new HashMap<String, MutableInteger>();
        for (final var sourceDir : project.getSourceDirectories()) {
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
        }
        return map;
    }

    private static List<WordFrequency> toList(final Map<String, MutableInteger> frequencies) {
        final var list = new ArrayList<WordFrequency>(frequencies.size());
        frequencies.forEach((word, count) -> list.add(new WordFrequency(word, count.value())));
        return list;
    }

    private static void generateWordCloud(final List<WordFrequency> wordFrequencies, final String outputFile) {
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(outputFile);
    }

}
