package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaMethodTest<M extends JavaMethod> extends TestCase {

    private M mth;

    public JavaMethodTest(String s) {
        super(s);
    }

    //constructors
    public abstract M newJavaMethod();
    public abstract M newJavaMethod(Type returns, String name);

    //setters
    public abstract void setExceptions(M method, List<JavaClass> exceptions);
    public abstract void setComment(M method, String comment);
    public abstract void setName(M method, String name);
    public abstract void setModifiers(M method, List<String> modifiers);
    public abstract void setParameters(M method, List<JavaParameter> parameters);
    public abstract void setParentClass(M method, JavaClass clazz);
    public abstract void setReturns(M method, Type type);
    public abstract void setSourceCode(M method, String code);
    
    
    public abstract JavaParameter newJavaParameter(Type type, String name);
    public abstract JavaParameter newJavaParameter(Type type, String name, boolean varArgs);
    public abstract Type newType(String fullname);
    public abstract Type newType(String fullname, int dimensions);
    
    protected void setUp() throws Exception {
        mth = newJavaMethod();
    }

    public void testDeclarationSignatureWithModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(true);
        assertEquals("protected final void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    public void testDeclarationSignatureWithoutModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(false);
        assertEquals("void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    public void testCallSignature() {
        createSignatureTestMethod();
        String signature = mth.getCallSignature();
        assertEquals("blah(count, t)", signature);
    }

    private void createSignatureTestMethod() {
        setName(mth, "blah");
        setModifiers(mth, Arrays.asList(new String[]{"protected", "final"}));
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[] {
            newType("FishException"),
            newType("FruitException"),
        } ));
        setParameters( mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t") ) );
    }

//    public void testSignatureWithVarArgs() throws Exception {
//        mth.setName( "method" );
//        mth.addParameter( new JavaParameter(new Type("java.lang.String"), "param", true) );
//        assertEquals( mth, clazz.getMethodBySignature( "method", new Type[] { new Type("java.lang.String", true)} ) );
//    }
    
    public void testGetCodeBlockSimple() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String"));
        assertEquals("java.lang.String doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneParam() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Collections.singletonList( newJavaParameter(newType("String"), "thingy") ) );
        assertEquals("void blah(String thingy);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t") ) );
        assertEquals("void blah(int count, MyThing t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t"), newJavaParameter(newType("java.lang.Meat"), "beef") ));
        assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockModifiersWithAccessLevelFirst() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setModifiers(mth, Arrays.asList(new String[]{"synchronized", "public", "final"}));
        assertEquals("public synchronized final void blah();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions( mth, Arrays.asList( new JavaClass[] { newType( "RuntimeException" ) } ) );
        assertEquals("void blah() throws RuntimeException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException")}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException"), newType("CowException")}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setComment(mth, "Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        assertEquals(expect, mth.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 1));
        assertEquals("java.lang.String[] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 2));
        assertEquals("java.lang.String[][] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockParamArray() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Arrays.asList( newJavaParameter( newType("int", 2), "count"), newJavaParameter( newType("MyThing", 1), "t") ) );
        assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithBody() throws Exception {
        setName(mth, "doStuff");
        setReturns(mth, newType("java.lang.String"));
        setSourceCode(mth, "  int x = 2;\n  return STUFF;\n");

        assertEquals("" +
                "java.lang.String doStuff() {\n" +
                "  int x = 2;\n" +
                "  return STUFF;\n" +
                "}\n",
                mth.getCodeBlock());
    }
    
    public void testEquals() throws Exception {
        setName(mth, "thing");
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thingy");
        setReturns(m3, newType("void"));

        M m4 = newJavaMethod();
        setName(m4, "thing");
        setReturns(m4, newType("int"));

        M m5 = newJavaMethod();
        M m6 = newJavaMethod();
        
        M m7 = newJavaMethod();
        setReturns(m7, newType("int"));
        
        M m8 = newJavaMethod();
        setReturns(m8, newType("int"));
//        JavaClass declaringClass = mock( JavaClass.class );
//        when( declaringClass.getFullyQualifiedName() ).thenReturn( "com.foo.bar" );
        setParentClass( m8, mock( JavaClass.class ) );

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertFalse(mth.equals(null));
        assertNotEquals( m4, m5 );
        assertNotEquals( m5, m4 );
        assertEquals( m5, m6 );
        assertNotEquals( m5, m7 );
        
        assertNotEquals( m7, m8 );
    }

    public void testEqualsWithParameters() throws Exception {
        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing"), newJavaParameter(newType("X", 3), "") ));
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        setParameters(m2, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "anotherName"), newJavaParameter(newType("X", 3), "blah") ));
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thing");
        setParameters(m3, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing") ) );
        setReturns(m3, newType("void"));

        // name
        M m4 = newJavaMethod(); 
        setName(m4, "thing");
        setParameters(m4, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing"), newJavaParameter(newType("TTTTTTTT", 3), "blah") ));
        setReturns(m4, newType("void"));

        // dimension
        M m5 = newJavaMethod();
        setName(m5, "thing");
        setParameters(m5, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing"), newJavaParameter(newType("X", 9), "blah") ));
        setReturns(m5, newType("void"));

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertNotEquals(mth, m5);
    }

    public void testHashCode() throws Exception {
        assertTrue( "hashCode should never resolve to 0", newJavaMethod( Type.VOID, "" ).hashCode() != 0 );
        
        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing"), newJavaParameter(newType("X", 3), "") ));
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        setParameters(m2, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "anotherName"), newJavaParameter(newType("X", 3), "blah") ));
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thing");
        setParameters(m3, Arrays.asList( newJavaParameter(newType("int", 1), "blah"), newJavaParameter(newType("java.lang.String", 2), "thing")));
        setReturns(m3, newType("void"));

        assertEquals(mth.hashCode(), m2.hashCode());
        assertTrue(mth.hashCode() != m3.hashCode());
    }

    public void testSignatureMatches() throws Exception {
        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "x"), newJavaParameter(newType("long", 2), "y") ));
        setReturns(mth, newType("void"));

        Type[] correctTypes = new Type[]{
            newType("int"),
            newType("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            newType("int", 2),
            newType("long")
        };

        Type[] wrongTypes2 = new Type[]{
            newType("int"),
            newType("long", 2),
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 )));
    }
    
    public void testVarArgSignatureMatches() throws Exception {
        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "x"), newJavaParameter(newType("long", 2), "y", true) ));
        setReturns(mth, newType("void"));

        Type[] correctTypes = new Type[]{
            newType("int"),
            newType("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            newType("int", 2),
            newType("long")
        };

        Type[] wrongTypes2 = new Type[]{
            newType("int"),
            newType("long", 2),
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( correctTypes ), false));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 ), true));
    }

    public void testParentClass() throws Exception {
        JavaClass clazz = mock(JavaClass.class);
        setParentClass( mth, clazz );
        assertSame(clazz, mth.getParentClass());
    }

    public void testCanGetParameterByName() throws Exception {
        JavaParameter paramX = newJavaParameter(newType("int"), "x");
        setParameters(mth, Arrays.asList( paramX, newJavaParameter(newType("string"), "y") ));
        
        assertEquals(paramX, mth.getParameterByName("x"));
        assertEquals(null, mth.getParameterByName("z"));
    }

    public void testToString() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "java.lang.Object" );
    	M mthd = newJavaMethod(newType("boolean"),"equals");
    	setParentClass(mthd, cls);
    	setModifiers(mthd, Arrays.asList(new String[]{"public"}));
    	setParameters(mthd, Collections.singletonList( newJavaParameter(newType("java.lang.Object"), null) ));
    	assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", mthd.toString());
    }
    
    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1.toString() + " should not equals " + o2.toString(), !o1.equals(o2));
    }
}