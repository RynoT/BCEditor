package ui.component;

import project.Project;
import project.filetype.ClassType;
import project.filetype.FileType;
import ui.component.explorer.IFileNode;
import ui.component.explorer.IFolderNode;
import ui.component.explorer.ITileNode;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IProjectExplorer extends IComponent {

    public static final Color BACKGROUND_COLOR = new Color(80, 80, 82);
    public static final Color TITLE_BACKGROUND_COLOR_PROJECT = new Color(70, 70, 90);
    public static final Color TITLE_BACKGROUND_COLOR_NO_PROJECT = new Color(75, 65, 65);

    public static final int TITLE_BAR_HEIGHT = 24;

    public static final String DEFAULT_TITLE_NO_PROJECT = "No Project";

    private final ILabel title;
    private final JPanel titlePanel, projectPanel;

    private Project project = null;
    private IRootNode root = null;

    public IProjectExplorer() {
        super.setLayout(new BorderLayout(0, 0));
        super.setBackground(IProjectExplorer.BACKGROUND_COLOR);

        final JPanel titleBar = new JPanel();
        {
            titleBar.setLayout(new BoxLayout(titleBar, BoxLayout.X_AXIS));
            titleBar.setBackground(IProjectExplorer.TITLE_BACKGROUND_COLOR_NO_PROJECT);
            titleBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, IProjectExplorer.TITLE_BAR_HEIGHT));
            titleBar.setBorder(new IBorder(0, 0, 1, 0));

            this.title = new ILabel(IProjectExplorer.DEFAULT_TITLE_NO_PROJECT);
            this.title.setAlignment(ITextAlign.LEFT);
            this.title.setPadding(0, 0, 0, ITileNode.TILE_DEFAULT_INSET);
            titleBar.add(this.title, BorderLayout.WEST);
        }
        this.titlePanel = titleBar;
        super.add(titleBar, BorderLayout.NORTH);

        final JPanel project = new JPanel();
        {
            project.setOpaque(false);
            project.setLayout(new BoxLayout(project, BoxLayout.Y_AXIS));
        }
        this.projectPanel = project;

        final IScrollPanel scrollPanel = new IScrollPanel(project, false, true);
        {
            scrollPanel.setOpaque(false);
        }
        super.add(scrollPanel, BorderLayout.CENTER);
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(final Project project) {
        if(this.project != null) {
            this.project.unload();
        }
        this.project = project;
        assert (SwingUtilities.isEventDispatchThread());
        this.projectPanel.removeAll();
        if(project == null) {
            this.root = null;
            this.title.setText(IProjectExplorer.DEFAULT_TITLE_NO_PROJECT);
            this.titlePanel.setBackground(IProjectExplorer.TITLE_BACKGROUND_COLOR_NO_PROJECT);
            return;
        }
        assert (project.isLoaded()); //project should always be loaded before setting

        // Set title of explorer
        this.title.setText(project.getName());
        this.titlePanel.setBackground(IProjectExplorer.TITLE_BACKGROUND_COLOR_PROJECT);
        // Populate the explorer
        synchronized(Project.class) {
            final Set<FileType> files = project.getFiles();

            // Populate files into a hierarchy
            final IRootNode root = new IRootNode();
            for(final FileType file : files) {
                IFolderNode target = root;
                for(final String name : file.getPath().split("/")) {
                    if(name.trim().equals("")) {
                        break;
                    }
                    final IFolderNode node = target.findFolder(name);
                    if(node == null) {
                        target.addChild(target = new IFolderNode(name, target));
                    } else {
                        target = node;
                    }
                }
                String name = file.getName();
                if(!(file instanceof ClassType)) {
                    // Show extension for all files which are not classes (.class)
                    name = name + "." + file.getExtension();
                }
                target.addChild(new IFileNode(file, name, target));
            }
            this.root = root;
        }
        this.root.sortAll(); // sort the hierarchy with recursion
        this.root.addTo(this.projectPanel);

        this.projectPanel.revalidate();
        this.projectPanel.repaint();
    }

    private class IRootNode extends IFolderNode {

        private IRootNode() {
            super("root", null);
        }

        private void sortAll() {
            this.sortRecursive(this);
        }

        private void sortRecursive(final IFolderNode node) {
            node.sort();
            for(final ITileNode next : node.getChildren()) {
                if(!(next instanceof IFolderNode)) {
                    break;
                }
                this.sortRecursive((IFolderNode) next);
            }
        }

        private void addTo(final Container parent) {
            assert (SwingUtilities.isEventDispatchThread());
            for(final ITileNode node : super.getChildren()) {
                parent.add(node);
                if(node instanceof IFileNode) {
                    ((IFileNode) node).updateIcon();
                }
            }
            parent.revalidate();
        }
    }
}
