# Frontend

Cliente web modular para la API de Spring Boot.

## Configuración

1. Cambia `API_URL` en `js/config.js` si el backend no utiliza `http://localhost:8080`.
2. Inicia el backend con sus variables de entorno configuradas.
3. Sirve esta carpeta mediante HTTP; los módulos ES no deben abrirse con `file://`.

Ejemplo con VS Code Live Server o con cualquier servidor estático en el puerto 5500. Abre `index.html` para iniciar sesión.

La API autentica mediante la cookie HTTP-only `authToken`. Por esta razón los services usan `credentials: "include"`.

Durante el desarrollo, el frontend puede abrirse desde `http://127.0.0.2:5500` y consumir la API en
`http://localhost:8080`. La API emite la cookie con `SameSite=None; Secure` y permite ese origen mediante CORS.
La cookie pertenece a `localhost`, por lo que debe revisarse en DevTools bajo el dominio de la API y no bajo
`127.0.0.2`.
