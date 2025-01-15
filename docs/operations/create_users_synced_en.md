### Create users synchronously instead of asynchronously
Users can be created synchronously instead of asynchronously by supplying the `sync` query parameter. This prevents corrupted LDAP states, but is considerably slower than synchronous creation.

Exampe:

`curl -U user:password https://domain/usermgt/api/users?sync=true` 
