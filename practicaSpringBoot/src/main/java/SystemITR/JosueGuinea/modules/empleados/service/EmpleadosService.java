package SystemITR.JosueGuinea.modules.empleados.service;

import SystemITR.JosueGuinea.modules.departamentos.dto.DepartamentoRequestDTO;
import SystemITR.JosueGuinea.modules.departamentos.entity.DepartamentosEntity;
import SystemITR.JosueGuinea.modules.departamentos.repository.DepartamentosRepository;
import SystemITR.JosueGuinea.modules.empleados.dto.EmpleadosDTO;
import SystemITR.JosueGuinea.modules.empleados.entity.EmpleadosEntity;
import SystemITR.JosueGuinea.modules.empleados.repository.EmpleadosRepository;
import SystemITR.JosueGuinea.exceptions.DataNotFoundException;
import SystemITR.JosueGuinea.exceptions.DuplicateDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadosService {

    private final EmpleadosRepository repo;
    private final DepartamentosRepository repoDepartamento;


    public EmpleadosDTO crearEmpleado(EmpleadosDTO dto){
        //Verificando que el correo exista en la base de datos
        if (repo.existsByEmail(dto.getEmail())){
            throw new DuplicateDataException("El correo ya está registrado con otro empleado.");
        }
        EmpleadosEntity entity = ConvertToEntity(dto);
        EmpleadosEntity entitySave = repo.save(entity);
        return ConvertirADTO(entitySave);
    }

    private EmpleadosDTO ConvertirADTO(EmpleadosEntity entitySave) {
        EmpleadosDTO dto = new EmpleadosDTO();
        dto.setId(entitySave.getId());
        dto.setNombre(entitySave.getNombre());
        dto.setApellido(entitySave.getApellido());
        dto.setEmail(entitySave.getEmail());
        dto.setFecha_ingreso(entitySave.getFechaIngreso());
        dto.setSalario(entitySave.getSalario());
        //*****************************
        if (entitySave.getDepartamento() != null){
            dto.setIdDepartamento(entitySave.getDepartamento().getId());
            dto.setNombreDepartamento(entitySave.getDepartamento().getNombreDepto());
        }else{
            dto.setNombreDepartamento("Sin departamento asignado");
            dto.setIdDepartamento(null);
        }
        return dto;
    }

    private EmpleadosEntity ConvertToEntity(EmpleadosDTO dto) {
        EmpleadosEntity objEntity = new EmpleadosEntity();
        objEntity.setNombre(dto.getNombre());
        objEntity.setApellido(dto.getApellido());
        objEntity.setEmail(dto.getEmail());
        objEntity.setFechaIngreso(dto.getFecha_ingreso());
        objEntity.setSalario(dto.getSalario());
        if (dto.getIdDepartamento() != null){
            DepartamentosEntity objDepartamentoEntity = repoDepartamento.findById(dto.getIdDepartamento())
                    .orElseThrow(() -> new DataNotFoundException("Departamento no encontrado."));
            objEntity.setDepartamento(objDepartamentoEntity);
        }
        return objEntity;
    }

    public List<EmpleadosDTO> obtenerTodos() {
        List<EmpleadosEntity> entidades = repo.findAll();
        //Convertir las entidades a dtos
        List<EmpleadosDTO> dtos = new ArrayList<>();
        for (EmpleadosEntity entity: entidades){
            dtos.add(ConvertirADTO(entity));
        }
        return dtos;
    }

    public EmpleadosDTO obtenerPorId(Long id) {
        Optional<EmpleadosEntity> entidadOpcional = repo.findById(id);
        //Si existe, lo convertimos a DTO y los retornamos; si no, retornamos null
        if (entidadOpcional.isPresent()){
            return ConvertirADTO(entidadOpcional.get());
        }
        throw new DataNotFoundException("ID de empleado no encontrado");
    }

    public boolean eliminarEmpleado(Long id) {
        //Paso 1. Verificamos que el registro exista en la base de datos mediante el uso de existsById()
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true; //Retornamos true si el valor se eliminó correctamente
        }
        return false; //Retornamos false si el valor no se encontró
    }
}
