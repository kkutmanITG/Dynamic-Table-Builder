package kg.service.dynamictablebuilder.generator;

import kg.service.dynamictablebuilder.mapper.DynamicTableBuilderMapper;
import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;

import java.util.List;
import java.util.Map;

public class SqlGenerator {

    public static String generateCreateTableSql(DynamicTableDefinition tableDef) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ")
                .append("\"").append(tableDef.getTableName()).append("\"")
                .append(" (\n  \"id\" SERIAL PRIMARY KEY,\n");

        for (int i = 0; i < tableDef.getColumns().size(); i++) {
            DynamicColumnDefinition col = tableDef.getColumns().get(i);
            sb.append("  \"")
                    .append(col.getColumnName())
                    .append("\" ")
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


    public static SqlQueryPair generateSelectAndCountSql(String tableName, int page, int size) {
        int offset = page * size;

        String selectSql = String.format("SELECT * FROM %s ORDER BY id ASC LIMIT ? OFFSET ?", tableName);
        String countSql = String.format("SELECT COUNT(*) FROM %s", tableName);

        return new SqlQueryPair(selectSql, countSql, offset);
    }

    public static String generateSelectByIdSql(String tableName) {
        return String.format("SELECT * FROM %s WHERE id = ?", tableName);
    }

    public static String generateDeleteSql(String tableName, Long id) {
        return String.format("DELETE FROM %s WHERE id = %s", tableName, id);
    }

    public static String generateInsertSql(String tableName, List<DynamicColumnDefinition> columnDefinitions, Map<String, String> columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");

        int i = 0;
        for (DynamicColumnDefinition colDef : columnDefinitions) {
            String colName = colDef.getColumnName();
            if (colName.equalsIgnoreCase("id")) continue;  // пропускаем ID

            if (columns.containsKey(colName)) {
                if (i > 0) {
                    sql.append(", ");
                    values.append(", ");
                }
                sql.append(colName);

                String val = columns.get(colName);

                if (colDef.getColumnType().equalsIgnoreCase("text") ||
                        colDef.getColumnType().equalsIgnoreCase("varchar") ||
                        colDef.getColumnType().equalsIgnoreCase("string")) {
                    values.append("'").append(val.replace("'", "''")).append("'");
                } else {
                    values.append(val);
                }

                i++;
            }
        }

        sql.append(")");
        values.append(")");
        sql.append(values);

        return sql.toString();
    }

    public static String generateUpdateSql(String tableName, List<DynamicColumnDefinition> columns) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

        int count = 0;
        for (DynamicColumnDefinition col : columns) {
            String colName = col.getColumnName();
            if ("id".equalsIgnoreCase(colName)) continue;
            if (count > 0) {
                sql.append(", ");  // <- запятая с пробелом
            }
            sql.append(colName).append(" = ?");
            count++;
        }

        sql.append(" WHERE id = ?");

        return sql.toString();
    }

    public record SqlQueryPair(String selectSql, String countSql, int offset) {}
}
