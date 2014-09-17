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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.InstanceLifecycleListener;
import org.czentral.data.binary.PropertyLifecycleListener;
import org.czentral.data.binary.PropertyBackedSerializationContext;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.SerializationState;
import org.czentral.data.binary.SerializationStateImpl;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;
import org.czentral.data.binary.annotation.ByteLength;
import org.czentral.data.binary.annotation.ProcessingAnnotation;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
class AnnotatedClass implements Serializer {
    
    protected final String PREFIX_GET = "get";
    
    protected final String PREFIX_SET = "set";
    
    protected Map<Class,Map<String,AbstractPropertyAccessor>> accessorCache = new HashMap<>();
    
    protected Map<Class,Map<Class,List<Class>>> listenerClassCache = new HashMap<>();

    protected Map<Class,Object> listenerCache = new HashMap<>();
    

    @Override
    public void serialize(Object o, SerializationContext sc) {
        SerializationStateImpl state = new SerializationStateImpl();
        state.setBuffer(sc.getBuffer());
        serializeWithType(o, o.getClass(), sc, state);
        runPostProcessing(o, o.getClass(), sc, state);
    }
    
    public void serializeWithType(Object o, Class type, SerializationContext sc, SerializationStateImpl state) {
        
        // recurse with superclass before processing
        Class supertype = type.getSuperclass();
        if (supertype != Object.class) {
            serializeWithType(o, supertype, sc, state);
        }
        
        BitTable bt = (BitTable)type.getAnnotation(BitTable.class);
        if (bt == null) {
            return;
        }
        
        List<InstanceLifecycleListener> instanceListeners = getAttachedListeners(o, type, InstanceLifecycleListener.class);
        List<PropertyLifecycleListener> propertyListeners = getAttachedListeners(o, type, PropertyLifecycleListener.class);
        
        // event
        for (InstanceLifecycleListener listener : instanceListeners) {
            listener.preSerializeInstance(o, state);
        }
        
        state.setPropertyQueue(new LinkedList<>(Arrays.asList(bt.propOrder())));
        
        while (!state.getPropertyQueue().isEmpty()) {
            
            String name = state.getPropertyQueue().remove(0);
            
            AbstractPropertyAccessor accessor = getAccessor(type, name);
            
            // event
            boolean skipProperty = false;
            for (PropertyLifecycleListener listener : propertyListeners) {
                skipProperty |= !listener.preSerializeProperty(o, accessor, state);
                if (skipProperty)
                    break;
            }
            if (skipProperty)
                continue;
            
            long bitPosition = sc.getBuffer().getBitPosition();

            Object fieldValue = accessor.getValue(o);
            SerializationContext subContext = new PropertyBackedSerializationContext(accessor.annotatedElement, sc.getBuffer(), accessor.type, sc.getRootSerializer());
            try {
                sc.getRootSerializer().serialize(fieldValue, subContext);
            } catch (Exception e) {
                throw new RuntimeException("Unable to serialize filed [" + type.getName() + "#" + name + "].", e);
            }
            
            long bitLength = sc.getBuffer().getBitPosition() - bitPosition;

            state.getBufferLayout().add(new SerializationStateImpl.PropertyLayoutImpl(name, bitPosition, bitLength));
            
            // event
            for (PropertyLifecycleListener listener : propertyListeners) {
                listener.postSerializeProperty(o, accessor, state);
            }
        }
        
        /*
        // event
        for (InstanceLifecycleListener listener : instanceListeners) {
            listener.postSerializeInstance(o, state);
        }
        */
        
    }
    
    public void runPostProcessing(Object o, Class type, SerializationContext sc, SerializationStateImpl state) {
        
        // event
        List<InstanceLifecycleListener> instanceListeners = getAttachedListeners(o, type, InstanceLifecycleListener.class);
        for (InstanceLifecycleListener listener : instanceListeners) {
            listener.postSerializeInstance(o, state);
        }
        
        // process re-queued elements
        for (String name : state.getRequeued()) {
            AbstractPropertyAccessor accessor = getAccessor(type, name);
            Object fieldValue = accessor.getValue(o);
            
            SerializationState.PropertyLayout fieldLayout = null;
            for (SerializationState.PropertyLayout layout : state.getBufferLayout()) {
                if (layout.getName().equals(name)) {
                    fieldLayout = layout;
                    break;
                }
            }
            if (fieldLayout == null) {
                throw new RuntimeException("Re-queued field previously not saved [" + name + "].");
            }
            
            BitBuffer subBuffer = new BitBuffer(sc.getBuffer().getBuffer(), sc.getBuffer().getBitOffset() + fieldLayout.getBitPosition(), fieldLayout.getBitLength());
            SerializationContext subContext = new PropertyBackedSerializationContext(accessor.annotatedElement, subBuffer, accessor.type, sc.getRootSerializer());
            sc.getRootSerializer().serialize(fieldValue, subContext);
            
            if (subBuffer.getBitPosition() != subBuffer.getBitLength()) {
                throw new RuntimeException("Re-saved field's length does not match previous value's in [" + name + "].");
            }
        }
        
        // recurse with superclass after processing
        /*
        Class supertype = type.getSuperclass();
        if (supertype != Object.class) {
            runPostProcessing(o, supertype, sc, state);
        }
        */
        
    }

    @Override
    public Object unserialize(SerializationContext sc) {
        
        Class type = sc.getType();
        BitTable bt = (BitTable)type.getAnnotation(BitTable.class);
        String[] names = bt.propOrder();
        
        Object value = null;
        try {
            value = type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate class [" + type.getName() + "].", e);
        }
        
        for (String name : names) {

            AbstractPropertyAccessor accessor = getAccessor(type, name);
            
            if (accessor.annotatedElement.getAnnotation(ByteLength.class) != null) {
                
            }
            
            SerializationContext subContext = new PropertyBackedSerializationContext(accessor.annotatedElement, sc.getBuffer(), accessor.type, sc.getRootSerializer());
            
            Object fieldValue = sc.getRootSerializer().unserialize(subContext);
            accessor.setValue(value, fieldValue);
        }
        
        return null;
    }
    
    protected <T> List<T> getAttachedListeners(Object o, Class objectType, Class<T> listenerType) {
        List<Class<T>> listenerClasses = getAttachedListenerClasses(o, objectType, listenerType);
        
        List<T> listeners = new LinkedList<>();
        for (Class<T> currentClass : listenerClasses) {
            T listener = instatiateListener(currentClass);
            listeners.add(listener);
        }
        if (listenerType.isAssignableFrom(objectType)) {
            listeners.add((T)o);
        }
        
        return listeners;
    }
    
    protected <T> List<Class<T>> getAttachedListenerClasses(Object o, Class objectType, Class<T> listenerType) {
        
        Map<Class,List<Class>> typeCache = listenerClassCache.get(objectType);
        if (typeCache == null) {
            typeCache = new HashMap<>();
            listenerClassCache.put(objectType, typeCache);
        }
        
        List<Class<T>> listenerClasses = (List)typeCache.get(listenerType);
        if (listenerClasses == null) {
            listenerClasses = new LinkedList<>();
            typeCache.put(listenerType, (List)listenerClasses);
            
            BitTable bt = (BitTable)objectType.getAnnotation(BitTable.class);
            if (bt == null) {
                throw new RuntimeException("Type is not BitTable annotated [" + objectType.getName() + "].");
            }

            // class-level listeners
            for (Class<?> currentClass :
                    bt.listeners()) {

                if (listenerType.isAssignableFrom(currentClass)) {
                    listenerClasses.add((Class<T>)currentClass);
                }
            }

            // scanning class annotations
            for (Annotation anno : objectType.getAnnotations()) {
                ProcessingAnnotation pa;
                if ((pa = anno.annotationType().getAnnotation(ProcessingAnnotation.class)) != null) {
                    for (Class<?> currentClass : pa.listeners()) {
                        if (listenerType.isAssignableFrom(currentClass)) {
                            listenerClasses.add((Class<T>)currentClass);
                        }
                    }
                }
            }

            // scanning method annotations
            for (Method method : objectType.getMethods()) {
                for (Annotation anno : method.getAnnotations()) {
                    ProcessingAnnotation pa;
                    if ((pa = anno.annotationType().getAnnotation(ProcessingAnnotation.class)) != null) {
                        for (Class<?> currentClass : pa.listeners()) {
                            if (listenerType.isAssignableFrom(currentClass)) {
                                listenerClasses.add((Class<T>)currentClass);
                            }
                        }
                    }
                }
            }

            // scanning field annotations
            for (Field field : objectType.getFields()) {
                for (Annotation anno : field.getAnnotations()) {
                    ProcessingAnnotation pa;
                    if ((pa = anno.annotationType().getAnnotation(ProcessingAnnotation.class)) != null) {
                        for (Class<?> currentClass : pa.listeners()) {
                            if (listenerType.isAssignableFrom(currentClass)) {
                                listenerClasses.add((Class<T>)currentClass);
                            }
                        }
                    }
                }
            }
        }
        
        return listenerClasses;
    }
    
    protected <T> T instatiateListener(Class<? extends T> currentClass) {
        T listener = (T)listenerCache.get(currentClass);
        if (listener == null) {
            try {
                listener = (T)currentClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate listener class [" + currentClass.getName() + "].", e);
            }
            listenerCache.put(currentClass, listener);
        }
        return listener;
    }
    
    protected Method findMethod(Class c, String methodName, Class[] paramTypes) {
        Method result = null;
        try {
            result = c.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
        }
        if (result == null && c.getSuperclass() != null) {
            result = findMethod(c.getSuperclass(), methodName, paramTypes);
        }
        return result;
    } 
        
    protected Field findField(Class c, String fieldName) {
        Field result = null;
        try {
            result = c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
        }
        if (result == null && c.getSuperclass() != null) {
            result = findField(c.getSuperclass(), fieldName);
        }
        return result;
    } 
        
    protected AbstractPropertyAccessor getAccessor(Class hostClass, String name) {
        
        Map<String,AbstractPropertyAccessor> typeCache = accessorCache.get(hostClass);
        if (typeCache == null) {
            typeCache = new HashMap<>();
            accessorCache.put(hostClass, typeCache);
        }

        AbstractPropertyAccessor pa = typeCache.get(name);
        if (pa == null) {

            String getterName = PREFIX_GET + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method getter = findMethod(hostClass, getterName, new Class[0]);
            if (getter != null) {
                if (getter.getAnnotation(BitField.class) != null) {

                    getter.setAccessible(true);

                    String setterName = PREFIX_SET + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method setter = null;
                    Method[] methods = hostClass.getMethods();
                    for (Method currentMethod : methods) {
                        if (currentMethod.getName().equals(setterName) && currentMethod.getParameterTypes().length == 1) {
                            setter = currentMethod;
                            break;
                        }
                    }

                    if (setter != null) {
                        setter.setAccessible(true);

                        Class valueType = getter.getReturnType();
                        pa = new MethodAccessor(getter, setter, valueType, name);
                    }

                }
            }

        }
            
        if (pa == null) {
            
            Field field = findField(hostClass, name);
            if (field != null) {
                if (field.getAnnotation(BitField.class) != null) {
                    
                    field.setAccessible(true);
                    
                    Class valueType = field.getType();
                    pa = new FieldAccessor(field, valueType, name);                    
                }
            }
            
        }
        
        if (pa == null) {
            throw new RuntimeException("No access method found for property [" + (hostClass.getName() + "#" + name) +  "].");
        }
            
        typeCache.put(name, pa);
        
        return pa;
    }

    
}
