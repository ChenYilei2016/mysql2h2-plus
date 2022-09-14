package com.chenyilei.mysql2h2plus.utils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public abstract class EditorUtils {
    public static Editor createEditor(Project project, FileType fileType, String content, boolean softWrap) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(content);
        Editor editor = editorFactory.createEditor(document, project);
        EditorSettings editorSettings = editor.getSettings();
        // 关闭虚拟空间
        editorSettings.setVirtualSpace(false);
        // 关闭标记位置（断点位置）
        editorSettings.setLineMarkerAreaShown(false);
        // 关闭缩减指南
        editorSettings.setIndentGuidesShown(false);
        // 显示行号
        editorSettings.setLineNumbersShown(true);
        // 支持代码折叠
        editorSettings.setFoldingOutlineShown(true);
        // 不取光标行号
        editorSettings.setCaretRowShown(false);
        // 使用软换行
        editorSettings.setUseSoftWraps(softWrap);
        // 附加行，附加列（提高视野）
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance()
                .createEditorHighlighter(project, fileType));
        return editor;
    }
}
