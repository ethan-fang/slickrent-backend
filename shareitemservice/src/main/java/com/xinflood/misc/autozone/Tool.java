package com.xinflood.misc.autozone;

import com.google.common.base.MoreObjects;

import java.net.URL;

/**
 * Created by xinxinwang on 2/7/15.
 */
class Tool {
    private final String categoryName;
    private final String toolName;
    private final URL imageUrl;
    private final URL toolUrl;

    Tool(String categoryName, String toolName, URL imageUrl, URL toolUrl) {
        this.categoryName = categoryName;
        this.toolName = toolName;
        this.imageUrl = imageUrl;
        this.toolUrl = toolUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getToolName() {
        return toolName;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public URL getToolUrl() {
        return toolUrl;
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