# DynamicRoles
 
The DynamicRoles module adds user roles at runtime to a user that logs in. Useful to implement dynamic access to modules e.g.
 
### Typical usage scenario
This module provides the solution in case user roles must be determined at runtime. E.g. to allow additional functionality or module access which needs to be configured at runtime. Very useful in a multitenant, module based solution where module access can be switched on and off (e.g. based on a license model)
 
### Features and limitations
* This module is an extension of [LicensePoolManager](https://github.com/ehuitema/LicensePoolManager) and can only be used in combination with this LicensePoolManager module.

 
# Dependencies
* Mendix 5.21.1
* [LicensePoolManager](https://github.com/ehuitema/LicensePoolManager) module

# Installation
### Prerequisities
* Add the [LicensePoolManager](https://github.com/ehuitema/LicensePoolManager) to your project, prior to this one

### Installation steps
* Import the module **[LicensePoolManager](https://github.com/ehuitema/LicensePoolManager)** in your project
* Import the module **DynamicRoles** in your project
 
# Getting Started
 
### configuration / how to use
* Add the startup flow from AfterStartupApplication folder AS_RegisterLogin to your project's startup.
* Do not add the LicensePoolManager startup flow! I.e. do not call the AddLoginListener Java action from LicensePoolManager in your project. Its function is taken over by the AddLoginListener Java action from DynamicRoles.
* Change the constant _DynamicRolesMicroFlow_ in the Resources folder if needed. Purpose of this flow is to return the list of roles that will be added to the logged in user.
* Change the flow (pointed by _DynamicRolesMicroFlow_ constant) to your needs. This flow must return a list of DynamicRole entities which contain the role names that will be added to the logged in user. The flow has 1 parameter, a User object named "User" which is the user that logs in.
* If you need to execute additional login logic, use the _SyncAfterLoginMicroFlow_ and/ or _AsyncAfterLoginMicroFlow_ constants. These constants specify the flow(s) to be executed immediately after the login handler is executed. You can use a sync or async version. Also change the call to the _AddAfterLoginListener_ Java action in the _AS_RegisterLogin_ flow and enable the required flow(s) with the boolean params. Be aware that the afterlogin flows do not know the currentUser, it uses the system context to execute.
 
# Remarks
### Background - how it works
The DynamicRoles implementation uses the concept of "custom loginhandler" ( [Explained here](https://bartgroot.nl/mendix/custom-checks-on-login/) ) to add roles at runtime when a user logs in. However, the module has been developed to work together with LicensePoolManager which already uses a custom login handler. In Mendix "there can be only one" custom login handler, so I've decided to inherit from the existing LicensePoolManager's LoginHandler class to implement DynamicRoles. Downside here is that DynamicRoles is now dependant on the LicensePoolManager module. Therefore, the LicensePoolManager's LoginListener must not be called, but instead the inherited LoginListener from DynamicRoles.
The DynamicRoles login handler first calls the LicensePoolManager login handler to create a valid user session. Then, it uses the constant _DynamicRolesMicroFlow_ to execute a Microflow that returns a list of **DynamicRole** entities, containing the role names that will be added to the current user's session.
It is possible to connect after login flows. These will be triggered immediately after the login handler code by Mendix. The after login flows can be either synchronous or asynchronous. These flows are executed in system context, so they do not know of the current logged in user.
 
# Known bugs

# Links
 
# License
Licensed under the Apache license.
 
# Developers notes
* `git clone https://github.com/ehuitema/DynamicRoles`
 
# Version history
