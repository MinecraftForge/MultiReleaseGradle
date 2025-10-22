/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle;

import org.gradle.api.Action;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

/// The extension provides the interface to [register][#register] new [containers][MultiReleaseContainer] and points to
/// the last registered container.
public interface MultiReleaseExtension extends MultiReleaseContainer {
    /// The default name for this extension.
    String NAME = "multiRelease";

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the default jar task.
    ///
    /// @return The container
    MultiReleaseContainer register();

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the default jar task.
    ///
    /// @param action The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(Action<? super MultiReleaseContainer> action) {
        var container = register();
        action.execute(container);
        return container;
    }

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the given jar task.
    ///
    /// @param jar The jar task
    /// @return The container
    MultiReleaseContainer register(Jar jar);

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the given jar task.
    ///
    /// @param jar    The jar task
    /// @param action The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(Jar jar, Action<? super MultiReleaseContainer> action) {
        var container = register(jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the given jar task.
    ///
    /// @param jar The jar task
    /// @return The container
    MultiReleaseContainer register(TaskProvider<? extends Jar> jar);

    /// Registers a new container using the [main][SourceSet#MAIN_SOURCE_SET_NAME] source set and the given jar task.
    ///
    /// @param jar    The jar task
    /// @param action The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(TaskProvider<? extends Jar> jar, Action<? super MultiReleaseContainer> action) {
        var container = register(jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @return The container
    MultiReleaseContainer register(SourceSet sourceSet);

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(SourceSet sourceSet, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    MultiReleaseContainer register(SourceSet sourceSet, Jar jar);

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(SourceSet sourceSet, Jar jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    MultiReleaseContainer register(SourceSet sourceSet, TaskProvider<? extends Jar> jar);

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(SourceSet sourceSet, TaskProvider<? extends Jar> jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet) {
        return register(sourceSet.get());
    }

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet, Jar jar) {
        return register(sourceSet.get(), jar);
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet, Jar jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet, TaskProvider<? extends Jar> jar) {
        return register(sourceSet.get(), jar);
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(Provider<? extends SourceSet> sourceSet, TaskProvider<? extends Jar> jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet) {
        return register(sourceSet.asProvider());
    }

    /// Registers a new container using the given source set and its jar task defined by [SourceSet#getJarTaskName()].
    ///
    /// @param sourceSet The source set
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet, Jar jar) {
        return register(sourceSet.asProvider(), jar);
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet, Jar jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet, TaskProvider<? extends Jar> jar) {
        return register(sourceSet.asProvider(), jar);
    }

    /// Registers a new container using the given source set and jar task.
    ///
    /// @param sourceSet The source set
    /// @param jar       The jar task
    /// @param action    The action to execute on the container
    /// @return The container
    default MultiReleaseContainer register(ProviderConvertible<? extends SourceSet> sourceSet, TaskProvider<? extends Jar> jar, Action<? super MultiReleaseContainer> action) {
        var container = register(sourceSet, jar);
        action.execute(container);
        return container;
    }
}
