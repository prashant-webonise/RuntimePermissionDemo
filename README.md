# RuntimePermissionDemo

An example implementation of a simple wrapper around Android 6.0 runtime permission api 
(RuntimePermissionHandler)

This library provides a api to do all you need to do for supporting runtime permissions.

The demo project has implementation with this RuntimePermissionHandler.


<b>Usage</b>

Anywhere in your Activity or Fragment that you want to ask for user's permisssion
```java
permissionHandler = new PermissionHandler(this);
        permissionHandler
                .addPermission(Manifest.permission.CALL_PHONE)
                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .ask(REQUEST_CODE, new PermissionHandler.PermissionCallback() {
                                    @Override
                                    public void permissionAccepted(int requestCode) {
                
                                    }
                
                                    @Override
                                    public void showRationale(int requestCode) {
                
                                    }
                
                                    @Override
                                    public void permissionRejected(int requestCode) {
                
                                    }
                                });
```
And add this to ```onRequestPermissionsResult()```
```java
permissionHandler.dispatchPermissionResult(grantResults);
```
<b>Integration</b>

Add it on your gradle build:

```groovy
dependencies {
    compile 'com.prashant.android:runtimepermissionhandler:1.0.2'
}
```

<b>License</b>

Apache-2.0 
