import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 */
public class ScraperMain {

    private static final String BASE_URL_STR = "http://www.autozone.com";

    public static void main(String[] args) throws IOException {
        process();
    }

    private static void process() throws IOException {
        final URL url = new URL(BASE_URL_STR);
        Document doc = Jsoup.connect(url + "/landing/page.jsp?name=loan-a-tool").get();

        Iterator<Element> it = doc.select("#row-one .grid-4 h3 a").listIterator();
        ImmutableList<ToolCategory> toolCategories = ImmutableList.copyOf(Iterators.transform(it, new Function<Element, ToolCategory>() {
            @Override
            public ToolCategory apply(Element input) {
                try {
                    URL categoryUrl = new URL(url + input.attr("href"));
                    String categoryName = input.text();
                    System.out.println(categoryName);
                    Iterable<Tool> tools = getTools(categoryName, categoryUrl);

                    return new ToolCategory(categoryName, categoryUrl, ImmutableList.copyOf(tools));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }));

        System.out.println(toolCategories);
    }

    private static Iterable<Tool> getTools(String categoryName, URL toolPageLink) throws IOException {
        Document doc = Jsoup.connect(toolPageLink.toString()).get();

        Iterable<Element> filtered = Iterables.filter(doc.select("#mainContent div.grid-24.clearfix .flush a.prodImg"), new Predicate<Element>() {
            @Override
            public boolean apply( Element input) {


                Elements span = input.select("span");
                Elements img = input.select("img");

                try {
                    return ! (span.isEmpty() || img.isEmpty())
                            && new URL(img.get(0).attr("src")) !=null;
                } catch (MalformedURLException e) {
                    return false;
                }
            }
        });

        return Iterables.transform(filtered, new ToolExtractor(categoryName, BASE_URL_STR));
    }

    private static class ToolExtractor implements Function<Element, Tool> {

        private final String categoryName;
        private final URL baseUrl;

        private ToolExtractor(String categoryName, String baseUrlString) throws MalformedURLException {
            this.categoryName = categoryName;
            this.baseUrl = new URL(baseUrlString);
        }

        @Override
        public Tool apply(Element input) {

            Elements span = input.select("span");
            Elements img = input.select("img");

            try {
                URL toolUrl = new URL(baseUrl.toString() + input.attr("href"));
                return new Tool(categoryName, span.get(0).text(), new URL(img.get(0).attr("src")), toolUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println(ImmutableMap.of("span", span, "img", img));
            }
            throw new RuntimeException();
        }
    }


    private static class ToolCategory {
        private final String categoryName;
        private final URL link;
        private final ImmutableList<Tool> tools;

        private ToolCategory(String categoryName, URL link, ImmutableList<Tool> tools) {
            this.categoryName = categoryName;
            this.link = link;
            this.tools = tools;
        }


        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("categoryName", categoryName)
                    .add("link", link)
                    .add("tools", tools)
                    .toString();
        }
    }

    private static class Tool {
        private final String categoryName;
        private final String toolName;
        private final URL imageUrl;
        private final URL toolUrl;

        private Tool(String categoryName, String toolName, URL imageUrl, URL toolUrl) {
            this.categoryName = categoryName;
            this.toolName = toolName;
            this.imageUrl = imageUrl;
            this.toolUrl = toolUrl;
        }


        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("categoryName", categoryName)
                    .add("toolName", toolName)
                    .add("imageUrl", imageUrl)
                    .add("toolUrl", toolUrl)
                    .toString();
        }
    }
}