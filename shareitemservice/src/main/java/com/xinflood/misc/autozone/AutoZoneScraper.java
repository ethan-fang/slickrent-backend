package com.xinflood.misc.autozone;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Range;
import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.xinflood.dao.ImageDao;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RentalPricePerHour;
import com.xinflood.misc.ItemScraper;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by xinxinwang on 2/7/15.
 */
public class AutoZoneScraper implements ItemScraper {

    private static final Logger LOG = LoggerFactory.getLogger(AutoZoneScraper.class);
    private final String baseUrl = "http://www.autozone.com";
    private final Client client;
    private final ImageDao imageDao;

    public AutoZoneScraper(ImageDao imageDao, Client client) {
        this.imageDao = imageDao;
        this.client = client;
    }



    public List<Tool> getTools() throws IOException {
        Document doc = Jsoup.connect(baseUrl + "/landing/page.jsp?name=loan-a-tool").get();

        Iterator<Element> it = doc.select("#row-one .grid-4 h3 a").listIterator();

        ImmutableList<ToolCategory> toolCategories = ImmutableList.copyOf(Iterators.transform(it, input -> {
            try {
                URL categoryUrl = new URL(baseUrl + input.attr("href"));
                String categoryName = input.text();
                System.out.println(categoryName);
                Iterable<Tool> tools = getTools(categoryName, categoryUrl);

                return new ToolCategory(categoryName, categoryUrl, ImmutableList.copyOf(tools));

            } catch (Exception e) {
                Throwables.propagate(e);
            }

            return null;
        }));

        ImmutableList.Builder<Tool> toolBuilder = ImmutableList.builder();

        toolCategories.forEach(new Consumer<ToolCategory>() {
            @Override
            public void accept(ToolCategory toolCategory) {
                toolBuilder.addAll(toolCategory.getTools());
            }
        });
        return toolBuilder.build();
    }


    @Override
    public List<Item> getItems() throws IOException {
        List<Tool> tools = getTools();

        return
            tools.stream().map(
                    tool -> {
                        UUID imageUuid = UUID.randomUUID();
                        WebResource resource = client.resource(tool.getImageUrl().toString());
                        try(InputStream inputStream = resource.get(InputStream.class)) {
                            imageDao.putImage(imageUuid.toString(), ByteStreams.toByteArray(inputStream));
                        } catch (IOException e) {
                            LOG.error("error in creating item",  e);
                            Throwables.propagate(e);
                        }
                        return new Item(
                                UUID.randomUUID(),
                                tool.getToolName(),
                                tool.getCategoryName(),
                                Optional.<RentalPricePerHour>absent(),
                                Optional.<Range<DateTime>>absent(),
                                ImmutableList.of(imageUuid),
                                UUID.fromString("7ffc2295-6875-4f40-bc65-827b8fd4535e") //autozone userid
                        );

                    }
            )
            .collect(Collectors.toList());
    }

    private Iterable<Tool> getTools(String categoryName, URL toolPageLink) throws IOException {
        Document doc = Jsoup.connect(toolPageLink.toString()).get();

        Iterable<Element> filtered = Iterables.filter(doc.select("#mainContent div.grid-24.clearfix .flush a.prodImg"), input -> {


            Elements span = input.select("span");
            Elements img = input.select("img");

            try {
                return !(span.isEmpty() || img.isEmpty())
                        && new URL(img.get(0).attr("src")) != null;
            } catch (MalformedURLException e) {
                return false;
            }
        });

        return Iterables.transform(filtered, new ToolExtractor(categoryName, baseUrl));
    }


    public static void main(String[] args) throws IOException {
//        try (InputStream inputStream = Files.asByteSource(new File("shareitemservice/shareitemservice.yml")).openStream()) {
//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//
//            mapper.registerModule(new GuavaModule());
//            mapper.registerModule(new LogbackModule());
//            mapper.registerModule(new GuavaExtrasModule());
//            mapper.registerModule(new JodaModule());
//
//            ShareItemServerConfiguration shareItemServerConfiguration = mapper.readValue(inputStream, ShareItemServerConfiguration.class);
//            System.out.println(shareItemServerConfiguration);
//        }



//
//
//        new AutoZoneScraper(new S3ImageDao());
    }



}
