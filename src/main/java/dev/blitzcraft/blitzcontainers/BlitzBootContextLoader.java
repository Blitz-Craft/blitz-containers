package dev.blitzcraft.blitzcontainers;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.test.context.ReactiveWebMergedContextConfiguration;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.web.SpringBootMockServletContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.reactive.context.GenericReactiveWebApplicationContext;
import org.springframework.boot.web.servlet.support.ServletContextApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.SpringVersion;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoaderUtils;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link ContextLoader} that can be used to test Spring Boot applications (those that
 * normally startup using {@link SpringApplication}). Although this loader can be used
 * directly, most test will instead want to use it with
 * {@link SpringBootTest @SpringBootTest}.
 * <p>
 * The loader supports both standard {@link MergedContextConfiguration} as well as
 * {@link WebMergedContextConfiguration}. If {@link WebMergedContextConfiguration} is used
 * the context will either use a mock servlet environment, or start the full embedded web
 * server.
 * <p>
 * If {@code @ActiveProfiles} are provided in the test class they will be used to create
 * the application context.
 *
 * @see SpringBootTest
 */
public class BlitzBootContextLoader extends SpringBootContextLoader {
    // Ugly Hack, need to follow https://github.com/spring-projects/spring-boot/issues/15077

    @Override
    public ApplicationContext loadContext(MergedContextConfiguration config) throws Exception {
        Class<?>[] configClasses = config.getClasses();
        String[] configLocations = config.getLocations();
        Assert.state(!ObjectUtils.isEmpty(configClasses) || !ObjectUtils.isEmpty(configLocations),
                () -> "No configuration classes or locations found in @SpringApplicationConfiguration. "
                        + "For default configuration detection to work you need Spring 4.0.3 or better (found "
                        + SpringVersion.getVersion() + ").");
        SpringApplication application = getSpringApplication();
        application.setMainApplicationClass(config.getTestClass());
        application.addPrimarySources(Arrays.asList(configClasses));
        application.getSources().addAll(Arrays.asList(configLocations));
        List<ApplicationContextInitializer<?>> initializers = getInitializers(config, application);
        if (config instanceof WebMergedContextConfiguration) {
            application.setWebApplicationType(WebApplicationType.SERVLET);
            if (!isEmbeddedWebEnvironment(config)) {
                new WebConfigurer().configure(config, application, initializers);
            }
        } else if (config instanceof ReactiveWebMergedContextConfiguration) {
            application.setWebApplicationType(WebApplicationType.REACTIVE);
        } else {
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        application.setApplicationContextFactory((type) -> {
            if (type != WebApplicationType.NONE && !isEmbeddedWebEnvironment(config)) {
                if (type == WebApplicationType.REACTIVE) {
                    return new GenericReactiveWebApplicationContext();
                } else if (type == WebApplicationType.SERVLET) {
                    return new GenericWebApplicationContext();
                }
            }
            return ApplicationContextFactory.DEFAULT.create(type);
        });
        application.setInitializers(initializers);
        ConfigurableEnvironment environment = getEnvironment();
        if (environment != null) {
            prepareEnvironment(config, application, environment, false);
            application.setEnvironment(environment);
        } else {
            application.addListeners(new PrepareEnvironmentListener(config));
        }
        return application.run();
    }

    private void prepareEnvironment(MergedContextConfiguration config, SpringApplication application,
                                    ConfigurableEnvironment environment, boolean applicationEnvironment) {
        setActiveProfiles(environment, config.getActiveProfiles(), applicationEnvironment);
        ResourceLoader resourceLoader = (application.getResourceLoader() != null) ? application.getResourceLoader()
                : new DefaultResourceLoader(null);
        TestPropertySourceUtils.addPropertiesFilesToEnvironment(environment, resourceLoader,
                config.getPropertySourceLocations());
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, getInlinedProperties(config));
        //------------custom code
        addBlitzTestcontainersProperties(environment, config.getTestClass());
        //------------custom code ends
    }

    private void addBlitzTestcontainersProperties(ConfigurableEnvironment environment, Class<?> testClass) {
        var addonsProperties = Addon.detectActiveAddonsAndReturnProperties(testClass);
        var propertySource = new MapPropertySource("blitzContainersPropertySource", addonsProperties);
        environment.getPropertySources().addFirst(propertySource);
    }

    private void setActiveProfiles(ConfigurableEnvironment environment, String[] profiles,
                                   boolean applicationEnvironment) {
        if (ObjectUtils.isEmpty(profiles)) {
            return;
        }
        if (!applicationEnvironment) {
            environment.setActiveProfiles(profiles);
        }
        String[] pairs = new String[profiles.length];
        for (int i = 0; i < profiles.length; i++) {
            pairs[i] = "spring.profiles.active[" + i + "]=" + profiles[i];
        }
        TestPropertyValues.of(pairs).applyTo(environment, TestPropertyValues.Type.MAP, "active-test-profiles");
    }

    /**
     * Builds new {@link org.springframework.boot.SpringApplication} instance. You can
     * override this method to add custom behavior
     *
     * @return {@link org.springframework.boot.SpringApplication} instance
     */
    protected SpringApplication getSpringApplication() {
        return new SpringApplication();
    }

    /**
     * Returns the {@link ConfigurableEnvironment} instance that should be applied to
     * {@link SpringApplication} or {@code null} to use the default. You can override this
     * method if you need a custom environment.
     *
     * @return a {@link ConfigurableEnvironment} instance
     */
    protected ConfigurableEnvironment getEnvironment() {
        return null;
    }

    protected String[] getInlinedProperties(MergedContextConfiguration config) {
        ArrayList<String> properties = new ArrayList<>();
        // JMX bean names will clash if the same bean is used in multiple contexts
        disableJmx(properties);
        properties.addAll(Arrays.asList(config.getPropertySourceProperties()));
        return StringUtils.toStringArray(properties);
    }

    private void disableJmx(List<String> properties) {
        properties.add("spring.jmx.enabled=false");
    }

    /**
     * Return the {@link ApplicationContextInitializer initializers} that will be applied
     * to the context. By default this method will adapt {@link ContextCustomizer context
     * customizers}, add {@link SpringApplication#getInitializers() application
     * initializers} and add
     * {@link MergedContextConfiguration#getContextInitializerClasses() initializers
     * specified on the test}.
     *
     * @param config      the source context configuration
     * @param application the application instance
     * @return the initializers to apply
     * @since 2.0.0
     */
    protected List<ApplicationContextInitializer<?>> getInitializers(MergedContextConfiguration config,
                                                                     SpringApplication application) {
        List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();
        for (ContextCustomizer contextCustomizer : config.getContextCustomizers()) {
            initializers.add(new ContextCustomizerAdapter(contextCustomizer, config));
        }
        initializers.addAll(application.getInitializers());
        for (Class<? extends ApplicationContextInitializer<?>> initializerClass : config
                .getContextInitializerClasses()) {
            initializers.add(BeanUtils.instantiateClass(initializerClass));
        }
        if (config.getParent() != null) {
            initializers.add(new ParentContextApplicationContextInitializer(config.getParentApplicationContext()));
        }
        return initializers;
    }

    //"overridden" method, all the rest should be a copy of SpringBootContextLoader
    private boolean isEmbeddedWebEnvironment(MergedContextConfiguration config) {
        return MergedAnnotations.from(config.getTestClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .get(BlitzBootTest.class)
                .synthesize(MergedAnnotation::isPresent)
                .map(BlitzBootTest::webEnvironment)
                .orElse(SpringBootTest.WebEnvironment.NONE)
                .isEmbedded();
    }

    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        super.processContextConfiguration(configAttributes);
        if (!configAttributes.hasResources()) {
            Class<?>[] defaultConfigClasses = detectDefaultConfigurationClasses(configAttributes.getDeclaringClass());
            configAttributes.setClasses(defaultConfigClasses);
        }
    }

    /**
     * Detect the default configuration classes for the supplied test class. By default
     * simply delegates to
     * {@link AnnotationConfigContextLoaderUtils#detectDefaultConfigurationClasses}.
     *
     * @param declaringClass the test class that declared {@code @ContextConfiguration}
     * @return an array of default configuration classes, potentially empty but never
     * {@code null}
     * @see AnnotationConfigContextLoaderUtils
     */
    protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
        return AnnotationConfigContextLoaderUtils.detectDefaultConfigurationClasses(declaringClass);
    }

    @Override
    public ApplicationContext loadContext(String... locations) throws Exception {
        throw new UnsupportedOperationException(
                "SpringApplicationContextLoader does not support the loadContext(String...) method");
    }

    @Override
    protected String[] getResourceSuffixes() {
        return new String[]{"-context.xml", "Context.groovy"};
    }

    @Override
    protected String getResourceSuffix() {
        throw new IllegalStateException();
    }

    /**
     * Inner class to configure {@link WebMergedContextConfiguration}.
     */
    private static class WebConfigurer {

        void configure(MergedContextConfiguration configuration, SpringApplication application,
                       List<ApplicationContextInitializer<?>> initializers) {
            WebMergedContextConfiguration webConfiguration = (WebMergedContextConfiguration) configuration;
            addMockServletContext(initializers, webConfiguration);
        }

        private void addMockServletContext(List<ApplicationContextInitializer<?>> initializers,
                                           WebMergedContextConfiguration webConfiguration) {
            SpringBootMockServletContext servletContext = new SpringBootMockServletContext(
                    webConfiguration.getResourceBasePath());
            initializers.add(0, new WebConfigurer.DefensiveWebApplicationContextInitializer(
                    new ServletContextApplicationContextInitializer(servletContext, true)));
        }

        /**
         * Decorator for {@link ServletContextApplicationContextInitializer} that prevents
         * a failure when the context type is not as was predicted when the initializer
         * was registered. This can occur when spring.main.web-application-type is set to
         * something other than servlet.
         */
        private static final class DefensiveWebApplicationContextInitializer
                implements ApplicationContextInitializer<ConfigurableApplicationContext> {

            private final ServletContextApplicationContextInitializer delegate;

            private DefensiveWebApplicationContextInitializer(ServletContextApplicationContextInitializer delegate) {
                this.delegate = delegate;
            }

            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                if (applicationContext instanceof ConfigurableWebApplicationContext) {
                    this.delegate.initialize((ConfigurableWebApplicationContext) applicationContext);
                }
            }

        }

    }

    /**
     * Adapts a {@link ContextCustomizer} to a {@link ApplicationContextInitializer} so
     * that it can be triggered via {@link SpringApplication}.
     */
    private static class ContextCustomizerAdapter
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private final ContextCustomizer contextCustomizer;

        private final MergedContextConfiguration config;

        ContextCustomizerAdapter(ContextCustomizer contextCustomizer, MergedContextConfiguration config) {
            this.contextCustomizer = contextCustomizer;
            this.config = config;
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            this.contextCustomizer.customizeContext(applicationContext, this.config);
        }

    }

    /**
     * {@link ApplicationContextInitializer} used to set the parent context.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    private static class ParentContextApplicationContextInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private final ApplicationContext parent;

        ParentContextApplicationContextInitializer(ApplicationContext parent) {
            this.parent = parent;
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.setParent(this.parent);
        }

    }

    /**
     * {@link ApplicationListener} used to prepare the application created environment.
     */
    private class PrepareEnvironmentListener
            implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, PriorityOrdered {

        private final MergedContextConfiguration config;

        PrepareEnvironmentListener(MergedContextConfiguration config) {
            this.config = config;
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            prepareEnvironment(this.config, event.getSpringApplication(), event.getEnvironment(), true);
        }

    }
}

