package com.xinflood.misc.autozone;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.net.URL;

/**
 * Created by xinxinwang on 2/7/15.
 */
class ToolCategory {
    private final String categoryName;
    private final URL link;
    private final ImmutableList<Tool> tools;

    ToolCategory(String categoryName, URL link, ImmutableList<Tool> tools) {
        this.categoryName = categoryName;
        this.link = link;
        this.tools = tools;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public URL getLink() {
        return link;
    }

    public ImmutableList<Tool> getTools() {
        return tools;
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
