package de.juserv.poe.hideout.service;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import de.juserv.poe.hideout.gui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;

import static com.sun.jna.platform.win32.User32.INSTANCE;

/**
 * Helper class which can check if Path of Exile is active, focused and send Inputs.
 */
public class PoEHelper {

    public static final int BUFFER_SIZE = 512;
    public static final String POE_CLASS = "POEWindowClass";
    private static final Clipboard clipboard;
    private static Robot robot;

    static {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("message.errorMessage", e.getMessage()),
                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tries to bring Path of Exile to the foreground.
     */
    public static void focus() {
        WinDef.HWND hWnd = getHWND();
        INSTANCE.SetForegroundWindow(hWnd);
        INSTANCE.ShowWindow(hWnd, User32.SW_RESTORE);
    }

    /**
     * Tries to bring Path of Exile to the foreground and wait till it is focused.
     *
     * @throws InterruptedException Exception if the wait is interrupted.
     */
    public static void focusAndWait() throws InterruptedException {
        boolean fail = false;
        focus();
        Thread.sleep(50);
        int i = 0;
        do {
            if (isFocused()) {
                return;
            }
            if (i >= 400) {
                fail = true;
            }
            i++;
            Thread.sleep(1);
        } while (!fail);
        throw new RuntimeException("Focus timeout");
    }

    /**
     * Gets the window handle to Path of Exile.
     *
     * @return {@link com.sun.jna.platform.win32.WinDef.HWND} to Path of Exile
     */
    public static WinDef.HWND getHWND() {
        AtomicReference<WinDef.HWND> result = new AtomicReference<>();
        char[] className = new char[BUFFER_SIZE];
        INSTANCE.EnumWindows((hWnd, arg1) -> {
            INSTANCE.GetClassName(hWnd, className, BUFFER_SIZE);
            String text = Native.toString(className);
            if (!text.isEmpty() && POE_CLASS.equals(text)) {
                result.set(hWnd);
                return false;
            }
            return true;
        }, null);
        return result.get();
    }

    /**
     * @return The {@link Rectangle} of the running game.
     */
    public static Rectangle getRect() {
        WinDef.RECT rect = new WinDef.RECT();
        boolean result = User32.INSTANCE.GetWindowRect(getHWND(), rect);
        if (result) {
            return rect.toRectangle();
        } else {
            return null;
        }
    }

    /**
     * Checks if Path of Exile is the active window.
     *
     * @return true, if the Game is focused.
     */
    public static boolean isFocused() {
        char[] text = new char[BUFFER_SIZE];
        INSTANCE.GetClassName(INSTANCE.GetForegroundWindow(), text, BUFFER_SIZE);
        String currentClass = Native.toString(text);
        return POE_CLASS.equals(currentClass);
    }

    /**
     * Checks if Path of Exile is running.
     *
     * @return true, if the Game is running.
     */
    public static boolean isRunning() {
        return getHWND() != null;
    }

    /**
     * This Method copies the the Text to search to the clipboard and than perform a search in Path of Exile.<br>
     * This is just sending Control+F followed by a Control+V
     *
     * @param s The Text which should be searched.
     */
    public static void performSearch(String s) {
        new Thread(() -> {
            try {
                clipboard.setContents(new StringSelection("\"" + s + "\""), null);
                focusAndWait();
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_F);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_F);
                Thread.sleep(5);
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_V);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
