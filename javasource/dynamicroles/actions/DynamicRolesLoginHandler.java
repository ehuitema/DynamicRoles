package dynamicroles.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import licensepoolmanager.actions.LoginHandler;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.ISession;

import dynamicroles.proxies.DynamicRole;

/*
 * this hander extends LoginHandler from LicensePoolManager
 */
public class DynamicRolesLoginHandler extends LoginHandler
{	
	private static ILogNode dynalog = Core.getLogger("DynamicRoles");
	
	public DynamicRolesLoginHandler(Map<String, Object> params) 
	{		
		super(params);
	}

	@Override
	public ISession executeAction() throws Exception
	{		
		ISession sess = super.executeAction();
		//do our stuff
		String flowName = (String) Core.getConfiguration().getConstantValue("DynamicRoles.DynamicRolesMicroFlow");
		Core.getLogger("Core").info("DynamicRoles.DynamicRolesMicroFlow constant has flowName: " + flowName);
		if(flowName != null && !flowName.isEmpty())
		{
			//lookup flow
			Set<String> flows = getComponent().core().getMicroflowNames();
			for(String flow : flows)
			{
				if(flow.contains(flowName))
				{
					//trigger the flow and handle return
					dynalog.debug("Start trigger flow: '" + flowName + "'.");
					IMendixObject mxUser = sess.getUser().getMendixObject();
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("User", mxUser);
					ArrayList<IMendixObject> result = getComponent().core().execute(getContext(), flow, parameters);
					//result is a list of DynamicRole
					java.util.List<dynamicroles.proxies.DynamicRole> roles = new java.util.ArrayList<dynamicroles.proxies.DynamicRole>();
					if (result != null)
						for (IMendixObject dynaRolesElement : result)
							roles.add(dynamicroles.proxies.DynamicRole.initialize(getContext(), dynaRolesElement));
					//add the roles retrieved from the microflow to the session
					for(DynamicRole role : roles)
					{
						sess.getUserRolesNames().add(role.getName());
					}
					return sess;
				}			
			}
			throw new CoreException("A flow with name: '" + flowName + "' does not exist. This is required for DynamicRoles.LoginListener. Check the 'DynamicRolesMicroFlow' constant.");			
		}		
		
		return sess;
	}
}
