package SystemITR.JosueGuinea.modules.empleados.controller;

import SystemITR.JosueGuinea.modules.departamentos.dto.DepartamentoRequestDTO;
import SystemITR.JosueGuinea.modules.departamentos.service.DepartamentosService;
import SystemITR.JosueGuinea.modules.empleados.dto.EmpleadosDTO;
import SystemITR.JosueGuinea.modules.empleados.service.EmpleadosService;
import SystemITR.JosueGuinea.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadosController {
    private final EmpleadosService service;

    public EmpleadosController(EmpleadosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpleadosDTO>> nuevoEmpleado(@Valid @RequestBody EmpleadosDTO json){
        try{
            EmpleadosDTO dto = service.crearEmpleado(json);
            ApiResponse<EmpleadosDTO> response = new ApiResponse<>(true, "Proceso completado", dto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<EmpleadosDTO> respuestaError = new ApiResponse<>(false, "El proceso de inserción no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpleadosDTO>>> obtenerDatos(){
        try{
            //El bloque try se ejecuta para obtener los datos, en dado caso ocurra un error automáticamente se ejecutará el bloque Catch
            List<EmpleadosDTO> lista = service.obtenerTodos();
            ApiResponse<List<EmpleadosDTO>> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", lista);
            return ResponseEntity.ok(respuestaExitosa);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<List<EmpleadosDTO>> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de departamentos", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpleadosDTO>> obtenerDatosId(@PathVariable Long id){
        try{
            //El bloque try se ejecuta para obtener los datos, en dado caso ocurra un error automáticamente se ejecutará el bloque Catch
            EmpleadosDTO lista = service.obtenerPorId(id);
            ApiResponse<EmpleadosDTO> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", lista);
            return ResponseEntity.ok(respuestaExitosa);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<EmpleadosDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de departamentos", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id){
        try{
            boolean respuesta = service.eliminarEmpleado(id);
            if (respuesta){
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true,"El empleado con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El empleado con ID: "+ id +" no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el empleado seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
