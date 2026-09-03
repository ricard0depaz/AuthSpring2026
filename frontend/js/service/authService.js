import { API_URL } from "../config.js";

// Ruta base de autenticación del backend.
const AUTH_ENDPOINT = `${API_URL}/api/auth`;

// Convierte las respuestas HTTP y ApiResponse en un error manejable por el controller.
async function readResponse(response) {
    const body = await response.json().catch(() => null);

    if (!response.ok || body?.success === false) {
        throw new Error(body?.message || body?.error || "No fue posible completar la solicitud.");
    }

    return body;
}

export async function iniciarSesion(username, password) {
    // credentials include permite recibir y reutilizar la cookie HTTP-only authToken.
    const response = await fetch(`${AUTH_ENDPOINT}/login`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    return await readResponse(response);
}

export async function obtenerSesionActual() {
    // /me valida la cookie HttpOnly; el token nunca se expone al JavaScript.
    const response = await fetch(`${AUTH_ENDPOINT}/me`, {
        method: "GET",
        credentials: "include"
    });

    return await readResponse(response);
}

export async function cerrarSesion() {
    // El backend invalida el JWT y envía una cookie expirada.
    const response = await fetch(`${AUTH_ENDPOINT}/logout`, {
        method: "POST",
        credentials: "include"
    });

    return await readResponse(response);
}
