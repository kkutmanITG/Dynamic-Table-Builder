package kg.service.dynamictablebuilder.generator;

import kg.service.dynamictablebuilder.mapper.DynamicTableBuilderMapper;
import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;

public class SqlGenerator {

    private DynamicTableBuilderMapper mapper;

    public static String generateCreateTableSql(DynamicTableDefinition tableDef) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ")
                .append(tableDef.getTableName())
                .append(" (\n  id SERIAL PRIMARY KEY,\n");

        for (int i = 0; i < tableDef.getColumns().size(); i++) {
            DynamicColumnDefinition col = tableDef.getColumns().get(i);
            sb.append("  ")
                    .append(col.getColumnName())
                    .append(" ")
                    .append(DynamicTableBuilderMapper.getColumnType(col.getColumnType()));

            if (!col.isNullable()) {
                sb.append(" NOT NULL");
            }

            if (i < tableDef.getColumns().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append(");");

        return sb.toString();
    }
}
