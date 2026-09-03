import { API_URL } from "../config.js";
import { manejarSesionExpirada } from "../authGuard.js";

// Todos los métodos de este módulo consumen /api/departamentos.
const DEPARTAMENTOS_ENDPOINT = `${API_URL}/api/departamentos`;

// Este helper incluye la cookie de sesión y unifica la lectura de ApiResponse.

// Helper común para enviar JSON, incluir la cookie y validar errores HTTP/API.
async function request(url, options = {}) {
    const response = await fetch(url, {
        credentials: "include",
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        }
    });

    const body = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok || body?.success === false) {
        if (response.status === 401 || response.status === 403) {
            await manejarSesionExpirada(response.status);
        }
        throw new Error(body?.message || body?.error || `Error HTTP ${response.status}`);
    }

    return body;
}

export async function obtenerDepartamentos() {
    // GET: obtiene la colección completa para la tabla.
    return await request(DEPARTAMENTOS_ENDPOINT);
}

export async function obtenerDepartamentoPorId(id) {
    // GET por ID: carga los datos antes de abrir el modal de edición.
    return await request(`${DEPARTAMENTOS_ENDPOINT}/${id}`);
}

export async function crearDepartamento(departamento) {
    // POST: crea un nuevo registro con el DTO esperado por Spring Boot.
    return await request(DEPARTAMENTOS_ENDPOINT, {
        method: "POST",
        body: JSON.stringify(departamento)
    });
}

export async function actualizarDepartamento(id, departamento) {
    // PUT: actualiza el registro seleccionado.
    return await request(`${DEPARTAMENTOS_ENDPOINT}/${id}`, {
        method: "PUT",
        body: JSON.stringify(departamento)
    });
}

export async function eliminarDepartamento(id) {
    // DELETE: elimina el departamento indicado.
    return await request(`${DEPARTAMENTOS_ENDPOINT}/${id}`, { method: "DELETE" });
}
