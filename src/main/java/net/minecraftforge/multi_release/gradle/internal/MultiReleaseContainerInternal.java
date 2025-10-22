/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.multi_release.gradle.internal;

import net.minecraftforge.multi_release.gradle.MultiReleaseContainer;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;

interface MultiReleaseContainerInternal extends MultiReleaseContainer, HasPublicType {
    @Override
    default TypeOf<?> getPublicType() {
        return TypeOf.typeOf(MultiReleaseContainer.class);
    }
}
