package com.thoughtworks.qdox.model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;
import com.thoughtworks.qdox.parser.structs.ClassDef;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * 
 * <p>
 * Normally you can generate your classLibrary like this:<br/>
 * <code>
 * 	ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * 
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br/> 
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>  
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 */
public class ClassLibrary implements Serializable {
    
    private JavaClassContext context = new JavaClassContext();

    private ModelBuilderFactory modelBuilderFactory;
    
    private JavaDocBuilder builder; //@todo remove
    
    private final Set classNames = new TreeSet();
    
    private boolean defaultClassLoadersAdded = false;
    private transient List classLoaders = new ArrayList();
    private List sourceFolders = new ArrayList(); //<File>

    
    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary() {}

    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary(ClassLoader loader) {
    	classLoaders.add(loader);
    }
    
    public void setBuilder( JavaDocBuilder builder )
    {
        this.builder = builder;
    }
    
    public void setContext( JavaClassContext context )
    {
        this.context = context;
    }
    
    public JavaClassContext getContext()
    {
        return context;
    }
    
    public void add(String className) {
        classNames.add(className);
    }

    public boolean contains(String className) {
        if (classNames.contains(className)) {
            return true;
        }
        else if (getSourceFile(className) != null) {
            return true;
        } else {
            return getClass(className) != null;
        }
    }

    public File getSourceFile( String className )
    {
        for(Iterator iterator = sourceFolders.iterator(); iterator.hasNext();) {
            File sourceFolder = (File) iterator.next();
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File(sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java");
            if ( classFile.exists() && classFile.isFile() ) {
                return classFile;
            }
        }
        return null;
    }

    public Class getClass(String className) {
        Class result = null;
        for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
            ClassLoader classLoader = (ClassLoader) iterator.next();
            if (classLoader == null) {
                continue;
            }
            try {
                result = classLoader.loadClass(className);
                if (result != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                // continue
            } catch (NoClassDefFoundError e) {
                // continue
            }
        }
        return result;
    }

    public Collection all() {
        return Collections.unmodifiableCollection(classNames);
    }

    public void addClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    public void addDefaultLoader() {
        if (!defaultClassLoadersAdded) {
            classLoaders.add(getClass().getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }
        defaultClassLoadersAdded = true;
    }

    public void addSourceFolder( File sourceFolder ) {
        sourceFolders.add( sourceFolder );
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if (defaultClassLoadersAdded) {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }

    public JavaClass getJavaClass(String name) {
        JavaClass result = context.getClassByName( name );
        if(result == null && builder != null) {
            result = createBinaryClass(name);
            
            if ( result == null ) {
                result = builder.createSourceClass(name);
            }
            if ( result == null ) {
                result = createUnknownClass(name);
            }
            
            if(result != null) {
                context.add(result);
            }
        }
        return result;
    }
    
    private JavaClass createBinaryClass(String name) {
        // First see if the class exists at all.
        Class clazz = getClass(name);
        if (clazz == null) {
            return null;
        } else {
            // Create a new builder and mimic the behaviour of the parser.
            // We're getting all the information we need via reflection instead.
            ModelBuilder binaryBuilder = modelBuilderFactory.newInstance();
            BinaryClassParser parser  = new BinaryClassParser( clazz, binaryBuilder );
            parser.parse();
            
            JavaSource binarySource = binaryBuilder.getSource();
            // There is always only one class in a "binary" source.
            JavaClass result = binarySource.getClasses()[0];
            return result;
        }
    }
    
    private JavaClass createUnknownClass(String name) {
        ModelBuilder unknownBuilder = modelBuilderFactory.newInstance();
        ClassDef classDef = new ClassDef();
        classDef.name = name;
        unknownBuilder.beginClass(classDef);
        unknownBuilder.endClass();
        JavaSource unknownSource = unknownBuilder.getSource();
        JavaClass result = unknownSource.getClasses()[0];
        return result;
    }
    
    public JavaClass[] getJavaClasses() {
        return context.getClasses();
    }
    
    public JavaPackage getJavaPackage( String name) {
        return context.getPackageByName( name );
    }
    
    public JavaPackage[] getJavaPackages() {
        return context.getPackages();
    }
    
    public JavaSource[] getJavaSources() {
        return context.getSources();
    }

    public void setModelBuilderFactory( ModelBuilderFactory builderFactory )
    {
        this.modelBuilderFactory = builderFactory;
    }
}
