// environment.ts
export const environment = {
    production: false,
    apiHost: 'http://localhost:8080/api',
    keycloak: {
        issuer: 'http://localhost:8081/realms/redroja-realm',
        clientId: 'redroja-frontend',
    },
};
