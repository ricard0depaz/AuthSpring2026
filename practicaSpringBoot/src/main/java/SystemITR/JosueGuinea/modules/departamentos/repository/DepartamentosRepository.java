package SystemITR.JosueGuinea.modules.departamentos.repository;

import SystemITR.JosueGuinea.modules.departamentos.entity.DepartamentosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Todas las interfaces que sean Repositorios deben contener la anotación @Repository
 */
@Repository
public interface DepartamentosRepository extends JpaRepository<DepartamentosEntity, Long> {

    //Creación de métodos personalizados

}
