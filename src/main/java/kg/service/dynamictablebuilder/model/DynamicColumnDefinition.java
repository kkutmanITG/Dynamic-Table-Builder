package kg.service.dynamictablebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_dynamic_column_definitions")
public class DynamicColumnDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_seq")
    @SequenceGenerator(name = "log_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_definition_id", nullable = false)
    private DynamicTableDefinition tableDefinition;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "column_type", nullable = false)
    private String columnType;

    @Column(name = "is_nullable", nullable = false)
    private boolean isNullable;

    @Column(name = "is_primary_key_internal", nullable = false)
    private boolean isPrimaryKeyInternal = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
