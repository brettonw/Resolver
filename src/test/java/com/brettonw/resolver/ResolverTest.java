package com.brettonw.resolver;

import com.brettonw.AppTest;
import com.brettonw.resolver.Resolver;
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
