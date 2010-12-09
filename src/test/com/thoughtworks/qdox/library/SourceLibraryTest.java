package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;

public class SourceLibraryTest
    extends TestCase
{
    private SourceLibrary sourceLibrary;
    
    protected void setUp()
        throws Exception
    {
        sourceLibrary = new SourceLibrary( new AbstractClassLibrary()
        {
            
            protected JavaClass resolveJavaClass( String name )
            {
                return null;
            }
            
            protected boolean containsClassReference( String name )
            {
                return false;
            }
        });
    }
    
    protected void tearDown()
        throws Exception
    {
        deleteDir("target/test-source");
    }
    
    private File createFile(String fileName, String packageName, String className) throws Exception {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write("// this file generated by JavaDocBuilderTest - feel free to delete it\n");
        writer.write("package " + packageName + ";\n\n");
        writer.write("public class " + className + " {\n\n  // empty\n\n}\n");
        writer.close();
        return file;
    }
    
    private void deleteDir(String path) {
        File dir = new File(path);
        File[] children = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
            File file = children[i];
            if (file.isDirectory()) {
                deleteDir(file.getAbsolutePath());
            } else {
                file.delete();
            }
        }
        dir.delete();
    }

    //QDOX-221
    public void testClosedStream() throws Exception {
        File badFile = createFile("target/test-source/com/blah/Bad.java", "com.blah", "@%! BAD {}}}}");
        InputStream stream = new FileInputStream( badFile );
        try {
            sourceLibrary.addSource( stream );
        }
        catch(ParseException ex) {
            try {
                stream.read();
                fail("Stream should be closed");
            }
            catch(IOException ioe) {}
        }
    }
    
    //QDOX-221
    public void testClosedReader() throws Exception {
        File badFile = createFile("target/test-source/com/blah/Bad.java", "com.blah", "@%! BAD {}}}}");
        Reader reader= new FileReader( badFile );
        try {
            sourceLibrary.addSource( reader );
        }
        catch(ParseException ex) {
            try {
                reader.read();
                fail("Reader should be closed");
            }
            catch(IOException ioe) {}
        }
    }
}