package com.thoughtworks.qdox.model.impl;

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

import java.beans.Introspector;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;

public class DefaultJavaMethod extends AbstractBaseMethod implements JavaMethod {

	private JavaClass returns = DefaultJavaType.VOID;
    private List<JavaTypeVariable<JavaMethod>> typeParameters = Collections.emptyList();
	
    /**
     * The default constructor
     */
    public DefaultJavaMethod() {
    }

    /**
     * Create new method without parameters and return type
     * 
     * @param name the name of the method
     */
    public DefaultJavaMethod(String name) {
        setName(name);
    }

    /**
     * Create a new method without parameters
     * 
     * @param returns the return type
     * @param name the name of this method
     */
    public DefaultJavaMethod(JavaClass returns, String name) {
        this.returns = returns;
        setName(name);
    }
    
    /** {@inheritDoc} */
    public JavaClass getReturns() {
        return returns;
    }
    
    public void setTypeParameters( List<JavaTypeVariable<JavaMethod>> typeParameters )
    {
        this.typeParameters = typeParameters;
    }

    /** {@inheritDoc} */
    public List<JavaTypeVariable<JavaMethod>> getTypeParameters()
    {
        return typeParameters;
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeMethod( this ).toString();
    }

    /**
     * @since 1.3
     */
    private String getSignature( boolean withModifiers, boolean isDeclaration )
    {
        StringBuilder result = new StringBuilder();
        if ( withModifiers )
        {
            for ( String modifier : getModifiers() )
            {
                // check for public, protected and private
                if ( modifier.startsWith( "p" ) )
                {
                    result.append( modifier ).append( ' ' );
                }
            }
            for ( String modifier : getModifiers() )
            {
                // check for public, protected and private
                if ( !modifier.startsWith( "p" ) )
                {
                    result.append( modifier ).append( ' ' );
                }
            }
        }

        if ( isDeclaration )
        {
            result.append( returns.getCanonicalName() );
            result.append( ' ' );
        }

        result.append( getName() );
        result.append( '(' );
        for ( ListIterator<JavaParameter> iter = getParameters().listIterator(); iter.hasNext(); )
        {
            JavaParameter parameter = iter.next();
            if ( isDeclaration )
            {
                result.append( parameter.getType().getCanonicalName() );
                if ( parameter.isVarArgs() )
                {
                    result.append( "..." );
                }
                result.append( ' ' );
            }
            result.append( parameter.getName() );
            if ( iter.hasNext() )
            {
                result.append( ", " );
            }
        }
        result.append( ')' );
        if ( isDeclaration && !getExceptions().isEmpty() )
        {
            result.append( " throws " );
            for ( Iterator<JavaClass> excIter = getExceptions().iterator(); excIter.hasNext(); )
            {
                result.append( excIter.next().getCanonicalName() );
                if ( excIter.hasNext() )
                {
                    result.append( ", " );
                }
            }
        }
        return result.toString();
    }


    /** {@inheritDoc} */
    public String getDeclarationSignature( boolean withModifiers )
    {
        return getSignature(withModifiers, true);
    }

    /** {@inheritDoc} */
    public String getCallSignature()
    {
        return getSignature(false, false);
    }

    /**
     * Define the return type of this method
     * 
     * @param returns the return type
     */
    public void setReturns(JavaClass returns)
    {
        this.returns = returns;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaMethod ) )
        {
            return false;
        }

        JavaMethod other = (JavaMethod) obj;
        if ( other.getDeclaringClass() != null ? !other.getDeclaringClass().equals( this.getDeclaringClass() ) : this.getDeclaringClass() != null )
        {
            return false;
        }
        
        //use 'this' from here to make it better readable
        if ( other.getName() != null ? !other.getName().equals( this.getName() ) :  this.getName() != null )
        {
            return false;
        }

        if ( other.getReturnType() != null ? !other.getReturnType().equals( this.getReturns() ) : this.getReturns() != null )
        {
            return false;
        }

        List<JavaParameter> thisParams = this.getParameters();
        List<JavaParameter> otherParams = other.getParameters();
        if ( otherParams.size() != thisParams.size() )
        {
            return false;
        }
        for ( int i = 0; i < thisParams.size(); i++ )
        {
            if ( !otherParams.get( i ).equals( thisParams.get( i ) ) )
            {
                return false;
            }
        }

        return this.isVarArgs() == other.isVarArgs();
    }

    @Override
    public int hashCode()
    {
        int hashCode = 7;
        if ( getDeclaringClass() != null )
        {
            hashCode *= 31 + getDeclaringClass().hashCode();
        }
        if ( getName() != null )
        {
            hashCode *= 37 + getName().hashCode();
        }
        hashCode *= 41 + getParameters().hashCode();
        if ( returns != null )
        {
            hashCode *= 43 + returns.hashCode();
        }
        return hashCode;
    }

    /** {@inheritDoc} */
    public boolean isPropertyAccessor()
    {
        if ( isStatic() ) 
        {
            return false;
        }
        if ( getParameters().size() != 0 )
        {
            return false;
        }
        if ( getName().startsWith( "is" ) )
        {
            return ( getName().length() > 2 && Character.isUpperCase( getName().charAt( 2 ) ) );
        }
        if ( getName().startsWith( "get" ) )
        {
            return ( getName().length() > 3 && Character.isUpperCase( getName().charAt( 3 ) ) );
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean isPropertyMutator()
    {
        if ( isStatic() ) 
        {
            return false;
        }
        if ( getParameters().size() != 1 ) 
        {
            return false;
        }
        
        if ( getName().startsWith( "set" ) ) 
        {
            return ( getName().length() > 3 && Character.isUpperCase( getName().charAt( 3 ) ) );
        }

        return false;
    }

    /** {@inheritDoc} */
    public JavaType getPropertyType() 
    {
        if ( isPropertyAccessor() ) 
        {
            return getReturns();
        }
        if ( isPropertyMutator() )
        {
            return getParameters().get(0).getType();
        } 
        return null;
    }

    /** {@inheritDoc} */
    public String getPropertyName()
    {
        int start = -1;
        if ( getName().startsWith( "get" ) || getName().startsWith( "set" ) )
        {
            start = 3;
        }
        else if ( getName().startsWith( "is" ) )
        {
            start = 2;
        }
        else
        {
            return null;
        }
        return Introspector.decapitalize( getName().substring( start ) );
    }

    /** {@inheritDoc} */
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if ( isPrivate() )
        {
            result.append( "private " );
        }
        else if ( isProtected() )
        {
            result.append( "protected " );
        }
        else if ( isPublic() )
        {
            result.append( "public " );
        }
        if ( isAbstract() )
        {
            result.append( "abstract " );
        }
        if ( isStatic() )
        {
            result.append( "static " );
        }
        if ( isFinal() )
        {
            result.append( "final " );
        }
        if ( isSynchronized() )
        {
            result.append( "synchronized " );
        }
        if ( isNative() )
        {
            result.append( "native " );
        }
        result.append( getReturns().getFullyQualifiedName() ).append( ' ' );
        if ( getParentClass() != null )
        {
            result.append( getParentClass().getFullyQualifiedName() );
            result.append( "." );
        }
        result.append( getName() );
        result.append( "(" );
        for ( int paramIndex = 0; paramIndex < getParameters().size(); paramIndex++ )
        {
            if ( paramIndex > 0 )
            {
                result.append( "," );
            }
            JavaType originalType = getParameters().get( paramIndex ).getType();
            JavaTypeVariable<?> typeVariable = DefaultJavaType.resolve( originalType, getTypeParameters() );
            result.append( typeVariable == null ? originalType.getFullyQualifiedName() : typeVariable.getBounds().get( 0 ).getFullyQualifiedName() );
        }
        result.append( ")" );
        if ( getExceptions().size() > 0 )
        {
            result.append( " throws " );
            for ( Iterator<JavaClass> excIter = getExceptions().iterator(); excIter.hasNext(); )
            {
                result.append( excIter.next().getFullyQualifiedName() );
                if ( excIter.hasNext() )
                {
                    result.append( "," );
                }
            }
        }
        return result.toString();
    }

    /** {@inheritDoc} */
    public JavaClass getGenericReturnType()
    {
        return returns;
    }

    /** {@inheritDoc} */
    public JavaType getReturnType()
    {
        return getReturnType( false );
    }
	
    /** {@inheritDoc} */
    public JavaType getReturnType( boolean resolve )
    {
        return returns;
    }
    
    public boolean signatureMatches( String name, List<JavaType> parameterTypes )
    {
        return signatureMatches( name, parameterTypes, false );
    }
    
    public boolean signatureMatches( String name, List<JavaType> parameterTypes, boolean varArg )
    {
        if ( !name.equals( this.getName() ) ) 
        {
            return false;    
        } 
        return signatureMatches( parameterTypes, varArg );
    }
}