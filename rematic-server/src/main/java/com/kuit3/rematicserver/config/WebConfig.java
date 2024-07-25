package com.kuit3.rematicserver.config;

import com.kuit3.rematicserver.common.argument_resolver.JwtAuthHandlerArgumentResolver;
import com.kuit3.rematicserver.common.interceptor.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthenticationInterceptor;
    private final JwtAuthHandlerArgumentResolver jwtAuthHandlerArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .order(1)
                .addPathPatterns("/auth/test")
                .addPathPatterns("/search/**")
                .excludePathPatterns("/search/post/guest")
//                .addPathPatterns("/post/**")
//                .excludePathPatterns("/post")
                .addPathPatterns("/user/**"); // 이게 없으면 인터셉터가 적용이 안되네요...
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtAuthHandlerArgumentResolver);
    }
}