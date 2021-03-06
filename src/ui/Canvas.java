package ui;

import project.FolderProject;
import project.Project;
import project.ZipProject;
import ui.component.*;
import util.AssetManager;
import util.async.Async;
import util.async.AsyncType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class Canvas extends JFrame {

    public static final int DEFAULT_WIDTH = 1200;
    public static final int DEFAULT_HEIGHT = 700;

    public static final int DEFAULT_MIN_WIDTH = 250;
    public static final int DEFAULT_MIN_HEIGHT = 140;

    public static final int MAX_ACTIVE_MENUS = 3;

    public static final String CANVAS_TITLE = "BCEditor";

    private static final IFileViewer viewer = new IFileViewer();
    private static final IProjectExplorer explorer = new IProjectExplorer();

    // Canvas is a singleton. Accessed using Canvas.getCanvas()
    private static final Canvas canvas = new Canvas();

    private String lastDirectory = System.getProperty("user.dir");

    private int activeMenuCount = 0;
    private IMenu[] activeMenu = new IMenu[Canvas.MAX_ACTIVE_MENUS];

    private Canvas() {
        // initialize the JFrame and populate it with content
        this.init();
    }

    public static Canvas getCanvas() {
        return Canvas.canvas;
    }

    public static IFileViewer getFileViewer() {
        return Canvas.viewer;
    }

    public static IProjectExplorer getProjectExplorer() {
        return Canvas.explorer;
    }

    public int getActiveMenuCount() {
        return this.activeMenuCount;
    }

    public IMenu getActiveMenu(final int index) {
        assert (index >= 0 && index < this.activeMenu.length);
        return this.activeMenu[index];
    }

    public void popActiveMenu() {
        assert this.activeMenuCount > 0 : "No menus to pop";
        this.activeMenu[this.activeMenuCount--] = null;
    }

    public void pushActiveMenu(final IMenu menu) {
        assert this.activeMenuCount + 1 < this.activeMenu.length : "Too many menus";
        this.activeMenu[this.activeMenuCount++] = menu;
    }

    public static void setProject(final Project project) {
        assert project == null || project.isLoaded();
        assert SwingUtilities.isEventDispatchThread();

        final IProjectExplorer explorer = Canvas.getProjectExplorer();
        if(explorer.getProject() != null) {
            explorer.clearProject();

            Canvas.getFileViewer().clearTabs();
        }
        assert explorer.getProject() == null : "Project must be unloaded";
        if(project == null) {
            Canvas.getCanvas().setTitle(Canvas.CANVAS_TITLE);
        } else {
            explorer.setProject(project);

            // Index the project
            Async.submit(project::index, AsyncType.SINGLE);

            Canvas.getCanvas().setTitle(Canvas.CANVAS_TITLE + " - " + project.getName() + " ~ [" + project.getPath() + "]");
        }
    }

    private void init() {
        super.setResizable(true);
        super.setTitle(Canvas.CANVAS_TITLE);
        super.setSize(Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT);
        super.setMinimumSize(new Dimension(Canvas.DEFAULT_MIN_WIDTH, Canvas.DEFAULT_MIN_HEIGHT));
        super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // we control closing through a window event

        // content pane consists of a menu bar and a main panel
        final JPanel content = new JPanel(new BorderLayout(0, 0));
        {
            final IMenuBar menuBar = new IMenuBar();
            {
                final IButton file = new IButton("File", KeyEvent.VK_F);
                {
                    file.addEvent(() -> {
                        final IMenu menu = new IMenu(file);
                        {
                            final IMenuItem open = new IMenuItem("Open...", AssetManager.MENU_EXPAND_ICON);
                            {
                                open.setMnemonic(KeyEvent.VK_O);
                                open.getInternalButton().addEvent(() -> {
                                    final JFileChooser chooser = new JFileChooser(this.lastDirectory);
                                    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                                    chooser.setFileFilter(new FileFilter() {
                                        @Override
                                        public boolean accept(final File file) {
                                            final String name = file.getName().toLowerCase();
                                            return file.isDirectory() || name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".class");
                                        }

                                        @Override
                                        public String getDescription() {
                                            return "Java (*.CLASS;*.JAR;*.ZIP)";
                                        }
                                    });
                                    if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                                        final File chosen = chooser.getSelectedFile();
                                        assert chosen != null && chosen.exists();
                                        this.lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();

                                        final Project project;
                                        if(chosen.isFile()) {
                                            project = new ZipProject(chosen.getAbsolutePath());
                                        } else {
                                            project = new FolderProject(chosen.getAbsolutePath());
                                        }
                                        Async.submit(() -> {
                                            try {
                                                if(project.load()) {
                                                    SwingUtilities.invokeLater(() -> Canvas.setProject(project));
                                                } else {
                                                    System.err.println("[Project] An error occurred whilst loading the project");
                                                }
                                            } catch(final IOException e) {
                                                e.printStackTrace();
                                            }
                                        }, AsyncType.SINGLE);
                                    }
                                });
                            }
                            menu.addItem(open);

                            final IMenuItem openTest = new IMenuItem("Open Test", AssetManager.MENU_EXPAND_ICON);
                            {
                                openTest.getInternalButton().addEvent(() -> {
                                    final Project project = new ZipProject("../Test.jar");
                                    Async.submit(() -> {
                                        try {
                                            if(project.load()) {
                                                SwingUtilities.invokeLater(() -> Canvas.setProject(project));
                                            } else {
                                                System.err.println("[Project] An error occurred whilst loading the project");
                                            }
                                        } catch(final IOException ignored) {
                                        }
                                    }, AsyncType.SINGLE);
                                });
                            }
                            menu.addItem(openTest);
                            //menu.addItem(new ISeparator(IOrientation.EAST));
                        }
                        this.pushActiveMenu(menu);
                    });
                }
                menuBar.add(file);

                final IButton edit = new IButton("Edit", KeyEvent.VK_E);
                {
                    edit.addEvent(() -> {
                        System.out.println("edit");
                    });
                }
                menuBar.add(edit);

                final IButton help = new IButton("Help", KeyEvent.VK_H);
                {
                    // bytecode mnemonic guide
                    // attribute guide
                    // constant pool entry types guide
                    // view source on github
                    // about (credits)

                    help.addEvent(() -> System.out.println("help"));
                }
                menuBar.add(help);
            }
            content.add(menuBar, BorderLayout.NORTH);

            final JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
            {
                mainPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

                final IToolbar toolbar = new IToolbar(IOrientation.WEST);
                {
                    final IButton project = new IButton("1: Project", KeyEvent.VK_1).setToggle(true);
                    toolbar.addTab(new ITab(project, Canvas.getProjectExplorer(), true), true);

                    project.click();

                    final IButton breakdown = new IButton("2: Breakdown", KeyEvent.VK_2).setToggle(true);
                    toolbar.addTab(new ITab(breakdown, new IBreakdown(), false), false);

                    breakdown.click();
                }
                mainPanel.add(toolbar, toolbar.getOrientation().getBorder());

                final IToolbar bottomBar = new IToolbar(IOrientation.SOUTH);
                {
                }
                mainPanel.add(bottomBar, bottomBar.getOrientation().getBorder());

                mainPanel.add(Canvas.viewer, BorderLayout.CENTER);
            }
            content.add(mainPanel, BorderLayout.CENTER);
        }
        super.setContentPane(content);

        // position the JFrame in the center of the computer screen
        super.setLocationRelativeTo(null);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
