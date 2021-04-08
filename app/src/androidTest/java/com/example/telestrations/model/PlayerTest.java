package com.example.telestrations.model;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PlayerTest {

    @Test
    public void createPlayer() throws InterruptedException {

        Player player = new Player("test", "1234", "avatar_r2d2", -3866369);

        assertEquals(player.getDisplayName().equals("test"), true);
        assertEquals(player.getId().equals("1234"), true);
        assertEquals(player.getImageFileName().equals("avatar_r2d2"), true);
        assertEquals(player.getNameColor(),-3866369);


    }

    @Test
    public void setImage() throws InterruptedException {
        String newAvatar = "avatar_bender";
        Player player = new Player("test", "1234", "avatar_r2d2", -3866369);

        player.setImageFileName(newAvatar);

        assertEquals(player.getImageFileName().equals(newAvatar), true);

    }

    @Test
    public void setNameColor() throws InterruptedException {
        int color = -65536;
        Player player = new Player("test", "1234", "avatar_r2d2", -3866369);

        player.setNameColor(color);

        assertEquals(player.getNameColor(),color);

    }


    @Test
    public void setDisplayName() throws InterruptedException {
        String newName = "NEW NAME";
        Player player = new Player("test", "1234", "avatar_r2d2", -3866369);

        player.setDisplayName(newName);

        assertEquals(player.getDisplayName().equals(newName), true);

    }


}
