import { cerrarSesion, obtenerSesionActual } from "./service/authService.js";

let revalidacionConfigurada = false;

function redirectToLogin() {
    // El usuario local no es una credencial: se elimina para impedir acceso visual.
    sessionStorage.removeItem("usuario");
    window.location.replace("index.html");
}

async function limpiarSesionYRedirigir() {
    // Se intenta revocar la cookie aunque la API no esté disponible.
    try {
        await cerrarSesion();
    } catch {
        // La redirección también debe ocurrir si la API no está disponible.
    } finally {
        redirectToLogin();
    }
}

export async function protegerRuta() {
    // Primero se exige el estado local y después se confirma contra el backend.
    const usuarioLocal = sessionStorage.getItem("usuario");
    if (!usuarioLocal) {
        await limpiarSesionYRedirigir();
        return false;
    }

    try {
        JSON.parse(usuarioLocal);
    } catch {
        await limpiarSesionYRedirigir();
        return false;
    }

    try {
        const response = await obtenerSesionActual();
        sessionStorage.setItem("usuario", JSON.stringify(response.data));
        document.documentElement.classList.remove("auth-pending");
        return true;
    } catch {
        await limpiarSesionYRedirigir();
        return false;
    }
}

export function configurarRevalidacionDeSesion() {
    if (revalidacionConfigurada) return;
    revalidacionConfigurada = true;

    // La copia almacenada en BFCache debe quedar oculta antes de abandonar la página.
    window.addEventListener("pagehide", () => {
        document.documentElement.classList.add("auth-pending");
    });

    // Al regresar con Atrás/Adelante se consulta nuevamente /auth/me antes de mostrarla.
    window.addEventListener("pageshow", (event) => {
        if (!event.persisted) return;

        document.documentElement.classList.add("auth-pending");
        void protegerRuta();
    });
}

export function configurarCierreSesion() {
    const logoutButton = document.querySelector("#logoutButton");
    if (!logoutButton) return;

    logoutButton.addEventListener("click", async () => {
        // Deshabilitar evita solicitudes duplicadas mientras se revoca la sesión.
        logoutButton.disabled = true;

        await limpiarSesionYRedirigir();
    });
}

export async function manejarSesionExpirada(status) {
    if (status !== 401 && status !== 403) return;
    await limpiarSesionYRedirigir();
}
