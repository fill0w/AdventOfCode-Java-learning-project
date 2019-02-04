package org.basseur.adventofcode.advent2018;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.no;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.basseur.adventofcode.advent2018.archUnitTestingUtils.FieldsCondition.haveFieldsThat;
import static org.basseur.adventofcode.advent2018.archUnitTestingUtils.IsAutowired.autowired;
import static org.basseur.adventofcode.advent2018.archUnitTestingUtils.IsSpringBootTest.springBootTests;
import static org.basseur.adventofcode.advent2018.archUnitTestingUtils.MethodCondition.be;
import static org.basseur.adventofcode.advent2018.archUnitTestingUtils.MethodTransformer.methods;

public class SpringBootConventionsTest {

    private final JavaClasses allClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DontIncludeTests())
            .importPackages("org.basseur.adventofcode");

    /**
     * @return Predicate that matches Spring Boot Application classes.
     */
    @Nonnull
    private static DescribedPredicate<CanBeAnnotated> springBootApplicationClass() {
        return annotatedWith(SpringBootApplication.class).as("@%s class", SpringBootApplication.class.getSimpleName());
    }

    @Test
    public void doNotCreateBeansInApplicationClass() {
        no(methods())
            .that(are(annotatedWith(Bean.class)))
            .should(be(declaredIn(springBootApplicationClass())))
            .because("this may lead to problems with missing dependencies in tests that bootstrap the Spring Boot Application only partially (e.g. @DataMongoTest tests). Simply declare your beans in separate @" + Configuration.class.getSimpleName() + " classes.")
            .check(allClasses);
    }

    @Test
    public void validateConfigurations() {
        classes()
            .that(are(annotatedWith(Configuration.class)))
            .should()
            .beAnnotatedWith(Validated.class)
            .because("otherwise mapped configuration values are *not* validated by Spring Boot.")
            .check(allClasses);
    }

    @Test
    public void doNotAutowireFields() {
        noClasses()
            .that(are(not(springBootTests())))
            .should(haveFieldsThat(are(autowired())))
            .because("autowiring fields is not recommended and makes classes harder to test. Inject necessary objects via constructor, split classes that require too many autowired constructor parameters.")
            .check(allClasses);
    }
}
