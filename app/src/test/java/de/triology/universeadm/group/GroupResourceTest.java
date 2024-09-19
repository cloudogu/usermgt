package de.triology.universeadm.group;

import de.triology.universeadm.Resources;
import de.triology.universeadm.user.UserManager;
import de.triology.universeadm.user.Users;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupResourceTest
{
  
  @Mock
  private UserManager userManager;
  
  @Mock
  private GroupManager groupManager;

  @Mock
  private UndeletableGroupManager undeletableGroupManager;
  
  private GroupResource resource;
  
  @Before
  public void setUp(){
    when(groupManager.get("heartOfGold")).thenReturn(Groups.createHeartOfGold());
    when(groupManager.get("brockian")).thenReturn(Groups.createBrockianUltraCricket());
    when(userManager.get("dent")).thenReturn(Users.createDent());
    List<String> undeletableGroups = new ArrayList<>();
    undeletableGroups.add("admin");
    undeletableGroups.add("cesManager");
    this.resource = new GroupResource(groupManager, userManager, undeletableGroupManager);
  }
  
  @Test
  public void testGet() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.get("/groups/heartOfGold");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    assertEquals(Groups.createHeartOfGold(), Resources.parseJson(response, Group.class));
  }
  
  @Test
  public void testGetNotFound() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.get("/groups/towel");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }
  
  @Test
  public void testAddMember() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest.post("/groups/brockian/members/dent");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
  }
  
  @Test
  public void testAddMemberNotFound() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest.post("/groups/towel/members/dent");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  @Test
  public void testAddMemberConflict() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest.post("/groups/heartOfGold/members/dent");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
  }
  
  @Test
  public void testAddMemberUserNotFound() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest.post("/groups/heartOfGold/members/slarti");
    MockHttpResponse response = Resources.dispatch(resource, request);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }
  
}
