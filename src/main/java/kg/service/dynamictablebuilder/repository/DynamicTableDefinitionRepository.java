package kg.service.dynamictablebuilder.repository;

import kg.service.dynamictablebuilder.model.DynamicTableDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicTableDefinitionRepository extends JpaRepository<DynamicTableDefinition, Long> {
    boolean existsByTableName(String tableName);
    DynamicTableDefinition findByTableName(String tableName);
    List<DynamicTableDefinition> getAll();
}
