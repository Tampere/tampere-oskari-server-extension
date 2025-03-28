package fi.tampere.ktjpdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.oskari.permissions.PermissionService;
import org.oskari.permissions.model.Permission;
import org.oskari.permissions.model.PermissionExternalType;
import org.oskari.permissions.model.PermissionType;
import org.oskari.permissions.model.Resource;
import org.oskari.permissions.model.ResourceType;

import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.util.DuplicateException;
import fi.nls.oskari.util.PropertyUtil;

public class KtjPdfHandlerTest {

    @Test
    public void testPreprocess() throws DuplicateException, ServiceException {
        Role dummy = new Role();
        dummy.setId(1);
        dummy.setName("dummy");

        Role ktjRole = new Role();
        ktjRole.setId(2);
        ktjRole.setName("ktj");

        Permission permissionToView = new Permission();
        permissionToView.setType(PermissionType.VIEW_LAYER);
        permissionToView.setExternalType(PermissionExternalType.ROLE);
        permissionToView.setRoleId((int) ktjRole.getId());

        String roleLayerId = "1337";

        Resource resource = new Resource();
        resource.setType(ResourceType.maplayer);
        resource.setMapping(roleLayerId);
        resource.addPermission(permissionToView);

        PermissionService psMock = mock(PermissionService.class);
        when(psMock.findResource(ResourceType.maplayer, roleLayerId)).thenReturn(Optional.of(resource));

        KtjPdfHandler h = new KtjPdfHandler(psMock);

        User user = new User();
        assertFalse(h.hasPermission(user));

        user.addRole(dummy);
        assertFalse(h.hasPermission(user));

        user.addRole(ktjRole);
        assertFalse(h.hasPermission(user));

        PropertyUtil.addProperty(KtjPdfHandler.PROPERTY_KTJPDF_ROLE_LAYER_ID, roleLayerId);
        assertTrue(h.hasPermission(user));
    }

    @Test
    public void testShortFormat() {
        assertEquals("837-999-0-1", KtjPdfHandler.toShortFormat("837-999-0000-0001"));
        assertEquals("837-999-999-1", KtjPdfHandler.toShortFormat("837-999-999-1"));
        assertEquals("837-999-1-1", KtjPdfHandler.toShortFormat("83799900010001"));
        assertNull(KtjPdfHandler.toShortFormat("83799900010001M123"));
    }

    @Test
    public void testGetKarttaoteURL() throws ActionException {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<karttaote>"
                + "<rekisteriyksikko>999-123-4567-6</rekisteriyksikko>"
                + "<palstalkm>1</palstalkm>"
                + "<oikeaksi_todistettava>false</oikeaksi_todistettava>"
                + "<lang>FI</lang>"
                + "<kopiointilupa/>"
                + "<linkki>"
                + "<url>https://ktjws.nls.fi/ktjkii/tuloste/karttaote/pdf?rekisteriyksikko=999-123-4567-6&amp;alkusivu=1&amp;loppusivu=1&amp;lang=FI&amp;kopiointilupa=&amp;palstalkm=1</url>"
                + "<alkusivu>1</alkusivu>"
                + "<loppusivu>1</loppusivu>"
                + "</linkki>"
                + "</karttaote>";
        String actual = KtjPdfHandler.getKarttaoteURL(input.getBytes());
        String expected = "https://ktjws.nls.fi/ktjkii/tuloste/karttaote/pdf?rekisteriyksikko=999-123-4567-6&alkusivu=1&loppusivu=1&lang=FI&kopiointilupa=&palstalkm=1";
        assertEquals(expected, actual);
    }

}
