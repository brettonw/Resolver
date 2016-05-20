package com.brettonw.resolver;

import com.brettonw.AppTest;
import com.brettonw.resolver.Resolver;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.io.File;
import java.net.URL;

public class ResolverTest
{
    public static boolean delete (File file) {
        // if the file refers to a directory, it must be empty before the directory can be deleted
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                if (! delete(new File (file, children[i]))) {
                    return false;
                }
            }
        }

        // everything is now suitable for deleting the file or directory, do it
        return file.delete();
    }

    @org.junit.Test
    public void test()
    {
        delete (new File (Resolver.DEFAULT_REPOSITORY_PATH));
        try {
            URL urls[] = Resolver.get ("com.brettonw", "bag", "RELEASE", null, null, null);
            AppTest.report (urls.length > 0, true, "Resolver should fetch the requested thing");
        } catch (DependencyResolutionException exception) {
            AppTest.report (true, false, "An exception is a failure");
        }
    }
}
