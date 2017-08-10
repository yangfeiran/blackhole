package com.chinacloud.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.filters.StaticResponseFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StaticFilter extends StaticResponseFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public Object uri() {
        return Pattern.compile("/api/static.*");
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public String responseBody() {
        String requestURI = RequestContext.getCurrentContext().getRequest().getRequestURI();
		if (requestURI.endsWith(".svg")) {

            RequestContext.getCurrentContext().getResponse().setContentType("image/svg+xml");

            try {
                InputStream inputStream = resourceLoader.getResource("classpath:static/example.svg").getInputStream();
                String result = new BufferedReader(new InputStreamReader(inputStream))
                        .lines()
                        .collect(Collectors.joining("\n"));
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }

        } else {

        	// RequestContext.getCurrentContext().getResponse().setContentType("image/svg+xml");
        	String file = requestURI.substring(4);
        	System.out.println(requestURI+"=="+file);

             try {
                 InputStream inputStream = resourceLoader.getResource("classpath:"+file).getInputStream();
                 String result = new BufferedReader(new InputStreamReader(inputStream))
                         .lines()
                         .collect(Collectors.joining("\n"));
                 return result;
             } catch (IOException e) {
                 e.printStackTrace();
                 return e.getMessage();
             }

        }
    }
}
