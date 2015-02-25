package com.xinflood.misc.autozone;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xinxinwang on 2/7/15.
 */
class ToolExtractor implements Function<Element, Tool> {

    private final String categoryName;
    private final URL baseUrl;

    ToolExtractor(String categoryName, String baseUrlString) throws MalformedURLException {
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
