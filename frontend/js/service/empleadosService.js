import { API_URL } from "../config.js";
import { manejarSesionExpirada } from "../authGuard.js";

// Todos los métodos de este módulo consumen /api/empleados.
const EMPLEADOS_ENDPOINT = `${API_URL}/api/empleados`;

// Este helper incluye la cookie de sesión y unifica la lectura de ApiResponse.

// Helper común para peticiones autenticadas y respuestas ApiResponse.
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

export async function obtenerEmpleados() {
    // GET: carga los empleados para la tabla principal.
    return await request(EMPLEADOS_ENDPOINT);
}

export async function obtenerEmpleadoPorId(id) {
    // GET por ID disponible para consultas puntuales.
    return await request(`${EMPLEADOS_ENDPOINT}/${id}`);
}

export async function crearEmpleado(empleado) {
    // POST: registra un empleado y su relación opcional con un departamento.
    return await request(EMPLEADOS_ENDPOINT, {
        method: "POST",
        body: JSON.stringify(empleado)
    });
}

export async function eliminarEmpleado(id) {
    // DELETE: elimina el empleado seleccionado.
    return await request(`${EMPLEADOS_ENDPOINT}/${id}`, { method: "DELETE" });
}
