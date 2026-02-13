# Authentication Audit — Actions, Issues & Proposed Fixes

Date: 2026-01-06

## Actions performed

1. Inspected authentication-related source files:
   - `src/main/java/com/springboot/AuctionBidder/Controller/AuthenticationController.java`
   - `src/main/java/com/springboot/AuctionBidder/Config/SecurityConfig.java`
   - `src/main/java/com/springboot/AuctionBidder/Util/JwtUtil.java`
   - `src/main/java/com/springboot/AuctionBidder/Util/JwtFilter.java`
   - `src/main/java/com/springboot/AuctionBidder/ServiceImplementation/CustomUserDetail.java`
   - `src/main/java/com/springboot/AuctionBidder/Repository/UserRepository.java`
   - `src/main/java/com/springboot/AuctionBidder/Entity/*` and `src/main/java/com/springboot/AuctionBidder/dto/*`

2. Ran `mvnw test` to observe runtime/warning output.

3. Summarized functional issues and proposed concrete fixes (below).

## Issues found (summary)

- Missing signup endpoint: Security config permits `/users/create` but no controller exists to create users.
- Security matcher missing leading slash: `"users/create"` should be `"/users/create"` (Spring warning).
- Password handling: no signup flow encodes passwords before saving; stored raw password will fail BCrypt checks.
- `JwtFilter` problems:
  - Incorrect import `io.jsonwebtoken.io.IOException` and mixed exception declarations.
  - Header check uses `startsWith("Bearer")` without space; prefer `"Bearer "`.
- Login response: token returned as raw string; clients usually expect JSON like `{ "token": "..." }`.
- Role/authority mapping: roles may need `ROLE_` prefix or mapping adjusted to match `hasRole(...)` checks.
- `User` entity uses public fields and lacks some getters (e.g., `id`) — reduces clarity and may cause issues.
- Minor: duplicate dependency warnings in `pom.xml` (not auth-critical).

## Proposed fixes (what I'll change if you approve)

1. Add signup endpoint:
   - `POST /users/create` in `AuthenticationController.java` that:
     - Accepts signup DTO (email, password, optional roles/profile).
     - Encodes password with `PasswordEncoder` before saving.
     - Assigns a default role (e.g., `ROLE_USER`) if none provided.
     - Returns `AuthResponse` JSON with token or created user id.

2. Update `SecurityConfig.java`:
   - Change `requestMatchers("/auth/**", "users/create")` to `requestMatchers("/auth/**", "/users/create")`.

3. Fix `JwtFilter.java`:
   - Remove wrong `io.jsonwebtoken.io.IOException` import.
   - Use `throws ServletException, java.io.IOException` and/or proper imports.
   - Use `header.startsWith("Bearer ")` and `header.substring(7)`.

4. Improve login response:
   - Return `ResponseEntity<AuthResponse>` instead of raw string.

5. Role handling:
   - Ensure stored role names and `SimpleGrantedAuthority` mapping align with security checks (add `ROLE_` prefix if required).

6. Optional cleanup:
   - Make `User` fields private and add missing getters/setters (id getter at minimum).
   - Resolve duplicate dependencies in `pom.xml`.

## Next steps (pick one)

- Reply `apply` — I will implement the changes above, run `mvnw test`, and report results.
- Reply `no` — I will not modify code; I can instead produce patch suggestions or diff snippets.
- Reply `partial` — specify which fixes to apply (e.g., `apply JwtFilter and SecurityConfig fixes`).

---

File created by assistant: `AUTH_SUMMARY.md` (root of project).