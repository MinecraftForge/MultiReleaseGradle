/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import net.minecraftforge.gradleutils.shared.EnhancedProblems;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.problems.Severity;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import javax.inject.Inject;
import java.io.Serial;

abstract class MultiReleaseProblems extends EnhancedProblems {
    private static final @Serial long serialVersionUID = 9118333711048006759L;

    @Inject
    public MultiReleaseProblems() {
        super(MultiReleasePlugin.NAME, MultiReleasePlugin.DISPLAY_NAME);
    }

    void reportContainerNotRegistered(String extensionName) {
        report("multi-release-extension-without-container", "Multi-release container was not registered", spec -> spec
            .details("""
                Attempted to use multi-release functionality before the first container has been registered.
                The default functionality was used: to use the 'main' sourceSet and the 'jar' task.
                It is strongly recommended for you to declare the registration using `%s.register`, even if you are using the default functionality.""".formatted(extensionName))
            .severity(Severity.ADVICE)
            .solution("Register the container using `%s.register` before using any other functionality.".formatted(extensionName))
            .solution(HELP_MESSAGE));
    }

    RuntimeException multiReleaseVersionTooLow(JavaLanguageVersion version, Dependency dependency) {
        var e = new IllegalArgumentException("Multi-release version %s is too low, minimum is 9".formatted(version));
        return throwing(e, "", "", spec -> spec
            .details("""
                Cannot add a multi-release dependency for a Java version lower than 9.
                Affected dependency: %s""".formatted(dependency))
            .severity(Severity.ERROR)
            .details("Use at least language level 9 for multi-release dependencies.")
            .details("Use separately-built JARs for Java versions lower than 9 (i.e. 5, 6, and 8).")
            .details(HELP_MESSAGE));
    }
}
