package com.brettonw;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.net.URL;

public class ResolverTest
{
    @org.junit.Test
    public void test()
    {
        try {
            URL urls[] = Resolver.get ("com.brettonw", "bag", "RELEASE", null, null);
            AppTest.report (urls.length > 0, true, "Resolver should fetch the requested thing");
        } catch (DependencyResolutionException exception) {
            AppTest.report (true, false, "An exception is a failure");
        }
    }
}
