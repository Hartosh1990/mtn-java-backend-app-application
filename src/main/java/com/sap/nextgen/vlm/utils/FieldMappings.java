package com.sap.nextgen.vlm.utils;

import com.google.common.collect.Lists;
import com.sap.ea.eacp.remotequeryclient.sql.builder.modifier.Column;
import com.sap.ea.eacp.remotequeryclient.sql.builder.modifier.ColumnBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FieldMappings {



    public static List<Column> getColumns(Map<String, Column> map, List<String> values) {
        final boolean allFields = values.stream().anyMatch("all"::equalsIgnoreCase);

        if (allFields) return Lists.newArrayList(map.values());

        return map.entrySet()
                .stream()
                // Match fully qualified field name or group of fields like masterData.account which would include masterData.account.id and masterData.account.name
                .filter(entry -> values.contains(entry.getKey()) || values.stream().anyMatch(v -> entry.getKey().startsWith(v + ".")))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public static Collection<Column> getAllColumns(Map<String, Column> map) {
        return map.values();

    }

    public static Optional<Column> getColumn(Map<String, Column> map, String value) {
        return Optional.ofNullable(map.get(value));
    }

    private static Column of(String column) {
        return ColumnBuilder.of(column).build();
    }

    private static Column ofWithAlias(String column, String alias) {
        return ColumnBuilder.of(column, alias).build();
    }

    private static Column ofAs(String column, String asColumn) {
        return ColumnBuilder.of(column).buildAs(asColumn);
    }

    private static Column ofWithAliasAs(String column, String alias, String asColumn) {
        return ColumnBuilder.of(column, alias).buildAs(asColumn);
    }

}
