/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.czentral.data.binary.serializer;

import java.lang.annotation.Annotation;
import org.czentral.data.binary.PropertyHandler;
import java.lang.reflect.AnnotatedElement;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
abstract class AbstractPropertyAccessor implements PropertyHandler {
    
    public Class type;

    public AnnotatedElement annotatedElement;
    
    public String name;

    public AbstractPropertyAccessor(Class type, AnnotatedElement annotatedElement, String name) {
        this.type = type;
        this.annotatedElement = annotatedElement;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Annotation getAnnotation(Class<? extends Annotation> c) {
        return annotatedElement.getAnnotation(c);
    }
    
    
}
