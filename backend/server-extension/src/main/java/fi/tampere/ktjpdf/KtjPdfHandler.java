package fi.tampere.ktjpdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;

import org.oskari.permissions.PermissionService;
import org.oskari.permissions.model.Permission;
import org.oskari.permissions.model.PermissionExternalType;
import org.oskari.permissions.model.Resource;
import org.oskari.permissions.model.ResourceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.cache.Cache;
import fi.nls.oskari.cache.CacheManager;
import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("KtjPdf")
public class KtjPdfHandler extends RestActionHandler {

    private static final Logger LOG = LogFactory.getLogger(KtjPdfHandler.class);

    private static final String PARAM_DOC = "doc";
    private static final String PARAM_KTUNNUS = "ktunnus";

    private static final String DOC_KIINTEISTOREKISTERIOTE = "kiinteistorekisteriote";
    private static final String DOC_LAINHUUTOTODISTUS = "lainhuutotodistus";
    private static final String DOC_KARTTAOTE = "karttaote";

    static final String PROPERTY_KTJPDF_ROLE_LAYER_ID = "ktjpdf.role.layer";

    private static final String PROPERTY_KTJPDF_USERNAME = "ktjpdf.username";
    private static final String PROPERTY_KTJPDF_PASSWORD = "ktjpdf.password";

    private static final String PROPERTY_URL_KIINTEISTOREKISTERIOTE = "ktjpdf.url.kiinteistorekisteriote";
    private static final String PROPERTY_URL_LAINHUUTOTODISTUS = "ktjpdf.url.lainhuutotodistus";
    private static final String PROPERTY_KTJPDF_URL_KARTTAOTE = "ktjpdf.url.karttaote";

    private static final String DEFAULT_URL_KIINTEISTOREKISTERIOTE = "https://ktjws.nls.fi/ktjkii/tuloste/kiinteistorekisteriote/rekisteriyksikko/pdf";
    private static final String DEFAULT_URL_LAINHUUTOTODISTUS = "https://ktjws.nls.fi/ktjkir/tuloste/lainhuutotodistus/pdf";
    private static final String DEFAULT_URL_KARTTAOTE = "https://ktjws.nls.fi/ktjkii/tuloste/karttaote/xml";

    private static final String PROPERTY_MML_REQUEST_HEADER_END_USER_ID = "ktjpdf.mml.header";

    private static final String DEFAULT_MML_REQUEST_HEADER_END_USER_ID = "enduserid";

    private PermissionService permissionService;

    public KtjPdfHandler() {}

    KtjPdfHandler(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    private PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = OskariComponentManager.getComponentOfType(PermissionService.class);
        }
        return permissionService;
    }

    @Override
    public void preProcess(ActionParameters params) throws ActionException {
        if (!hasPermission(params.getUser())) {
            throw new ActionDeniedException("No access");
        }
    }

    boolean hasPermission(User user) {
        if (user == null || user.isGuest()) {
            return false;
        }
        Resource resource = getResource();
        if (resource == null) {
            return false;
        }
        return resource.getPermissions().stream()
                .anyMatch(p -> matches(p, user));
    }

    private boolean matches(Permission p, User user) {
        long id = p.getExternalId();
        PermissionExternalType type = p.getExternalType();
        switch (type) {
        case ROLE:
            return user.getRoles().stream().anyMatch(role -> role.getId() == id);
        case USER:
            return user.getId() == id;
        default:
            return false;
        }
    }

    private Resource getResource() {
        Cache<Resource> cache = CacheManager.getCache(this.getClass().getName());
        String cacheKey = PropertyUtil.get(PROPERTY_KTJPDF_ROLE_LAYER_ID);
        Resource resource = cache.get(cacheKey);
        if (resource == null) {
            Optional<Resource> maybeResource =
                    getPermissionService().findResource(ResourceType.maplayer, cacheKey);
            if (!maybeResource.isPresent()) {
                return null;
            }
            resource = maybeResource.get();
            cache.put(cacheKey, resource);
        }
        return resource;
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        String doc = params.getRequiredParam(PARAM_DOC);
        String kiinteistotunnus = params.getRequiredParam(PARAM_KTUNNUS);
        User user = params.getUser();

        kiinteistotunnus = toShortFormat(kiinteistotunnus);
        if (kiinteistotunnus == null) {
            throw new ActionParamsException("Invalid ktunnus");
        }

        byte[] tuloste = getTuloste(user, doc, kiinteistotunnus);
        ResponseHelper.writeResponse(params, 200, "application/pdf", tuloste);
    }

    static String toShortFormat(String kiinteistotunnus) {
        String a, b, c, d;

        String[] parts = kiinteistotunnus.split("-");
        if (parts.length == 1) {
            if (parts[0].length() != 3+3+4+4) {
                return null;
            }
            a = parts[0].substring(0, 3);
            b = parts[0].substring(3, 6);
            c = parts[0].substring(6, 10);
            d = parts[0].substring(10, 14);
        } else if (parts.length == 4) {
            a = parts[0];
            b = parts[1];
            c = parts[2];
            d = parts[3];
        } else {
            return null;
        }

        try {
            int ia = Integer.parseInt(a);
            int ib = Integer.parseInt(b);
            int ic = Integer.parseInt(c);
            int id = Integer.parseInt(d);

            if (ia < 0 || ia > 999
                    || ib < 0 || ib > 999
                    || ic < 0 || ic > 9999
                    || id < 0 || id > 9999) {
                return null;
            }

            return String.format("%d-%d-%d-%d", ia, ib, ic, id);
        } catch (Exception ignore) {
            return null;
        }
    }

    private byte[] getTuloste(User user, String doc, String kiinteistotunnus) throws ActionException {
        switch (doc) {
        case DOC_KIINTEISTOREKISTERIOTE:
            return getKiinteistorekisteriote(user, kiinteistotunnus);
        case DOC_LAINHUUTOTODISTUS:
            return getLainhuutotodistus(user, kiinteistotunnus);
        case DOC_KARTTAOTE:
            return getKarttaote(user, kiinteistotunnus);
        default:
            throw new ActionParamsException("Invalid value for param 'doc'");
        }
    }

    private byte[] getKiinteistorekisteriote(User user, String kiinteistotunnus) throws ActionException {
        String url = PropertyUtil.get(PROPERTY_URL_KIINTEISTOREKISTERIOTE, DEFAULT_URL_KIINTEISTOREKISTERIOTE);
        return fetch(user, url, Collections.singletonMap("kiinteistotunnus", kiinteistotunnus));
    }

    private byte[] getLainhuutotodistus(User user, String kiinteistotunnus) throws ActionException {
        String url = PropertyUtil.get(PROPERTY_URL_LAINHUUTOTODISTUS, DEFAULT_URL_LAINHUUTOTODISTUS);
        return fetch(user, url, Collections.singletonMap("kohdetunnus", kiinteistotunnus));
    }

    private byte[] getKarttaote(User user, String kiinteistotunnus) throws ActionException {
        String url = PropertyUtil.get(PROPERTY_KTJPDF_URL_KARTTAOTE, DEFAULT_URL_KARTTAOTE);
        Map<String, String> query = Collections.singletonMap("rekisteriyksikko", kiinteistotunnus);
        byte[] xmlString = fetch(user, url, query);
        String karttaoteURL = getKarttaoteURL(xmlString);
        return fetch(user, karttaoteURL, query);
    }

    static String getKarttaoteURL(byte[] xmlString) throws ActionException {
        Document xml;
        try {
            xml = readXML(xmlString);
        } catch (Exception e) {
            LOG.warn("Could not parse XML document from input:", e);
            LOG.debug(new String(xmlString));
            throw new ActionException("Error occured");
        }

        Element root = xml.getDocumentElement();
        if (!"karttaote".equals(root.getNodeName())) {
            LOG.warn("Could not find <karttaote> as the root element");
            throw new ActionException("Error occured");
        }

        Element linkki = getFirstChildElementWithName(root, "linkki");
        if (linkki == null) {
            LOG.warn("Could not find <linkki> element from <karttaote> element");
            throw new ActionException("Error occured");
        }

        Element url = getFirstChildElementWithName(linkki, "url");
        if (url == null) {
            LOG.warn("Could not find <url> element from <linkki> element");
            throw new ActionException("Error occured");
        }
        return url.getTextContent();
    }

    private byte[] fetch(User user, String url, Map<String, String> query) throws ActionException {
        try {
            String username = PropertyUtil.getNecessary(PROPERTY_KTJPDF_USERNAME);
            String password = PropertyUtil.getNecessary(PROPERTY_KTJPDF_PASSWORD);
            String endUserIdHeader = PropertyUtil.get(PROPERTY_MML_REQUEST_HEADER_END_USER_ID, DEFAULT_MML_REQUEST_HEADER_END_USER_ID);
            String endUserId = getEndUserId(user);
            Map<String, String> headers = Collections.singletonMap(endUserIdHeader, endUserId);
            LOG.debug("Requesting", url, "with headers:", headers);
            HttpURLConnection c = IOHelper.getConnection(url, username, password, query, headers);
            return IOHelper.readBytes(c);
        } catch (NoSuchElementException | IOException e) {
            LOG.warn(e);
            throw new ActionException("Error occured");
        }
    }

    private String getEndUserId(User user) {
        return Long.toString(user.getId());
    }

    private static Document readXML(byte[] xmlString) throws Exception {
        InputStream in = new ByteArrayInputStream(xmlString);
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(in);
    }

    private static Element getFirstChildElementWithName(Element element, String name) {
        Node node = element.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (name.equals(e.getNodeName())) {
                    return e;
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }

}
