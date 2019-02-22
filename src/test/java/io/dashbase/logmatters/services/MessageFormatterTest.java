package io.dashbase.logmatters.services;

import org.junit.Assert;
import org.junit.Test;

public class MessageFormatterTest {
    @Test
    public void testJsonFormatter() throws Exception {
        JsonFormatter formatter = new JsonFormatter();
        String formatted = formatter.format("this is a test");
        Assert.assertEquals("{\"message\":\"this is a test\"}", formatted);
    }

    @Test
    public void testXmlFormatter() throws Exception {
        XmlFormatter formatter = new XmlFormatter();
        String formatted = formatter.format("this is a test");
        Assert.assertEquals("<xml><message>this is a test</message></xml>", formatted);
    }
}
