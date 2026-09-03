package SystemITR.JosueGuinea.modules.departamentos.controller;

import SystemITR.JosueGuinea.modules.departamentos.dto.DepartamentoRequestDTO;
import SystemITR.JosueGuinea.modules.departamentos.service.DepartamentosService;
import SystemITR.JosueGuinea.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentosService service;
    public DepartamentoController(DepartamentosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartamentoRequestDTO>> nuevoDepartamento(@Valid @RequestBody DepartamentoRequestDTO json){
        try{
            DepartamentoRequestDTO dto = service.nuevoDepartamento(json);
            ApiResponse<DepartamentoRequestDTO> response = new ApiResponse<>(true, "Procceso completado", dto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<DepartamentoRequestDTO> respuestaError = new ApiResponse<>(false, "El proceso de inserción no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartamentoRequestDTO>>> obtenerDatos(){
        System.out.println("Entre al controller de Get Departamentos");
        try{
            //El bloque try se ejecuta para obtener los datos, en dado caso ocurra un error automáticamente se ejecutará el bloque Catch
            List<DepartamentoRequestDTO> lista = service.obtenerTodos();
            ApiResponse<List<DepartamentoRequestDTO>> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", lista);
            return ResponseEntity.ok(respuestaExitosa);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<List<DepartamentoRequestDTO>> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de departamentos", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoRequestDTO>> obtenerDatosId(@PathVariable Long id){
        try{
            //El bloque try se ejecuta para obtener los datos, en dado caso ocurra un error automáticamente se ejecutará el bloque Catch
            DepartamentoRequestDTO lista = service.obtenerPorId(id);
            ApiResponse<DepartamentoRequestDTO> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", lista);
            return ResponseEntity.ok(respuestaExitosa);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<DepartamentoRequestDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos de departamentos", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id){
        try{
            boolean respuesta = service.eliminarDepartamento(id);
            if (respuesta){
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true,"El departamento con ID: " + id + " ha sido eliminado", null);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El departamento con ID: "+ id +" no se encontró", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el departamento seleccionado", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoRequestDTO>> actualizarDepartamento(
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoRequestDTO dto
    ){
        try{
            DepartamentoRequestDTO data = service.actualizarData(dto, id);
            if (data != null){
                ApiResponse<DepartamentoRequestDTO> respuestaExito = new ApiResponse<>(true, "Proceso completado", data);
                log.info("El departamento con ID" + id + ", fue actualizado.");
                return ResponseEntity.ok(respuestaExito);
            }
            ApiResponse<DepartamentoRequestDTO> respuestaFallida = new ApiResponse<>(true, "Proceso no completado");
            log.info("El departamento con ID" + id + ", no se pudo actualizar.");
            return ResponseEntity.ok(respuestaFallida);        }catch (Exception e){
            e.printStackTrace();
            ApiResponse<DepartamentoRequestDTO> respuestaError = new ApiResponse<>(false, "El proceso de inserción no se pudo completar", dto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
