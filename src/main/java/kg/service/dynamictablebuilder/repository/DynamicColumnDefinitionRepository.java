package kg.service.dynamictablebuilder.repository;

import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicColumnDefinitionRepository extends JpaRepository<DynamicColumnDefinition, Long> {
    List<DynamicColumnDefinition> findDynamicColumnDefinition(Long tableDefinition);
}
