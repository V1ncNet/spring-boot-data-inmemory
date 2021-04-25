package de.vinado.spring.data.inmemory.repository.config;

import de.vinado.spring.data.inmemory.repository.support.InMemoryRepositoryFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable in-memory repositories. Will scan the package of the annotated configuration class for Spring
 * Data repositories by default.
 *
 * @author Vincent Nadoll
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({InMemoryRepositoriesRegistrar.class, DelegatingInMemoryRepositoryConfiguration.class})
public @interface EnableInMemoryRepositories {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableInMemoryRepositories("de.team7.swt")} instead of
     * {@code @EnableInMemoryRepositories(basePackages="de.team7.swt")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with)
     * this attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components.
     * The package of each class specified will be scanned. Consider creating a special no-op marker class or interface
     * in each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or
     * filters.
     */
    Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * This property has no effect due to its missing implementation.
     *
     * @return the location of where to find the Spring Data named queries properties file.
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link InMemoryRepositoryFactoryBean}.
     */
    Class<?> repositoryFactoryBeanClass() default InMemoryRepositoryFactoryBean.class;
}
