/*
 Copyright 2014 Red Hat, Inc. and/or its affiliates.

 This file is part of darcy.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.uiautotool.ui.elements;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.redhat.uiautotool.ui.By;
import com.redhat.uiautotool.ui.LazyElementInvocationHandler;
import com.redhat.uiautotool.ui.LazyViewInvocationHandler;
import com.redhat.uiautotool.ui.View;

/**
 * Static factories for the fundamental UI elements. Specifically, these return proxy instances of
 * those elements, so that they may be defined statically and loaded lazily.
 * 
 * @author ahenning
 * @see {@link LazyElement}
 * @see {@link com.redhat.uiautotool.ui.LazyElementInvocationHandler LazyElementInvocationHandler}
 *
 */
public abstract class Elements {
    /**
     * Looks up a automation-library-specific implementation for that element type, assuming an
     * implementation is registered for that class.
     * <P>
     * If the class is a subclass of View and has a public no arg constructor, then it will be 
     * instantiated and passed to {@link #element(View, By)}.
     * 
     * @param type
     * @param locator
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Element> T element(Class<T> type, By locator) {
        if (View.class.isAssignableFrom(type) && !type.isInterface()) {
            try {
                return (T) element((View) type.newInstance(), locator);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        
        InvocationHandler invocationHandler;
        
        invocationHandler = new LazyElementInvocationHandler(type, locator);
        
        return (T) Proxy.newProxyInstance(Elements.class.getClassLoader(), 
                new Class[] { type, LazyElement.class },
                invocationHandler);
    }
    
    /**
     * Wraps the implementation in a proxy so that it may be assigned to a parent View lazily, like
     * elements.
     * 
     * @param implementation
     * @param locator
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T element(T implementation, By locator) {
        InvocationHandler invocationHandler;
        
        invocationHandler = new LazyViewInvocationHandler(implementation, locator);
        
        return (T) Proxy.newProxyInstance(Elements.class.getClassLoader(), 
                new Class[] { implementation.getClass(), LazyElement.class },
                invocationHandler);
    }
    
    public static Element element(By locator) {
        return element(Element.class, locator);
    }
    
    public static TextInput textInput(By locator) {
        return element(TextInput.class, locator);
    }
    
    public static Button button(By locator) {
        return element(Button.class, locator);
    }
    
    public static Link link(By locator) {
        return element(Link.class, locator);
    }
    
    public static Label label(By locator) {
        return element(Label.class, locator);
    }
}