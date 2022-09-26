package com.chenyilei.mysql2h2plus.dlg;

import com.chenyilei.mysql2h2plus.context.DlgMetaContext;
import com.chenyilei.mysql2h2plus.utils.BaseUtils;
import com.chenyilei.mysql2h2plus.utils.EditorUtils;
import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
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
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class MysqlToH2Dlg extends JDialog {
    public static final String VERSION = "1.0.2";
    private final Editor mysqlEditor;
    private final JTextPane h2TxtPnl;
    private final JPanel mainPanel;

    public MysqlToH2Dlg(Project project) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /*弹框最小宽1150,高680*/
        this.setPreferredSize(new Dimension(Math.max((int) (0.7 * screenSize.getWidth()), 1150),
                Math.max((int) (0.7 * screenSize.getHeight()), 680)));
        setTitle("MysqlToH2 plus " + VERSION + ", 作者: chenyilei");
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        setAlwaysOnTop(true);

        mysqlEditor = EditorUtils.createEditor(project, PlainTextFileType.INSTANCE, "", true);
        h2TxtPnl = new JTextPane();
        Splitter splitter = new Splitter(false, 0.5F);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(mysqlEditor.getComponent()));
        splitter.setSecondComponent(ScrollPaneFactory.createScrollPane(h2TxtPnl));
        mainPanel.add(ActionManager.getInstance().createActionToolbar("Tool bar",
                topActionGroup(project), true).getComponent(), BorderLayout.NORTH);
        mainPanel.add(splitter, BorderLayout.CENTER);


        try {
//            Caused by: java.lang.NoSuchMethodError: 'void com.chenyilei.mysql2h2plus.dlg.MysqlToH2Dlg$1.<init>(com.chenyilei.mysql2h2plus.dlg.MysqlToH2Dlg, java.net.URI)'
            final URI uri = new URI("https://github.com/ChenYilei2016/mysql2h2-plus");
            JButton button = new JButton("https://github.com/ChenYilei2016/mysql2h2-plus");
            button.setBorderPainted(false);
            button.setOpaque(false);
            button.setBackground(JBColor.WHITE);
            button.setToolTipText(uri.toString());
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.addActionListener(event -> {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException eio) { /* TODO: error handling */ }
                } else { /* TODO: error handling */ }
            });
            mainPanel.add(button, BorderLayout.SOUTH);
        } catch (Throwable ignore) {
        }
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
                if (BaseUtils.isEmpty(mysqlEditor.getDocument().getText())) {
                    //提示内容为空
                    h2TxtPnl.setText("ERROR: 源内容为空");
                    return;
//                    Messages.showWarningDialog("Error:" + e.getMessage(), "Warn");
                }
                convert(myDlg);
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
                    MysqlToH2Action.dlg.repaint();
                } finally {
                    myDlg.setAlwaysOnTop(true);
                }
            }
        });
        actionGroup.addAction(new ToggleAction("table是否 drop table if exist", "table是否 drop table if exist", AllIcons.Actions.SetDefault) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return DlgMetaContext.dropTableIfExists;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                DlgMetaContext.dropTableIfExists = state;
            }
        });

        actionGroup.addAction(new ToggleAction("table是否 create if not exist", "table是否 create if not exist", AllIcons.Actions.SetDefault) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return DlgMetaContext.createTableIfNotExists;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                DlgMetaContext.createTableIfNotExists = state;
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
