## Long-polling

If you use servlet-api 2.5, the long-polling will not work since this version don't support `Async`.
You can test it using the code below.

```java
@Override
protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    mEngineIoServer.handleRequest(new HttpServletRequestWrapper(request) {
        @Override
        public boolean isAsyncSupported() {
            return false; // if you want the long-polling to make a effect, you need let it return true
        }
    }, response);
    mEngineIoServer.handleRequest(request, response);
}
```

Then pls set your client request options as following

```node
const opts = {
    path:  '/rest-api/sock',
    transports: ['polling'],
    upgrade: false
}
```