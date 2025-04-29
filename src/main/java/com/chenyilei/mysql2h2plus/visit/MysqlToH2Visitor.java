package com.chenyilei.mysql2h2plus.visit;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.chenyilei.mysql2h2plus.context.DlgMetaContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chenyilei.mysql2h2plus.visit.MysqlToH2Helper.unquote;

/**
 * @author chenyilei
 * @date 2022/09/14 11:04
 */
public class MysqlToH2Visitor extends MySqlOutputVisitor {
    public static final AtomicInteger atomicInteger = new AtomicInteger();
    private boolean createTableIfNotExists = true;

    public MysqlToH2Visitor(Appendable appender) {
        super(appender);
        createTableIfNotExists = DlgMetaContext.createTableIfNotExists;
    }

    public boolean visit(MySqlCreateTableStatement x) {
        x.setIfNotExiists(createTableIfNotExists);
//        this.addTable(x.getName().getSimpleName());
        Map<String, SQLObject> tableOptions = x.getTableOptions();

        Set<Map.Entry<String, SQLObject>> set = tableOptions.entrySet();
        Set<String> deleteKey = new HashSet<>();

        /**
         * ....  暂时加个true , 表后属性 所有都删了
         */
        for (Map.Entry<String, SQLObject> tmp : set) {
            if ("AUTO_INCREMENT".equalsIgnoreCase(tmp.getKey()) ||
                    "CHARSET".equalsIgnoreCase(tmp.getKey()) ||
                    "CHARACTER SET".equalsIgnoreCase(tmp.getKey()) ||
                    "AVG_ROW_LENGTH".equalsIgnoreCase(tmp.getKey()) ||
                    "ENGINE".equalsIgnoreCase(tmp.getKey())
                    || true
            ) {
                deleteKey.add(tmp.getKey());
            }
        }

        for (String key : deleteKey) {
            tableOptions.remove(key);
        }
        return super.visit(x);
    }

    protected void printDataType(SQLDataType x) {
        if (x instanceof SQLCharacterDataType) {
            SQLCharacterDataType dataType = (SQLCharacterDataType) x;
            dataType.setCharSetName((String) null);
            dataType.setCollate((String) null);
        } else if (!"double".equalsIgnoreCase(x.getName()) && !"float".equalsIgnoreCase(x.getName())) {
            if ("enum".equalsIgnoreCase(x.getName())) {
                ((SQLColumnDefinition) x.getParent()).setCharsetExpr((SQLExpr) null);
            }
        } else {
            x.getArguments().clear();
        }

        super.printDataType(x);
    }

    public boolean visit(MySqlPrimaryKey x) {
        x.setIndexType((String) null);
        return super.visit(x);
    }


    public boolean visit(MySqlUnique x) {
        x.setName(unquote(x.getName().getSimpleName()) + "_" + atomicInteger.incrementAndGet());
        x.setIndexType((String) null); // bTree
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        x.setUsing(null);
        SQLName name = x.getName();
        if (name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) name;
            sqlIdentifierExpr.setName(unquote(x.getName().getSimpleName()) + "_" + atomicInteger.incrementAndGet());
        }
        return super.visit(x);
    }


    public boolean visit(MySqlTableIndex x) {
        x.setName(new SQLIdentifierExpr(unquote(x.getName().getSimpleName()) + "_" + atomicInteger.incrementAndGet()));
        x.setIndexType((String) null);
        //做length去除
        if (DlgMetaContext.enableBetaFunction) {
            if (null != x.getColumns()) {
                for (SQLSelectOrderByItem column : x.getColumns()) {
                    if (column.getExpr() instanceof SQLMethodInvokeExpr) {
                        column.setExpr(new SQLIdentifierExpr(((SQLMethodInvokeExpr) column.getExpr()).getMethodName()));
                    }
                }
            }
        }
        return super.visit(x);
    }

    public boolean visit(MySqlKey x) {
        x.setName(unquote(x.getName().getSimpleName()) + "_" + atomicInteger.incrementAndGet());
        x.setIndexType((String) null);
        return super.visit(x);
    }


    public boolean visit(MySqlLockTableStatement x) {
        return false;
    }

    public boolean visit(MySqlUnlockTablesStatement x) {
        return false;
    }

    protected void printTableElements(List<SQLTableElement> tableElementList) {
        Iterator<SQLTableElement> iterator = tableElementList.iterator();

        while (iterator.hasNext()) {
            SQLTableElement sqlTableElement = iterator.next();
            if (sqlTableElement instanceof SQLColumnDefinition) {
                SQLColumnDefinition sqlColumnDefinition = (SQLColumnDefinition) sqlTableElement;
                if ("json".equalsIgnoreCase(sqlColumnDefinition.getDataType().getName())) {
//                    sqlColumnDefinition.setDataType(new SQLDataTypeImpl("varchar", 6666));
                    sqlColumnDefinition.setDataType(new SQLDataTypeImpl("text"));
                }
                /**
                 * h2本身支持虚拟列, 但是不能用 virtual字段
                 */
//                sqlColumnDefinition.setGeneratedAlawsAs(null);
                sqlColumnDefinition.setVirtual(false);
            }
            if (
                    sqlTableElement instanceof MysqlForeignKey
//                            || sqlTableElement instanceof MySqlKey
            ) {
                iterator.remove();
            }
        }
        super.printTableElements(tableElementList);

//        while (true) {
//            SQLTableElement sqlTableElement;
//            do {
//                if (!iterator.hasNext()) {
//                    super.printTableElements(tableElementList);
//                    return;
//                }
//
//                sqlTableElement = (SQLTableElement) iterator.next();
//            } while (!(sqlTableElement instanceof MysqlForeignKey) && !(sqlTableElement instanceof MySqlKey));
//
//            iterator.remove();
//        }
    }

    public boolean visit(SQLSetStatement x) {
        List<SQLAssignItem> items = x.getItems();
        Iterator<SQLAssignItem> iterator = items.iterator();

        while (true) {
            String target;
            do {
                if (!iterator.hasNext()) {
                    if (items.size() > 0) {
                        return super.visit(x);
                    }

                    x.setAfterSemi(false);
                    return false;
                }

                target = ((SQLAssignItem) iterator.next()).getTarget().toString();
            } while (!"SQL_MODE".equalsIgnoreCase(target) && !"time_zone".equalsIgnoreCase(target));

            iterator.remove();
        }
    }

    public boolean visit(SQLCharExpr x) {
        if ("0000-00-00 00:00:00".equalsIgnoreCase(x.getText())) {
            x.setText("0001-01-01 00:00:00");
        }

        return super.visit(x);
    }

    public boolean visit(SQLBinaryExpr x) {
        this.print0(x.getText());
        return false;
    }

    public boolean visit(SQLCreateDatabaseStatement x) {
        x.setAfterSemi(false);
        return false;
    }

    public boolean visit(SQLUseStatement x) {
        x.setAfterSemi(false);
        return false;
    }

    public boolean visit(SQLStartTransactionStatement x) {
        x.setAfterSemi(false);
        return false;
    }

    @Override
    public boolean visit(SQLCreateViewStatement.Column x) {
        return super.visit(x);
    }

}
