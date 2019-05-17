package ut.com.dfn.bamboointegration;

import org.junit.Test;
import com.dfn.bamboointegration.api.MyPluginComponent;
import com.dfn.bamboointegration.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}