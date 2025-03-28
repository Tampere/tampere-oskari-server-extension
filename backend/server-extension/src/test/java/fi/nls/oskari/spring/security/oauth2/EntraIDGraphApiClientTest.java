package fi.nls.oskari.spring.security.oauth2;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntraIDGraphApiClientTest extends EntraIDGraphApiClient {


//    @Test()
    public void authTest(){
        @NotNull String attr = this.getExtensionAttribute7("");
        System.out.println("attr: " + attr);
    }

    @Test
    public void testParseNullValue() {
        String args = "{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users(id,displayName,onPremisesExtensionAttributes)/$entity\",\"id\":\"42c934e6-d21c-44ae-af4f-ccad3ff59c26\",\"displayName\":\"Tuomas Riihimäki\",\"onPremisesExtensionAttributes\":{\"extensionAttribute1\":null,\"extensionAttribute2\":null,\"extensionAttribute3\":null,\"extensionAttribute4\":null,\"extensionAttribute5\":null,\"extensionAttribute6\":null,\"extensionAttribute7\":null,\"extensionAttribute8\":null,\"extensionAttribute9\":null,\"extensionAttribute10\":null,\"extensionAttribute11\":null,\"extensionAttribute12\":null,\"extensionAttribute13\":null,\"extensionAttribute14\":null,\"extensionAttribute15\":null}}";
        String attr = parseExtensionAttribute7FromUserinfo(args);
        assertEquals("", attr);
    }

    @Test
    public void testParseValue() {
        String args = "{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users(id,displayName,onPremisesExtensionAttributes)/$entity\",\"id\":\"42c934e6-d21c-44ae-af4f-ccad3ff59c26\",\"displayName\":\"Tuomas Riihimäki\",\"onPremisesExtensionAttributes\":{\"extensionAttribute1\":null,\"extensionAttribute2\":null,\"extensionAttribute3\":null,\"extensionAttribute4\":null,\"extensionAttribute5\":null,\"extensionAttribute6\":null,\"extensionAttribute7\": \"Foobar\",\"extensionAttribute8\":null,\"extensionAttribute9\":null,\"extensionAttribute10\":null,\"extensionAttribute11\":null,\"extensionAttribute12\":null,\"extensionAttribute13\":null,\"extensionAttribute14\":null,\"extensionAttribute15\":null}}";
        String attr = this.parseExtensionAttribute7FromUserinfo(args);
        assertEquals("Foobar", attr);
    }

    @Test
    public void testParseEmptyJson() {
        EntraIDGraphApiClient api = new EntraIDGraphApiClient();
        {
            String attr = this.parseExtensionAttribute7FromUserinfo("");
            assertEquals("", attr);
        }
        {
            String attr = this.parseExtensionAttribute7FromUserinfo("{}");
            assertEquals("", attr);
        }
        {
            String attr = this.parseExtensionAttribute7FromUserinfo(null);
            assertEquals("", attr);
        }
    }

}