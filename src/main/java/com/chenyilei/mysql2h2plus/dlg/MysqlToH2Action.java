package com.chenyilei.mysql2h2plus.dlg;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * ICON : https://jetbrains.design/intellij/resources/icons_list/
 */
public class MysqlToH2Action extends AnAction {

    public static MysqlToH2Dlg dlg = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = getEventProject(event);
        if (project == null) {
            return;
        }

        dlg = new MysqlToH2Dlg(project);
        dlg.open();

//        project.
//        dlg.dispose();

    }
}
