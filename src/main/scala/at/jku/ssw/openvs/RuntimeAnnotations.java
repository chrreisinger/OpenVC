/*
 *     OpenVC, an open source VHDL compiler/simulator
 *     Copyright (C) 2010  Christian Reisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.jku.ssw.openvs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

final public class RuntimeAnnotations {
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface PackageHeaderAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface PackageBodyAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ArchitectureAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface EntityAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ConfigurationAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ComponentAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface PostponedProcessAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ProcessAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface BlockAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface RecordAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface EnumerationAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ProtectedTypeHeaderAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ProtectedTypeBodyAnnotation {
    }
}