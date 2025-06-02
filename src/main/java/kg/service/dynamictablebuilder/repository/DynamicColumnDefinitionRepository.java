package kg.service.dynamictablebuilder.repository;

import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicColumnDefinitionRepository extends JpaRepository<DynamicColumnDefinition, Long> {

    @Query("SELECT d FROM DynamicColumnDefinition d WHERE d.tableDefinition = :tableDefinition")
    List<DynamicColumnDefinition> findDynamicColumnDefinition(@Param("tableDefinition") DynamicTableDefinition tableDefinition);

}
