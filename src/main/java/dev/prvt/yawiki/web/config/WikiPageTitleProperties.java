package dev.prvt.yawiki.web.config;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "yawiki.web.title-identifier")
public class WikiPageTitleProperties {
    private final Map<String, Namespace> identifierMap;

    public WikiPageTitleProperties(
            @DefaultValue({"틀", "Template"}) List<String> template,
            @DefaultValue({"파일", "File"}) List<String> file,
            @DefaultValue({"분류", "Category"}) List<String> category,
            @DefaultValue({"대문", "Main"}) List<String> main
    ) {
        HashMap<String, Namespace> temp = new HashMap<>();
        template.forEach(str -> temp.put(str, Namespace.TEMPLATE));
        file.forEach(str -> temp.put(str, Namespace.FILE));
        category.forEach(str -> temp.put(str, Namespace.CATEGORY));
        main.forEach(str -> temp.put(str, Namespace.MAIN));

        this.identifierMap = Collections.unmodifiableMap(temp);
    }
}
