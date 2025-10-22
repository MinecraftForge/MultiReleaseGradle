/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import net.minecraftforge.multi_release.gradle.MultiReleaseExtension;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConsumableConfiguration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.provider.Provider;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

interface MultiReleaseExtensionInternal extends MultiReleaseExtension, HasPublicType, Named {
    @Override
    default TypeOf<?> getPublicType() {
        return TypeOf.typeOf(MultiReleaseExtension.class);
    }

    /// The last registered container to delegate to.
    MultiReleaseContainerInternal getContainer();

    /* CONTAINER DELEGATION */

    @Override
    default TaskProvider<Jar> getJar() {
        return this.getContainer().getJar();
    }

    @Override
    default NamedDomainObjectProvider<ConsumableConfiguration> getApiElements() {
        return this.getContainer().getApiElements();
    }

    @Override
    default NamedDomainObjectProvider<ConsumableConfiguration> getRuntimeElements() {
        return this.getContainer().getRuntimeElements();
    }

    @Override
    default AdhocComponentWithVariants getComponent() {
        return this.getContainer().getComponent();
    }

    @Override
    default void add(JavaLanguageVersion version, Project dependency, Action<? super Configuration> action) {
        this.getContainer().add(version, dependency, action);
    }

    @Override
    default void add(JavaLanguageVersion version, Dependency dependency, Action<? super Configuration> action) {
        this.getContainer().add(version, dependency, action);
    }

    @Override
    default void add(JavaLanguageVersion version, Provider<? extends Dependency> dependency, Action<? super Configuration> action) {
        this.getContainer().add(version, dependency, action);
    }
}
