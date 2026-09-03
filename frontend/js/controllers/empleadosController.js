import { crearEmpleado, eliminarEmpleado, obtenerEmpleados } from "../service/empleadosService.js";
import { obtenerDepartamentos } from "../service/departamentosService.js";
import {
    configurarCierreSesion,
    configurarRevalidacionDeSesion,
    protegerRuta
} from "../authGuard.js";

// El controller no registra eventos ni carga datos hasta confirmar la sesión.
configurarRevalidacionDeSesion();
const sesionValida = await protegerRuta();

if (sesionValida) {
configurarCierreSesion();

// Referencias a la tabla, modal y controles del formulario.
const tableBody = document.querySelector("#empleadosTableBody");
const form = document.querySelector("#empleadoForm");
const modal = bootstrap.Modal.getOrCreateInstance(document.querySelector("#empleadoModal"));
const alertContainer = document.querySelector("#alertContainer");
const departamentoSelect = document.querySelector("#idDepartamento");
const saveButton = document.querySelector("#saveButton");

function escapeHtml(value) {
    // Evita interpretar datos provenientes de la API como etiquetas HTML.
    return String(value ?? "").replace(/[&<>'"]/g, (character) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;"
    })[character]);
}

function showAlert(message, type = "success") {
    alertContainer.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${escapeHtml(message)}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>`;
}

function formatMoney(value) {
    return new Intl.NumberFormat("es-SV", { style: "currency", currency: "USD" }).format(value || 0);
}

function renderEmpleados(empleados) {
    // Genera las filas visibles a partir del DTO de empleados.
    if (!empleados.length) {
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center text-secondary py-4">No hay empleados registrados.</td></tr>';
        return;
    }

    tableBody.innerHTML = empleados.map((empleado) => `
        <tr>
            <td>${empleado.id}</td>
            <td class="fw-semibold">${escapeHtml(empleado.nombre)} ${escapeHtml(empleado.apellido)}</td>
            <td>${escapeHtml(empleado.email)}</td>
            <td>${escapeHtml(empleado.fecha_ingreso || "—")}</td>
            <td>${formatMoney(empleado.salario)}</td>
            <td>${escapeHtml(empleado.nombreDepartamento || "Sin departamento")}</td>
            <td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${empleado.id}">Eliminar</button></td>
        </tr>
    `).join("");
}

async function loadEmpleados() {
    // Obtiene y renderiza la lista, mostrando un estado de carga mientras espera.
    tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4"><span class="spinner-border spinner-border-sm me-2"></span>Cargando...</td></tr>';
    try {
        const response = await obtenerEmpleados();
        renderEmpleados(response.data || []);
    } catch (error) {
        showAlert(error.message, "danger");
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center text-danger py-4">No se pudo cargar la información.</td></tr>';
    }
}

async function loadDepartamentos() {
    // Carga las opciones relacionadas para el select del formulario.
    const response = await obtenerDepartamentos();
    departamentoSelect.innerHTML = '<option value="">Sin departamento</option>' + (response.data || []).map((departamento) =>
        `<option value="${departamento.id}">${escapeHtml(departamento.nombreDepto)}</option>`
    ).join("");
}

document.querySelector("#newButton").addEventListener("click", async () => {
    form.reset();
    try {
        await loadDepartamentos();
        modal.show();
    } catch (error) {
        showAlert(error.message, "danger");
    }
});

// Delegación de eventos para eliminar filas sin registrar listeners individuales.
tableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action='delete']");
    if (!button || !window.confirm("¿Deseas eliminar este empleado?")) return;

    try {
        await eliminarEmpleado(button.dataset.id);
        showAlert("Empleado eliminado correctamente.");
        await loadEmpleados();
    } catch (error) {
        showAlert(error.message, "danger");
    }
});

form.addEventListener("submit", async (event) => {
    // Construye el DTO con los nombres exactos esperados por EmpleadosDTO.
    event.preventDefault();
    saveButton.disabled = true;

    const empleado = {
        nombre: form.nombre.value.trim(),
        apellido: form.apellido.value.trim(),
        email: form.email.value.trim(),
        fecha_ingreso: form.fecha_ingreso.value,
        salario: Number(form.salario.value),
        idDepartamento: form.idDepartamento.value ? Number(form.idDepartamento.value) : null
    };

    try {
        await crearEmpleado(empleado);
        modal.hide();
        showAlert("Empleado creado correctamente.");
        await loadEmpleados();
    } catch (error) {
        showAlert(error.message, "danger");
    } finally {
        saveButton.disabled = false;
    }
});

loadEmpleados();
}
