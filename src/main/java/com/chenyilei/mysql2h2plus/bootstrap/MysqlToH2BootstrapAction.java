package com.chenyilei.mysql2h2plus.bootstrap;

import com.chenyilei.mysql2h2plus.dlg.MysqlToH2Dialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * ICON : https://jetbrains.design/intellij/resources/icons_list/
 */
public class MysqlToH2BootstrapAction extends AnAction {

    private static final Map<Project, MysqlToH2Dialog> dialogMap = new WeakHashMap<>();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = getEventProject(event);
        if (project == null) {
            return;
        }

        MysqlToH2Dialog mysqlToH2Dialog = null;
        mysqlToH2Dialog = dialogMap.get(project);

        if (null == mysqlToH2Dialog) {
            mysqlToH2Dialog = new MysqlToH2Dialog(project);
            dialogMap.put(project, mysqlToH2Dialog);
        }
        mysqlToH2Dialog.open();

//        dlg.dispose();
    }
}
