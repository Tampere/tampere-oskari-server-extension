package fi.nls.oskari.spring.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Component
public class EntraIDGraphApiClient {
    private static final Logger logger = LogFactory.getLogger(EntraIDGraphApiClient.class);

    private static final URI userinfoEndpoint = URI.create("https://graph.microsoft.com/v1.0/me?$select=onPremisesExtensionAttributes");
    private final ObjectMapper mapper;

    @Autowired
    public EntraIDGraphApiClient() {
        mapper = new ObjectMapper();
    }

    @NotNull
    public String getExtensionAttribute7(String bearerToken) {
        String userInfo = getUserInfo(bearerToken);
        return parseExtensionAttribute7FromUserinfo(userInfo);
    }


    protected String parseExtensionAttribute7FromUserinfo(String userInfo) {
        if (userInfo == null || userInfo.isBlank()) {
            return "";
        }
        try {
            JsonNode infoTree = mapper.readTree(userInfo);
            JsonNode onPremAttributes = infoTree.get("onPremisesExtensionAttributes");
            JsonNode attr7 = onPremAttributes.get("extensionAttribute7");
            return attr7.asText("");
        } catch (JsonProcessingException | NullPointerException e) {
            logger.warn(e, "Error parsing extension attributes from userinfo: ", userInfo);
        }
        return "";
    }

    @NotNull
    public String getUserInfo(String bearerToken) {
        HttpRequest query = HttpRequest.newBuilder(userinfoEndpoint)
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();

        try {
            HttpResponse<String> val = HttpClient.newBuilder().build().send(query, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return val.body();
        } catch (IOException | InterruptedException e) {
            logger.warn(e, "Error querying user information from EntraID api");
        }
        return "";
    }

}
