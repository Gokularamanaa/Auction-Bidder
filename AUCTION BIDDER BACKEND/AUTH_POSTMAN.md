Postman import & quick checks

1) Import collection
- In Postman: File > Import > Choose File > select `AUTH_POSTMAN_collection.json` from the project root.
- Set an environment (or use Globals) with `baseUrl` = `http://localhost:8080`.

2) Usage
- Run the `Register` request to create an account (201 Created expected).
- Run the `Login` request to get a JSON body with `token`.
- The `Login` request test script stores `token` into environment variable `token`.

3) Quick curl equivalents
- Register:
  curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"name":"Gokul","email":"gokul@example.com","password":"secret123"}'

- Login:
  curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"gokul@example.com","password":"secret123"}'

4) After you have `token`
- Add header to other requests: Authorization: Bearer {{token}}

Notes
- Ensure the Spring Boot app is running on `baseUrl` before running requests.
- If the DB is not reachable, registration may fail; check application console logs for JDBC errors.
