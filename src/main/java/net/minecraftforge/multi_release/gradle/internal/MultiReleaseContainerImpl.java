/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import net.minecraftforge.gradleutils.shared.Closures;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConsumableConfiguration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.file.ArchiveOperations;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.java.archives.Manifest;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import javax.inject.Inject;

abstract class MultiReleaseContainerImpl implements MultiReleaseContainerInternal {
    protected abstract @Inject Project getProject();

    protected abstract @Inject ObjectFactory getObjects();

    protected abstract @Inject ProviderFactory getProviders();

    protected abstract @Inject ArchiveOperations getArchiveOperations();

    protected abstract @Inject SoftwareComponentFactory getSoftwareComponentFactory();

    protected abstract @Inject DependencyFactory getDependencyFactory();

    private final MultiReleaseProblems problems = getObjects().newInstance(MultiReleaseProblems.class);

    private final SourceSet sourceSet;
    private final TaskProvider<? extends Jar> jar;
    private final TaskProvider<Jar> multiReleaseJar;

    private final Attribute<Boolean> attribute;
    private final NamedDomainObjectProvider<ConsumableConfiguration> apiElements;
    private final NamedDomainObjectProvider<ConsumableConfiguration> runtimeElements;
    private final AdhocComponentWithVariants softwareComponent;

    @Inject
    public MultiReleaseContainerImpl(SourceSet sourceSet, TaskProvider<? extends Jar> jar) {
        this.sourceSet = sourceSet;
        this.jar = jar;
        this.multiReleaseJar = createJar();

        this.attribute = Attribute.of("net.minecraftforge.multi-release." + sourceSet.getName(), Boolean.class);
        this.apiElements = createConfiguration(getProject().getConfigurations().named(sourceSet.getApiElementsConfigurationName()));
        this.runtimeElements = createConfiguration(getProject().getConfigurations().named(sourceSet.getRuntimeElementsConfigurationName()));
        this.softwareComponent = createSoftwareComponent();
    }

    /* SETUP */

    private TaskProvider<Jar> createJar() {
        return getProject().getTasks().register("multiRelease" + StringGroovyMethods.capitalize(jar.getName()), Jar.class, task -> {
            task.setGroup(LifecycleBasePlugin.BUILD_GROUP);
            task.dependsOn(jar);

            task.with(jar.get());
            task.setManifest(jar.get().getManifest());

            task.manifest(Closures.<Manifest>consumer(manifest -> manifest.getAttributes().put("Multi-Release", "true")));
        });
    }

    private NamedDomainObjectProvider<ConsumableConfiguration> createConfiguration(NamedDomainObjectProvider<? extends Configuration> baseConfiguration) {
        return getProject().getConfigurations().consumable("multiRelease" + StringGroovyMethods.capitalize(baseConfiguration.getName()), configuration -> {
            var description = baseConfiguration.map(Configuration::getDescription).map(StringGroovyMethods::uncapitalize);
            if (description.isPresent())
                configuration.setDescription("Multi-release " + description.get());

            configuration.attributes(attributes -> {
                attributes.attribute(attribute, true);
                attributes.addAllLater(baseConfiguration.map(Configuration::getAttributes).get());
            });

            configuration.outgoing(outgoing ->
                outgoing.artifact(multiReleaseJar)
            );

            configuration.extendsFrom(baseConfiguration.get());
        });
    }

    private AdhocComponentWithVariants createSoftwareComponent() {
        var softwareComponent = getSoftwareComponentFactory().adhoc("multiRelease" + (SourceSet.isMain(sourceSet) ? "" : StringGroovyMethods.capitalize(sourceSet.getName())) + "Java");
        softwareComponent.addVariantsFromConfiguration(apiElements.get(), variant -> variant.mapToMavenScope("compile"));
        softwareComponent.addVariantsFromConfiguration(runtimeElements.get(), variant -> variant.mapToMavenScope("runtime"));
        Util.ensureAfterEvaluate(getProject(), project -> {
            var sourcesElements = project.getConfigurations().findByName(sourceSet.getSourcesElementsConfigurationName());
            if (sourcesElements != null) {
                softwareComponent.addVariantsFromConfiguration(sourcesElements, variant -> {
                    variant.mapToMavenScope("runtime");
                    variant.mapToOptional();
                });
            }

            var javadocElements = project.getConfigurations().findByName(sourceSet.getJavadocElementsConfigurationName());
            if (javadocElements != null) {
                softwareComponent.addVariantsFromConfiguration(javadocElements, variant -> {
                    variant.mapToMavenScope("runtime");
                    variant.mapToOptional();
                });
            }
        });
        getProject().getComponents().add(softwareComponent);
        return softwareComponent;
    }

    /* EXPOSED API */

    @Override
    public TaskProvider<Jar> getJar() {
        return this.multiReleaseJar;
    }

    @Override
    public NamedDomainObjectProvider<ConsumableConfiguration> getApiElements() {
        return this.apiElements;
    }

    @Override
    public NamedDomainObjectProvider<ConsumableConfiguration> getRuntimeElements() {
        return this.runtimeElements;
    }

    @Override
    public AdhocComponentWithVariants getComponent() {
        return this.softwareComponent;
    }

    /* ADDING DEPENDENCIES */

    @Override
    public void add(JavaLanguageVersion version, Project dependency, Action<? super Configuration> action) {
        this.add(version, getDependencyFactory().create(dependency), action);
    }

    @Override
    public void add(JavaLanguageVersion version, Dependency dependency, Action<? super Configuration> action) {
        this.add(version, getProject().getConfigurations().detachedConfiguration(dependency), action);
    }

    @Override
    public void add(JavaLanguageVersion version, Provider<? extends Dependency> dependency, Action<? super Configuration> action) {
        this.add(version, getProject().getConfigurations().detachedConfiguration().withDependencies(dependencies -> dependencies.addLater(dependency)), action);
    }

    private void add(JavaLanguageVersion version, Configuration configuration, Action<? super Configuration> action) {
        if (version.compareTo(JavaLanguageVersion.of(8)) <= 0) {
            var dependency = configuration.getDependencies().iterator().next();
            throw problems.multiReleaseVersionTooLow(version, dependency);
        }

        configuration.setTransitive(false);
        action.execute(configuration);

        Util.ensureAfterEvaluate(getProject(), project ->
            multiReleaseJar.get().configure(Closures.<Jar>consumer(multiReleaseJar -> {
                multiReleaseJar.dependsOn(configuration.getBuildDependencies());
                multiReleaseJar.into("META-INF/versions/" + version.asInt(), into -> {
                    into.setDuplicatesStrategy(DuplicatesStrategy.WARN);
                    for (var file : configuration.getFiles()) {
                        into.from(getProviders().provider(() -> getArchiveOperations().zipTree(file)), from -> {
                            from.setDuplicatesStrategy(DuplicatesStrategy.WARN);
                            from.exclude("**/META-INF/**");
                        });
                    }
                });
            }))
        );
    }
}
