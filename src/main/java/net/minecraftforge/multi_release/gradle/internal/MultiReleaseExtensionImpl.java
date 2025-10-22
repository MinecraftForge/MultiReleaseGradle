/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import net.minecraftforge.multi_release.gradle.MultiReleaseContainer;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

abstract class MultiReleaseExtensionImpl implements MultiReleaseExtensionInternal {
    protected abstract @Inject Project getProject();

    protected abstract @Inject ObjectFactory getObjects();

    private final String name;
    private @Nullable MultiReleaseContainerInternal container;

    private final MultiReleaseProblems problems = getObjects().newInstance(MultiReleaseProblems.class);

    @Inject
    public MultiReleaseExtensionImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /* REGISTERING CONTAINERS */

    @Override
    public MultiReleaseContainer register() {
        final var main = getMainSourceSet();
        return register(main, getJarTask(main));
    }

    @Override
    public MultiReleaseContainer register(Jar jar) {
        return register(findSourceSetFromJar(jar.getName()), getJarTask(jar.getName()));
    }

    @Override
    public MultiReleaseContainer register(TaskProvider<? extends Jar> jar) {
        return register(findSourceSetFromJar(jar.getName()), jar);
    }

    @Override
    public MultiReleaseContainer register(SourceSet sourceSet) {
        return register(sourceSet, getJarTask(sourceSet));
    }

    @Override
    public MultiReleaseContainer register(SourceSet sourceSet, Jar jar) {
        return register(sourceSet, getJarTask(jar.getName()));
    }

    @Override
    public MultiReleaseContainer register(SourceSet sourceSet, TaskProvider<? extends Jar> jar) {
        return this.container = getObjects().newInstance(MultiReleaseContainerImpl.class, sourceSet, jar);
    }

    @Override
    public MultiReleaseContainerInternal getContainer() {
        if (this.container == null) {
            problems.reportContainerNotRegistered(getName());
            this.register();
        }

        return this.container;
    }

    /* UTILITY METHODS */

    private SourceSet getMainSourceSet() {
        return getProject().getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    private SourceSet findSourceSetFromJar(String jarTaskName) {
        var ret = Util.findSourceSetFromJar(getProject(), jarTaskName);
        if (ret == null)
            throw new IllegalArgumentException("Could not find source set for jar task " + jarTaskName);
        return ret;
    }

    private TaskProvider<Jar> getJarTask(String name) {
        return getProject().getTasks().named(name, Jar.class);
    }

    private TaskProvider<Jar> getJarTask(SourceSet sourceSet) {
        return getJarTask(sourceSet.getJarTaskName());
    }
}
