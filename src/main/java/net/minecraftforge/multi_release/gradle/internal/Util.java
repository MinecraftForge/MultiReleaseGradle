/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.gradleutils.shared.SharedUtil;

final class Util extends SharedUtil {
    private Util() { }

    static @Nullable SourceSet findSourceSetFromJar(Project project, String jarTaskName) {
        for (SourceSet sourceSet : project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets()) {
            if (sourceSet.getJarTaskName().equals(jarTaskName)) {
                return sourceSet;
            }
        }

        return null;
    }
}
