package kg.service.dynamictablebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_dynamic_table_definitions")
public class DynamicTableDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dynamic_table_definition_seq")
    @SequenceGenerator(name = "dynamic_table_definition_seq")
    private Long id;

    @Column(name = "table_name", nullable = false, unique = true)
    private String tableName;

    @Column(name = "user_friendly_name")
    private String userFriendlyName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "tableDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DynamicColumnDefinition> columns;
}
