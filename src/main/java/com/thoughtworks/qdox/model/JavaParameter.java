package com.thoughtworks.qdox.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * JavaParameter is an extended version of JavaClass and doesn't exist in the java api. 
 * 
 * @author Robert Scholte
 *
 */
public interface JavaParameter extends JavaAnnotatedElement, Comparable<JavaParameter>
{

    public String getName();

    public Type getType();

    public JavaMethod getParentMethod();

    public JavaClass getParentClass();

    /**
     * Is this a Java 5 var args type specified using three dots. e.g. void doStuff(Object... thing)
     * @since 1.6
     */
    public boolean isVarArgs();

    /**
     * 
     * @return the resolved value if the method has typeParameters, otherwise type's value
     * @since 1.10
     */
    public String getResolvedValue();

    public String getResolvedGenericValue();
}