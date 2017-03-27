package dynamicroles.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.core.action.user.LoginAction;
import com.mendix.core.component.LocalComponent;
import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.systemwideinterfaces.core.AuthenticationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.systemwideinterfaces.core.IUser;
import com.mendix.systemwideinterfaces.core.UserAction;
import com.mendix.webui.CustomJavaAction;

public class AsyncAfterLoginAction extends UserAction<Boolean> 
{	
	private static ILogNode log = Core.getLogger("DynamicRoles");

	public AsyncAfterLoginAction(LocalComponent component, IContext context)
	{
		super(context);
	}
	
	@Override	
	public Boolean executeAction() throws Exception
	{
		String flowName = (String) Core.getConfiguration().getConstantValue("DynamicRoles.AsyncAfterLoginMicroFlow");
		//trigger flow async.. 
		Set<String> flows = getComponent().core().getMicroflowNames();
		for(String flow : flows)
		{
			if(flow.contains(flowName))
			{
				//trigger the flow async
				log.debug("Begin trigger flow: " + flowName + " asynchronously");
				getComponent().core().executeAsync(getContext(), flow, new Object[] {});
				return true;
			}
		
		}
		throw new CoreException("A flow with name: '" + flowName + "' does not exist. This is required for DynamicRoles.AfterLoginListener when 'triggerAsyncAfterLoginWorkflow' parameter is set to true");
	}	
}
