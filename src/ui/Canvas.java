package ui;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ui.component.*;
import ui.component.event.IActionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class Canvas extends JFrame {

    public static final int DEFAULT_WIDTH = 1200;
    public static final int DEFAULT_HEIGHT = 700;

    public static final int MAX_ACTIVE_MENUS = 3;

    // Canvas is a singleton. Accessed using Canvas.getCanvas()
    private static final Canvas canvas = new Canvas();

    private int activeMenuCount = 0;
    private IMenu[] activeMenu = new IMenu[Canvas.MAX_ACTIVE_MENUS];

    private Canvas(){
        // initialize the JFrame and populate it with content
        this.init();
    }

    public static Canvas getCanvas(){
        return Canvas.canvas;
    }

    public int getActiveMenuCount(){
        return this.activeMenuCount;
    }

    public IMenu getActiveMenu(final int index){
        assert (index >= 0 && index < this.activeMenu.length);
        return this.activeMenu[index];
    }

    public void popActiveMenu(){
        assert (this.activeMenuCount > 0);
        this.activeMenu[this.activeMenuCount--] = null;
    }

    public void pushActiveMenu(final IMenu menu){
        assert(this.activeMenuCount + 1 < this.activeMenu.length);
        this.activeMenu[this.activeMenuCount++] = menu;
    }

    private void init(){
        super.setResizable(true);
        super.setSize(Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT);
        super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // we control closing through a window event

        // content pane consists of a menu bar and a main panel
        final JPanel content = new JPanel(new BorderLayout(0, 0));
        {
            final IMenuBar menuBar = new IMenuBar();
            {
                IButton button = new IButton("File", KeyEvent.VK_F);
               // button.setToggle(true);
                button.addEvent(() -> {
                    System.out.println("file");
                });
                button.getInternalLabel().setIcon(new File("E:\\OneDrive\\Personal\\Programming\\JetBrains\\IntelliJ Projects\\BCEditor\\Icons\\INTERFACE_ICON.png"));
                menuBar.add(button);

                IButton edit = new IButton("Edit", KeyEvent.VK_E);
                {
                    edit.addEvent(() -> {
                        System.out.println("edit");
                    });
                }
                menuBar.add(edit);
            }
            content.add(menuBar, BorderLayout.NORTH);

            final JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
            {
                mainPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

//                final JPanel main = new JPanel();
//                {
//                    main.setLayout(new BorderLayout(0, 0));
//                    main.setBackground(Color.RED);
//                    main.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
//
//                    final JPanel left = new JPanel();
//                    {
//                        left.setBackground(Color.GREEN);
//                        left.setPreferredSize(new Dimension(20, Integer.MAX_VALUE));
//                    }
//                    main.add(left, BorderLayout.WEST);
//
//                    final JPanel right = new JPanel();
//                    {
//                        right.setBackground(Color.PINK);
//                        right.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
//                    }
//                    main.add(right, BorderLayout.EAST);
//                }
//                mainPanel.add(main, BorderLayout.WEST);

                final IToolbar toolbar = new IToolbar(IOrientation.WEST);
                {
                    IButton button = new IButton("Button");
                    button.setToggle(true);
                    toolbar.addTab(new ITab(button, new IToolbar(IOrientation.EAST)));
                    //button.getInternalLabel().setOrientation(IOrientation.SOUTH);
                    //button.getInternalLabel().setIcon(new File("E:\\OneDrive\\Personal\\Programming\\JetBrains\\IntelliJ Projects\\BCEditor\\Icons\\INTERFACE_ICON.png"));
                }
                mainPanel.add(toolbar, toolbar.getOrientation().getBorder());
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
