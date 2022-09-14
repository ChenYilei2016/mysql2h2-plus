package com.chenyilei.mysql2h2plus.dlg;

import com.chenyilei.mysql2h2plus.utils.BaseUtils;
import com.chenyilei.mysql2h2plus.utils.EditorUtils;
import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.IconManager;
import com.intellij.ui.ScrollPaneFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MysqlToH2Dlg extends JDialog {

    private static Editor mysqlEditor;
    private final JTextPane h2TxtPnl;
    private static JPanel mainPanel;

    public MysqlToH2Dlg(Project project) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /*弹框最小宽1150,高680*/
        this.setPreferredSize(new Dimension(Math.max((int) (0.7 * screenSize.getWidth()), 1150),
                Math.max((int) (0.7 * screenSize.getHeight()), 680)));
        setTitle("mysql to h2 plus , 作者: chenyilei, 文本较大时候转换会卡 ");
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        setAlwaysOnTop(true);

        if (mysqlEditor == null) {
            mysqlEditor = EditorUtils.createEditor(project, PlainTextFileType.INSTANCE, "", true);
        }
        h2TxtPnl = new JTextPane();
        Splitter splitter = new Splitter(false, 0.5F);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(mysqlEditor.getComponent()));
        splitter.setSecondComponent(ScrollPaneFactory.createScrollPane(h2TxtPnl));
        mainPanel.add(ActionManager.getInstance().createActionToolbar("Tool bar",
                topActionGroup(project), true).getComponent(), BorderLayout.NORTH);
        mainPanel.add(splitter, BorderLayout.CENTER);

    }

    private static @NotNull Icon load(@NotNull String path) {
        return IconManager.getInstance().getIcon(path, MysqlToH2Dlg.class);
    }

    private DefaultActionGroup topActionGroup(Project project) {
        JDialog myDlg = this;
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAction(new AnAction("Open Mysql File", "Open file", load("/icon/open.svg")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    myDlg.setAlwaysOnTop(false);
                    loadFile(project);
                } finally {
                    myDlg.setAlwaysOnTop(true);
                }
                convert(myDlg);
            }
        });
        actionGroup.addAction(new AnAction("Convert", "Convert", AllIcons.Actions.Redo) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                convert(myDlg);
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(!BaseUtils.isEmpty(mysqlEditor.getDocument().getText()));
            }
        });
        actionGroup.addAction(new AnAction("Save", "Save", load("/icon/save.svg")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    myDlg.setAlwaysOnTop(false);
                    saveFile(project);
                } finally {
                    myDlg.setAlwaysOnTop(true);
                }
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(!BaseUtils.isEmpty(h2TxtPnl.getText()));
            }
        });
        actionGroup.addAction(new AnAction("Refresh", "Refresh", AllIcons.Actions.GC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    myDlg.setAlwaysOnTop(false);
                    h2TxtPnl.setText("");
                    mysqlEditor.getDocument().setText("");
                    mainPanel.updateUI();
                } finally {
                    myDlg.setAlwaysOnTop(true);
                }
            }
        });
        return actionGroup;
    }

    private void saveFile(Project project) {
        final String h2Txt = h2TxtPnl.getText();
        if (!BaseUtils.isEmpty(h2Txt)) {
            VirtualFileWrapper targetFile = FileChooserFactory.getInstance().createSaveFileDialog(
                            new FileSaverDescriptor("Save to", "", "sql"), project)
                    .save(ProjectUtil.guessProjectDir(project), "h2.sql");
            if (targetFile != null) {
                FileUtils.copyToFile(h2Txt.getBytes(StandardCharsets.UTF_8), targetFile.getFile());
            }
        }
    }

    private void convert(JDialog myDlg) {
        try {
            final String convertTxt = MysqlToH2Utils.convert(mysqlEditor.getDocument().getText());
            h2TxtPnl.setText(convertTxt);
            mysqlEditor.getDocument().setText("");
        } catch (Exception e) {
            myDlg.setAlwaysOnTop(false);
            Messages.showWarningDialog("Error:" + e.getMessage(), "Warn");
        }
    }

    private void loadFile(Project project) {
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor("sql"),
                project, null);
        if (virtualFile == null) return;
        long length = virtualFile.getLength();
        if (length > 1048576) {
            Messages.showWarningDialog("File size:" + length + "B. Directly convert when the file larger then 1M.", "Warn");
            VirtualFileWrapper targetFile = FileChooserFactory.getInstance().createSaveFileDialog(
                            new FileSaverDescriptor("Save to", "", "sql"), project)
                    .save(ProjectUtil.guessProjectDir(project), "h2.sql");
            if (targetFile != null) {
                try {
                    MysqlToH2Utils.convert(virtualFile.getPath(), targetFile.getFile().getCanonicalPath());
                } catch (IOException e) {
                    Messages.showWarningDialog("Error:" + e.getMessage(), "Warn");
                }
            }
        } else {
            final String content = FileUtils.copyToString(new File(virtualFile.getPath()), StandardCharsets.UTF_8);
            WriteCommandAction.runWriteCommandAction(project, () -> mysqlEditor.getDocument().setText(content));
        }
    }


    public void open() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
