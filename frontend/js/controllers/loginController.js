import { iniciarSesion } from "../service/authService.js";

// Este controller coordina la vista; la comunicación con la API vive en authService.
// Referencias a los elementos que controla esta vista.
const form = document.querySelector("#loginForm");
const submitButton = document.querySelector("#submitButton");
const alertContainer = document.querySelector("#alertContainer");

function showAlert(message, type = "danger") {
    // Presenta al usuario los mensajes de validación o error del backend.
    alertContainer.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;
}

form.addEventListener("submit", async (event) => {
    // Evita la recarga de la página y ejecuta el flujo de login con fetch.
    event.preventDefault();
    alertContainer.innerHTML = "";
    submitButton.disabled = true;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Ingresando...';

    try {
        const username = form.username.value.trim();
        const password = form.password.value;
        const response = await iniciarSesion(username, password);

        // Se conserva la información no sensible para mostrar estado de sesión en el cliente.
        sessionStorage.setItem("usuario", JSON.stringify(response.data));
        window.location.href = "departamentos.html";
    } catch (error) {
        showAlert(error.message);
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = "Iniciar sesión";
    }
});
