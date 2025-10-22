/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConsumableConfiguration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.file.CopySpec;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.annotations.Range;

/// A container that holds the dependencies and objects to be used for the built multi-release JAR and the publishing
/// software component.
///
/// @see #add
/// @see #getJar()
/// @see #getComponent()
public interface MultiReleaseContainer {
    /* EXPOSED API */

    /// Gets the multi-release JAR task to be used for the multi-release output.
    ///
    /// This is *not* the same as the jar task passed in to [register][MultiReleaseExtension#register] this container.
    /// However, it does parent off of its contents using [Jar#with(CopySpec...)].
    ///
    /// The naming convention for this task is:
    /// `multiRelease${jar.`{@link org.codehaus.groovy.runtime.StringGroovyMethods#capitalize(CharSequence)
    /// capitalize()}`}`
    ///
    /// @return A provider for the output multi-release JAR task
    TaskProvider<Jar> getJar();

    /// Gets the consumable multi-release API elements used by [#getComponent()].
    ///
    /// This is *not* the same as the API elements configuration from the source set passed in to
    /// {@linkplain MultiReleaseExtension#register register} this container. However, it does parent off of its
    /// [attributes][Configuration#getAttributes()] and [dependencies][Configuration#getDependencies()] using
    /// [org.gradle.api.attributes.AttributeContainer#addAllLater(AttributeContainer)] and
    /// [Configuration#extendsFrom(Configuration...)] respectively.
    ///
    /// The naming convention for this configuration is
    /// `multiRelease${apiElements.`{@link org.codehaus.groovy.runtime.StringGroovyMethods#capitalize(CharSequence)
    /// capitalize()}`}`, where `apiElements` is [org.gradle.api.tasks.SourceSet#getApiElementsConfigurationName()].
    ///
    /// @return A provider for the consumable multi-release API elements
    NamedDomainObjectProvider<ConsumableConfiguration> getApiElements();

    /// Gets the consumable multi-release runtime elements used by [#getComponent()].
    ///
    /// This is *not* the same as the runtime elements configuration from the source set passed in to
    /// {@linkplain MultiReleaseExtension#register register} this container. However, it does parent off of its
    /// [attributes][Configuration#getAttributes()] and [dependencies][Configuration#getDependencies()] using
    /// [org.gradle.api.attributes.AttributeContainer#addAllLater(AttributeContainer)] and
    /// [Configuration#extendsFrom(Configuration...)] respectively.
    ///
    /// The naming convention for this configuration is:
    /// `multiRelease${runtimeElements.`{@link org.codehaus.groovy.runtime.StringGroovyMethods#capitalize(CharSequence)
    /// capitalize()}`}`, where `runtimeElements` is
    /// [org.gradle.api.tasks.SourceSet#getRuntimeElementsConfigurationName()].
    ///
    /// @return A provider for the consumable multi-release runtime elements
    NamedDomainObjectProvider<ConsumableConfiguration> getRuntimeElements();

    /// Gets the software component to be used to publish this container's [output JAR][#getJar()].
    ///
    /// While not a direct copy, the Multi-Release Java plugin attempts to parent off of the standard `java` software
    /// component as much as possible, using effective copies of [API elements][#getApiElements()] and
    /// {@linkplain #getRuntimeElements() runtime elements}, as well as adding the input source set's
    /// {@linkplain org.gradle.api.tasks.SourceSet#getSourcesElementsConfigurationName() sources elements} and
    /// {@linkplain org.gradle.api.tasks.SourceSet#getJavadocElementsConfigurationName() javadoc elements}, if they
    /// exist.
    ///
    /// The naming convention for this component is
    /// `multiRelease${sourceSet.name.`{@link org.codehaus.groovy.runtime.StringGroovyMethods#capitalize(CharSequence)
    /// capitalize()}`}Java`. If the source set is the
    /// {@linkplain org.gradle.api.tasks.SourceSet#isMain(org.gradle.api.tasks.SourceSet) main source set}, this will be
    /// `multiReleaseJava`.
    ///
    /// @return The software component containing the multi-release JAR
    AdhocComponentWithVariants getComponent();

    /* ADDING DEPENDENCIES */

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(int, Project, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Project dependency) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Project dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(int, Dependency, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Dependency dependency) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Dependency dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(int, Provider, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Provider<? extends Dependency> dependency) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, Provider<? extends Dependency> dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(int, ProviderConvertible, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, ProviderConvertible<? extends Dependency> dependency) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(@Range(from = 9, to = Integer.MAX_VALUE) int version, ProviderConvertible<? extends Dependency> dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaVersion, Project, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaVersion version, Project dependency) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(JavaVersion version, Project dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaVersion, Dependency, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaVersion version, Dependency dependency) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(JavaVersion version, Dependency dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaVersion, Provider, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaVersion version, Provider<? extends Dependency> dependency) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(JavaVersion version, Provider<? extends Dependency> dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaVersion, ProviderConvertible, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaVersion version, ProviderConvertible<? extends Dependency> dependency) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(JavaVersion version, ProviderConvertible<? extends Dependency> dependency, Action<? super Configuration> action) {
        add(JavaLanguageVersion.of(version.getMajorVersion()), dependency, action);
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaLanguageVersion, Project, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaLanguageVersion version, Project dependency) {
        this.add(version, dependency, it -> { });
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    void add(JavaLanguageVersion version, Project dependency, Action<? super Configuration> action);

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaLanguageVersion, Dependency, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaLanguageVersion version, Dependency dependency) {
        this.add(version, dependency, it -> { });
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    void add(JavaLanguageVersion version, Dependency dependency, Action<? super Configuration> action);

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaLanguageVersion, Provider, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaLanguageVersion version, Provider<? extends Dependency> dependency) {
        this.add(version, dependency, it -> { });
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    void add(JavaLanguageVersion version, Provider<? extends Dependency> dependency, Action<? super Configuration> action);

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. To make changes to the detached configuration used to add the
    /// dependency, use [#add(JavaLanguageVersion, ProviderConvertible, Action)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    default void add(JavaLanguageVersion version, ProviderConvertible<? extends Dependency> dependency) {
        add(version, dependency.asProvider());
    }

    /// Adds a dependency to the multi-release JAR for the given version.
    ///
    /// By default, the dependency will not be transitive. The given action will be run on the detached configuration
    /// used to add the dependency. This can, for example, be used to force the configuration to resolve transitively
    /// using [Configuration#setTransitive(boolean)].
    ///
    /// @param version    The version to use (must be at least 9)
    /// @param dependency The dependency to use
    /// @param action     The action to run on the detached configuration to be used for the dependency
    default void add(JavaLanguageVersion version, ProviderConvertible<? extends Dependency> dependency, Action<? super Configuration> action) {
        add(version, dependency.asProvider(), action);
    }
}
