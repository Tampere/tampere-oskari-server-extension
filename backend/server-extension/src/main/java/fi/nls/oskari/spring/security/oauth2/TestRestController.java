package fi.nls.oskari.spring.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestRestController {


    public static record ResultRecord(java.security.Principal userPrincipal, Map<String, String> sessionAttributes) {
    }

    @GetMapping("/test1")
    public ResultRecord test1(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        Map<String, String> sessionAttributes = new HashMap<>();
        if (session != null) {
            while (session.getAttributeNames().hasMoreElements()) {
                String name = session.getAttributeNames().nextElement();
                sessionAttributes.put(name, session.getAttribute(name).toString());
            }
        }
        return new ResultRecord(httpRequest.getUserPrincipal(), sessionAttributes);

    }

}
