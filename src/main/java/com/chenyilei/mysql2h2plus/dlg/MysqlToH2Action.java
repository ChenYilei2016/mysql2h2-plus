package com.chenyilei.mysql2h2plus.dlg;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MysqlToH2Action extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = getEventProject(event);
        if (project == null) {
            return;
        }

        new MysqlToH2Dlg(project).open();
    }
}
