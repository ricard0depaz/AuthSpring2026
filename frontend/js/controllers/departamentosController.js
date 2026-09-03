import {
    actualizarDepartamento,
    crearDepartamento,
    eliminarDepartamento,
    obtenerDepartamentoPorId,
    obtenerDepartamentos
} from "../service/departamentosService.js";
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

// Elementos principales de la vista y del formulario modal.
const tableBody = document.querySelector("#departamentosTableBody");
const form = document.querySelector("#departamentoForm");
const modalElement = document.querySelector("#departamentoModal");
const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
const alertContainer = document.querySelector("#alertContainer");
const saveButton = document.querySelector("#saveButton");

function escapeHtml(value) {
    // Evita insertar contenido de la API como HTML ejecutable.
    return String(value ?? "").replace(/[&<>'"]/g, (character) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;"
    })[character]);
}

function showAlert(message, type = "success") {
    // Muestra confirmaciones y errores sin abandonar la vista actual.
    alertContainer.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${escapeHtml(message)}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>`;
}

function renderDepartamentos(departamentos) {
    // Transforma la colección recibida del service en filas de la tabla.
    if (!departamentos.length) {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-secondary py-4">No hay departamentos registrados.</td></tr>';
        return;
    }

    tableBody.innerHTML = departamentos.map((departamento) => `
        <tr>
            <td>${departamento.id}</td>
            <td class="fw-semibold">${escapeHtml(departamento.nombreDepto)}</td>
            <td><span class="badge text-bg-light">${escapeHtml(departamento.abreviatura || "—")}</span></td>
            <td>${escapeHtml(departamento.ubicacion || "—")}</td>
            <td class="text-end text-nowrap">
                <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${departamento.id}">Editar</button>
                <button class="btn btn-sm btn-outline-danger ms-1" data-action="delete" data-id="${departamento.id}">Eliminar</button>
            </td>
        </tr>
    `).join("");
}

async function loadDepartamentos() {
    // Carga inicial y refresco posterior a crear, editar o eliminar.
    tableBody.innerHTML = '<tr><td colspan="5" class="text-center py-4"><span class="spinner-border spinner-border-sm me-2"></span>Cargando...</td></tr>';
    try {
        const response = await obtenerDepartamentos();
        renderDepartamentos(response.data || []);
    } catch (error) {
        showAlert(error.message, "danger");
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger py-4">No se pudo cargar la información.</td></tr>';
    }
}

function openCreateModal() {
    form.reset();
    form.departamentoId.value = "";
    document.querySelector("#modalTitle").textContent = "Nuevo departamento";
    modal.show();
}

async function openEditModal(id) {
    // Obtiene el registro seleccionado y precarga sus campos en el modal.
    try {
        const response = await obtenerDepartamentoPorId(id);
        const departamento = response.data;
        if (!departamento) throw new Error("El departamento no existe.");

        form.departamentoId.value = departamento.id;
        form.nombreDepto.value = departamento.nombreDepto || "";
        form.abreviatura.value = departamento.abreviatura || "";
        form.ubicacion.value = departamento.ubicacion || "";
        document.querySelector("#modalTitle").textContent = "Editar departamento";
        modal.show();
    } catch (error) {
        showAlert(error.message, "danger");
    }
}

async function removeDepartamento(id) {
    // Solicita confirmación antes de ejecutar DELETE.
    if (!window.confirm("¿Deseas eliminar este departamento?")) return;
    try {
        await eliminarDepartamento(id);
        showAlert("Departamento eliminado correctamente.");
        await loadDepartamentos();
    } catch (error) {
        showAlert(error.message, "danger");
    }
}

document.querySelector("#newButton").addEventListener("click", openCreateModal);

// Delegación de eventos: funciona también para botones creados dinámicamente.
tableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    if (button.dataset.action === "edit") await openEditModal(button.dataset.id);
    if (button.dataset.action === "delete") await removeDepartamento(button.dataset.id);
});

form.addEventListener("submit", async (event) => {
    // Decide entre POST y PUT según exista un ID oculto en el formulario.
    event.preventDefault();
    saveButton.disabled = true;

    const id = form.departamentoId.value;
    const departamento = {
        nombreDepto: form.nombreDepto.value.trim(),
        abreviatura: form.abreviatura.value.trim(),
        ubicacion: form.ubicacion.value.trim()
    };

    try {
        if (id) await actualizarDepartamento(id, departamento);
        else await crearDepartamento(departamento);
        modal.hide();
        showAlert(id ? "Departamento actualizado." : "Departamento creado.");
        await loadDepartamentos();
    } catch (error) {
        showAlert(error.message, "danger");
    } finally {
        saveButton.disabled = false;
    }
});

loadDepartamentos();
}
