package SystemITR.JosueGuinea.modules.departamentos.service;

import SystemITR.JosueGuinea.modules.departamentos.dto.DepartamentoRequestDTO;
import SystemITR.JosueGuinea.modules.departamentos.entity.DepartamentosEntity;
import SystemITR.JosueGuinea.modules.departamentos.repository.DepartamentosRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DepartamentosService {

    //Forma 2: Creando un constructor en la que se crea un objeto del Repositorio
    private final DepartamentosRepository repo;

    public DepartamentosService(DepartamentosRepository repo) {
        this.repo = repo;
    }

    public DepartamentoRequestDTO nuevoDepartamento(DepartamentoRequestDTO dto){
        //Crear un entity
        DepartamentosEntity datosConvertidos = convertirAEntity(dto);
        //Realizar la inserción a la base de datos
        DepartamentosEntity respuesta = repo.save(datosConvertidos);
        return convertirADTO(respuesta);
    }

    private DepartamentoRequestDTO convertirADTO(DepartamentosEntity respuesta) {
        DepartamentoRequestDTO dto = new DepartamentoRequestDTO();
        dto.setId(respuesta.getId());
        dto.setNombreDepto(respuesta.getNombreDepto());
        dto.setAbreviatura(respuesta.getAbreviatura());
        dto.setUbicacion(respuesta.getUbicacion());
        return dto;
    }

    private DepartamentosEntity convertirAEntity(DepartamentoRequestDTO dto) {
        //¿Inicialmente toda la información donde está?
        //Getter: Obtener datos
        //Setter: Asignar datos
        //Paso 1. Creamos un objeto de tipo Entity
        DepartamentosEntity entity = new DepartamentosEntity();
        //Paso 2. Transferir datos
        entity.setNombreDepto(dto.getNombreDepto());
        entity.setAbreviatura(dto.getAbreviatura());
        entity.setUbicacion(dto.getUbicacion());
        //Paso 3. Retonar el objeto entity
        return entity;
    }

    public List<DepartamentoRequestDTO> obtenerTodos() {
        List<DepartamentosEntity> entidades = repo.findAll();
        //Convertir las entidades a dtos
        List<DepartamentoRequestDTO> dtos = new ArrayList<>();
        for (DepartamentosEntity entity: entidades){
            dtos.add(convertirADTO(entity));
        }
        return dtos;
    }

    public DepartamentoRequestDTO obtenerPorId(Long id) {
        Optional<DepartamentosEntity> entidadOpcional = repo.findById(id);
        //Si existe, lo convertimos a DTO y los retornamos; si no, retornamos null
        if (entidadOpcional.isPresent()){
            return convertirADTO(entidadOpcional.get());
        }
        return null;
    }

    public boolean eliminarDepartamento(Long id) {
        //Paso 1. Verificamos que el registro exista en la base de datos mediante el uso de existsById()
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true; //Retornamos true si el valor se eliminó correctamente
        }
        return false; //Retornamos false si el valor no se encontró
    }

    public DepartamentoRequestDTO actualizarData(@Valid DepartamentoRequestDTO dto, Long id) {
        try{
            //1. Buscamos si el departamento realmente existes por su ID
            Optional<DepartamentosEntity> registroExistente = repo.findById(id);
            //2. Se verifica si el objeto contiene valores
            if (registroExistente.isPresent()){
                //3. Creamos un nuevo objeto entidad
                DepartamentosEntity entidad = registroExistente.get();
                //4. Convertir y asignar los dtos (nuevos valores) a la entidad
                entidad.setNombreDepto(dto.getNombreDepto());
                entidad.setAbreviatura(dto.getAbreviatura());
                entidad.setUbicacion(dto.getUbicacion());
                //5. Actualizar datos en la base de datos
                DepartamentosEntity datosGuardados = repo.save(entidad);
                //6. Retornar la data convertida a DTO
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Ooops, ocurrió un error al procesar la información");
            return null;
        }
    }
}
