package com.thoughtworks.qdox.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class JavaClass extends AbstractJavaEntity implements JavaClassParent {

	public static final Type OBJECT = new Type("java.lang.Object", 0);

	private List methods = new LinkedList();
	private JavaMethod[] methodsArray;
	private List fields = new LinkedList();
	private JavaField[] fieldsArray;
	private List classes = new LinkedList();
	private JavaClass[] classesArray;
	private boolean interfce;
    // Don't access this directly. Use asType() to get my Type
	private Type type;
	private Type superClass;
	private Type[] implementz = new Type[0];
	private JavaClassParent parent;

	private JavaClassCache javaClassCache;

	public void setJavaClassCache(JavaClassCache javaClassCache) {
		this.javaClassCache = javaClassCache;
	}

	/**
	 * Interface or class?
	 */
	public boolean isInterface() {
		return interfce;
	}

	public Type getSuperClass() {
        boolean iAmJavaLangObject = OBJECT.equals(asType());
		if (!interfce && superClass == null && !iAmJavaLangObject) {
			return OBJECT;
		}
		return superClass;
	}

	public JavaClass getSuperJavaClass() {
		if (javaClassCache == null) {
			throw new java.lang.UnsupportedOperationException("JavaClassCache unavailable for this JavaClass");
		}
		Type superType = getSuperClass();
		if (superType == null) {
			return null;
		}
		else {
			return javaClassCache.getClassByName(superType.getValue());
		}
	}

	public Type[] getImplements() {
		return implementz;
	}

	protected void writeBody(IndentBuffer result) {

		writeAccessibilityModifier(result);
		writeNonAccessibilityModifiers(result);

		result.write(interfce ? "interface " : "class ");
		result.write(name);

		// subclass
		if (superClass != null) {
			result.write(" extends ");
			result.write(superClass.getValue());
		}
		// implements
		if (implementz.length > 0) {
			result.write(interfce ? " extends " : " implements ");
			for (int i = 0; i < implementz.length; i++) {
				if (i > 0) result.write(", ");
				result.write(implementz[i].getValue());
			}
		}
		result.write(" {");
		result.newline();
		result.indent();

		// fields
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
			JavaField javaField = (JavaField)iterator.next();
			result.newline();
			javaField.write(result);
		}

		// methods
		for (Iterator iterator = methods.iterator(); iterator.hasNext();) {
			JavaMethod javaMethod = (JavaMethod)iterator.next();
			result.newline();
			javaMethod.write(result);
		}

		// inner-classes
		for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
			JavaClass javaClass = (JavaClass)iterator.next();
			result.newline();
			javaClass.write(result);
		}

		result.deindent();
		result.newline();
		result.write('}');
		result.newline();
	}

	public void setInterface(boolean interfce) {
		this.interfce = interfce;
	}

	public void addMethod(JavaMethod meth) {
		methods.add(meth);
		meth.setParentClass(this);
		methodsArray = null;
	}

	public void setSuperClass(Type type) {
		superClass = type;
	}

	public void setImplementz(Type[] implementz) {
		this.implementz = implementz;
	}

	public void addField(JavaField javaField) {
		fields.add(javaField);
		javaField.setParentClass(this);
		fieldsArray = null;
	}

	public void setParent(JavaClassParent parent) {
		this.parent = parent;
	}

	public JavaClassParent getParent() {
		return parent;
	}

	public JavaSource getParentSource() {
		JavaClassParent parent = getParent();
		return (parent == null ? null : parent.getParentSource());
	}

	public String getPackage() {
		return getParentSource().getPackage();
	}

	public String getFullyQualifiedName() {
        if( getParent() != null ) {
    		return getParent().asClassNamespace() + "." + getName();
        } else {
            return null;
        }
	}

	public String asClassNamespace() {
		return getFullyQualifiedName();
	}

	public Type asType() {
		if (type == null) {
			type = new Type(getFullyQualifiedName(), 0);
		}
		return type;
	}

	public JavaMethod[] getMethods() {
		if (methodsArray == null) {
			methodsArray = new JavaMethod[methods.size()];
			methods.toArray(methodsArray);
		}
		return methodsArray;
	}

	public JavaMethod getMethodBySignature(String name, 
										   Type[] parameterTypes) 
	{
		JavaMethod[] methods = getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].signatureMatches(name, parameterTypes)) {
				return methods[i];
			}
		}
		return null;
	}
	
	public JavaField[] getFields() {
		if (fieldsArray == null) {
			fieldsArray = new JavaField[fields.size()];
			fields.toArray(fieldsArray);
		}
		return fieldsArray;
	}
	
	public JavaField getFieldByName(String name) {
		JavaField[] fields = getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				return fields[i];
			}
		}
		return null;
	}

	public void addClass(JavaClass cls) {
		classes.add(cls);
		cls.setParent(this);
		classesArray = null;
	}

	public JavaClass[] getClasses() {
		if (classesArray == null) {
			classesArray = new JavaClass[classes.size()];
			classes.toArray(classesArray);
		}
		return classesArray;
	}

	public JavaClass getInnerClassByName(String name) {
		JavaClass[] classes = getClasses();
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].getName().equals(name)) {
				return classes[i];
			}
		}
		return null;
	}

}
